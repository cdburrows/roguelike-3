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
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.util.modifier.IModifier;
import com.cburrows.android.roguelike.Monster.MonsterState;
import com.cburrows.android.roguelike.components.FloatingText;
import com.cburrows.android.roguelike.components.ProgressBar;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

public class BattleScene extends GameScene  {
    
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 1024;
    
    private static final float HP_OPACITY = 0.40f;
    private static final float XP_SCROLL_TIME = 0.5f;
    private static final int NUM_DAMAGE_TEXTS = 10;
    
    private static final float MONSTER_FADE_DELAY = 0.3f;       // how long after the scene is presented to start presenting the monster
    private static final float MONSTER_FADE_DURATION = 0.3f;    // how long to fade in the monster
    
    private static final int MONSTER_NAME_FRAME_Y = 18;
    private static final int MONSTER_NAME_FRAME_WIDTH = 160;
    private static final int MONSTER_NAME_FRAME_HEIGHT = 48;
    private static final float MONSTER_NAME_DISPLAY_DURATION = 1.5f;
    private static final float MONSTER_NAME_OPACITY = 0.8f;
    private static final int MONSTER_NAME_TEXT_Y = 8;
    private static final int MONSTER_LEVEL_TEXT_Y = 30;
    
    private static final float MONSTER_HP_X = 0;
    private static final float MONSTER_HP_Y = 8;
    private static final int MONSTER_HP_WIDTH = 192;
    private static final int MONSTER_HP_HEIGHT = 6;
    private static final int MONSTER_HP_COLOR = Color.RED;
    private static final float MONSTER_HP_ALPHA = 0.75f;
    
    private static final float CAMERA_SHAKE_INTENSITY = 10.0f;
    private static final float CAMERA_SHAKE_DURATON = 0.35f;
    
    // All relative to spoils panel
    private static final float VICTORY_TEXT_Y = 8;
    private static final String VICTORY_TEXT = "Victory!";
    private static final float LEVEL_TEXT_Y = 40;
    private static final float LEVEL_TEXT_X = 8;
    private static final float NO_SPOILS_TEXT_Y = 92;
    private static final float XP_BAR_X = 120;
    private static final float XP_BAR_Y = 40;
    private static final float XP_BAR_WIDTH = 112;
    private static final float XP_BAR_HEIGHT = 16;
    //private static final int ITEM_X = 0;
    private static final int ITEM_Y = 92;

    private static final int SWIPE_RIGHT = 0;
    private static final int SWIPE_DOWN_RIGHT = 1;
    private static final int SWIPE_DOWN = 2;
    private static final int SWIPE_DOWN_LEFT = 3;
    private static final int SWIPE_LEFT = 4;
    private static final int SWIPE_UP_LEFT = 5;
    private static final int SWIPE_UP = 6;
    private static final int SWIPE_UP_RIGHT = 7;
   
    private static float sScaleX;
    private static float sScaleY;
    
    private HUD mHud;
    
    private Sprite mMonsterSprite;
    private Sprite mBackgroundSprite;
    private Sprite mPopupTitleSprite;
    private Sprite mHPBar;
    private Sprite mHPBarFill;
    //private Sprite mSpoilsSprite;
    private Sprite mXpBarSprite;
    private Sprite mXpBarFillSprite;    
    private TiledSprite mSpoilItem;
    private ChangeableText mVictoryText;
    private ChangeableText mLevelText;
    private ChangeableText mMonsterNameText;
    private ChangeableText mMonsterLevelText;
    private Text mNoSpoilsText;
    private Text mPlusText;
    private ProgressBar mMonsterHp;
    
    private FloatingText[] mDamageNumber;
    private int mCurDamageNumber;
    
    private Monster mMonster;
    private Player mPlayer;
    private Animation[] mSlash;
    
    private boolean mSceneReady;
    private boolean mAnimating;
    
