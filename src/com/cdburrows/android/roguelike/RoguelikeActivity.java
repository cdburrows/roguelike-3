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

package com.cdburrows.android.roguelike;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Display;
import android.widget.Toast;

import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.map.GameMap;
import com.cdburrows.android.roguelike.player.Player;
import com.cdburrows.android.roguelike.scene.BaseScene;
import com.cdburrows.android.roguelike.scene.BattleScene;
import com.cdburrows.android.roguelike.scene.LoadingScene;
import com.cdburrows.android.roguelike.scene.MainScene;
import com.cdburrows.android.roguelike.scene.SceneManager;
import com.cdburrows.android.roguelike.scene.StatusScene;

/**
 * Loads assets and initializes objects necessary to start game.
 */
public class RoguelikeActivity extends BaseGameActivity implements IOnSceneTouchListener {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int DESIRED_WIDTH = 320;

    public static final int DESIRED_HEIGHT = 240;

    // ===========================================================
    // Fields
    // ===========================================================

    public static float sScaleX;

    public static float sScaleY;

    public static int sCameraWidth;

    public static int sCameraHeight;

    private static RoguelikeActivity sContext;

    public static BoundCamera sCamera;

    public static MainScene sMainScene;

    public static BattleScene sBattleScene;

    public static StatusScene sStatusScene;

    public static BaseScene sMinimapScene;

    private static Player sPlayer;

    public static boolean sMusicEnabled = false;

    public static boolean sSoundEnabled = false;

    private static Engine sEngine;

    private static Random sRand; // All random numbers generated here

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public static RoguelikeActivity getContext() {
        return sContext;
    }

    public static BoundCamera getCamera() {
        return sCamera;
    }

    public static Player getPlayer() {
        return sPlayer;
    }

    public static Display getDisplay() {
        return sContext.getWindowManager().getDefaultDisplay();
    }

    public static GameMap getCurrentGameMap() {
        return DungeonManager.getGameMap();
    }

    public static BattleScene getBattleScene() {
        return (BattleScene)sBattleScene;
    }

    private static void setPlayer(Player player) {
        sPlayer = player;
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    public Engine onLoadEngine() {
        final Display display = getWindowManager().getDefaultDisplay();

        sCameraWidth = display.getWidth();
        sCameraHeight = display.getHeight();
        sScaleX = display.getWidth() / (float)RoguelikeActivity.DESIRED_WIDTH;
        sScaleY = display.getHeight() / (float)RoguelikeActivity.DESIRED_HEIGHT;

        sCamera = new BoundCamera(0, 0, display.getWidth(), display.getHeight());

        sEngine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(display.getWidth(), display.getHeight()),
                RoguelikeActivity.sCamera));
        sEngine.registerUpdateHandler(new FPSLogger());

        return sEngine;
    }

    public void onLoadResources() {
        sContext = this;

        Graphics.initialize(this, DESIRED_WIDTH, DESIRED_HEIGHT);
        Audio.initialize();
        SceneManager.initialize();

        sRand = new Random(); // set global game seed here
    }

    public Scene onLoadScene() {
        ILoadingTask[] tasks = {
                loadDungeonManager, loadItems, loadPlayer, loadScenes, startGame
        };
        SceneManager.pushScene(new LoadingScene(tasks));
        return SceneManager.getTopScene();
    }

    public void onLoadComplete() {
    }

    public void onBackPressed() {
        endScene();
    }

    /**
     * Refuses to handle touch events
     */
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public static void end() {
        Audio.stop();

        Graphics.end();
        Audio.end();
        DungeonManager.end();
        ItemFactory.end();
        Player.end();

        sCamera = null;
        sPlayer = null;

        sMainScene = null;
        sBattleScene = null;
        sStatusScene = null;
        sMinimapScene = null;

        getContext().finish();
    }

    public static void restart() {
    }

    public static void pause() {
        SceneManager.pauseScene();

    }

    public static void resume() {
        SceneManager.resumeScene();
    }

    // Game logic

    public static void openCombat() {
        SceneManager.pushScene(sBattleScene);
    }

    public static void openStatus() {
        SceneManager.pushScene(sStatusScene);
    }

    public static void openMinimap() {
        SceneManager.pushScene(sMinimapScene);
    }
    
    public static void reloadBattleBackground() {
        if (sBattleScene == null)
            return;

        sBattleScene.reloadBattleBackground();
    }
    
    public static void endScene() {
        SceneManager.popScene();
    }

    // Aux functions

    public static void loadTexture(final BitmapTextureAtlas atlas) {
        getContext().getEngine().getTextureManager().loadTexture(atlas);
    }

    public static void gameToast(final String msg, final int duration) {
        sContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(sContext, msg, duration).show();
            }
        });
    }

    // IO functions

    public static FileOutputStream getOutputStream(final String filePath) throws IOException {
        RoguelikeActivity.getContext();
        return sContext.openFileOutput(filePath, RoguelikeActivity.MODE_PRIVATE);
    }

    public static FileInputStream getInputStream(final String filePath) throws IOException {
        RoguelikeActivity.getContext();
        return sContext.openFileInput(filePath);
    }

    // Random number functions

    public static int nextInt(int i) {
        return sRand.nextInt(i);
    }

    public static float nextFloat() {
        return sRand.nextFloat();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    final static ILoadingTask loadDungeonManager = new ILoadingTask() {
        public boolean Load() {
            DungeonManager.initialize("xml/dungeon_definition.xml");
            return true;
        }
    };

    final static ILoadingTask loadItems = new ILoadingTask() {
        public boolean Load() {
            ItemFactory.loadResources();
            return true;
        }
    };

    final static ILoadingTask loadPlayer = new ILoadingTask() {
        public boolean Load() {
            Graphics.beginLoad("gfx/", 256, 512);
            Player p = new Player(Graphics.createAnimatedSprite("hero.png", 4, 4));
            Graphics.endLoad();
            setPlayer(p);
            return true;
        }
    };

    final static ILoadingTask loadScenes = new ILoadingTask() {
        public boolean Load() {
            sBattleScene = new BattleScene();
            sBattleScene.loadResources();

            sStatusScene = new StatusScene();
            sStatusScene.loadResources();

            sMainScene = new MainScene();
            sMainScene.loadResources();

            return true;
        }
    };

    final static ILoadingTask startGame = new ILoadingTask() {
        public boolean Load() {
            SceneManager.pushScene(sMainScene);
            return true;
        }
    };
}
