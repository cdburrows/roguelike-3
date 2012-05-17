package com.cburrows.android.roguelike;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.FadeInModifier;
import org.anddev.andengine.entity.modifier.FadeOutModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.ease.EaseLinear;

import android.util.Log;
import android.view.Display;

public abstract class GameScene extends Scene {
    protected RoguelikeActivity mContext;
    protected BoundCamera mCamera;
    protected BoundCamera mLastCamera;
    
    protected int mCameraWidth;
    protected int mCameraHeight;
    
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mFadeTextureRegion;
    private Sprite fadeSprite;
    
    protected boolean mLoaded;
    protected boolean mInitialized;
    protected boolean mTransitionOver;
    
    public GameScene(RoguelikeActivity context) { 
        mContext = context;
        mCamera = context.getCamera();
        
        final Display display = context.getWindowManager().getDefaultDisplay();
        mCameraWidth = display.getWidth();
        mCameraHeight = display.getHeight();
        
        setupFade();
        fadeSprite = new Sprite(mCamera.getMinX(), mCamera.getMinY(), mCameraWidth, mCameraHeight, mFadeTextureRegion);
        fadeSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void loadResources() { mLoaded = true; }
    public void initialize() { mInitialized = true; }
    public abstract void pause();
    
    public boolean isLoaded() { return mLoaded; }
    
    public boolean isInitialized() { return mInitialized; }
    
    public void fadeOut(float duration, IEntityModifierListener listener) {
        mCamera.updateChaseEntity();
        fadeSprite.setPosition(mCamera.getMinX(), mCamera.getMinY());
        AlphaModifier prFadeOutModifier = new FadeInModifier(duration, listener, EaseLinear.getInstance()); 
        fadeSprite.setAlpha(0.0f);
        if (!fadeSprite.hasParent()) this.attachChild(fadeSprite);
        fadeSprite.registerEntityModifier(prFadeOutModifier);
    }
    
    public void fadeIn(float duration, IEntityModifierListener listener) {
        mCamera.updateChaseEntity();
        fadeSprite.setPosition(mCamera.getMinX(), mCamera.getMinY());
        
        listener = new IEntityModifierListener() {
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                mTransitionOver = true;
            }  
        };
        
        AlphaModifier prFadeOutModifier = new FadeOutModifier(duration, listener, EaseLinear.getInstance()); 
        fadeSprite.setAlpha(1.0f);
        if (!fadeSprite.hasParent()) attachChild(fadeSprite);
        fadeSprite.registerEntityModifier(prFadeOutModifier);
    }

    private void setupFade() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        if (mBitmapTextureAtlas == null) { 
            mBitmapTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        }

        if (mFadeTextureRegion == null) {
        mFadeTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, mContext, "fade.png", 0, 0);  
        }
        mContext.getEngine().getTextureManager().loadTexture(mBitmapTextureAtlas);
    }
}