package com.cburrows.android.roguelike;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.DurationEntityModifier;
import org.anddev.andengine.entity.modifier.EntityModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.IModifier.DeepCopyNotSupportedException;

import com.cburrows.android.roguelike.Monster.MonsterState;

import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public class BattleScene extends GameScene  {
    
    private static final float HP_OPACITY = 0.40f;
    private static final float XP_SCROLL_TIME = 0.5f;
    
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 1024;
    private static final int BG_IMAGE_X = 0;
    private static final int BG_IMAGE_Y = 0;
    private static final int POPUP_IMAGE_Y = 320;
    private static final int MONSTER_IMAGE_Y = 352;
    
    private static final float VICTORY_TEXT_Y = 8;
    private static final String VICTORY_TEXT = "Victory!";
    private static final float LEVEL_TEXT_Y = 48;
    private static final float LEVEL_TEXT_X = 8;
    private static final float NO_SPOILS_TEXT_Y = 92;
    
    // All relative to spoils panel
    private static final float XP_BAR_X = 120;
    private static final float XP_BAR_Y = 48;
    private static final float XP_BAR_WIDTH = 104;
    private static final float XP_BAR_HEIGHT = 16;
    
    private static final float POPUP_POS_X = 80;
    private static final float POPUP_POS_Y = 16;
    
    private static final float MONSTER_FADE_DELAY = 0.3f;       // how long after the scene is presented to start presenting the monster
    private static final float MONSTER_FADE_DURATION = 0.3f;    // how long to fade in the monster
    private static final float TEXT_OPACITY = 0.8f;
    private static final float TEXT_DISPLAY_DURATION = 1.5f;  
    
    private static final float CAMERA_SHAKE_INTENSITY = 10.0f;
    private static final float CAMERA_SHAKE_DURATON = 0.35f;
    
    private static final int SWIPE_RIGHT = 0;
    private static final int SWIPE_DOWN_RIGHT = 1;
    private static final int SWIPE_DOWN = 2;
    private static final int SWIPE_DOWN_LEFT = 3;
    private static final int SWIPE_LEFT = 4;
    private static final int SWIPE_UP_LEFT = 5;
    private static final int SWIPE_UP = 6;
    private static final int SWIPE_UP_RIGHT = 7;
   
    private HUD mHud;
    
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mBackgroundTextureRegion;
    private Sprite mBackgroundSprite;
    private TextureRegion mPopupTitleTextureRegion;
    private Sprite mPopupTitleSprite;
    private TextureRegion mMonsterTextureRegion;
    private Sprite mMonsterSprite;
    
    private TextureRegion mHpBarRegion;
    private TextureRegion mHpBarFillRegion;
    private Sprite mHPBar;
    private Sprite mHPBarFill;
    
    private TextureRegion mSpoilsTitleTextureRegion;
    private TextureRegion mXpBarRegion;
    private TextureRegion mXpBarFillRegion;
    private Sprite mSpoilsSprite;
    private Sprite mXpBarSprite;
    private Sprite mXpBarFillSprite;
    private ChangeableText mVictoryText;
    private ChangeableText mLevelText;
    private Text mNoSpoilsText;

    private ChangeableText mMonsterNameText;
    
    private Monster mMonster;
    private Player mPlayer;
    private Animation[] mSlash;
    
    private boolean mSceneReady;
    private boolean mAnimating;
    
    private float mTouchX;
    private float mTouchY;
    private float mTouchUpX;
    private float mTouchUpY;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private float mTotalTouchOffsetX;
    private float mTotalTouchOffsetY;
    private int mSwipeDirection;
    
    private float mTime = 0f;
    private int mXpGained;
    
    private Random rand;
    
    public BattleScene(RoguelikeActivity context) {
        super(context);
        rand = new Random(System.currentTimeMillis());
    }
    
    @Override
    public void loadResources() {
        long timeStart = System.currentTimeMillis();

        mHud = new HUD();
        
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        mBitmapTextureAtlas = new BitmapTextureAtlas(TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT, 
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        // The background
        mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "dungeon_bg_320.png", BG_IMAGE_X, BG_IMAGE_Y);
        mBackgroundSprite = new Sprite(0, 0, mCameraWidth, mCameraHeight, mBackgroundTextureRegion);
        
        // The monster
        mMonsterTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "monsters/monsters.png", 0, MONSTER_IMAGE_Y);
        mMonsterSprite = new Sprite(
                (mCameraWidth / 2) - (mMonsterTextureRegion.getWidth() / 2),
                (mCameraHeight / 2) - (mMonsterTextureRegion.getHeight() / 2), mMonsterTextureRegion);
        mMonsterSprite.setScale(mContext.getGameScaleX(), mContext.getGameScaleY());
        mMonsterSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        mMonster = new Monster(mContext.getGameScaleX(), mContext.getGameScaleY());
        mMonster.setSprite(mMonsterSprite);
        
        // The monster name popup panel
        mPopupTitleTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "panels/popup_title.png", 0, POPUP_IMAGE_Y);
        mPopupTitleSprite = new Sprite (POPUP_POS_X * mContext.getGameScaleX(),
                POPUP_POS_Y * mContext.getGameScaleY(), 160 * mContext.getGameScaleX(),
                32 * mContext.getGameScaleY(), mPopupTitleTextureRegion);
        mPopupTitleSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  

        // The monster name text
        mMonsterNameText = new ChangeableText(0, POPUP_IMAGE_Y + (8 * mContext.getGameScaleY()), mContext.Font, "MONSTER NAME");
        mMonsterNameText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mPopupTitleSprite.attachChild(mMonsterNameText);
        
        // The HP bar
        int HP_IMAGE_Y = MONSTER_IMAGE_Y + mMonsterTextureRegion.getHeight();
        mHpBarRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "panels/hp_bar.png", 0, HP_IMAGE_Y);
        mHPBar = new Sprite((mCameraWidth / 2) - (mHpBarRegion.getWidth() / 2 * mContext.getGameScaleX()), 
                mCameraHeight - (24 * mContext.getGameScaleY()),
                mHpBarRegion.getWidth() * mContext.getGameScaleX(),
                mHpBarRegion.getHeight() * mContext.getGameScaleY(),
                mHpBarRegion);
        mHPBar.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mHPBar.setAlpha(HP_OPACITY);
        
        int HP_FILL_IMAGE_Y = HP_IMAGE_Y + mHpBarRegion.getHeight();
        mHpBarFillRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "panels/hp_fill_bar.png", 0, HP_FILL_IMAGE_Y);
        mHPBarFill = new Sprite((mCameraWidth / 2) - (mHpBarFillRegion.getWidth() / 2 * mContext.getGameScaleX()), 
                mCameraHeight - (24 * mContext.getGameScaleY()),
                mHpBarFillRegion.getWidth() * mContext.getGameScaleX(),
                mHpBarFillRegion.getHeight() * mContext.getGameScaleY(),
                mHpBarFillRegion);
        mHPBarFill.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mHPBarFill.setAlpha(HP_OPACITY);
        
        // The spoils panel
        mSpoilsTitleTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "panels/display_panel.png", 0, 
                        HP_FILL_IMAGE_Y + mHpBarFillRegion.getHeight());
        mSpoilsSprite = new Sprite (
                (mCamera.getWidth() / 2) - (mSpoilsTitleTextureRegion.getWidth() / 2 * mContext.getGameScaleX()),
                (mCamera.getHeight() / 2) - (mSpoilsTitleTextureRegion.getHeight() / 2 * mContext.getGameScaleY()) - (8 * mContext.getGameScaleY()),
                mSpoilsTitleTextureRegion.getWidth() * mContext.getGameScaleX(),
                mSpoilsTitleTextureRegion.getHeight() * mContext.getGameScaleY(),
                mSpoilsTitleTextureRegion);
        mSpoilsSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mSpoilsSprite.setAlpha(0.0f);
        
        mXpBarRegion = mHpBarRegion.deepCopy();
        mXpBarSprite = new Sprite(XP_BAR_X * mContext.getGameScaleX(), XP_BAR_Y * mContext.getGameScaleY(), 
                XP_BAR_WIDTH * mContext.getGameScaleX(), XP_BAR_HEIGHT * mContext.getGameScaleY(),
                mXpBarRegion);
        mXpBarSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                
        mXpBarFillRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "panels/xp_fill_bar.png", 0, 
                        mSpoilsTitleTextureRegion.getTexturePositionY() + mSpoilsTitleTextureRegion.getHeight());
        mXpBarFillSprite = new Sprite((XP_BAR_X+1) * mContext.getGameScaleX(), (XP_BAR_Y+1) * mContext.getGameScaleY(), 
                (XP_BAR_WIDTH-2) * mContext.getGameScaleX(), (XP_BAR_HEIGHT-2) * mContext.getGameScaleY(),
                mXpBarFillRegion);
        mXpBarFillSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        mVictoryText = new ChangeableText(0, VICTORY_TEXT_Y * mContext.getGameScaleY(), mContext.LargeFont, VICTORY_TEXT);
        mVictoryText.setPosition(mSpoilsSprite.getWidth() / 2 - (mVictoryText.getWidth() / 2), VICTORY_TEXT_Y * mContext.getGameScaleY());
        mVictoryText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mLevelText = new ChangeableText(LEVEL_TEXT_X * mContext.getGameScaleX(), LEVEL_TEXT_Y * mContext.getGameScaleY(), mContext.Font, "Lvl 88");
        mLevelText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mNoSpoilsText = new Text(0, NO_SPOILS_TEXT_Y * mContext.getGameScaleY(), mContext.Font, "No spoils!");
        mNoSpoilsText.setPosition(mSpoilsSprite.getWidth() / 2 - (mNoSpoilsText.getWidth() / 2), NO_SPOILS_TEXT_Y * mContext.getGameScaleY());
        mNoSpoilsText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        mSpoilsSprite.attachChild(mVictoryText);
        mSpoilsSprite.attachChild(mLevelText);
        mSpoilsSprite.attachChild(mNoSpoilsText);
        mSpoilsSprite.attachChild(mXpBarSprite);
        mSpoilsSprite.attachChild(mXpBarFillSprite);
        
        mContext.getTextureManager().loadTexture(mBitmapTextureAtlas);
        
        // The slash animations
        mSlash = new Animation[8];
        for (int i = 0; i < 8; i++) {
            switch (i) {            
                case SWIPE_DOWN_LEFT:
                    mSlash[SWIPE_DOWN_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN_LEFT].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_DOWN_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN_RIGHT:
                    mSlash[SWIPE_DOWN_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN_RIGHT].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_DOWN_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_DOWN_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_RIGHT:
                    mSlash[SWIPE_UP_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_RIGHT].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_UP_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_UP_RIGHT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_UP_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_LEFT:
                    mSlash[SWIPE_UP_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_LEFT].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_UP_LEFT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_UP_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_LEFT:
                    mSlash[SWIPE_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY(),
                            mCamera.getCenterX()-48, mCamera.getCenterY());
                    mSlash[SWIPE_LEFT].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_LEFT].setRotation(45.0f);
                    mSlash[SWIPE_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_RIGHT:
                    mSlash[SWIPE_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY(),
                            mCamera.getCenterX()+48, mCamera.getCenterY());
                    mSlash[SWIPE_RIGHT].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_RIGHT].setRotation(-135.0f);
                    mSlash[SWIPE_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP:
                    mSlash[SWIPE_UP] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()+48,
                            mCamera.getCenterX(), mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_UP].setRotation(135.0f);
                    mSlash[SWIPE_UP].loadAnimation(mContext);
                    mSlash[SWIPE_UP].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN:
                    mSlash[SWIPE_DOWN] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()-48,
                            mCamera.getCenterX(), mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN].setScale(mContext.getGameScaleX());
                    mSlash[SWIPE_DOWN].setRotation(-45.0f);
                    mSlash[SWIPE_DOWN].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN].attachOnFinishListener(onAnimationDone);
                    break;
            }
        }
        
        attachChild(mBackgroundSprite);
        attachChild(mPopupTitleSprite);
        attachChild(mMonsterSprite);
        attachChild(mSpoilsSprite);
        
        mHud.attachChild(mHPBar);
        mHud.attachChild(mHPBarFill);
        
        mPlayer = mContext.getPlayer();
        
        this.registerUpdateHandler(updateHandler);
        
        mLoaded = true;
        
        Log.d("BATTLE", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    @Override
    public void initialize() {
        mSceneReady = false;
        
        mMonsterSprite.setAlpha(0f);
        mMonster.setMonsterState(MonsterState.MONSTER_TRANSITION_IN);
        mMonster.setDead(false);
        mMonster.setMaxHP(60);
        mMonster.setCurHP(60);
        mMonster.setAttack(8);
        mMonster.setDefense(3);
        mMonster.setSpeed(4.5f);
        
        mSpoilsSprite.setAlpha(0f);
        mVictoryText.setAlpha(0f);
        mLevelText.setAlpha(0f);
        mNoSpoilsText.setAlpha(0f);
        mXpBarSprite.setAlpha(0f);
        mXpBarFillSprite.setAlpha(0f);
        
        mPopupTitleSprite.setAlpha(0f);
        mMonsterNameText.setAlpha(0);
        mMonsterNameText.setText("Slime");
        mMonsterNameText.setPosition((mPopupTitleSprite.getWidth() / 2) - (mMonsterNameText.getWidth() / 2), 
                8 * mContext.getGameScaleY());
        
        updateHP();
        updateXP();
        
        mCamera.setHUD(mHud);
        mCamera.setChaseEntity(null);
        mCamera.setCenter(mCameraWidth / 2,  mCameraHeight / 2);
        
        monsterFadeInModifier.reset();
        monsterNameFadeOutModifier.reset();
        spoilsFadeInModifer.reset();
        xpBarModifier.reset();
        
        this.registerEntityModifier(monsterFadeInModifier);
        this.registerEntityModifier(monsterNameFadeOutModifier);

        mTime = 0f;
        mInitialized = true;
    }
    
    public void pause() {
        
    }
    
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            mTouchX = pTouchEvent.getMotionEvent().getX();
            mTouchY = pTouchEvent.getMotionEvent().getY();
            mTotalTouchOffsetX = 0;
            mTotalTouchOffsetY = 0;     
            
            if (mSpoilsSprite.getAlpha() == TEXT_OPACITY && mSceneReady 
                    && mSpoilsSprite.contains(mTouchX, mTouchY)) {
                // If the player closes the spoils menu, make sure we take care
                // of xp.
                //xpBarModifier.reset();
                mSceneReady = false;
                mPlayer.increaseXP(mXpGained);
                updateXP();
                updateHP();
                mContext.endCombat();
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
            
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            mTouchUpX = pTouchEvent.getMotionEvent().getX();
            mTouchUpY = pTouchEvent.getMotionEvent().getY();
            
            float diffX = mTouchUpX - mTouchX;
            float diffY = mTouchUpY - mTouchY;
 
            if (diffX != 0 && diffY != 0) {    
                float angle = (float)Math.toDegrees(Math.atan2(diffY, diffX));
                if (angle < 0) angle += 360;
    
                mSwipeDirection = (int)(((angle + 22.5) % 360) / 45);

                checkHit();
            }
        }
        return true;
    }
    
    private void checkHit() {
        Line line = new Line(mTouchX, mTouchY, mTouchUpX, mTouchUpY);
        if (mSceneReady && !mMonster.isDead() && !mAnimating /* && mMonsterSprite.collidesWith(line) */) {
            attachChild(mSlash[mSwipeDirection].getSprite());
            mAnimating = true;
            mSlash[mSwipeDirection].start();
            
            int damage = mMonster.hit(mPlayer.getAttack());
            //mContext.gameToast("You deal " + damage + " damage!", Toast.LENGTH_SHORT);
            if (mMonster.getCurHP() <= 0) {
                mXpGained = 8;
                mMonster.setMonsterState(MonsterState.MONSTER_DEAD);
                mMonster.fadeOut(MONSTER_FADE_DURATION, battleWinListener);
            }
        }
    }
    
    private void updateHP() {
        mHPBarFill.setWidth( ((float)mHpBarFillRegion.getWidth()) * mPlayer.getHPFraction() * mContext.getGameScaleX());
    }
    
    private void updateXP() {
        mXpBarFillSprite.setWidth( ((float)XP_BAR_WIDTH-2) * mPlayer.getXPFraction() * mContext.getGameScaleX());
        mLevelText.setText("Lvl " + mPlayer.getLevel());
    }
    
    private void endCombat() {
        this.registerEntityModifier(spoilsFadeInModifer);
        //mContext.endCombat();
    }
    
    private final IUpdateHandler updateHandler = new IUpdateHandler() {

        public void onUpdate(float pSecondsElapsed) {
           mTime += pSecondsElapsed;
           if (mMonster.targetable() && mTime > 1.5f) {
               mTime = 0f;
               if (rand.nextFloat() < 0.5f) 
                   mMonster.jumpForward(0.4f, monsterAttackListener);
               else
                   mMonster.jumpBackward(0.4f);
           }
        }
        public void reset() {}
    };
    
    
    // Make the monster fade in, after MONSTER_FADE_DELAY seconds
    final DelayModifier monsterFadeInModifier = new DelayModifier(MONSTER_FADE_DELAY, new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mMonster.fadeIn(MONSTER_FADE_DURATION, sceneLoadListener);
            mPopupTitleSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));
            mMonsterNameText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));
        }
    });
    
    // Make the monster name panel to fade out  after MONSTER_FADE_DELAY + TEXT_DISPLAY_DURATION seconds
    final DelayModifier monsterNameFadeOutModifier = new DelayModifier(MONSTER_FADE_DELAY + TEXT_DISPLAY_DURATION,
            new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mPopupTitleSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, TEXT_OPACITY, 0f));
            mMonsterNameText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, TEXT_OPACITY, 0f));    
        }
    });
    
    // Make the spoils panel fade in
    final DelayModifier spoilsFadeInModifer = new DelayModifier(MONSTER_FADE_DELAY,
            new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mSpoilsSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));
            mVictoryText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));
            mLevelText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY)); 
            mNoSpoilsText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY)); 
            mXpBarSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));
            mXpBarFillSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));

            registerEntityModifier(xpBarModifier);
            
        }
    });
    
    // Increases the xp bar after combat
    final DurationEntityModifier xpBarModifier = new DurationEntityModifier(XP_SCROLL_TIME) { 
        float dx;
        float xp;
        
        public EntityModifier deepCopy() throws DeepCopyNotSupportedException {
            return null;
        }
        
        @Override
        protected void onManagedUpdate(float pSecondsElapsed, IEntity pItem) {
            if (mSceneReady) {
                xp += dx * pSecondsElapsed;
                float fraction = (mPlayer.getCurXP() + xp) / mPlayer.getNextXP();
                if (fraction >= 1f) {
                    // level up !
                    
                    int next = mPlayer.getNextXP();
                    mPlayer.increaseXP((int)xp);
                    mXpGained -= (int)xp;
                    this.
                    dx = mXpGained / (XP_SCROLL_TIME - this.getSecondsElapsed());
                    xp = 0; 
                    fraction = xp / mPlayer.getNextXP();
                    updateHP();
                    updateXP();
                }
                mXpBarFillSprite.setWidth( ((float)XP_BAR_WIDTH-2) * fraction * mContext.getGameScaleX());
            }
        }
        
        @Override
        protected void onManagedInitialize(IEntity pItem) {
            xp = 0;
            dx = mXpGained / XP_SCROLL_TIME;
        }
    };
    
    // Listens for the monster to stop animating
    final IEntityModifierListener onAnimationDone = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mAnimating = false;
        }
    };
    
    // Listens for the monster and name panel to load
    final IEntityModifierListener sceneLoadListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mSceneReady = true;
        }
    };
    
    // Listens for monster attack to shake camera and take damage
    final IEntityModifierListener monsterAttackListener  = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {    }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            shake(CAMERA_SHAKE_DURATON, CAMERA_SHAKE_INTENSITY);    
            mPlayer.decreaseHP(rand.nextInt(10)+1);
            updateHP();
        }
    };
    
    // Opens spoils panel
    final IEntityModifierListener battleWinListener  = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            DelayModifier delayMod = new DelayModifier(MONSTER_FADE_DELAY);
            delayMod.addModifierListener(new IEntityModifierListener() {
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                    endCombat();
                }    
            });
            pItem.registerEntityModifier(delayMod);
        }
    };
    
}
