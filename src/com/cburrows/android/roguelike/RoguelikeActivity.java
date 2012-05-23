package com.cburrows.android.roguelike;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class RoguelikeActivity extends BaseGameActivity implements
    IOnSceneTouchListener {
    
    public static final int FONT_LARGE_SIZE = 20;
    public static final int FONT_SIZE = 14;
    public static final int FONT_SMALL_SIZE = 10;
    public static final int DESIRED_WIDTH = 320;
    public static final int DESIRED_HEIGHT = 240;
    
    public static float sScaleX;
    public static float sScaleY;
    public static int sCameraWidth;
    public static int sCameraHeight;
    
    private static RoguelikeActivity mContext;
    private SceneManager mSceneManager;
    
    private BoundCamera mCamera;
    
    public Font Font;
    public Font SmallFont;
    public Font LargeFont;
    
    private Player mPlayer;
    
    public GameScene mMainScene;
    public GameScene mBattleScene;
    public GameScene mStatusScene;
    
    //private Random rand;
        
    public Engine onLoadEngine() {
        //rand = new Random(System.currentTimeMillis());
        
        final Display display = getWindowManager().getDefaultDisplay();
        sCameraWidth = display.getWidth();
        sCameraHeight = display.getHeight();
        sScaleX = sCameraWidth / (float)DESIRED_WIDTH; 
        sScaleY = sCameraHeight / (float)DESIRED_HEIGHT;
        
        Graphics.initialize(this, DESIRED_WIDTH, DESIRED_HEIGHT);
        mContext = this;
        mSceneManager = new SceneManager(this);
        mCamera = new BoundCamera(0, 0, sCameraWidth, sCameraHeight);
               
        // Engine with various options 
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(sCameraWidth, sCameraHeight), mCamera));
    }

    public void onLoadResources() {
        
        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/prstart.ttf");
        Font = Graphics.createFont(t, FONT_SIZE, Color.WHITE);
        LargeFont = Graphics.createFont(t, FONT_LARGE_SIZE, Color.WHITE);
        SmallFont = Graphics.createFont(t, FONT_SMALL_SIZE, Color.WHITE);
        
        ItemFactory.loadResources();
        
        Graphics.beginLoad("gfx/", 256, 512);
        
        mPlayer = new Player(Graphics.createAnimatedSprite("hero.png", 4, 4));
        mPlayer.equipWeapon(ItemFactory.createRandomWeapon(2)); //createItem(this, "Dagger", 0, 0, 5, 0, 0));
        mPlayer.equipArmour(ItemFactory.createRandomArmour(1)); //.createItem(this, "Buckler", 50, 1, 0, 4, 0));
        
        Graphics.endLoad();
        
        mMainScene = new MainScene(this);
        mBattleScene = new BattleScene(this);
        mStatusScene = new StatusScene(this);
        mMainScene.loadResources();
        mBattleScene.loadResources();
        mStatusScene.loadResources();
    }

    public Scene onLoadScene() {
        
        mSceneManager.pushScene(mMainScene);
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
    
    public void onBackPressed() {
        mSceneManager.popScene();
        if (mSceneManager.getSize() == 0) {
            mMainScene = null;
            mBattleScene = null;
            finish();        
        }
    }
    
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }
    
    public void startCombat() {
        mSceneManager.pushScene(mBattleScene);
    }
    
    public void endCombat() {
        mSceneManager.popScene();
    }
    
    public void openStatus() {
        mSceneManager.pushScene(mStatusScene);
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