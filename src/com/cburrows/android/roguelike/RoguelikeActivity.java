package com.cburrows.android.roguelike;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.inputmethod.BaseInputConnection;
import android.widget.Toast;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.cburrows.android.roguelike.TmxMap.Map;
import com.cburrows.android.roguelike.xml.ItemDefinitions;

public class RoguelikeActivity extends BaseGameActivity implements
    IOnSceneTouchListener {
    
    public static final int FONT_LARGE_SIZE = 20;
    public static final int FONT_SIZE = 16;
    public static final int FONT_SMALL_SIZE = 10;
    
    private SceneManager mSceneManager;
    
    private BoundCamera mCamera;
    
    private int mCameraWidth;
    private int mCameraHeight;
    private float mGameScaleX;
    private float mGameScaleY;
    
    private BitmapTextureAtlas mFontTexture;
    private BitmapTextureAtlas mFontLargeTexture;
    private BitmapTextureAtlas mFontSmallTexture;
    public Font Font;
    public Font SmallFont;
    public Font LargeFont;
    
    private TextureRegion mEquipmentTextureRegion;
    
    private Player mPlayer;
    
    public GameScene mMainScene;
    public GameScene mBattleScene;
    public GameScene mStatusScene;
    
    private Random rand;
    
        
    public Engine onLoadEngine() {
        rand = new Random(System.currentTimeMillis());
        
        final Display display = getWindowManager().getDefaultDisplay();
        mCameraWidth = display.getWidth();
        mCameraHeight = display.getHeight();
        mGameScaleX = mCameraWidth / 320.0f; 
        mGameScaleY = mCameraHeight / 240.0f;
        
        mSceneManager = new SceneManager(this);
        
        mCamera = new BoundCamera(0, 0, mCameraWidth, mCameraHeight);
               
        // Engine with various options 
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(mCameraWidth, mCameraHeight), mCamera));
    }

    public void onLoadResources() {
        
        BitmapTextureAtlas mBitmapTextureAtlas = 
                new BitmapTextureAtlas(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mFontLargeTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mFontSmallTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        getTextureManager().loadTextures(mBitmapTextureAtlas, mFontTexture, mFontLargeTexture, mFontSmallTexture);
        
        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/prstart.ttf");
        Font = FontFactory.create( mFontTexture, t, FONT_SIZE * getGameScaleX(), true, Color.WHITE);
        LargeFont = FontFactory.create( mFontLargeTexture, t, FONT_LARGE_SIZE * getGameScaleX(), true, Color.WHITE);
        SmallFont = FontFactory.create( mFontSmallTexture, t, FONT_SMALL_SIZE * getGameScaleX(), true, Color.WHITE);
        getEngine().getFontManager().loadFonts(Font, LargeFont, SmallFont);
        
        ItemFactory.loadResources(this);
        
        mPlayer = new Player(BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mBitmapTextureAtlas, this, "hero.png", 0, 0, 4, 4),
                this.getGameScaleX(), this.getGameScaleY());
        mPlayer.equipWeapon(ItemFactory.createRandomWeapon(this, 2)); //createItem(this, "Dagger", 0, 0, 5, 0, 0));
        mPlayer.equipArmour(ItemFactory.createRandomArmour(this, 1)); //.createItem(this, "Buckler", 50, 1, 0, 4, 0));
        
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
    
    
    public Context getContext() { return this; }
    
    public BoundCamera getCamera() { return mCamera; }
    
    public float getGameScaleX() { return mGameScaleX; }
    
    public float getGameScaleY() { return mGameScaleY; }
    
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