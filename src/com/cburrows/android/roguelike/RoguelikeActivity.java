package com.cburrows.android.roguelike;

import android.view.Display;
import android.widget.Toast;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class RoguelikeActivity extends BaseGameActivity implements
    IOnSceneTouchListener {
    
    public static final int DESIRED_WIDTH = 320;
    public static final int DESIRED_HEIGHT = 240;
    
    public static float sScaleX;
    public static float sScaleY;
    public static int sCameraWidth;
    public static int sCameraHeight;
    
    private static RoguelikeActivity mContext;
    private SceneManager mSceneManager;
    
    private BoundCamera mCamera;
    
    private Player mPlayer;
    
    public static GameScene sMainScene;
    public static GameScene sBattleScene;
    public static GameScene sStatusScene;
    
    public static Dungeon sDungeon;
    
    //private Random rand;
        
    public Engine onLoadEngine() {
        //rand = new Random(System.currentTimeMillis());
        
        final Display display = getWindowManager().getDefaultDisplay();
        sCameraWidth = display.getWidth();
        sCameraHeight = display.getHeight();
        sScaleX = sCameraWidth / (float)DESIRED_WIDTH; 
        sScaleY = sCameraHeight / (float)DESIRED_HEIGHT;
        
        mContext = this;
        mSceneManager = new SceneManager(this);
        mCamera = new BoundCamera(0, 0, sCameraWidth, sCameraHeight);
               
        // Engine with various options 
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(sCameraWidth, sCameraHeight), mCamera));
    }

    public void onLoadResources() {   
        Graphics.initialize(this, DESIRED_WIDTH, DESIRED_HEIGHT);
        
        // Load the dungeon from definition file
        sDungeon = new Dungeon("dungeon_definition.xml");
        
        // Prepare our item factory by loading all the assets from
        // which every item is created
        ItemFactory.loadResources();
        
        // Prepare the player
        Graphics.beginLoad("gfx/", 256, 512);
        mPlayer = new Player(Graphics.createAnimatedSprite("hero.png", 4, 4));
        Graphics.endLoad();
        mPlayer.equipWeapon(ItemFactory.createRandomWeapon(2));
        mPlayer.equipArmour(ItemFactory.createRandomArmour(2));
        
        // Setup all of the main panels used in the game
        sMainScene = new MainScene(this);
        sBattleScene = new BattleScene(this);
        sStatusScene = new StatusScene(this);
        sMainScene.loadResources();
        sBattleScene.loadResources();
        sStatusScene.loadResources();
    }

    public Scene onLoadScene() {
        
        mSceneManager.pushScene(sMainScene);
        //gameToast(mGameScaleX + ", " + mGameScaleY, Toast.LENGTH_SHORT);
        return mSceneManager.getTopScene();
    }
        
    public void onLoadComplete() {
        
    }
    
    public void restart() {
        /*
        Log.d("MAIN", "restart");
        runOnUpdateThread(new Runnable() {

            public void run() {
                mMainScene.detachChildren();
                mMainScene.attachChild(mTile, 0);
            }
            
        });
        */
    }
    
    /*
    protected void onPause() {
        
    }
    
    public void onResumeGame() {
        super.onResumeGame();
    }
    */
    
    /*
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        
        return false;
    }
     */
    
    public static void destroy() {
        sMainScene = null;
        sBattleScene = null;
        getContext().finish();    
    }
    
    public void onBackPressed() {
        mSceneManager.popScene();
    }
    
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }
    
    public void startCombat() {
        mSceneManager.pushScene(sBattleScene);
    }
    
    public void endCombat() {
        mSceneManager.popScene();
    }
    
    public void openStatus() {
        mSceneManager.pushScene(sStatusScene);
    }
    
    public void closeStatus() {
        mSceneManager.popScene();
    }

    public static RoguelikeActivity getContext() { return mContext; }
    
    public BoundCamera getCamera() { return mCamera; }
    
    public Player getPlayer() { return mPlayer; }
    
    public void setPlayer(Player player) { mPlayer = player; }

    public void gameToast(final String msg, final int duration) {
        this.runOnUiThread(new Runnable() {
            public void run() {
               Toast.makeText(RoguelikeActivity.this, msg, duration).show();
            }
        });
    }   
}