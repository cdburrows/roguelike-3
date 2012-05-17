package com.cburrows.android.roguelike;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.modifier.IModifier;

import com.cburrows.android.roguelike.Monster.MonsterState;

import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public class BattleScene extends GameScene  {
    
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 512;
    private static final int BG_IMAGE_X = 0;
    private static final int BG_IMAGE_Y = 0;
    private static final float MONSTER_FADE_DELAY = 0.4f;       // how long after the scene is presented to start presenting the monster
    private static final float MONSTER_FADE_DURATION = 0.3f;    // how long to fade in the monster
    
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
    private TextureRegion mMonsterTextureRegion;
    private Sprite mMonsterSprite;
    
    private Monster mMonster;
    private Animation[] mSlash;
    
    private boolean mSceneReady;
    private boolean mAnimating;
    
    private float mTouchX;
    private float mTouchY;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private float mTotalTouchOffsetX;
    private float mTotalTouchOffsetY;
    private int mSwipeDirection;
    
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

        mBitmapTextureAtlas = new BitmapTextureAtlas(TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "dungeon_bg_320.png", BG_IMAGE_X, BG_IMAGE_Y);
        mBackgroundSprite = new Sprite(0, 0, mCameraWidth, mCameraHeight, mBackgroundTextureRegion);
        
        mMonsterTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "monsters/monsters.png", 0, 320);
        mMonsterSprite = new Sprite(
                (mCameraWidth / 2) - (mMonsterTextureRegion.getWidth() / 2),
                (mCameraHeight / 2) - (mMonsterTextureRegion.getHeight() / 2), mMonsterTextureRegion);
        mMonsterSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        mMonster = new Monster();
        mMonster.setSprite(mMonsterSprite);
        
        mContext.getTextureManager().loadTexture(mBitmapTextureAtlas);
        
        mSlash = new Animation[8];
        for (int i = 0; i < 8; i++) {
            switch (i) {            
                case SWIPE_DOWN_LEFT:
                    mSlash[SWIPE_DOWN_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()+48);
                     mSlash[SWIPE_DOWN_LEFT].loadAnimation(mContext);
                     mSlash[SWIPE_DOWN_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN_RIGHT:
                    mSlash[SWIPE_DOWN_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_DOWN_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_RIGHT:
                    mSlash[SWIPE_UP_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_UP_RIGHT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_UP_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_LEFT:
                    mSlash[SWIPE_UP_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_LEFT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_UP_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_LEFT:
                    mSlash[SWIPE_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY(),
                            mCamera.getCenterX()-48, mCamera.getCenterY());
                    mSlash[SWIPE_LEFT].setRotation(45.0f);
                    mSlash[SWIPE_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_RIGHT:
                    mSlash[SWIPE_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY(),
                            mCamera.getCenterX()+48, mCamera.getCenterY());
                    mSlash[SWIPE_RIGHT].setRotation(-135.0f);
                    //mSlash[SWIPE_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP:
                    mSlash[SWIPE_UP] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()+48,
                            mCamera.getCenterX(), mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP].setRotation(135.0f);
                    mSlash[SWIPE_UP].loadAnimation(mContext);
                    mSlash[SWIPE_UP].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN:
                    mSlash[SWIPE_DOWN] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()-48,
                            mCamera.getCenterX(), mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN].setRotation(-45.0f);
                    mSlash[SWIPE_DOWN].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN].attachOnFinishListener(onAnimationDone);
                    break;
            }
        }
        
        attachChild(mBackgroundSprite);
        attachChild(mMonsterSprite);
        
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
        
        mCamera.setHUD(mHud);
        mCamera.setChaseEntity(null);
        mCamera.setCenter(mCameraWidth / 2,  mCameraHeight / 2);
        
        this.registerEntityModifier(new DelayModifier(MONSTER_FADE_DELAY, new IEntityModifierListener() {
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                mMonster.fadeIn(MONSTER_FADE_DURATION, sceneLoadListener);
            }
        }));

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
            float mTouchUpX = pTouchEvent.getMotionEvent().getX();
            float mTouchUpY = pTouchEvent.getMotionEvent().getY();
            
            float angle = (float)Math.toDegrees(Math.atan2(mTouchUpY - mTouchY, mTouchUpX - mTouchX));
            if (angle < 0) angle += 360;

            mSwipeDirection = (int)(((angle + 22.5) % 360) / 45);
            
            checkHit();
        }
        return true;
    }
    
    void checkHit() {
        Log.d("FIGHT", mSceneReady + ", " + !mMonster.isDead() + ", " + !mAnimating + ", " + mSwipeDirection);
        if (mSceneReady && !mMonster.isDead() && !mAnimating) {
            
            attachChild(mSlash[mSwipeDirection].getSprite());
            mAnimating = true;
            mSlash[mSwipeDirection].start();
            
            int damage = mMonster.hit(10);
            //mContext.gameToast("You deal " + damage + " damage!", Toast.LENGTH_SHORT);
            if (mMonster.getCurHP() <= 0) {
                mMonster.setMonsterState(MonsterState.MONSTER_DEAD);
                mMonster.fadeOut(MONSTER_FADE_DURATION, battleWinListener);
            }
        }
    }
    
    final IEntityModifierListener onAnimationDone = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            Log.d("FIGHT", "ANIMATIO NDONE");
            mAnimating = false;
        }
    };
    
    final IEntityModifierListener sceneLoadListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mSceneReady = true;
        }
    };
    
    final IEntityModifierListener battleWinListener  = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            DelayModifier delayMod = new DelayModifier(MONSTER_FADE_DELAY);
            delayMod.addModifierListener(new IEntityModifierListener() {
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                    mContext.endCombat();
                }
                
            });
            pItem.registerEntityModifier(delayMod);
        }
        
    };
    
}
