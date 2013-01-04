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

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;

import android.graphics.Color;
import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.component.Chest;
import com.cdburrows.android.roguelike.component.ProgressBar;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.Item;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.map.Minimap;
import com.cdburrows.android.roguelike.player.Player;
import com.cdburrows.android.roguelike.scene.MainScene.SceneState;

/**
 * Handles the displaying of all graphical elements for the MainScene class and
 * processes input.
 * 
 * @author cburrows
 */
public class MainSceneUI {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final float TOUCH_SENSITIVITY = 32.0f; // How sensitive input
                             // is to movement

    private static final int TEXTURE_ATLAS_WIDTH = 512;

    private static final int TEXTURE_ATLAS_HEIGHT = 512;

    private static final float HUD_OPACITY = 0.5f;

    private static final int HP_BAR_WIDTH = 160;

    private static final int HP_BAR_HEIGHT = 16;

    private static final float HP_OFF_Y = 24;

    private static final int HP_BAR_COLOR = Color.RED;

    private static final float HP_BAR_ALPHA = HUD_OPACITY;
    
 // The index of each icon image in the icons bitmap
    // TODO: Find a better way of handling this besides hardcoding right here
    public static final int ICON_STATUS_UP = 0;

    public static final int ICON_STATUS_DOWN = 1;

    public static final int ICON_MINIMAP_UP = 2;

    public static final int ICON_MINIMAP_DOWN = 3;

    public static final int ICON_BACK_UP = 4;

    public static final int ICON_BACK_DOWN = 5;

    public static final int ICON_POTION_UP = 6;

    public static final int ICON_POTION_DOWN = 7;

    public static final int ICON_WEAPON_UP = 8;

    public static final int ICON_WEAPON_DOWN = 9;

    public static final int ICON_ARMOUR_UP = 10;

    public static final int ICON_ARMOUR_DOWN = 11;

    public static final int ICON_SKILL_UP = 12;

    public static final int ICON_SKILL_DOWN = 13;

    private static final float MINIMAP_SCROLL_FACTOR = 1f; // How fast the
                                                           // mimimap scrolls

    private static final int DOUBLE_TAP_DURATION = 300;

    // ===========================================================
    // Fields
    // ===========================================================

    private MainScene mParent;

    private Player mPlayer;

    private HUD mHud;

    private boolean mMinimapVisible;

    private int mCameraWidth;

    private int mCameraHeight;

    private Chest mChest;

    // TODO: Replace these with a new Button class
    private TiledSprite mStatusIcon;

    private TiledSprite mMapIcon;

    private TiledSprite mPotionIcon;

    private ChangeableText mPotionText;

    private ProgressBar mHPBar;

    // Input fields
    private float mTouchX;

    private float mTouchY;

    private float mTouchOffsetX;

    private float mTouchOffsetY;

    private float mTotalTouchOffsetX;

    private float mTotalTouchOffsetY;

