package com.cburrows.android.roguelike;

import java.util.Random;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import android.util.Log;
import android.view.MotionEvent;

public class MainScene extends GameScene {
    public static final float FIGHT_CHANCE = 0.40f;
    private static final float TOUCH_SENSITIVITY = 32.0f;
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 512;
    //private static final int HUD_IMAGE_X = 0;
    //private static final int HUD_IMAGE_Y = 128;
    //private static final int HP_IMAGE_Y = 256;
    //private static final int HP_FILL_IMAGE_Y = 272;
    private static final float HUD_OPACITY = 0.3f;
    
    private Player mPlayer;
    private Dungeon mDungeon;
    //private GameMap mMap;
    //private TMXTiledMap mTMXTiledMap;
    
    private TiledSprite mStatusIcon;
    private TiledSprite mMapIcon;
    private TiledSprite mPotionIcon;
    private ChangeableText mPotionText;
    
    private Sprite mHPBar;
    private Sprite mHPBarFill;
    
    private HUD mHud;
    
    private float mTouchX;
    private float mTouchY;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private float mTotalTouchOffsetX;
    private float mTotalTouchOffsetY;
    
    private Random rand;
    
    private Sprite mItem;

    public MainScene(RoguelikeActivity context) {
        super(context);
        rand = new Random(System.currentTimeMillis());
    }
    
    public void loadResources() {
        long timeStart = System.currentTimeMillis();
        
        float scaleX = RoguelikeActivity.sScaleX;
        float scaleY = RoguelikeActivity.sScaleY;
        
        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        
        mStatusIcon = Graphics.createTiledSprite("icons.png", 4, 4, 
                scaleX, scaleY, HUD_OPACITY);
        
        mMapIcon = Graphics.createTiledSprite("icons.png", 4, 4, HUD_OPACITY);
        mMapIcon.setPosition(
                mCameraWidth - mMapIcon.getWidth(),
                scaleY); 
        
        mPotionIcon = Graphics.createTiledSprite("icons.png", 4, 4, HUD_OPACITY);
        mPotionIcon.setPosition(
                mCameraWidth - mPotionIcon.getWidth() - scaleX,
                mCameraHeight - mPotionIcon.getHeight() - scaleY);
   
        mPotionText = Graphics.createChangeableText(mPotionIcon.getX() - (28 * scaleX) , 
                mPotionIcon.getY() + (11 * scaleY), 
                Graphics.SmallFont, "88x", HUD_OPACITY);
        
        mHPBar = Graphics.createSprite("panels/hp_bar.png", 0, 0, HUD_OPACITY * 1.5f);
        mHPBar.setPosition(
                (mCameraWidth / 2) - (mHPBar.getWidth() / 2), 
                mCameraHeight - (24 * scaleY));
        
        mHPBarFill =  Graphics.createSprite("panels/hp_fill_bar.png", 0, 0, HUD_OPACITY * 1.5f);
        mHPBarFill.setPosition(
                (mCameraWidth / 2) - (mHPBarFill.getWidth() / 2), 
                mCameraHeight - (24 * scaleY));
        
        Graphics.endLoad("MAIN");
        
        // TODO: Dump gameMap map data when tmx max loaded -- maybe keep the tmx data inside gameMap.
        //mMap = RoguelikeActivity.sDungeonDefinition.getGameMap(); //new GameMap(66, 45);
        
        /*
        try {
            Map.deflate(mMap, mContext.openFileOutput("TEXT.tmx", mContext.MODE_WORLD_WRITEABLE));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        
       // mTMXTiledMap = mMap.getTmxTiledMap(mContext, mContext.getTextureManager());
        //TMXLoader loader = new TMXLoader(mContext, mContext.getTextureManager());

        /*
        try {
            mTMXTiledMap = loader.load(mContext.openFileInput("untitled1.tmx"));
        } catch (TMXLoadException e) {
            // TODO Auto-generated catch block
            Log.d("ERROR", "ERROR");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.d("ERROR", "ERROR");
        }
        */
        
        //mMap = RoguelikeActivity.sDungeon.getGameMap();
        //mTMXTiledMap = RoguelikeActivity.sDungeon.getTmxMap();
        //TMXLayer layer = mTMXTiledMap.getTMXLayers().get(0);
        //layer.setScaleCenter(0, 0);
        //layer.setScale(scaleX, scaleY);
        mDungeon = RoguelikeActivity.sDungeon;
        MonsterFactory.initialize(mDungeon.getMonsterList());
        attachChild(RoguelikeActivity.sDungeon.getSprite());
        
        //Log.d("Map", "0,0:" + layer.getTMXTile(0, 0).getTileY());
        
        mPlayer = mContext.getPlayer();
        mPlayer.setParentMap(mDungeon.getGameMap());
        mPlayer.setRoom(0, 0);
        mPlayer.setCurHP(80);
        mContext.setPlayer(mPlayer);
        
        mHud = new HUD();
        mHud.attachChild(mStatusIcon);
        mHud.attachChild(mMapIcon);
        mHud.attachChild(mHPBar);
        mHud.attachChild(mHPBarFill);
        mHud.attachChild(mPotionIcon);
        mHud.attachChild(mPotionText);
        attachChild(mPlayer.getAnimatedSprite());
        
        mContext.getEngine().registerUpdateHandler(new UpdateHandler());
        setOnSceneTouchListener((IOnSceneTouchListener)mContext);
        
        mLoaded = true;
        Log.d("MAIN", "Load time: " + (System.currentTimeMillis() - timeStart));
    }
    