    private float mTouchX;
    private float mTouchY;
    private float mTouchUpX;
    private float mTouchUpY;
    //private float mTouchOffsetX;
    //private float mTouchOffsetY;
    //private float mTotalTouchOffsetX;
    //private float mTotalTouchOffsetY;
    private int mSwipeDirection;
    
    private float mTime = 0f;
    private int mXpGained;
    
    private static Random sRand = new Random(System.currentTimeMillis());;
    private Sprite mPotionIconSprite;
    
    public BattleScene(RoguelikeActivity context) {
        super(context);
        sScaleX = RoguelikeActivity.sScaleX;
        sScaleY = RoguelikeActivity.sScaleY;
    }
    
    @Override
    public void loadResources() {
        long timeStart = System.currentTimeMillis();

        mHud = new HUD();
        
        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        
        // The background
        mBackgroundSprite = Graphics.createSprite("dungeon_bg_320.png");
        
        // The monster name popup panel
        mPopupTitleSprite = Graphics.createSprite("panels/popup_title.png", 0, 0);
        mPopupTitleSprite.setSize(MONSTER_NAME_FRAME_WIDTH * sScaleX, MONSTER_NAME_FRAME_HEIGHT * sScaleY);
        mPopupTitleSprite.setPosition(
                (mCameraWidth / 2) - (mPopupTitleSprite.getWidth() / 2), MONSTER_NAME_FRAME_Y * sScaleY );
        
        // The monster name text
        mMonsterNameText = Graphics.createChangeableText(0, MONSTER_NAME_FRAME_Y * sScaleY + (MONSTER_NAME_TEXT_Y * sScaleY), 
                Graphics.Font, "MONSTER NAME");
        mPopupTitleSprite.attachChild(mMonsterNameText);
        
        mMonsterLevelText = Graphics.createChangeableText(0, MONSTER_NAME_FRAME_Y * sScaleY + (MONSTER_LEVEL_TEXT_Y * sScaleY), 
                Graphics.SmallFont, "Level XX");
        mPopupTitleSprite.attachChild(mMonsterLevelText);
        
        // The HP bar
        mHPBar = Graphics.createSprite("panels/hp_bar.png", 0, 0, HP_OPACITY * 1.5f);
        mHPBar.setPosition(
                (mCameraWidth / 2) - (mHPBar.getWidth() / 2), 
                mCameraHeight - (24 * sScaleY));
        
        mHPBarFill = Graphics.createSprite("panels/hp_fill_bar.png", 0, 0, HP_OPACITY * 1.5f);
        mHPBarFill.setPosition(
                (mCameraWidth / 2) - (mHPBarFill.getWidth() / 2), 
                mCameraHeight - (24 * sScaleY));
               
        // The spoils panel
        /*
        mSpoilsSprite = Graphics.createSprite("panels/display_panel.png");
        mSpoilsSprite.setPosition(
                (mCamera.getWidth() / 2) - (mSpoilsSprite.getWidth() / 2 ),
                (mCamera.getHeight() / 2) - (mSpoilsSprite.getHeight() / 2) - (8 * sScaleY));
        */
        mXpBarSprite = Graphics.createSprite("panels/hp_bar.png", 
                XP_BAR_X * sScaleX, 
                XP_BAR_Y * sScaleY);
        mXpBarSprite.setWidth(XP_BAR_WIDTH * sScaleX);
        mXpBarSprite.setHeight(XP_BAR_HEIGHT * sScaleY);
        
        mXpBarFillSprite = Graphics.createSprite("panels/xp_fill_bar.png",
                XP_BAR_X * sScaleX, 
                XP_BAR_Y *sScaleY);
        mXpBarFillSprite.setWidth(XP_BAR_WIDTH * sScaleX);
        mXpBarFillSprite.setHeight(XP_BAR_HEIGHT * sScaleY);
        
        Graphics.endLoad("BATTLE");
        
        mPotionIconSprite = ItemFactory.getPotionSprite();
        mPotionIconSprite.setPosition(
                (mCameraWidth / 2) - (mPotionIconSprite.getWidth() / 2),
                NO_SPOILS_TEXT_Y * sScaleY);
        mPotionIconSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        mVictoryText = Graphics.createChangeableText(0, VICTORY_TEXT_Y * sScaleY, Graphics.LargeFont, VICTORY_TEXT);
        mVictoryText.setPosition(mCameraWidth / 2 - (mVictoryText.getWidth() / 2), VICTORY_TEXT_Y * sScaleY);
        mLevelText = Graphics.createChangeableText(LEVEL_TEXT_X * sScaleX, LEVEL_TEXT_Y * sScaleY, Graphics.Font, "Lvl 88");
        mNoSpoilsText = Graphics.createText(0, NO_SPOILS_TEXT_Y * sScaleY, Graphics.Font, "No spoils!");
        mNoSpoilsText.setPosition(mCameraWidth / 2 - (mNoSpoilsText.getWidth() / 2), NO_SPOILS_TEXT_Y * sScaleY);
        mPlusText = Graphics.createText( (mCameraWidth / 2) - (mPotionIconSprite.getWidth() / 2) - (20 * sScaleX), 
                (NO_SPOILS_TEXT_Y + 4) * sScaleY, Graphics.LargeFont, "+");
        
        mMonsterHp = new ProgressBar(MONSTER_HP_X, MONSTER_HP_Y, MONSTER_HP_WIDTH, MONSTER_HP_HEIGHT, 
                MONSTER_HP_COLOR, MONSTER_HP_ALPHA, 100);
 
        // The slash animations
        mSlash = new Animation[8];
        for (int i = 0; i < 8; i++) {
            switch (i) {            
                case SWIPE_DOWN_LEFT:
                    mSlash[SWIPE_DOWN_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN_LEFT].setScale(sScaleX);
                    mSlash[SWIPE_DOWN_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN_RIGHT:
                    mSlash[SWIPE_DOWN_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN_RIGHT].setScale(sScaleX);
                    mSlash[SWIPE_DOWN_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_DOWN_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_RIGHT:
                    mSlash[SWIPE_UP_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_RIGHT].setScale(sScaleX);
                    mSlash[SWIPE_UP_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_UP_RIGHT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_UP_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_LEFT:
                    mSlash[SWIPE_UP_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_LEFT].setScale(sScaleX);
                    mSlash[SWIPE_UP_LEFT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_UP_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_LEFT:
                    mSlash[SWIPE_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY(),
                            mCamera.getCenterX()-48, mCamera.getCenterY());
                    mSlash[SWIPE_LEFT].setScale(sScaleX);
                    mSlash[SWIPE_LEFT].setRotation(45.0f);
                    mSlash[SWIPE_LEFT].loadAnimation(mContext);
                    mSlash[SWIPE_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_RIGHT:
                    mSlash[SWIPE_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY(),
                            mCamera.getCenterX()+48, mCamera.getCenterY());
                    mSlash[SWIPE_RIGHT].setScale(sScaleX);
                    mSlash[SWIPE_RIGHT].setRotation(-135.0f);
                    mSlash[SWIPE_RIGHT].loadAnimation(mContext);
                    mSlash[SWIPE_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP:
                    mSlash[SWIPE_UP] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()+48,
                            mCamera.getCenterX(), mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP].setScale(sScaleX);
                    mSlash[SWIPE_UP].setRotation(135.0f);
                    mSlash[SWIPE_UP].loadAnimation(mContext);
                    mSlash[SWIPE_UP].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN:
                    mSlash[SWIPE_DOWN] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()-48,
                            mCamera.getCenterX(), mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN].setScale(sScaleX);
                    mSlash[SWIPE_DOWN].setRotation(-45.0f);
                    mSlash[SWIPE_DOWN].loadAnimation(mContext);
                    mSlash[SWIPE_DOWN].attachOnFinishListener(onAnimationDone);
                    break;
            }
        }
        
        attachChild(mBackgroundSprite);
        attachChild(mPopupTitleSprite);
        
        attachChild(mVictoryText);
        attachChild(mLevelText);
        attachChild(mNoSpoilsText);
        attachChild(mPlusText);
        attachChild(mPotionIconSprite);
        attachChild(mXpBarSprite);
        attachChild(mXpBarFillSprite);
        attachChild(mMonsterHp.getEntity());
        
        mHud.attachChild(mHPBar);
        mHud.attachChild(mHPBarFill);
        
        mDamageNumber = new FloatingText[NUM_DAMAGE_TEXTS];
        for (int i = 0; i < NUM_DAMAGE_TEXTS; i++) {
            mDamageNumber[i] = new FloatingText(Graphics.Font, Color.WHITE, 0, 0, "00");
            mHud.attachChild(mDamageNumber[i].getEntity());
        }
        
        mPlayer = mContext.getPlayer();
        
        this.registerUpdateHandler(updateHandler);
        
        
        
        mLoaded = true;
        
        Log.d("BATTLE", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    @Override
    public void prepare(IEntityModifierListener preparedListener) {
        mSceneReady = false;
        
        //mSpoilsSprite.setAlpha(0f);
        mVictoryText.setAlpha(0f);
        mLevelText.setAlpha(0f);
        mNoSpoilsText.setAlpha(0f);
        mPlusText.setAlpha(0f);
        mPotionIconSprite.setAlpha(0f);
        mXpBarSprite.setAlpha(0f);
        mXpBarFillSprite.setAlpha(0f);
        
        mPopupTitleSprite.setAlpha(0f);
        mMonsterNameText.setAlpha(0f);
        mMonsterLevelText.setAlpha(0f);
        
        // The monster
        mMonster = MonsterFactory.generateMonster();
        mMonster.setMonsterState(MonsterState.MONSTER_TRANSITION_IN);
        mMonster.setDead(false);
        
        mMonsterSprite = mMonster.getSprite();
        mMonsterSprite.setAlpha(0f);
        mMonsterSprite.setPosition(
                (mCameraWidth / 2) - (mMonsterSprite.getWidth() / 2),
                (mCameraHeight / 2) - (mMonsterSprite.getHeight() / 2) + (mMonster.getOffY() * sScaleY)
                );
        attachChild(mMonsterSprite);
        
        mMonsterHp.setPosition((mCameraWidth / 2) - (mMonsterHp.getWidth() / 2),
                MONSTER_HP_Y * sScaleY);
        mMonsterHp.setMaxValue(mMonster.getMaxHP());
        mMonsterHp.setCurValue(mMonster.getCurHP());
        mMonsterHp.setAlpha(0f);
        
        mMonsterNameText.setText(mMonster.getName());
        mMonsterNameText.setPosition((mPopupTitleSprite.getWidth() / 2) - (mMonsterNameText.getWidth() / 2), 
                MONSTER_NAME_TEXT_Y * sScaleY);
        mMonsterLevelText.setText("Level " + mMonster.getLevel());
        mMonsterLevelText.setPosition((mPopupTitleSprite.getWidth() / 2) - (mMonsterLevelText.getWidth() / 2), 
                MONSTER_LEVEL_TEXT_Y * sScaleY);
        
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
        mCurDamageNumber = 0;
        
        mPrepared = true;
        preparedListener.onModifierFinished(null, this);
        
    }
    
    public void destroy() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                detachChild(mMonsterSprite);
                unregisterEntityModifier(monsterFadeInModifier);
                unregisterEntityModifier(monsterNameFadeOutModifier);
            }
        });
        
        mMonster = null;
        mMonsterSprite = null;
    }
    
    public void pause() { }
    
    public boolean onSceneTouchEvent(TouchEvent pTouchEvent) {
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            mTouchX = pTouchEvent.getMotionEvent().getX();
            mTouchY = pTouchEvent.getMotionEvent().getY();
            //mTotalTouchOffsetX = 0;
            //mTotalTouchOffsetY = 0;     
            
            // Interupt xp bar fill
            if (mVictoryText.getAlpha() == MONSTER_NAME_OPACITY && mSceneReady 
                    /*&& mSpoilsSprite.contains(mTouchX, mTouchY)*/) {
                // If the player closes the spoils menu, make sure we take care
                // of xp.
                //xpBarModifier.reset();
                mSceneReady = false;
                mPlayer.increaseXP(mXpGained);
                updateXP();
                updateHP();
                if (mSpoilItem != null) {
                    RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
                        public void run() {
                            mSpoilItem.setVisible(false);
                            mSpoilItem.detachSelf();
                        }
                    });
                }
                mContext.endCombat();
            }
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {    
            //float newX = pTouchEvent.getMotionEvent().getX();
            //float newY = pTouchEvent.getMotionEvent().getY();
           
            //mTouchOffsetX = (newX - mTouchX);
            //mTouchOffsetY = (newY - mTouchY);
            //mTotalTouchOffsetX += mTouchOffsetX;
            //mTotalTouchOffsetY += mTouchOffsetY;                                          
            
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
        //Line line = new Line(mTouchX, mTouchY, mTouchUpX, mTouchUpY);
        if (mSceneReady && !mMonster.isDead() && !mAnimating /* && mMonsterSprite.collidesWith(line) */) {
            attachChild(mSlash[mSwipeDirection].getSprite());
            mAnimating = true;
            mSlash[mSwipeDirection].start();
                        
            int damage = mMonster.hit(mPlayer.getTotalAttack());
            
            mMonsterHp.setCurValue(mMonster.getCurHP());
            mDamageNumber[mCurDamageNumber].activate(damage, 
                    getCenterX() + (sRand.nextInt(32) - 16), 
                    getCenterY() - (32 * sScaleY));
            mCurDamageNumber = (mCurDamageNumber + 1) % NUM_DAMAGE_TEXTS;
            
            //mContext.gameToast("You deal " + damage + " damage!", Toast.LENGTH_SHORT);
            if (mMonster.getCurHP() <= 0) {
                mXpGained = mMonster.getXp();
                mMonster.setMonsterState(MonsterState.MONSTER_DEAD);
                mMonster.fadeOut(MONSTER_FADE_DURATION, battleWinListener);
                mMonsterHp.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, MONSTER_NAME_OPACITY, 0f));
            }
        }
    }
    
    private void updateHP() {
        mHPBarFill.setWidth( ((float)mHPBar.getWidth()) * mPlayer.getHPFraction());
    }
    
    private void updateXP() {
        mXpBarFillSprite.setWidth( ((float)XP_BAR_WIDTH-2) * mPlayer.getXPFraction());
        mLevelText.setText("Lvl " + mPlayer.getLevel());
    }
    
    private void endCombat() {
        int spoils = sRand.nextInt(100);
        
        if (spoils < 10) {
            // No spoils!
            mNoSpoilsText.setVisible(true);
            mPotionIconSprite.setVisible(false);
            mPlusText.setVisible(false);
            if (mSpoilItem != null) mSpoilItem.setVisible(false);
        } else if (spoils < 20) {
            // A potion
            mNoSpoilsText.setVisible(false);
            mPotionIconSprite.setVisible(true);
            mPlusText.setVisible(true);
            if (mSpoilItem != null) mSpoilItem.setVisible(false);
            
            mPlayer.increasePotions(1);
        } else {
            // An item!
            RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {

                public void run() {
                    if (mSpoilItem != null) mSpoilItem.detachSelf();
                    mNoSpoilsText.setVisible(false);
                    mPotionIconSprite.setVisible(false);
                    mPlusText.setVisible(true);
                    
                    Item item = ItemFactory.createRandomWeapon(sRand.nextInt(3)+1);
                    mSpoilItem = item.copySprite();
                    mSpoilItem.setPosition((mCameraWidth / 2) - (mSpoilItem.getWidth() / 2), ITEM_Y * sScaleY);
                    attachChild(mSpoilItem);
                    mPlayer.addItem(item);
                    
                    if (mSpoilItem != null) {
                        mSpoilItem.setVisible(true);
                        mSpoilItem.setAlpha(0f);
                        for (int i = 0; i < mSpoilItem.getChildCount(); i++) {
                            mSpoilItem.getChild(i).setAlpha(0f);
                        }
                    }
                }
                
            });
        }
        this.registerEntityModifier(spoilsFadeInModifer);
    }
    
    private final IUpdateHandler updateHandler = new IUpdateHandler() {
        public void onUpdate(float pSecondsElapsed) {
           
            // Monster update
            mTime += pSecondsElapsed;
            if (mMonster.targetable() && mTime > 1.5f) {
                mTime = 0f;
                float attackSpeed = 1.2f - (mMonster.getSpeed() / 100);
                if (sRand.nextFloat() < 0.5f) 
                    mMonster.jumpForward(attackSpeed, monsterAttackListener);
                else
                    mMonster.jumpBackward(attackSpeed);
            }
            
            // Damage text update
            for (FloatingText t : mDamageNumber) {
                t.update(pSecondsElapsed);
            }
        }
        public void reset() {}
    };
    
    
    // Make the monster fade in, after MONSTER_FADE_DELAY seconds
    final DelayModifier monsterFadeInModifier = new DelayModifier(MONSTER_FADE_DELAY, new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mMonster.fadeIn(MONSTER_FADE_DURATION, sceneLoadListener);
            mPopupTitleSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            mMonsterNameText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            mMonsterLevelText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            mMonsterHp.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
        }
    });
    
    // Make the monster name panel to fade out  after MONSTER_FADE_DELAY + TEXT_DISPLAY_DURATION seconds
    final DelayModifier monsterNameFadeOutModifier = new DelayModifier(MONSTER_FADE_DELAY + MONSTER_NAME_DISPLAY_DURATION,
            new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mPopupTitleSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, MONSTER_NAME_OPACITY, 0f));
            mMonsterNameText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, MONSTER_NAME_OPACITY, 0f));    
            mMonsterLevelText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, MONSTER_NAME_OPACITY, 0f));
        }
    });
    
    // Make the spoils panel fade in
    final DelayModifier spoilsFadeInModifer = new DelayModifier(MONSTER_FADE_DELAY,
            new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            //mSpoilsSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));
            mVictoryText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            mLevelText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY)); 
            if (mNoSpoilsText.isVisible()) mNoSpoilsText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            if (mPlusText.isVisible()) mPlusText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            if (mPotionIconSprite.isVisible()) mPotionIconSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            if (mSpoilItem != null && mSpoilItem.isVisible()) {
                mSpoilItem.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
                for (int i = 0; i < mSpoilItem.getChildCount(); i++) {
                    mSpoilItem.getChild(i).registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
                }
            }
            mXpBarSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
            mXpBarFillSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));

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
                    
                    //int next = mPlayer.getNextXP();
                    mPlayer.increaseXP((int)xp);
                    mXpGained -= (int)xp;
                    this.
                    dx = mXpGained / (XP_SCROLL_TIME - this.getSecondsElapsed());
                    xp = 0; 
                    fraction = xp / mPlayer.getNextXP();
                    updateHP();
                    updateXP();
                }
                mXpBarFillSprite.setWidth( ((float)XP_BAR_WIDTH-2) * fraction);
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
            mMonster.startIdleAnimation();
        }
    };
    
    // Listens for monster attack to shake camera and take damage
    final IEntityModifierListener monsterAttackListener  = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {    }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            shake(CAMERA_SHAKE_DURATON, CAMERA_SHAKE_INTENSITY);    
            mPlayer.decreaseHP(sRand.nextInt(10)+1);
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
