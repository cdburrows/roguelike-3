/*
 * Copyright (c) 2012, Christopher Burrows
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cdburrows.android.roguelike.scene;

import java.io.IOException;
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



import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.component.FloatingText;
import com.cdburrows.android.roguelike.component.ProgressBar;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.Item;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.monster.Monster;
import com.cdburrows.android.roguelike.monster.MonsterFactory;
import com.cdburrows.android.roguelike.monster.Monster.MonsterState;
import com.cdburrows.android.roguelike.player.Animation;
import com.cdburrows.android.roguelike.player.Player;
import com.cdburrows.android.roguelike.skill.SkillDirection;
import com.cdburrows.android.roguelike.skill.SkillManager;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Handles combat
 *
 */
public class BattleScene extends BaseScene  {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 1024;
    
    // Most of this shouldn't be here
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
    
    private static final int HP_COLOR = Color.RED;
    private static final float HP_ALPHA = 0.75f;
    
    private static final float PLAYER_HP_X = 0;
    private static final float PLAYER_HP_Y = 24;
    private static final int PLAYER_HP_WIDTH = 160;
    private static final int PLAYER_HP_HEIGHT = 16;
    
    private static final float MONSTER_HP_X = 0;
    private static final float MONSTER_HP_Y = 8;
    private static final int MONSTER_HP_WIDTH = 192;
    private static final int MONSTER_HP_HEIGHT = 6;
    
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
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static float sScaleX;
    private static float sScaleY;
    
    private static HUD sHud;
    
    private static Sprite mMonsterSprite;
    private static Sprite mBackgroundSprite;
    private static Sprite mPopupTitleSprite;
    private static Sprite mXpBarSprite;
    private static Sprite mXpBarFillSprite;    
    private static TiledSprite mSpoilItem;
    private static ChangeableText mVictoryText;
    private static ChangeableText mLevelText;
    private static ChangeableText mMonsterNameText;
    private static ChangeableText mMonsterLevelText;
    private static Text mNoSpoilsText;
    private static Text mPlusText;
    private static ProgressBar mPlayerHp;
    private static ProgressBar mMonsterHp;
    
    private static FloatingText[] mDamageNumber;
    private static int mCurDamageNumber;
    
    private static Monster mMonster;
    private static Player mPlayer;
    private static Animation[] mSlash;
    
    private static boolean mSceneReady;
    private static boolean mAnimating;
    
    private static float mTouchX;
    private static float mTouchY;
    private static float mTouchUpX;
    private static float mTouchUpY;

    private static int mSwipeDirection;
    
    private static float mTime = 0f;
    private static int mXpGained;
    
    private static Random sRand = new Random(System.currentTimeMillis());;
    private static Sprite mPotionIconSprite;
    
    // Shouldn't be here
    private static MediaPlayer mBackgroundMusic;
    private static MediaPlayer mSwordSlashEffect;
    private static MediaPlayer mMonsterAttackEffect;
    private static MediaPlayer mMonsterEvadeEffect;
    private static MediaPlayer mVictoryEffect;
    
