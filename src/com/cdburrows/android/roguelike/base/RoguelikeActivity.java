package com.cdburrows.android.roguelike.base;

import android.content.res.AssetManager;
import android.widget.Toast;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import com.cburrows.android.roguelike.Dungeon;
import com.cburrows.android.roguelike.ItemFactory;
import com.cburrows.android.roguelike.Player;
import com.cdburrows.android.roguelike.scenes.BattleScene;
import com.cdburrows.android.roguelike.scenes.BaseScene;
import com.cdburrows.android.roguelike.scenes.MainScene;
import com.cdburrows.android.roguelike.scenes.SceneManager;
import com.cdburrows.android.roguelike.scenes.StatusScene;


public class RoguelikeActivity extends LoadingGameActivity implements
    IOnSceneTouchListener {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    public static final int DESIRED_WIDTH = 320;
    public static final int DESIRED_HEIGHT = 240;
    
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
    
    private static Player sPlayer;
    
    public static BaseScene sMainScene;
    public static BaseScene sBattleScene;
    public static BaseScene sStatusScene;
    public static BaseScene sMinimapScene;
    
    public static Dungeon sDungeon;
    
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
    
    public static Dungeon getDungeon() { return sDungeon; }
    
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
    
    @Override
    protected Scene onAssetsLoaded() {
        SceneManager.pushScene(sMainScene);
        return SceneManager.getTopScene();
    }

    @Override
    protected void assetsToLoad() {
        sContext = this;
        
        LoadingGameActivity.setLoadingText("Graphics");
        Graphics.initialize(this, DESIRED_WIDTH, DESIRED_HEIGHT);
        
        LoadingGameActivity.setLoadingText("Audio");
        AudioManager.initialize();
        
        // Load the dungeon from definition file
        LoadingGameActivity.setLoadingText("Dungeon definition");
        sDungeon = new Dungeon("dungeon_definition.xml");
        
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
         
        LoadingGameActivity.setLoadingText("Battle scene");
        sBattleScene = new BattleScene();
        sBattleScene.loadResources();
        
        LoadingGameActivity.setLoadingText("Status scene");
        sStatusScene = new StatusScene();
        sStatusScene.loadResources();
        
     // Setup all of the main panels used in the game
        LoadingGameActivity.setLoadingText("Main scene");
        sMainScene = new MainScene();
        sMainScene.loadResources();
        
        LoadingGameActivity.setLoadingText("Done!");
    }   
    
    public void onBackPressed() {
        SceneManager.popScene();
    }
    
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public static void restart() {
    }
    
    public static void destroy() {
        AudioManager.stop();
        sMainScene = null;
        sBattleScene = null;
        getContext().finish();    
    }
    
    public static void startCombat() {
        SceneManager.pushScene(sBattleScene);
    }
    
    public static void endCombat() {
        SceneManager.popScene();
    }
    
    public static void openStatus() {
        SceneManager.pushScene(sStatusScene);
    }
    
    public static void closeStatus() {
        SceneManager.popScene();
    }
    
    public static void openMinimap() {
        SceneManager.pushScene(sMinimapScene);
    }
    
    public static void closeMinimap() {
        SceneManager.popScene();
    }

    public static void gameToast(final String msg, final int duration) {
        sContext.runOnUiThread(new Runnable() {
            public void run() {
               Toast.makeText(sContext, msg, duration).show();
            }
        });
    }

}