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

package com.cdburrows.android.roguelike;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;
import bsh.EvalError;
import bsh.Interpreter;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.mvel2.MVEL;
import org.mvel2.util.Make;

import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.player.Player;
import com.cdburrows.android.roguelike.scene.BaseScene;
import com.cdburrows.android.roguelike.scene.BattleScene;
import com.cdburrows.android.roguelike.scene.MainScene;
import com.cdburrows.android.roguelike.scene.SceneManager;
import com.cdburrows.android.roguelike.scene.StatusScene;

/**
 * Loads assets and initializes objects necessary to start game.
 */
public class RoguelikeActivity extends LoadingGameActivity implements
    IOnSceneTouchListener {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    public static final int DESIRED_WIDTH = 320;
    public static final int DESIRED_HEIGHT = 240;
    
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

    // ===========================================================
    // Fields
    // ===========================================================

    public static float sScaleX;
    public static float sScaleY;
    
    public static int sCameraWidth;
    public static int sCameraHeight;
    
    private static RoguelikeActivity sContext;
    
    public static BoundCamera sCamera;
    
    public static BaseScene sMainScene;
    public static BaseScene sBattleScene;
    public static BaseScene sStatusScene;
    public static BaseScene sMinimapScene;
    
    private static Player sPlayer;
    
    public static DungeonManager sDungeon;
    
    public static boolean sMusicEnabled = false;
    public static boolean sSoundEnabled = false;
    
    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public static RoguelikeActivity getContext() { return sContext; }
    
    public static BoundCamera getCamera() { return sCamera; }
    
    public static Player getPlayer() { return sPlayer; }
    
    public static void setPlayer(Player player) { sPlayer = player; }
    
    public static DungeonManager getDungeon() { return sDungeon; }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    public Engine onLoadEngine() {
        return super.onLoadEngine();
    }
   
    public void onLoadResources() {
        super.onLoadResources();
    }
   
    public Scene onLoadScene() {
        return super.onLoadScene();
    }
        
    public void onLoadComplete() {
        // Leave empty
    }
    
    /**
     * Displays MainScene when assets have loaded
     */
    @Override
    protected Scene onAssetsLoaded() {
        SceneManager.pushScene(sMainScene);
        return SceneManager.getTopScene();
    }

    /**
     * Prepares game by initializing graphics and audio, setting up
     * dungeon and player, and loading the scenes into memory
     */
    @Override
    protected void assetsToLoad() {
        sContext = this;
        
        LoadingGameActivity.setLoadingText("Graphics");
        Graphics.initialize(this, DESIRED_WIDTH, DESIRED_HEIGHT);
        
        LoadingGameActivity.setLoadingText("Audio");
        Audio.initialize();
        
        // Load the dungeon from definition file
        LoadingGameActivity.setLoadingText("Dungeon definition");
        sDungeon = new DungeonManager("dungeon_definition.xml");
        
        // Prepare our item factory by loading all the assets from
        // which every item is created
        LoadingGameActivity.setLoadingText("Items");
        ItemFactory.loadResources();

        // Prepare the player
        Graphics.beginLoad("gfx/", 256, 512);
        LoadingGameActivity.setLoadingText("Player");
        sPlayer = new Player(Graphics.createAnimatedSprite("hero.png", 4, 4));
        Graphics.endLoad();
        sPlayer.equipWeapon(ItemFactory.createRandomWeapon(2));
        sPlayer.equipArmour(ItemFactory.createRandomArmour(2));
        
        // Setup all of the main panels used in the game
        LoadingGameActivity.setLoadingText("Battle scene");
        sBattleScene = new BattleScene();
        sBattleScene.loadResources();
        
        LoadingGameActivity.setLoadingText("Status scene");
        sStatusScene = new StatusScene();
        sStatusScene.loadResources();
        
        LoadingGameActivity.setLoadingText("Main scene");
        sMainScene = new MainScene();
        sMainScene.loadResources();
        
        LoadingGameActivity.setLoadingText("Done!");
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

    public static void restart() {
    }
    
    public static void destroy() {
        Audio.stop();
        sMainScene = null;
        sBattleScene = null;
        getContext().finish();    
    }
    
    public static void openCombat() {
        SceneManager.pushScene(sBattleScene);
    }    
    
    public static void openStatus() {
        SceneManager.pushScene(sStatusScene);
    }
    
    public static void openMinimap() {
        SceneManager.pushScene(sMinimapScene);
    }
    
    public static void endScene() {
        SceneManager.popScene();
    }

    public static void gameToast(final String msg, final int duration) {
        sContext.runOnUiThread(new Runnable() {
            public void run() {
               Toast.makeText(sContext, msg, duration).show();
            }
        });
    }

    public static Display getDisplay() {
        return sContext.getWindowManager().getDefaultDisplay();
    }

}