    public void prepare(IEntityModifierListener preparedListener) {
     // Load the map
        
        mStatusIcon.setCurrentTileIndex(0);
        mMapIcon.setCurrentTileIndex(2);
        mPotionIcon.setCurrentTileIndex(6);
        mPotionText.setText(String.format("%02d", mPlayer.getNumPotions()) + "x");
        updateHP();

        /* Create the sprite and add it to the scene. */
        mCamera.setChaseEntity(mPlayer.getAnimatedSprite());
        mCamera.updateChaseEntity();
        mCamera.setHUD(mHud);
        
        mPlayer.setPlayerState(PlayerState.IDLE);
        
        mTransitionOver = false;
        mPrepared = true;
        preparedListener.onModifierFinished(null, this);
    }
    
    public void pause() {
        
    }
    
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
        if (!mTransitionOver) return true;
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            mTouchX = pTouchEvent.getMotionEvent().getX();
            mTouchY = pTouchEvent.getMotionEvent().getY();
            mTotalTouchOffsetX = 0;
            mTotalTouchOffsetY = 0;
            
            if (mStatusIcon.contains(mTouchX, mTouchY)) {
                mStatusIcon.setCurrentTileIndex(1);
                openStatus();
            }
            
            if (mMapIcon.contains(mTouchX, mTouchY)) {
                mMapIcon.setCurrentTileIndex(3);
                openMiniMap();
            }
            
            if (mPotionIcon.contains(mTouchX, mTouchY)) {
                mPotionIcon.setCurrentTileIndex(7);
                usePotion();
            }
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {    
            float newX = pTouchEvent.getMotionEvent().getX();
            float newY = pTouchEvent.getMotionEvent().getY();
           
            mTouchOffsetX = (newX - mTouchX);
            mTouchOffsetY = (newY - mTouchY);
            mTotalTouchOffsetX += mTouchOffsetX;
            mTotalTouchOffsetY += mTouchOffsetY;
                                           
            if (Math.abs(mTotalTouchOffsetX) >= TOUCH_SENSITIVITY) {
                if (mTotalTouchOffsetX < 0) {
                    mPlayer.move(Direction.DIRECTION_RIGHT, mPlayer.getTileWidth() * Dungeon.getRoomWidth());
                } else if (mTotalTouchOffsetX > 0) {
                    mPlayer.move(Direction.DIRECTION_LEFT, mPlayer.getTileWidth() * Dungeon.getRoomWidth());
                }
            } else if (Math.abs(mTotalTouchOffsetY) >= TOUCH_SENSITIVITY) {
                if (mTotalTouchOffsetY < 0) {
                    mPlayer.move(Direction.DIRECTION_DOWN, mPlayer.getTileHeight() * Dungeon.getRoomHeight());
                } else if (mTotalTouchOffsetY > 0) {
                    mPlayer.move(Direction.DIRECTION_UP, mPlayer.getTileHeight() * Dungeon.getRoomHeight());
                }
            }
            
            mTouchX = newX;
            mTouchY = newY;
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            mStatusIcon.setCurrentTileIndex(0);
            mMapIcon.setCurrentTileIndex(2);
            mPotionIcon.setCurrentTileIndex(6);
            
        }
        return true;
    }

    
    
    private void openStatus() {
        mContext.openStatus();
    }
    
    private void openMiniMap() {
        if (mItem == null || !mItem.isVisible()) {
            //Item item = ItemFactory.createRandomWeapon(1);
            //mItem = item.getSprite();
            //mItem.setPosition(64, 96);
            //if (!mItem.hasParent()) attachChild(mItem);
            //mItem.setVisible(true);
        } else {
            //mItem.setVisible(false);
        }
            
        
        //ItemFactory.createRandomItem(mContext, 0);
        //shake(2.0f, 2.0f);
        //mContext.gameToast("MINIMAP", 50);
        /*
        Scene minimapScene = new Scene();
        
        BitmapTextureAtlas minimapTextureAtlas = new BitmapTextureAtlas(
                64, 32, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        TiledTextureRegion minimapTextureRegion = 
                BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mIconTextureAtlas, this, "minimap_icons.png", 0, 0, 4, 2);
        mStatusIcon = new AnimatedSprite(8,8,mIconTextureRegion); 
        
        mMainScene.attachChild(minimapScene);
        */
    }
    
    private void updateHP() {
        mHPBarFill.setWidth( ((float)mHPBar.getWidth()) * mPlayer.getHPFraction());
    }
    
    private void usePotion() {
        mPlayer.usePotion();
        mPotionText.setText(String.format("%02d", mPlayer.getNumPotions()) + "x");
        updateHP();
    }
    
    private class UpdateHandler implements IUpdateHandler {
        public void onUpdate(float pSecondsElapsed) {
            Event event = mPlayer.update(pSecondsElapsed);
            
            switch (event) {
                case EVENT_NO_EVENT:
                    break;
                    
                case EVENT_NEW_ROOM:
                    if (rand.nextFloat() <= FIGHT_CHANCE) {
                        mPlayer.setPlayerState(PlayerState.FIGHTING);
                        mContext.startCombat();
                    }
                    break;
            }
        }

        public void reset() {               
        }
    }
}