    private static BaseScene sScene;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public BattleScene() {
        sScene = this;
        sScaleX = RoguelikeActivity.sScaleX;
        sScaleY = RoguelikeActivity.sScaleY;
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public static BaseScene getScene() { return sScene; }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    @Override
    public void loadResources() {
        long timeStart = System.currentTimeMillis();

        sHud = new HUD();
        
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
        /*
        mHPBar = Graphics.createSprite("panels/hp_bar.png", 0, 0, HP_OPACITY * 1.5f);
        mHPBar.setPosition(
                (mCameraWidth / 2) - (mHPBar.getWidth() / 2), 
                mCameraHeight - (24 * sScaleY));
        
        mHPBarFill = Graphics.createSprite("panels/hp_fill_bar.png", 0, 0, HP_OPACITY * 1.5f);
        mHPBarFill.setPosition(
                (mCameraWidth / 2) - (mHPBarFill.getWidth() / 2), 
                mCameraHeight - (24 * sScaleY));
        */
               
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
        
        mPlayerHp = new ProgressBar(
                (mCameraWidth / 2) - (PLAYER_HP_WIDTH * RoguelikeActivity.sScaleX / 2),
                mCameraHeight - (PLAYER_HP_Y * RoguelikeActivity.sScaleY), PLAYER_HP_WIDTH, PLAYER_HP_HEIGHT, 
                HP_COLOR, HP_ALPHA, 100);
        
        mMonsterHp = new ProgressBar(MONSTER_HP_X, MONSTER_HP_Y, MONSTER_HP_WIDTH, MONSTER_HP_HEIGHT, 
                HP_COLOR, HP_ALPHA, 100);
        mMonsterHp.setPosition((mCameraWidth / 2) - (mMonsterHp.getWidth() / 2),
                MONSTER_HP_Y * sScaleY);
 
        // The slash animations
        mSlash = new Animation[8];
        for (int i = 0; i < 8; i++) {
            switch (i) {            
                case SWIPE_DOWN_LEFT:
                    mSlash[SWIPE_DOWN_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN_LEFT].setScale(sScaleX);
                    mSlash[SWIPE_DOWN_LEFT].loadAnimation();
                    mSlash[SWIPE_DOWN_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN_RIGHT:
                    mSlash[SWIPE_DOWN_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()-48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN_RIGHT].setScale(sScaleX);
                    mSlash[SWIPE_DOWN_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_DOWN_RIGHT].loadAnimation();
                    mSlash[SWIPE_DOWN_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_RIGHT:
                    mSlash[SWIPE_UP_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()+48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_RIGHT].setScale(sScaleX);
                    mSlash[SWIPE_UP_RIGHT].setFlippedHorizontal(true);
                    mSlash[SWIPE_UP_RIGHT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_RIGHT].loadAnimation();
                    mSlash[SWIPE_UP_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP_LEFT:
                    mSlash[SWIPE_UP_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY()+48,
                            mCamera.getCenterX()-48, mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP_LEFT].setScale(sScaleX);
                    mSlash[SWIPE_UP_LEFT].setFlippedVertical(true);
                    mSlash[SWIPE_UP_LEFT].loadAnimation();
                    mSlash[SWIPE_UP_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_LEFT:
                    mSlash[SWIPE_LEFT] = new Animation(mCamera.getCenterX()+48, mCamera.getCenterY(),
                            mCamera.getCenterX()-48, mCamera.getCenterY());
                    mSlash[SWIPE_LEFT].setScale(sScaleX);
                    mSlash[SWIPE_LEFT].setRotation(45.0f);
                    mSlash[SWIPE_LEFT].loadAnimation();
                    mSlash[SWIPE_LEFT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_RIGHT:
                    mSlash[SWIPE_RIGHT] = new Animation(mCamera.getCenterX()-48, mCamera.getCenterY(),
                            mCamera.getCenterX()+48, mCamera.getCenterY());
                    mSlash[SWIPE_RIGHT].setScale(sScaleX);
                    mSlash[SWIPE_RIGHT].setRotation(-135.0f);
                    mSlash[SWIPE_RIGHT].loadAnimation();
                    mSlash[SWIPE_RIGHT].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_UP:
                    mSlash[SWIPE_UP] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()+48,
                            mCamera.getCenterX(), mCamera.getCenterY()-48);
                    mSlash[SWIPE_UP].setScale(sScaleX);
                    mSlash[SWIPE_UP].setRotation(135.0f);
                    mSlash[SWIPE_UP].loadAnimation();
                    mSlash[SWIPE_UP].attachOnFinishListener(onAnimationDone);
                    break;
                case SWIPE_DOWN:
                    mSlash[SWIPE_DOWN] = new Animation(mCamera.getCenterX(), mCamera.getCenterY()-48,
                            mCamera.getCenterX(), mCamera.getCenterY()+48);
                    mSlash[SWIPE_DOWN].setScale(sScaleX);
                    mSlash[SWIPE_DOWN].setRotation(-45.0f);
                    mSlash[SWIPE_DOWN].loadAnimation();
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
        attachChild(mPlayerHp.getEntity());
        attachChild(mMonsterHp.getEntity());
        
        mDamageNumber = new FloatingText[NUM_DAMAGE_TEXTS];
        for (int i = 0; i < NUM_DAMAGE_TEXTS; i++) {
            mDamageNumber[i] = new FloatingText(Graphics.Font, Color.WHITE, 0, 0, "00");
            sHud.attachChild(mDamageNumber[i].getEntity());
        }
        
        mPlayer = RoguelikeActivity.getPlayer();
        
        this.registerUpdateHandler(updateHandler);
        
        try {
            if (RoguelikeActivity.sMusicEnabled) {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/music/Insidia.aac");
                mBackgroundMusic = new MediaPlayer();
                mBackgroundMusic.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            }
            
            if (RoguelikeActivity.sSoundEnabled) {            
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/sword_slash.mp3");
                mSwordSlashEffect = new MediaPlayer();
                mSwordSlashEffect.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mSwordSlashEffect.prepare();
                
                afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/monster_attack.mp3");
                mMonsterAttackEffect = new MediaPlayer();
                mMonsterAttackEffect.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMonsterAttackEffect.prepare();
                
                afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/monster_evade_28.mp3");
                mMonsterEvadeEffect = new MediaPlayer();
                mMonsterEvadeEffect.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMonsterEvadeEffect.prepare();
                
                afd = RoguelikeActivity.getContext().getAssets().openFd("sfx/victory.mp3");
                mVictoryEffect = new MediaPlayer();
                mVictoryEffect.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mVictoryEffect.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        mLoaded = true;
        
        Log.d("BATTLE", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    @Override
    public void prepare(IEntityModifierListener preparedListener) {
        mSceneReady = false;
        
        updateHP();
        updateXP();
        
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
        
        mMonsterHp.setMaxValue(mMonster.getMaxHP());
        mMonsterHp.setCurValue(mMonster.getCurHP());
        mMonsterHp.setAlpha(0f);
        
        mMonsterNameText.setText(mMonster.getName());
        mMonsterNameText.setPosition((mPopupTitleSprite.getWidth() / 2) - (mMonsterNameText.getWidth() / 2), 
                MONSTER_NAME_TEXT_Y * sScaleY);
        mMonsterLevelText.setText("Level " + mMonster.getLevel());
        mMonsterLevelText.setPosition((mPopupTitleSprite.getWidth() / 2) - (mMonsterLevelText.getWidth() / 2), 
                MONSTER_LEVEL_TEXT_Y * sScaleY);
        
        mCamera.setHUD(sHud);
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
        
        Audio.pushMusic(mBackgroundMusic);
        
        sScene = this;
        
        mPrepared = true;
        preparedListener.onModifierFinished(null, this);
    }
    
    public void pause() { }
    
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
        
        if (RoguelikeActivity.sSoundEnabled) {
            mVictoryEffect.pause();
            mVictoryEffect.seekTo(0);
        }
        Audio.popMusic();
    }
     
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
                RoguelikeActivity.endScene();
            }
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {                                       
            
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
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private static void checkHit() {
        //Line line = new Line(mTouchX, mTouchY, mTouchUpX, mTouchUpY);
        if (mSceneReady && !mMonster.isDead() && !mAnimating /* && mMonsterSprite.collidesWith(line) */) {
            SkillManager.queueAction(SkillDirection.getDirection(mSwipeDirection));
            
            sScene.attachChild(mSlash[mSwipeDirection].getSprite());
            mAnimating = true;
            mSlash[mSwipeDirection].start();
            
            if (RoguelikeActivity.sSoundEnabled) {
                mSwordSlashEffect.seekTo(0);
                mSwordSlashEffect.start();
            }
                        
            int damage = mMonster.hit(mPlayer.getTotalAttack());
            
            mMonsterHp.setCurValue(mMonster.getCurHP());
            showFloatingText(damage);
            
            if (mMonster.getCurHP() <= 0) {
                mXpGained = mMonster.getXp();
                mMonster.setMonsterState(MonsterState.MONSTER_DEAD);
                mMonster.fadeOut(MONSTER_FADE_DURATION, battleWinListener);
                mMonsterHp.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, MONSTER_NAME_OPACITY, 0f));
            }
        }
    }
    
    private static void updateHP() {
        mPlayerHp.setMaxValue(mPlayer.getMaxHP());
        mPlayerHp.setCurValue(mPlayer.getCurHP());
    }
    
    private static void updateXP() {
        mXpBarFillSprite.setWidth( ((float)XP_BAR_WIDTH-2) * mPlayer.getXPFraction());
        mLevelText.setText("Lvl " + mPlayer.getLevel());
    }
    
    private static void endCombat() {
        int spoils = sRand.nextInt(100);
        Audio.stop();
        SkillManager.reset();
        
        if (RoguelikeActivity.sSoundEnabled) {
            mVictoryEffect.start();
        }
        
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
                    
                    Item item = ItemFactory.createRandomItem(sRand.nextInt(3)+1);
                    mSpoilItem = item.copySprite();
                    mSpoilItem.setPosition((sScene.mCameraWidth / 2) - (mSpoilItem.getWidth() / 2), ITEM_Y * sScaleY);
                    sScene.attachChild(mSpoilItem);
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
        sScene.registerEntityModifier(spoilsFadeInModifer);
    }
    
    public static void showFloatingText(int text) { showFloatingText(String.valueOf(text)); }
    
    public static void showFloatingText(String text) { showFloatingText(text, 0); }
    
    public static void showFloatingText(final String text, final int offX) {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
            mDamageNumber[mCurDamageNumber].activate(text, 
                    sScene.getCenterX() + ((sRand.nextInt(32) - 16) + offX) * sScaleX, 
                    sScene.getCenterY() - (32 * sScaleY));
            mCurDamageNumber = (mCurDamageNumber + 1) % NUM_DAMAGE_TEXTS;
        }});
    }
    
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    private final IUpdateHandler updateHandler = new IUpdateHandler() {
        public void onUpdate(float pSecondsElapsed) {
           
            // Monster update
            mTime += pSecondsElapsed;
            if (mMonster.targetable() && mTime > 1.5f) {
                mTime = 0f;
                float attackSpeed = 1.2f - (mMonster.getSpeed() / 100);

                if (sRand.nextFloat() < 0.5f) { 
                    mMonster.jumpForward(attackSpeed, monsterAttackListener);
                } else {
                    mMonster.jumpBackward(attackSpeed, monsterEvadeListener);
                    if (RoguelikeActivity.sSoundEnabled) {
                        mMonsterEvadeEffect.start();
                    }
                }
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
    
    // Make the monster name panel to fade out after MONSTER_FADE_DELAY + TEXT_DISPLAY_DURATION seconds
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
    final static DelayModifier spoilsFadeInModifer = new DelayModifier(MONSTER_FADE_DELAY,
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

            sScene.registerEntityModifier(xpBarModifier);
            
        }
    });
    
    // Increases the xp bar after combat
    final static DurationEntityModifier xpBarModifier = new DurationEntityModifier(XP_SCROLL_TIME) { 
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
    final static IEntityModifierListener onAnimationDone = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mAnimating = false;
        }
    };
    
    // Listens for the monster and name panel to load
    final static IEntityModifierListener sceneLoadListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mSceneReady = true;
        }
    };
    
    // Listens for monster attack to shake camera and take damage
    final static IEntityModifierListener monsterAttackListener  = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {    }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            if (RoguelikeActivity.sSoundEnabled) {
                mMonsterAttackEffect.start();
            }
            sScene.shake(CAMERA_SHAKE_DURATON, CAMERA_SHAKE_INTENSITY);    
            mPlayer.decreaseHP(sRand.nextInt(10)+1);
            updateHP();
        }
    };
    
    // Listens for monster attack to shake camera and take damage
    final static IEntityModifierListener monsterEvadeListener  = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {    }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            
        }
    };
    
    // Opens spoils panel
    final static IEntityModifierListener battleWinListener  = new IEntityModifierListener() {
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