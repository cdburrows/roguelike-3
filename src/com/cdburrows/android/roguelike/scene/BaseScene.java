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

package com.cdburrows.android.roguelike.scene;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.DurationEntityModifier;
import org.anddev.andengine.entity.modifier.EntityModifier;
import org.anddev.andengine.entity.modifier.FadeInModifier;
import org.anddev.andengine.entity.modifier.FadeOutModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.ease.EaseLinear;

import com.cdburrows.android.roguelike.RoguelikeActivity;


import android.view.Display;

/**
 * All scenes must extend this class. Provides simple
 * fade in/out and shaking operations.
 */
public abstract class BaseScene extends Scene {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    protected BoundCamera mCamera;
    
    protected int mCameraWidth;
    protected int mCameraHeight;
    
    protected boolean mLoaded;
    protected boolean mPrepared;
    protected boolean mTransitionOver;
    
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mFadeTextureRegion;
    private Sprite fadeSprite;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public BaseScene() { 
        mCamera = RoguelikeActivity.getCamera();
        
        mCameraWidth = RoguelikeActivity.getDisplay().getWidth();
        mCameraHeight = RoguelikeActivity.getDisplay().getHeight();
        
        setupFade();
        fadeSprite = new Sprite(mCamera.getMinX(), mCamera.getMinY(), mCameraWidth, mCameraHeight, mFadeTextureRegion);
        fadeSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public int getWidth() { return mCameraWidth; }
    
    public int getHeight() { return mCameraHeight; }
    
    public float getCenterX() { return mCameraWidth / 2; }
    
    public float getCenterY() { return mCameraHeight / 2; }
    
    protected void setTransitioning(boolean value) { mTransitionOver = true; }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    public abstract void loadResources();
    
    public abstract void prepare(IEntityModifierListener preparedListener);
    
    public abstract void pause();
    
    public abstract void destroy();
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    public boolean isLoaded() { return mLoaded; }
    
    public boolean isPrepared() { return mPrepared; }
    
    /**
     * Fades a scene out
     */
    public void fadeOut(float duration, IEntityModifierListener listener) {
        mCamera.updateChaseEntity();
        fadeSprite.setPosition(mCamera.getMinX(), mCamera.getMinY());
        AlphaModifier prFadeOutModifier = new FadeInModifier(duration, listener, EaseLinear.getInstance());
        prFadeOutModifier.setRemoveWhenFinished(true);
        fadeSprite.setAlpha(0.0f);
        if (!fadeSprite.hasParent()) this.attachChild(fadeSprite);
        fadeSprite.registerEntityModifier(prFadeOutModifier);
    }
    
    // Broken?
    public void fadeTo(float duration, float from, float to) {
        mCamera.updateChaseEntity();
        fadeSprite.setPosition(mCamera.getMinX(), mCamera.getMinY());
        AlphaModifier fadeModifier = new AlphaModifier(duration, from, to, EaseLinear.getInstance());
        fadeModifier.setRemoveWhenFinished(true);
        fadeSprite.registerEntityModifier(fadeModifier);
    }
    
    /**
     * Fades a scene in
     */
    public void fadeIn(float duration, final IEntityModifierListener listener) {
        mCamera.updateChaseEntity();
        fadeSprite.setPosition(mCamera.getMinX(), mCamera.getMinY());
        

        IEntityModifierListener fadeListener = new IEntityModifierListener() {
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                setTransitioning(false);
                //listener.onModifierFinished(null, null);
            }
        };

        AlphaModifier prFadeOutModifier = new FadeOutModifier(duration, fadeListener, EaseLinear.getInstance());
        prFadeOutModifier.setRemoveWhenFinished(true);
        
        fadeSprite.setAlpha(1.0f);
        if (!fadeSprite.hasParent()) attachChild(fadeSprite);
        fadeSprite.registerEntityModifier(prFadeOutModifier);
    }
    
    /**
     * Shakes the camera left and right
     */
    public void shake(final float duration, final float intensity) {
        final float x = mCamera.getCenterX();
        final float y = mCamera.getCenterY();
        
        this.registerEntityModifier(new DurationEntityModifier(duration, 
                new IEntityModifierListener() {
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        // reset the camera after modifier is done
                        mCamera.setCenter(x, y);
                    }
                }) {
                        public EntityModifier deepCopy() throws DeepCopyNotSupportedException { return null; }
                        
                        @Override
                        protected void onManagedUpdate(float pSecondsElapsed, IEntity pItem) {
                            // Just shake left and right
                            int sentitX =   1;
                            //int sentitY =   1;
                            if(Math.random() < 0.5) sentitX = -1;
                            //if(Math.random() < 0.5) sentitY = -1;
                            mCamera.setCenter( (float)(x + Math.random()*intensity*sentitX*RoguelikeActivity.sScaleX),
                                                            (float)(y));
                        }
                        
                        @Override
                        protected void onManagedInitialize(IEntity pItem) {}
                    });
    }
    
    /**
     * Loads assets. 
     */
    private void setupFade() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        if (mBitmapTextureAtlas == null) { 
            mBitmapTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        }

        if (mFadeTextureRegion == null) {
        mFadeTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(mBitmapTextureAtlas, RoguelikeActivity.getContext(), "fade.png", 0, 0);  
        }
        RoguelikeActivity.getContext().getEngine().getTextureManager().loadTexture(mBitmapTextureAtlas);
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
}