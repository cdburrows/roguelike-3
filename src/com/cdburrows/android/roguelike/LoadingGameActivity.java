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
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.Display;

/**
 * Allows for preloading of assets. 
 * TODO: Clean up class to have RoguelikeActivity do more work.
 */
public abstract class LoadingGameActivity extends BaseGameActivity {
    
    // ===========================================================
    // Constants
    // ===========================================================
 
    // How fast the loading wheel rotates
    private static final float ROTATE_CYCLE_DURATION = 0.5f;
    
    // ===========================================================
    // Fields
    // ===========================================================
 
    protected static Engine sEngine;
    
    protected static TextureRegion sBackgroundRegion;
    protected static TextureRegion sSpinnerRegion;
    protected static Sprite sSpinnerSprite;
    protected static ChangeableText sLoadingText;
    
    protected static MediaPlayer sBackgroundMusic;
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public static void setLoadingText(String text) {
        sLoadingText.setText(text);
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
 
    /**
     * Prepares the Engine
     */
    public Engine onLoadEngine() {
        final Display display = getWindowManager().getDefaultDisplay();
        RoguelikeActivity.sCameraWidth = display.getWidth();
        RoguelikeActivity.sCameraHeight = display.getHeight();
        RoguelikeActivity.sScaleX = display.getWidth() / (float)RoguelikeActivity.DESIRED_WIDTH; 
        RoguelikeActivity.sScaleY = display.getHeight() / (float)RoguelikeActivity.DESIRED_HEIGHT;
        RoguelikeActivity.sCamera = new BoundCamera(0, 0, display.getWidth(), display.getHeight());       
 
        sEngine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(display.getWidth(), display.getHeight()), 
                RoguelikeActivity.sCamera));
        sEngine.registerUpdateHandler(new FPSLogger());
        
        return sEngine;
    }
 
    /**
     * Loads resources for the loading scene
     */
    public void onLoadResources() {
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "gfx/splash_loading.png", 0, 0);
        sSpinnerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "gfx/spinner_black_48.png", 0, 240);
        
        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/prstart.ttf");
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        getTextureManager().loadTextures(atlas);
        Font font = FontFactory.create(atlas, t, 10 * RoguelikeActivity.sScaleX, true, Color.WHITE);
        getEngine().getFontManager().loadFonts(font);
        sLoadingText = new ChangeableText(16 * RoguelikeActivity.sScaleX, 120 * RoguelikeActivity.sScaleY, font, 
                "Initializing", 32);
        
        sEngine.getTextureManager().loadTextures(bitmapTextureAtlas, atlas);
        
        if (RoguelikeActivity.sMusicEnabled) {
            try {
                 AssetFileDescriptor afd = getAssets().openFd("sfx/music/MeadowOfThePast.aac");
                 sBackgroundMusic = new MediaPlayer();
                 sBackgroundMusic.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                 sBackgroundMusic.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
      * Displays loading scene and loads assets
      */
    public Scene onLoadScene() {
        if (RoguelikeActivity.sMusicEnabled) sBackgroundMusic.start();
        
        Scene scene = new Scene();
        scene.attachChild(new Sprite(0, 0, RoguelikeActivity.sCameraWidth, RoguelikeActivity.sCameraHeight, sBackgroundRegion));
        sSpinnerSprite = new Sprite(284 * RoguelikeActivity.sScaleX, 210 * RoguelikeActivity.sScaleY, 
                16 * RoguelikeActivity.sScaleX, 16 * RoguelikeActivity.sScaleY,
                sSpinnerRegion);
        sSpinnerSprite.registerEntityModifier(new LoopEntityModifier(new RotationModifier(ROTATE_CYCLE_DURATION, 0, 360)));
        scene.attachChild(sSpinnerSprite);
        scene.attachChild(sLoadingText);

        // Load assets
        IAsyncCallback callback = new IAsyncCallback() {
 
            public void workToDo() {
                assetsToLoad();
            }
 
            public void onComplete() {
                unloadLoadingScene();
                Scene scene = onAssetsLoaded();
                
                sEngine.setScene(scene);
            }
        };
 
        new AsyncTaskLoader().execute(callback);
 
        return scene;
    }
 
    // ===========================================================
    // Methods
    // ===========================================================
 
    private void unloadLoadingScene(){
        if (RoguelikeActivity.sMusicEnabled) sBackgroundMusic.stop();
        sBackgroundRegion = null;
        sSpinnerRegion = null;
        sSpinnerSprite = null;
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
    
    public class AsyncTaskLoader extends AsyncTask<IAsyncCallback, Integer, Boolean> {
        
        // ===========================================================
        // Fields
        // ===========================================================
     
        IAsyncCallback[] _params;
     
        // ===========================================================
        // Inherited Methods
        // ===========================================================
     
        protected Boolean doInBackground(IAsyncCallback... params) {
            this._params = params;
            int count = params.length;
            for(int i = 0; i < count; i++){
                params[i].workToDo();
            }
            return true;
        }
     
        @Override
        protected void onPostExecute(Boolean result) {
            int count = this._params.length;
            for(int i = 0; i < count; i++){
                this._params[i].onComplete();
            }
        }
    }
    
    public interface IAsyncCallback {
        // ===========================================================
        // Methods
        // ===========================================================
     
        public abstract void workToDo();
     
        public abstract void onComplete();
     
    }
 
}