    private long mLastTapTime = System.currentTimeMillis();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Sets up the camera and loads the player.
     * 
     * @param parent The MainScene to display UI for
     */
    public MainSceneUI(MainScene parent) {
        mParent = parent;
        mCameraWidth = parent.mCameraWidth;
        mCameraHeight = parent.mCameraHeight;

        mPlayer = RoguelikeActivity.getPlayer();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * Loads assets and sets up display.
     */
    public void loadResources() {
        loadGraphics();
        loadUI();
        loadHud();
    }

    /**
     * Rebuilds the UI.
     */
    public void reloadUI() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                mParent.pause();
                mHud.detachChild(Minimap.getSprite());
                mParent.detachChildren();

                loadUI();
                mHud.attachChild(Minimap.getSprite());
                Minimap.setVisible(mMapIcon.getCurrentTileIndex() == ICON_MINIMAP_DOWN);

                mParent.resume();
            }
        });
    }

    /**
     * Updates the UI elements.
     */
    public void prepare() {
        mStatusIcon.setCurrentTileIndex(ICON_STATUS_UP);
        // sMapIcon.setCurrentTileIndex(2);
        mPotionIcon.setCurrentTileIndex(ICON_POTION_UP);
        mPotionText.setText(String.format("%02d", mPlayer.mInventory.getNumPotions()) + "x");
        mChest.setVisible(false);
        updateHP();
    }

    /**
     * Clears all input data.
     */
    public void resetInput() {
        mTouchOffsetX = 0;
        mTouchOffsetY = 0;
        mTotalTouchOffsetX = 0;
        mTotalTouchOffsetY = 0;
    }

    /**
     * Processes input to scroll the minimap, react to button presses, and move
     * the player.
     * 
     * @param touchEvent the input data
     * @return true if the input was consumed, otherwise false
     */
    public boolean onSceneTouchEvent(TouchEvent touchEvent) {
        // See if we're scrolling the minimap
        if (mMinimapVisible && touchEvent.getMotionEvent().getPointerCount() == 2) {
            mParent.setSceneState(SceneState.STATE_MINIMAP_SCROLL);
        }

        // Depending on the scene state, respond to input differently
        switch (mParent.getSceneState()) {

        // Ignore input if the scene is transitioning in or out
            case STATE_TRANSITION:
                return true;

                // If a chest is displayed, send input to that object
            case STATE_CHEST:
                mTotalTouchOffsetX = 0;
                mTotalTouchOffsetY = 0;
                return mChest.handleTouchEvent(touchEvent);

                // Take care of scrolling the minimap
            case STATE_MINIMAP_SCROLL:
                return minimapScrollHandler.handleTouchEvent(touchEvent);

                // Main input state where a player interacts with buttons, moves
                // around the map,
                // and interacts with objects such as stairs
            case STATE_READY:
                return readyInputHandler.handleTouchEvent(touchEvent);
        }
        return false;
    }

    /**
     * Displays the chest.
     * 
     * @param item the item stored in the chest
     */
    public void showChest(Item item) {
        mChest.show(item.copySprite());
    }

    /**
     * Loads graphical assets.
     */
    private void loadGraphics() {
        float scaleX = RoguelikeActivity.sScaleX;
        float scaleY = RoguelikeActivity.sScaleY;

        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        mStatusIcon = Graphics.createTiledSprite("icons.png", 4, 4, scaleX, scaleY, HUD_OPACITY);

        mMapIcon = Graphics.createTiledSprite("icons.png", 4, 4, HUD_OPACITY);
        mMapIcon.setPosition(mCameraWidth - mMapIcon.getWidth(), scaleY);
        mMapIcon.setCurrentTileIndex(ICON_MINIMAP_UP);

        mPotionIcon = Graphics.createTiledSprite("icons.png", 4, 4, HUD_OPACITY);
        mPotionIcon.setPosition(mCameraWidth - mPotionIcon.getWidth() - scaleX, mCameraHeight
                - mPotionIcon.getHeight() - scaleY);

        mPotionText = Graphics.createChangeableText(mPotionIcon.getX() - (28 * scaleX),
                mPotionIcon.getY() + (11 * scaleY), Graphics.SmallFont, "88x", HUD_OPACITY);

        Graphics.endLoad("MAIN");

        mHPBar = new ProgressBar((mCameraWidth / 2)
                - (HP_BAR_WIDTH * RoguelikeActivity.sScaleX / 2), mCameraHeight
                - (HP_OFF_Y * RoguelikeActivity.sScaleY), HP_BAR_WIDTH, HP_BAR_HEIGHT,
                HP_BAR_COLOR, HP_BAR_ALPHA, mPlayer.mStats.getMaxHP());

        mChest = new Chest(mParent, mCameraWidth / 2, mCameraHeight / 2);
    }

    /**
     * Sets up the UI by creating the minimap and displaying the map and player
     * sprites.
     */
    private void loadUI() {
        Minimap.initialize(RoguelikeActivity.getCurrentGameMap());
        Minimap.setCenter(mPlayer.getX(), mPlayer.getY());

        // Dungeon sprite
        mParent.attachChild(DungeonManager.getSprite(0));

        // Player sprite
        mParent.attachChild(mPlayer.getAnimatedSprite());
    }

    /**
     * Displays the UI buttons and minimap.
     */
    private void loadHud() {
        mHud = new HUD();
        mHud.attachChild(Minimap.getSprite());
        mHud.attachChild(mStatusIcon);
        mHud.attachChild(mMapIcon);
        mHud.attachChild(mHPBar.getEntity());
        mHud.attachChild(mPotionIcon);
        mHud.attachChild(mPotionText);
        mHud.attachChild(mChest.getSprite());
        mParent.setHud(mHud);
    }

    /**
     * Updates the player's health bar.
     */
    private void updateHP() {
        mHPBar.setMaxValue(mPlayer.mStats.getMaxHP());
        mHPBar.setCurValue(mPlayer.mStats.getCurHP());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    /**
     * The handler to process minimap scrolling.
     */
    final InputEvent minimapScrollHandler = new InputEvent() {

        public void actionDown(TouchEvent touchEvent) {
            // Not scrolling yet, so update input fields
            mTouchX = touchEvent.getMotionEvent().getX();
            mTouchY = touchEvent.getMotionEvent().getY();
            mTotalTouchOffsetX = 0;
            mTotalTouchOffsetY = 0;
        }

        public void actionMove(TouchEvent touchEvent) {
            // Scrolling the minimap now
            float newX = touchEvent.getMotionEvent().getX();
            float newY = touchEvent.getMotionEvent().getY();

            mTouchOffsetX = (newX - mTouchX);
            mTouchOffsetY = (newY - mTouchY);
            mTotalTouchOffsetX += mTouchOffsetX;
            mTotalTouchOffsetY += mTouchOffsetY;

            Minimap.scroll(mTotalTouchOffsetX * MINIMAP_SCROLL_FACTOR, mTotalTouchOffsetY
                    * MINIMAP_SCROLL_FACTOR);

            mTouchX = newX;
            mTouchY = newY;
        }

        public void actionUp(TouchEvent touchEvent) {
            // Done scrolling the minimap
            mTotalTouchOffsetX = 0;
            mTotalTouchOffsetY = 0;

            Minimap.scroll(mTotalTouchOffsetX, mTotalTouchOffsetY);
            mParent.setSceneState(SceneState.STATE_READY);
        }
    };

    /**
     * The handler to process button interaction and player movement.
     */
    final InputEvent readyInputHandler = new InputEvent() {

        public void actionDown(TouchEvent touchEvent) {
            // See if buttons where touched, and handle double taps
            mTouchX = touchEvent.getMotionEvent().getX();
            mTouchY = touchEvent.getMotionEvent().getY();
            mTotalTouchOffsetX = 0;
            mTotalTouchOffsetY = 0;

            if (System.currentTimeMillis() < mLastTapTime + DOUBLE_TAP_DURATION) {
                mParent.interactStairs();
            }

            if (mStatusIcon.contains(mTouchX, mTouchY)) {
                mParent.openStatus();
                mStatusIcon.setCurrentTileIndex(ICON_STATUS_DOWN);
            } else if (mMapIcon.contains(mTouchX, mTouchY)) {
                mParent.openMiniMap();
                mMapIcon.setCurrentTileIndex(ICON_MINIMAP_DOWN);

                if (Minimap.isVisible()) {
                    Minimap.setVisible(false);
                    mMinimapVisible = false;
                    mMapIcon.setCurrentTileIndex(ICON_MINIMAP_UP);
                } else {
                    Minimap.setVisible(true);
                    mMinimapVisible = true;
                }
            } else if (mPotionIcon.contains(mTouchX, mTouchY)) {
                mParent.usePotion();
                mPotionIcon.setCurrentTileIndex(ICON_POTION_DOWN);
                mPotionText.setText(String.format("%02d", mPlayer.mInventory.getNumPotions()) + "x");
                updateHP();
            }

            mLastTapTime = System.currentTimeMillis();
        }

        public void actionMove(TouchEvent touchEvent) {
            // Move the player to another room
            float newX = touchEvent.getMotionEvent().getX();
            float newY = touchEvent.getMotionEvent().getY();

            mTouchOffsetX = (newX - mTouchX);
            mTouchOffsetY = (newY - mTouchY);
            mTotalTouchOffsetX += mTouchOffsetX;
            mTotalTouchOffsetY += mTouchOffsetY;

            if (Math.abs(mTotalTouchOffsetX) >= TOUCH_SENSITIVITY) {
                if (mTotalTouchOffsetX < 0) {
                    mPlayer.move(Direction.DIRECTION_RIGHT,
                            mPlayer.getTileWidth() * DungeonManager.getRoomWidth());
                } else if (mTotalTouchOffsetX > 0) {
                    mPlayer.move(Direction.DIRECTION_LEFT,
                            mPlayer.getTileWidth() * DungeonManager.getRoomWidth());
                }
            } else if (Math.abs(mTotalTouchOffsetY) >= TOUCH_SENSITIVITY) {
                if (mTotalTouchOffsetY < 0) {
                    mPlayer.move(Direction.DIRECTION_DOWN,
                            mPlayer.getTileHeight() * DungeonManager.getRoomHeight());
                } else if (mTotalTouchOffsetY > 0) {
                    mPlayer.move(Direction.DIRECTION_UP,
                            mPlayer.getTileHeight() * DungeonManager.getRoomHeight());
                }
            }

            mTouchX = newX;
            mTouchY = newY;
        }

        public void actionUp(TouchEvent touchEvent) {
            // Reset button icons
            mStatusIcon.setCurrentTileIndex(ICON_STATUS_UP);
            mPotionIcon.setCurrentTileIndex(ICON_POTION_UP);

            mTotalTouchOffsetX = 0;
            mTotalTouchOffsetY = 0;
        }
    };
}
