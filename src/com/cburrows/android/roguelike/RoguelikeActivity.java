package com.cburrows.android.roguelike;

import java.util.Random;

import android.content.Context;
import android.util.Log;
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
    
    private SceneManager mSceneManager;
    
    private BoundCamera mCamera;
    
    private int mCameraWidth;
    private int mCameraHeight;
    private float mGameScaleX;
    private float mGameScaleY;
    
    public GameScene mMainScene;
    public GameScene mBattleScene;
    public GameScene mStatusScene;
    
    private Random rand;
        
    public Engine onLoadEngine() {
        rand = new Random(System.currentTimeMillis());
        
        mSceneManager = new SceneManager(this);
              
        final Display display = getWindowManager().getDefaultDisplay();
        mCameraWidth = display.getWidth();
        mCameraHeight = display.getHeight();
        mGameScaleX = mCameraWidth / 320.0f; 
        mGameScaleY = mCameraHeight / 240.0f;
        
        mCamera = new BoundCamera(0, 0, mCameraWidth, mCameraHeight);
               
        // Engine with various options 
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(mCameraWidth, mCameraHeight), mCamera));
    }

    public void onLoadResources() {
        mMainScene = new MainScene(this);
        mBattleScene = new BattleScene(this);
        mStatusScene = new StatusScene(this);
        mMainScene.loadResources();
        mBattleScene.loadResources();
        mStatusScene.loadResources();
    }

    public Scene onLoadScene() {
        
        mSceneManager.pushScene(mMainScene);
        gameToast(mGameScaleX + ", " + mGameScaleY, Toast.LENGTH_SHORT);
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
        GameScene scene = mSceneManager.popScene();
        if (mSceneManager.getSize() == 0) {
            mMainScene = null;
            mBattleScene = null;
            finish();        
        }
    }
    
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }
    
    public Context getContext() { return this; }
    
    public BoundCamera getCamera() { return mCamera; }
    
    public float getGameScaleX() { return mGameScaleX; }
    
    public float getGameScaleY() { return mGameScaleY; }
    
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
    
    public void gameToast(final String msg, final int duration) {
        this.runOnUiThread(new Runnable() {
            public void run() {
               Toast.makeText(RoguelikeActivity.this, msg, duration).show();
            }
        });
    }   
}