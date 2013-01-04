/*
 * Copyright (c) 2012-2013, Christopher Burrows
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

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
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

import android.graphics.Color;
import android.media.MediaPlayer;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.animation.Animation;
import com.cdburrows.android.roguelike.animation.SlashAnimation;
import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.component.FloatingText;
import com.cdburrows.android.roguelike.component.ProgressBar;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.monster.Monster;
import com.cdburrows.android.roguelike.monster.Monster.MonsterState;
import com.cdburrows.android.roguelike.player.Player;

public class BattleSceneUI {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int TEXTURE_ATLAS_WIDTH = 512;

    private static final int TEXTURE_ATLAS_HEIGHT = 1024;

    // Most of this shouldn't be here
    // private static final float HP_OPACITY = 0.40f;
    private static final float XP_SCROLL_TIME = 0.5f;

    private static final int NUM_DAMAGE_TEXTS = 10;

    private static final float MONSTER_FADE_DELAY = 0.3f; // how long after the
                                                          // scene is presented
                                                          // to start presenting
                                                          // the monster

    private static final float MONSTER_FADE_DURATION = 0.3f; // how long to fade
                                                             // in the monster

    private static final int MONSTER_NAME_FRAME_Y = 18;

    private static final int MONSTER_NAME_FRAME_WIDTH = 160;

    private static final int MONSTER_NAME_FRAME_HEIGHT = 48;

    private static final float MONSTER_NAME_DISPLAY_DURATION = 1.5f;

    private static final float MONSTER_NAME_OPACITY = 0.8f;

    private static final int MONSTER_NAME_TEXT_Y = 8;

    private static final int MONSTER_LEVEL_TEXT_Y = 30;

    private static final int HP_COLOR = Color.RED;

    private static final float HP_ALPHA = 0.75f;

    // private static final float PLAYER_HP_X = 0;
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

    // private static final int ITEM_X = 0;
    private static final int ITEM_Y = 92;

    // ===========================================================
    // Fields
    // ===========================================================

    private BattleScene mParent;

    private Camera mCamera;

    private HUD sHud;

    private Sprite mMonsterSprite;

    private Sprite mBackgroundSprite;

    private Sprite mPopupTitleSprite;

    private Sprite mXpBarSprite;

    private Sprite mXpBarFillSprite;

    private Sprite mPotionIconSprite;

    private TiledSprite mSpoilItem;

    private ChangeableText mVictoryText;

    private ChangeableText mLevelText;

    private ChangeableText mMonsterNameText;

    private ChangeableText mMonsterLevelText;

    private Text mNoSpoilsText;

    private Text mPlusText;

    private ProgressBar mPlayerHp;

    private ProgressBar mMonsterHp;

    // Shouldn't be here
    private MediaPlayer mBackgroundMusic;

    private MediaPlayer mSwordSlashEffect;

    private MediaPlayer mMonsterAttackEffect;

    private MediaPlayer mMonsterEvadeEffect;

    private MediaPlayer mVictoryEffect;

    // Whether the player is attacking
    private boolean mAnimating;

    private int mXpGained;

    private Animation[] mSlash;

    private FloatingText[] mDamageNumber;

    private int mCurDamageNumber;

    private float mTouchX;

    private float mTouchY;

    private float mTouchUpX;

    private float mTouchUpY;

    // private int mSwipeDirection;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BattleSceneUI(BattleScene parent) {
        mParent = parent;
        mCamera = parent.getCamera();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public boolean isAnimating() {
        return mAnimating;
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * Loads the graphical and audio resources for the battle UI
     */
    public void loadResources() {
        loadGraphics();

        loadHud();

        loadUI();

        loadAnimations();

        loadAudio();
    }

    /**
     * Recreates the UI
     */
    public void reloadUI() {
        mParent.detachChildren();
        loadUI();
    }

    /**
     * Loads UI graphics
     */
    private void loadGraphics() {
        float scaleX = RoguelikeActivity.sScaleX;
        float scaleY = RoguelikeActivity.sScaleY;

        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);

        // The background
        mBackgroundSprite = Graphics.createSprite(DungeonManager.getBattleBackground());

        // The monster name and level text
        mMonsterNameText = Graphics.createChangeableText(0, 
                MONSTER_NAME_FRAME_Y * scaleY + (MONSTER_NAME_TEXT_Y * scaleY), 
                Graphics.Font, "MONSTER NAME");
        mMonsterLevelText = Graphics.createChangeableText(0, 
                MONSTER_NAME_FRAME_Y * scaleY + (MONSTER_LEVEL_TEXT_Y * scaleY), 
                Graphics.SmallFont, "Level XX");
        
        // The monster name pop-up panel
        mPopupTitleSprite = Graphics.createSprite("panels/popup_title.png", 0, 0);
        mPopupTitleSprite.setSize(MONSTER_NAME_FRAME_WIDTH * scaleX,
                MONSTER_NAME_FRAME_HEIGHT * scaleY);
        mPopupTitleSprite.setPosition(
                (mParent.getWidth() / 2) - (mPopupTitleSprite.getWidth() / 2), 
                MONSTER_NAME_FRAME_Y * scaleY);        
        mPopupTitleSprite.attachChild(mMonsterNameText);
        mPopupTitleSprite.attachChild(mMonsterLevelText);

        // The spoils panel
        /*
         * mSpoilsSprite = Graphics.createSprite("panels/display_panel.png");
         * mSpoilsSprite.setPosition( (mCamera.getWidth() / 2) -
         * (mSpoilsSprite.getWidth() / 2 ), (mCamera.getHeight() / 2) -
         * (mSpoilsSprite.getHeight() / 2) - (8 * scaleY));
         */
        mXpBarSprite = Graphics.createSprite("panels/hp_bar.png", 
                XP_BAR_X * scaleX, XP_BAR_Y * scaleY);
        mXpBarSprite.setWidth(XP_BAR_WIDTH * scaleX);
        mXpBarSprite.setHeight(XP_BAR_HEIGHT * scaleY);

        mXpBarFillSprite = Graphics.createSprite("panels/xp_fill_bar.png", 
                XP_BAR_X * scaleX, XP_BAR_Y * scaleY);
        mXpBarFillSprite.setWidth(XP_BAR_WIDTH * scaleX);
        mXpBarFillSprite.setHeight(XP_BAR_HEIGHT * scaleY);

        Graphics.endLoad("BATTLE");

        mPotionIconSprite = ItemFactory.getPotionSprite();
        mPotionIconSprite.setPosition(
                (mParent.getWidth() / 2) - (mPotionIconSprite.getWidth() / 2), 
                NO_SPOILS_TEXT_Y * scaleY);
        mPotionIconSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        mVictoryText = Graphics.createChangeableText(0, VICTORY_TEXT_Y * scaleY,
                Graphics.LargeFont, VICTORY_TEXT);
        mVictoryText.setPosition(mParent.getWidth() / 2 - (mVictoryText.getWidth() / 2),
                VICTORY_TEXT_Y * scaleY);
        mLevelText = Graphics.createChangeableText(LEVEL_TEXT_X * scaleX, LEVEL_TEXT_Y * scaleY,
                Graphics.Font, "Lvl 88");
        mNoSpoilsText = Graphics.createText(0, NO_SPOILS_TEXT_Y * scaleY, Graphics.Font,
                "No spoils!");
        mNoSpoilsText.setPosition(mParent.getWidth() / 2 - (mNoSpoilsText.getWidth() / 2),
                NO_SPOILS_TEXT_Y * scaleY);
        mPlusText = Graphics.createText((mParent.getWidth() / 2)
                - (mPotionIconSprite.getWidth() / 2) - (20 * scaleX), (NO_SPOILS_TEXT_Y + 4)
                * scaleY, Graphics.LargeFont, "+");

        mPlayerHp = new ProgressBar((mParent.getWidth() / 2) - (PLAYER_HP_WIDTH * scaleX / 2),
                mParent.getHeight() - (PLAYER_HP_Y * scaleY), PLAYER_HP_WIDTH, PLAYER_HP_HEIGHT,
                HP_COLOR, HP_ALPHA, 100);

        mMonsterHp = new ProgressBar(MONSTER_HP_X, MONSTER_HP_Y, MONSTER_HP_WIDTH,
                MONSTER_HP_HEIGHT, HP_COLOR, HP_ALPHA, 100);
        mMonsterHp.setPosition((mParent.getWidth() / 2) - (mMonsterHp.getWidth() / 2), MONSTER_HP_Y
                * scaleY);
    }

    /**
     * Attaches UI elements to scene
     */
    private void loadUI() {
        mParent.attachChild(mBackgroundSprite);
        mParent.attachChild(mPopupTitleSprite);

        mParent.attachChild(mVictoryText);
        mParent.attachChild(mLevelText);
        mParent.attachChild(mNoSpoilsText);
        mParent.attachChild(mPlusText);
        mParent.attachChild(mPotionIconSprite);
        mParent.attachChild(mXpBarSprite);
        mParent.attachChild(mXpBarFillSprite);
        mParent.attachChild(mPlayerHp.getEntity());
        mParent.attachChild(mMonsterHp.getEntity());
    }

    /**
     * Creates Hud and prepares floating texts
     */
    private void loadHud() {
        sHud = new HUD();

        mDamageNumber = new FloatingText[NUM_DAMAGE_TEXTS];
        for (int i = 0; i < NUM_DAMAGE_TEXTS; i++) {
            mDamageNumber[i] = new FloatingText(Graphics.Font, Color.WHITE, 0, 0, "00");
            sHud.attachChild(mDamageNumber[i].getEntity());
        }
    }

    /**
     * Loads battle animations 
     */
    private void loadAnimations() {
        mSlash = new Animation[SlashAnimation.values().length];
        for (SlashAnimation s : SlashAnimation.values()) {
            mSlash[s.getIndex()] = Animation.create(
                    mCamera.getCenterX() + s.getStartX(), mCamera.getCenterY() + s.getStartY(), 
                    mCamera.getCenterX() + s.getEndX(), mCamera.getCenterY() + s.getEndY(),
                    RoguelikeActivity.sScaleX, s.getRotation(), s.isFlippedX(), s.isFlippedY(), onAnimationDone);
        }
    }

    /**
     * Loads battle music and sound effects
     */
    private void loadAudio() {
        if (RoguelikeActivity.sMusicEnabled) {
            mBackgroundMusic = Audio.createAudio("sfx/music/Insidia.aac");
        }

        if (RoguelikeActivity.sSoundEnabled) {
            mSwordSlashEffect = Audio.prepareAudio("sfx/sword_slash.mp3");
            mMonsterAttackEffect = Audio.prepareAudio("sfx/monster_attack.mp3");
            mMonsterEvadeEffect = Audio.prepareAudio("sfx/monster_evade_28.mp3");
            mVictoryEffect = Audio.prepareAudio("sfx/victory.mp3");
        }
    }

    /**
     * Sets UI elements' visibility and positions
     */
    public void prepare() {
        float scaleY = RoguelikeActivity.sScaleY;

        // mSpoilsSprite.setAlpha(0f);
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

        Monster monster = mParent.getMonster();
        mMonsterSprite = monster.getSprite();
        mMonsterSprite.setAlpha(0f);
        mMonsterSprite.setPosition((mParent.getWidth() / 2) - (mMonsterSprite.getWidth() / 2),
                (mParent.getHeight() / 2) - (mMonsterSprite.getHeight() / 2)
                        + (monster.getOffY() * scaleY));
        mParent.attachChild(mMonsterSprite);

        mMonsterHp.setMaxValue(monster.getMaxHP());
        mMonsterHp.setCurValue(monster.getCurHP());
        mMonsterHp.setAlpha(0f);

        mMonsterNameText.setText(monster.getName());
        mMonsterNameText.setPosition(
                (mPopupTitleSprite.getWidth() / 2) - (mMonsterNameText.getWidth() / 2),
                MONSTER_NAME_TEXT_Y * scaleY);
        mMonsterLevelText.setText("Level " + monster.getLevel());
        mMonsterLevelText.setPosition(
                (mPopupTitleSprite.getWidth() / 2) - (mMonsterLevelText.getWidth() / 2),
                MONSTER_LEVEL_TEXT_Y * scaleY);

        mCamera.setHUD(sHud);
        mCamera.setChaseEntity(null);
        mCamera.setCenter(mParent.getWidth() / 2, mParent.getHeight() / 2);

        monsterFadeInModifier.reset();
        monsterNameFadeOutModifier.reset();
        spoilsFadeInModifer.reset();
        xpBarModifier.reset();

        mCurDamageNumber = 0;

        Audio.pushMusic(mBackgroundMusic);

        mParent.registerEntityModifier(monsterFadeInModifier);
        mParent.registerEntityModifier(monsterNameFadeOutModifier);
    }

    /**
     * Reattaches the background image
     */
    public void reloadBattleBackground() {
        if (mBackgroundSprite != null)
            mBackgroundSprite.detachSelf();

        Graphics.beginLoad("gfx/", 512, 512);
        mBackgroundSprite = Graphics.createSprite(DungeonManager.getBattleBackground());
        Graphics.endLoad("Refresh Battle BG");
        reloadUI();
    }

    /**
     * Destroys the monster sprite
     */
    public void destory() {
        mParent.detachChild(mMonsterSprite);

        mParent.unregisterEntityModifier(monsterFadeInModifier);
        mParent.unregisterEntityModifier(monsterNameFadeOutModifier);

        mMonsterSprite = null;

        if (RoguelikeActivity.sSoundEnabled) {
            mVictoryEffect.pause();
            mVictoryEffect.seekTo(0);
        }
        Audio.popMusic();
    }

    /**
     * Processes user input to interact with buttons or attack the monster
     * 
     * @param touchEvent The user input
     * @return true, if the input is recognized, otherwise false
     */
    public boolean onSceneTouchEvent(TouchEvent touchEvent) {
       return slashHandler.handleTouchEvent(touchEvent);
    }

    /**
     * Displays no spoils result
     */
    public void spoilsNone() {
        mNoSpoilsText.setVisible(true);
        mPotionIconSprite.setVisible(false);
        mPlusText.setVisible(false);
        if (mSpoilItem != null)
            mSpoilItem.setVisible(false);
        mParent.registerEntityModifier(spoilsFadeInModifer);
    }

    /**
     * Displays potion result
     */
    public void spoilsPotion() {
        mNoSpoilsText.setVisible(false);
        mPotionIconSprite.setVisible(true);
        mPlusText.setVisible(true);
        if (mSpoilItem != null)
            mSpoilItem.setVisible(false);
        mParent.registerEntityModifier(spoilsFadeInModifer);
    }

    /**
     * Displays item result
     * 
     * @param item the item gained
     */
    public void spoilsItem(final TiledSprite item) {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                if (mSpoilItem != null)
                    mSpoilItem.detachSelf();

                mNoSpoilsText.setVisible(false);
                mPotionIconSprite.setVisible(false);
                mPlusText.setVisible(true);

                mSpoilItem = item;
                mSpoilItem.setPosition((mParent.mCameraWidth / 2) - (mSpoilItem.getWidth() / 2),
                        ITEM_Y * RoguelikeActivity.sScaleY);
                mParent.attachChild(mSpoilItem);

                if (mSpoilItem != null) {
                    mSpoilItem.setVisible(true);
                    mSpoilItem.setAlpha(0f);
                    for (int i = 0; i < mSpoilItem.getChildCount(); i++) {
                        mSpoilItem.getChild(i).setAlpha(0f);
                    }
                }
            }

        });
        mParent.registerEntityModifier(spoilsFadeInModifer);
    }

    // This will be moved to some Monster AI class
    public void update(float secondsElapsed) {
        // Damage text update
        for (FloatingText t : mDamageNumber) {
            t.update(secondsElapsed);
        }
    }

    public void playerAttack(Monster monster, int swipeDirection, int damage) {
        // Handle attack sprite
        mParent.attachChild(mSlash[swipeDirection].getSprite());
        mAnimating = true;
        mSlash[swipeDirection].start();

        if (RoguelikeActivity.sSoundEnabled) {
            mSwordSlashEffect.seekTo(0);
            mSwordSlashEffect.start();
        }

        mMonsterHp.setCurValue(monster.getCurHP());
        showFloatingText(damage);

        // Check if monster is dead
        if (monster.getCurHP() <= 0) {
            mXpGained = monster.getXp();
            monster.setMonsterState(MonsterState.MONSTER_DEAD);
            monster.fadeOut(MONSTER_FADE_DURATION, battleWinListener);
            mMonsterHp.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                    MONSTER_NAME_OPACITY, 0f));
        }
    }

    public void showFloatingText(int text) {
        showFloatingText(String.valueOf(text));
    }

    public void showFloatingText(String text) {
        showFloatingText(text, 0);
    }

    /**
     * Displays text to scroll up automatically
     * 
     * @param text the text to display
     * @param offX the horizontal offset of the text
     */
    public void showFloatingText(final String text, final int offX) {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                mDamageNumber[mCurDamageNumber].activate(text,
                        mParent.getCenterX() + ((RoguelikeActivity.nextInt(32) - 16) + offX)
                                * RoguelikeActivity.sScaleX, mParent.getCenterY()
                                - (32 * RoguelikeActivity.sScaleY));
                mCurDamageNumber = (mCurDamageNumber + 1) % NUM_DAMAGE_TEXTS;
            }
        });
    }

    public void monsterAttack() {
        if (RoguelikeActivity.sSoundEnabled) {
            mMonsterAttackEffect.start();
        }
        mParent.shake(CAMERA_SHAKE_DURATON, CAMERA_SHAKE_INTENSITY);
        updateHPBar(mParent.getPlayer());
    }

    public void monsterEvade() {
        if (RoguelikeActivity.sSoundEnabled) {
            mMonsterEvadeEffect.start();
        }
    }

    private void updateHPBar(Player player) {
        mPlayerHp.setMaxValue(player.mStats.getMaxHP());
        mPlayerHp.setCurValue(player.mStats.getCurHP());
    }

    private void updateXPBar(Player player) {
        mXpBarFillSprite.setWidth(((float)XP_BAR_WIDTH - 2) * player.mStats.getXPFraction());
        mLevelText.setText("Lvl " + player.mStats.getLevel());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    /**
     * The handler to process user slash attacks.
     */
    final InputEvent slashHandler = new InputEvent() {

        public void actionDown(TouchEvent touchEvent) {
            mTouchX = touchEvent.getMotionEvent().getX();
            mTouchY = touchEvent.getMotionEvent().getY();

            // Interrupt xp bar fill
            if (mVictoryText.getAlpha() == MONSTER_NAME_OPACITY && mParent.isSceneReady()) {
                // If the player closes the spoils menu, make sure we take
                // care of xp.
                mParent.interruptXpFill(mXpGained);

                updateXPBar(mParent.getPlayer());
                updateHPBar(mParent.getPlayer());
                if (mSpoilItem != null) {
                    // Destroy any gained item sprite
                    RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
                        public void run() {
                            mSpoilItem.setVisible(false);
                            mSpoilItem.detachSelf();
                        }
                    });
                }
                mParent.endCombat();
            }
        }

        public void actionMove(TouchEvent touchEvent) { }

        public void actionUp(TouchEvent touchEvent) {
            mTouchUpX = touchEvent.getMotionEvent().getX();
            mTouchUpY = touchEvent.getMotionEvent().getY();

            float diffX = mTouchUpX - mTouchX;
            float diffY = mTouchUpY - mTouchY;

            if (diffX != 0 && diffY != 0) {
                float angle = (float)Math.toDegrees(Math.atan2(diffY, diffX));
                if (angle < 0)
                    angle += 360;

                int swipeDirection = (int)(((angle + 22.5) % 360) / 45);

                mParent.checkHit(swipeDirection);
            }

        }
    };

    /**
     *  Make the monster fade in, after MONSTER_FADE_DELAY seconds
     */
    final DelayModifier monsterFadeInModifier = new DelayModifier(MONSTER_FADE_DELAY,
            new IEntityModifierListener() {
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                }

                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                    mParent.getMonster().fadeIn(MONSTER_FADE_DURATION, sceneLoadListener);
                    mPopupTitleSprite.registerEntityModifier(new AlphaModifier(
                            MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
                    mMonsterNameText.registerEntityModifier(new AlphaModifier(
                            MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
                    mMonsterLevelText.registerEntityModifier(new AlphaModifier(
                            MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
                    mMonsterHp.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f,
                            MONSTER_NAME_OPACITY));
                }
            });

    /**
     *  Make the monster name panel to fade out after MONSTER_FADE_DELAY +
     *  TEXT_DISPLAY_DURATION seconds
     */
    final DelayModifier monsterNameFadeOutModifier = new DelayModifier(MONSTER_FADE_DELAY
            + MONSTER_NAME_DISPLAY_DURATION, new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mPopupTitleSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                    MONSTER_NAME_OPACITY, 0f));
            mMonsterNameText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                    MONSTER_NAME_OPACITY, 0f));
            mMonsterLevelText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                    MONSTER_NAME_OPACITY, 0f));
        }
    });

    /**
     *  Make the spoils panel fade in
     */
    final DelayModifier spoilsFadeInModifer = new DelayModifier(MONSTER_FADE_DELAY,
            new IEntityModifierListener() {
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                }

                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                    // mSpoilsSprite.registerEntityModifier(new
                    // AlphaModifier(MONSTER_FADE_DURATION, 0f, TEXT_OPACITY));
                    mVictoryText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                            0f, MONSTER_NAME_OPACITY));
                    mLevelText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION, 0f,
                            MONSTER_NAME_OPACITY));
                    if (mNoSpoilsText.isVisible())
                        mNoSpoilsText.registerEntityModifier(new AlphaModifier(
                                MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
                    if (mPlusText.isVisible())
                        mPlusText.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                                0f, MONSTER_NAME_OPACITY));
                    if (mPotionIconSprite.isVisible())
                        mPotionIconSprite.registerEntityModifier(new AlphaModifier(
                                MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));
                    if (mSpoilItem != null && mSpoilItem.isVisible()) {
                        mSpoilItem.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                                0f, MONSTER_NAME_OPACITY));
                        for (int i = 0; i < mSpoilItem.getChildCount(); i++) {
                            mSpoilItem.getChild(i).registerEntityModifier(
                                    new AlphaModifier(MONSTER_FADE_DURATION, 0f,
                                            MONSTER_NAME_OPACITY));
                        }
                    }
                    mXpBarSprite.registerEntityModifier(new AlphaModifier(MONSTER_FADE_DURATION,
                            0f, MONSTER_NAME_OPACITY));
                    mXpBarFillSprite.registerEntityModifier(new AlphaModifier(
                            MONSTER_FADE_DURATION, 0f, MONSTER_NAME_OPACITY));

                    mParent.registerEntityModifier(xpBarModifier);

                }
            });

    /**
     *  Increases the xp bar after combat
     */
    final DurationEntityModifier xpBarModifier = new DurationEntityModifier(XP_SCROLL_TIME) {
        float dx;

        float xp;

        public EntityModifier deepCopy() throws DeepCopyNotSupportedException {
            return null;
        }

        @Override
        protected void onManagedUpdate(float pSecondsElapsed, IEntity pItem) {
            if (mParent.isSceneReady()) {
                Player player = mParent.getPlayer();
                xp += dx * pSecondsElapsed;
                float fraction = (player.mStats.getCurXP() + xp) / player.mStats.getNextXP();
                if (fraction >= 1f) {
                    // level up !

                    // TODO: XP should be rewritten, so the player instantly
                    // gets xp,
                    // and the UI works of other variables.

                    player.mStats.increaseXP((int)xp);
                    mXpGained -= (int)xp;
                    dx = mXpGained / (XP_SCROLL_TIME - this.getSecondsElapsed());
                    xp = 0;
                    fraction = xp / player.mStats.getNextXP();
                    updateHPBar(player);
                    updateXPBar(player);
                }
                mXpBarFillSprite.setWidth(((float)XP_BAR_WIDTH - 2) * fraction);
            }
        }

        @Override
        protected void onManagedInitialize(IEntity pItem) {
            xp = 0;
            dx = mXpGained / XP_SCROLL_TIME;
        }
    };

    /**
     *  Listens for the attack animation to finish
     */
    final IEntityModifierListener onAnimationDone = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mAnimating = false;
        }
    };

    /**
     *  Listens for the monster and name panel to load
     */
    final IEntityModifierListener sceneLoadListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mParent.setSceneReady(true);
        }
    };

    /**
     *  Opens spoils panel
     */
    final IEntityModifierListener battleWinListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            DelayModifier delayMod = new DelayModifier(MONSTER_FADE_DELAY);
            delayMod.addModifierListener(new IEntityModifierListener() {
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                }

                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                    mParent.displaySpoils();

                    if (RoguelikeActivity.sSoundEnabled) {
                        Audio.stop();
                        mVictoryEffect.start();
                    }
                }
            });
            pItem.registerEntityModifier(delayMod);
        }
    };
}
