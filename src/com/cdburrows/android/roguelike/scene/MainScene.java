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

import java.io.IOException;
import java.util.Random;

import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.input.touch.TouchEvent;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import com.cdburrows.android.roguelike.Event;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.item.Item;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.monster.MonsterFactory;
import com.cdburrows.android.roguelike.player.Player;
import com.cdburrows.android.roguelike.player.PlayerState;

/**
 * Displays a map that a player can explore and fight in.
 * 
 * @author cburrows
 */
public class MainScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private static BaseScene sScene;

    private MainSceneUI mUI;

    private HUD mHud;

    private Player mPlayer;

    private Event mEvent;

    private SceneState mSceneState;

    private Random mRand;

    // These should be somewhere else
    private MediaPlayer mBackgroundMusic;

    private MediaPlayer mPotionEffect;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MainScene() {
        super();

        sScene = this;
        mUI = new MainSceneUI(this);
        mRand = new Random(System.currentTimeMillis());
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public SceneState getSceneState() {
        return mSceneState;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setSceneState(SceneState stateTransition) {
        mSceneState = stateTransition;
        if (mUI != null)
            mUI.resetInput();
    }

    public void setHud(HUD hud) {
        mHud = hud;
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    /**
     * Loads all assets used by the scene.
     */
    @Override
    public void loadResources() {
        long timeStart = System.currentTimeMillis();

        loadPlayer();

        loadMonsters();

        mUI.loadResources();

        loadAudio();

        RoguelikeActivity.getContext().getEngine().registerUpdateHandler(new UpdateHandler());
        setOnSceneTouchListener((IOnSceneTouchListener)RoguelikeActivity.getContext());

        mLoaded = true;
        Log.d("MAIN", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    /**
     * Updates the scene every time it is returned to after combat or the status
     * scene.
     */
    @Override
    public void prepare(IEntityModifierListener preparedListener) {
        setSceneState(SceneState.STATE_TRANSITION);

        mCamera.setChaseEntity(mPlayer.getAnimatedSprite());
        mCamera.updateChaseEntity();
        mCamera.setHUD(mHud);

        mPlayer.setPlayerState(PlayerState.IDLE);

        mUI.prepare();

        setTransitioning(false);
        Audio.play();

        mPrepared = true;

        preparedListener.onModifierFinished(null, this);
    }

    /**
     * Pauses the scene, preventing input or updates from processing.
     */
    @Override
    public void pause() {
        mPaused = true;
    }

    /**
     * Resumes the scene, allowing input and updates to process.
     */
    @Override
    public void resume() {
        mPaused = false;
    }

    /**
     * Frees all memory.
     */
    @Override
    public void destroy() {
        Log.d("MAIN", "MAIN DESTROYED");

        sScene = null;
        mUI = null;
        mHud = null;
        mPlayer = null;
        mEvent = null;
        mSceneState = null;
        mRand = null;
        mBackgroundMusic = null;
        mPotionEffect = null;
    }

    /**
     * Sends input to the UI handling class.
     */
    @Override
    public boolean onSceneTouchEvent(TouchEvent touchEvent) {
        if (mPaused)
            return true;

        return mUI.onSceneTouchEvent(touchEvent);
    }

    /**
     * Coordinates the SceneState with transition status.
     */
    @Override
    protected void setTransitioning(boolean value) {
        mTransitioning = value;

        if (value) {
            setSceneState(SceneState.STATE_TRANSITION);
        } else {
            setSceneState(SceneState.STATE_READY);
        }
    }

    /**
     * Loads the Player object and places it on the map.
     */
    private void loadPlayer() {
        mPlayer = RoguelikeActivity.getPlayer();
        mPlayer.setParentMap(DungeonManager.getGameMap());
        mPlayer.setRoom(DungeonManager.getStartX(), DungeonManager.getStartY());
    }

    /**
     * Initialize the MonsterFamily with the current map's monsters.
     */
    private void loadMonsters() {
        // TODO: Should this be unitialized first?
        MonsterFactory.initialize(DungeonManager.getMonsterList());
    }

    /**
     * Loads all the music and sound effects used in the map.
     */
    private void loadAudio() {
        // Should be all defined in XML
        if (RoguelikeActivity.sMusicEnabled) {
            try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets()
                        .openFd("sfx/music/CornFields.aac");
                mBackgroundMusic = new MediaPlayer();
                mBackgroundMusic.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                        afd.getLength());
                Audio.pushMusic(mBackgroundMusic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (RoguelikeActivity.sSoundEnabled) {
            try {
                AssetFileDescriptor afd = RoguelikeActivity.getContext().getAssets()
                        .openFd("sfx/power_up.mp3");
                mPotionEffect = new MediaPlayer();
                mPotionEffect.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                        afd.getLength());
                mPotionEffect.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * Checks if stairs are present when a player interacts with a room.
     */
    public void interactStairs() {
        if (DungeonManager.interact(mPlayer.getRoomX(), mPlayer.getRoomY())) {
            mUI.reloadUI();
            reload();
        }
    }

    /**
     * Displays the status scene.
     */
    public void openStatus() {
        Audio.playClick();
        RoguelikeActivity.openStatus();
    }

    /**
     * Displays the MiniMap.
     */
    public void openMiniMap() {
        Audio.playClick();
    }

    /**
     * Uses a potion.
     */
    public void usePotion() {
        if (RoguelikeActivity.sSoundEnabled)
            mPotionEffect.start();
        mPlayer.usePotion();
    }

    /**
     * TODO: Tidy this up, as it meshes with UI handler
     */
    public void closeChest() {
        if (mSceneState == SceneState.STATE_CHEST) {
            sScene.fadeTo(0.5f, 0.5f, 0f);
            setSceneState(SceneState.STATE_READY);
        }
    }

    /**
     * Handles the events that occur as the player travels from room to room,
     * such as chests and combat.
     * 
     * @author cburrows
     */
    private class UpdateHandler implements IUpdateHandler {
        public void onUpdate(float secondsElapsed) {
            if (mPaused)
                return;

            mEvent = mPlayer.update(secondsElapsed);

            switch (mEvent) {
                case EVENT_NO_EVENT:
                    break;

                case EVENT_NEW_ROOM:
                    if (DungeonManager.hasChest(mPlayer.getRoomX(), mPlayer.getRoomY())) {

                        findChest();

                    } else if (mRand.nextFloat() <= DungeonManager.getCurrentFloor().mMonsterSpawnRate) {

                        mPlayer.setPlayerState(PlayerState.FIGHTING);
                        RoguelikeActivity.openCombat();

                    }
                    break;
            }
        }

        public void reset() {
        }
    }

    /**
     * Reloads the player and monsters when the map has changed.
     */
    private void reload() {
        loadPlayer();
        loadMonsters();
    }

    /**
     * Displays a chest for the player to open, and fades the map.
     */
    private void findChest() {
        setSceneState(SceneState.STATE_CHEST);

        sScene.fadeTo(0.5f, 0f, 0.5f);

        Item item = ItemFactory.createRandomItem(DungeonManager.getCurrentDepth());
        mPlayer.addItem(item);
        mUI.showChest(item);

        DungeonManager.setChest(mPlayer.getRoomX(), mPlayer.getRoomY(), false);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public enum SceneState {
        STATE_TRANSITION, STATE_READY, STATE_CHEST, STATE_MINIMAP_SCROLL
    }

}
