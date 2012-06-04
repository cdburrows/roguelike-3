package base;

import java.io.IOException;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;

public abstract class LoadingGameActivity extends BaseGameActivity {
    
    // ===========================================================
    // Constants
    // ===========================================================
 
    private static final float ROTATE_CYCLE_DURATION = 0.5f;
    
    // ===========================================================
    // Fields
    // ===========================================================
 
    protected Engine mEngine;
    protected TextureRegion mBackgroundRegion;
    protected TextureRegion mSpinnerRegion;
    protected Sprite mSpinnerSprite;
    protected MediaPlayer mBackgroundMusic;
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
 
    public Engine onLoadEngine() {
        final Display display = getWindowManager().getDefaultDisplay();
        RoguelikeActivity.sCameraWidth = display.getWidth();
        RoguelikeActivity.sCameraHeight = display.getHeight();
        RoguelikeActivity.sScaleX = display.getWidth() / (float)RoguelikeActivity.DESIRED_WIDTH; 
        RoguelikeActivity.sScaleY = display.getHeight() / (float)RoguelikeActivity.DESIRED_HEIGHT;
        RoguelikeActivity.sCamera = new BoundCamera(0, 0, display.getWidth(), display.getHeight());       
 
        mEngine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(display.getWidth(), display.getHeight()), 
                RoguelikeActivity.sCamera));
        
        return mEngine;
    }
 
    public void onLoadResources() {
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "gfx/splash_loading.png", 0, 0);
        mSpinnerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "gfx/spinner_black_48.png", 0, 240);
        mEngine.getTextureManager().loadTexture(bitmapTextureAtlas);
        
        if (RoguelikeActivity.sMusicEnabled) {
            try {
                 AssetFileDescriptor afd = getAssets().openFd("sfx/music/MeadowOfThePast.aac");
                 mBackgroundMusic = new MediaPlayer();
                 mBackgroundMusic.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                 mBackgroundMusic.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
      * This is where you create your loading screen. 
      */
    public Scene onLoadScene() {
        Scene scene = new Scene();
        
        if (RoguelikeActivity.sMusicEnabled) mBackgroundMusic.start();
        scene.attachChild(new Sprite(0, 0, RoguelikeActivity.sCameraWidth, RoguelikeActivity.sCameraHeight, mBackgroundRegion));
        mSpinnerSprite = new Sprite(284 * RoguelikeActivity.sScaleX, 210 * RoguelikeActivity.sScaleY, 
                16 * RoguelikeActivity.sScaleX, 16 * RoguelikeActivity.sScaleY,
                mSpinnerRegion);
        mSpinnerSprite.registerEntityModifier(new LoopEntityModifier(new RotationModifier(ROTATE_CYCLE_DURATION, 0, 360)));
        scene.attachChild(mSpinnerSprite);
        
        Log.d("LOAD", "onLoadScene");
 
 
        /*
         * Here's where the assets are loaded in the background behind the loading scene.
         */
        IAsyncCallback callback = new IAsyncCallback() {
 
            public void workToDo() {
                assetsToLoad();
            }
 
            public void onComplete() {
                unloadLoadingScene();
                Scene scene = onAssetsLoaded();
                
                Log.d("LOAD", "Engine: " + (mEngine != null));
                Log.d("LOAD", "Scene: " + (scene != null));
                
                mEngine.setScene(scene);
            }
        };
 
        new AsyncTaskLoader().execute(callback);
 
        return scene;
    }
 
    // ===========================================================
    // Methods
    // ===========================================================
 
    private void unloadLoadingScene(){
        if (RoguelikeActivity.sMusicEnabled) mBackgroundMusic.stop();
        mBackgroundRegion = null;
        mSpinnerRegion = null;
        mSpinnerSprite = null;
    }
 
    /**
     * This will be called after all of the asyc assets are loaded.
     * The loader will be in charge of changing the scenes.
     */
    protected abstract Scene onAssetsLoaded();
 
 
    /**
     * This is called when assets need to be loaded in the background.
     */
    protected abstract void assetsToLoad();
 
}
