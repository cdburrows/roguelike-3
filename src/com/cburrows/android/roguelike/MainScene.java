package com.cburrows.android.roguelike;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.cburrows.android.roguelike.TmxMap.Map;

public class MainScene extends GameScene {
    private static final float TOUCH_SENSITIVITY = 32.0f;
    private static final int TEXTURE_ATLAS_WIDTH = 128;
    private static final int TEXTURE_ATLAS_HEIGHT = 256;
    private static final int SPRITE_IMAGE_X = 0;
    private static final int SPRITE_IMAGE_Y = 0;
    private static final int HUD_IMAGE_X = 0;
    private static final int HUD_IMAGE_Y = 128;
    public static final float FIGHT_CHANCE = 0.40f;
    
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private Player mPlayer;
    private GameMap mMap;
    private TMXTiledMap mTMXTiledMap;
    
    private TiledTextureRegion mIconTextureRegion;
    private AnimatedSprite mStatusIcon;
    private AnimatedSprite mMapIcon;
    
    private HUD mHud;
    
    private float mTouchX;
    private float mTouchY;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private float mTotalTouchOffsetX;
    private float mTotalTouchOffsetY;
    
    private Random rand;

    public MainScene(RoguelikeActivity context) {
        super(context);
        rand = new Random(System.currentTimeMillis());
    }
    
    public void loadResources() {
        long timeStart = System.currentTimeMillis();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        mBitmapTextureAtlas = new BitmapTextureAtlas(TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        mPlayer = new Player(BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mBitmapTextureAtlas, mContext, "hero.png", SPRITE_IMAGE_X, SPRITE_IMAGE_Y, 4, 4) );
        
        mIconTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mBitmapTextureAtlas, mContext, "icons.png", HUD_IMAGE_X, HUD_IMAGE_Y, 2, 2);
        
        mStatusIcon = new AnimatedSprite (8, 8, 32, 32, mIconTextureRegion);
        mStatusIcon.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mStatusIcon.setAlpha(0.50f);
        
        mMapIcon = new AnimatedSprite(mCameraWidth - 40, 8, mIconTextureRegion.deepCopy());
        mMapIcon.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mMapIcon.setAlpha(0.50f);
        
        mContext.getTextureManager().loadTexture(mBitmapTextureAtlas);
        
        // TODO: Dump gameMap map data when tmx max loaded -- maybe keep the tmx data inside gameMap.
        mMap = new GameMap(66, 55);
        mPlayer.setParentMap(mMap);
        mPlayer.setRoom(0, 0);
        
        FileOutputStream fos;
        try {
            fos = mContext.openFileOutput("TEST.tmx", Context.MODE_WORLD_READABLE);
            Map.deflate(mMap, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTMXTiledMap = mMap.getTmxTiledMap(mContext, mContext.getTextureManager());
        attachChild(mTMXTiledMap.getTMXLayers().get(0));
        
        mHud = new HUD();
        mHud.attachChild(mStatusIcon);
        mHud.attachChild(mMapIcon);
        
        mContext.getEngine().registerUpdateHandler(new UpdateHandler());
        
        attachChild(mPlayer.getAnimatedSprite());

        setOnSceneTouchListener((IOnSceneTouchListener)mContext);
        
        mLoaded = true;
        Log.d("MAIN", "Load time: " + (System.currentTimeMillis() - timeStart));
    }
    
    public void initialize() {
     // Load the map
        
        mStatusIcon.setCurrentTileIndex(0);
        mMapIcon.setCurrentTileIndex(2);
        
        /* Make the camera not exceed the bounds of the TMXEntity. */
        mCamera.setBounds(0, mTMXTiledMap.getTMXLayers().get(0).getWidth(),
                0, mTMXTiledMap.getTMXLayers().get(0).getHeight());
        mCamera.setBoundsEnabled(false);

        /* Create the sprite and add it to the scene. */
        mCamera.setChaseEntity(mPlayer.getAnimatedSprite());
        mCamera.updateChaseEntity();
        mCamera.setHUD(mHud);
        
        mPlayer.setPlayerState(PlayerState.IDLE);
        
        mTransitionOver = false;
        mInitialized = true;
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
                    mPlayer.move(Direction.DIRECTION_RIGHT, mPlayer.getTileWidth() * mMap.ROOM_WIDTH);
                } else if (mTotalTouchOffsetX > 0) {
                    mPlayer.move(Direction.DIRECTION_LEFT, mPlayer.getTileWidth() * mMap.ROOM_WIDTH);
                }
            } else if (Math.abs(mTotalTouchOffsetY) >= TOUCH_SENSITIVITY) {
                if (mTotalTouchOffsetY < 0) {
                    mPlayer.move(Direction.DIRECTION_DOWN, mPlayer.getTileHeight() * mMap.ROOM_HEIGHT);
                } else if (mTotalTouchOffsetY > 0) {
                    mPlayer.move(Direction.DIRECTION_UP, mPlayer.getTileHeight() * mMap.ROOM_HEIGHT);
                }
            }
            
            mTouchX = newX;
            mTouchY = newY;
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            mStatusIcon.setCurrentTileIndex(0);
            mMapIcon.setCurrentTileIndex(2);
            
        }
        return true;
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
    
    public void openStatus() {
        //mContext.gameToast("STATUS", 50);
        //mContext.startCombat();
        Animation slash = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()-48,
                mCamera.getCenterX()-48, mCamera.getCenterY()+48);
        AnimatedSprite animation = slash.loadAnimation(mContext);
        attachChild(animation);
        slash.start();
    }
    
    public void openMiniMap() {
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
}
