package base;

import android.widget.Toast;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import com.cburrows.android.roguelike.Dungeon;
import com.cburrows.android.roguelike.ItemFactory;
import com.cburrows.android.roguelike.Player;

import scenes.BattleScene;
import scenes.GameScene;
import scenes.MainScene;
import scenes.SceneManager;
import scenes.StatusScene;

public class RoguelikeActivity extends LoadingGameActivity implements
    IOnSceneTouchListener {
    
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
    private static SceneManager sSceneManager;
    
    public static BoundCamera sCamera;
    
    private static Player sPlayer;
    
    public static GameScene sMainScene;
    public static GameScene sBattleScene;
    public static GameScene sStatusScene;
    
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
        sSceneManager.pushScene(sMainScene);
        return sSceneManager.getTopScene();
    }

    @Override
    protected void assetsToLoad() {
        sContext = this;
        sSceneManager = new SceneManager(this);
        
        Graphics.initialize(this, DESIRED_WIDTH, DESIRED_HEIGHT);
        AudioManager.initialize();
        
        // Load the dungeon from definition file
        sDungeon = new Dungeon("dungeon_definition.xml");
        
        // Prepare our item factory by loading all the assets from
        // which every item is created
        ItemFactory.loadResources();
        
        // Prepare the player
        Graphics.beginLoad("gfx/", 256, 512);
        sPlayer = new Player(Graphics.createAnimatedSprite("hero.png", 4, 4));
        Graphics.endLoad();
        sPlayer.equipWeapon(ItemFactory.createRandomWeapon(2));
        sPlayer.equipArmour(ItemFactory.createRandomArmour(2));
        
        // Setup all of the main panels used in the game
        sMainScene = new MainScene(this);
        sBattleScene = new BattleScene(this);
        sStatusScene = new StatusScene(this);
        sMainScene.loadResources();
        sBattleScene.loadResources();
        sStatusScene.loadResources();
    }   
    
    public void onBackPressed() {
        sSceneManager.popScene();
    }
    
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void restart() {
    }
    
    public static void destroy() {
        AudioManager.stop();
        sMainScene = null;
        sBattleScene = null;
        getContext().finish();    
    }
    
    public void startCombat() {
        sSceneManager.pushScene(sBattleScene);
    }
    
    public void endCombat() {
        sSceneManager.popScene();
    }
    
    public void openStatus() {
        sSceneManager.pushScene(sStatusScene);
    }
    
    public void closeStatus() {
        sSceneManager.popScene();
    }

    public void gameToast(final String msg, final int duration) {
        this.runOnUiThread(new Runnable() {
            public void run() {
               Toast.makeText(RoguelikeActivity.this, msg, duration).show();
            }
        });
    }

}