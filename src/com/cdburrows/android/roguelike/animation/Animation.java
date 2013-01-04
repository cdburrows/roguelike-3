/*
 * Copyright (c) 2012-2013, Christopher Burrows
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

package com.cdburrows.android.roguelike.animation;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.TiledSprite;

import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.graphics.Graphics;

public class Animation {
    private static final int TEXTURE_ATLAS_WIDTH = 512;

    private static final int TEXTURE_ATLAS_HEIGHT = 512;

    private String mAnimationName;

    private int mFrameWidth;

    private int mFrameHeight;

    private int mFrameIndex;

    private int mFramesX;

    private int mFramesY;

    private float mPosX;

    private float mPosY;

    private float mStartX;

    private float mStartY;

    private float mDuration; // seconds

    private float mCurrentTime;

    private float mDx;

    private float mDy;

    private List<KeyFrame> mKeyFrames;

    private int mKeyFrameIndex;

    private float mNextKeyFrameTime;

    private boolean mFlippedHorizontal;

    private boolean mFlippedVertical;

    private float mRotatation = 0f;

    private float mScale = 1f;

    private float mSpeed = 1f;

    private TiledSprite mAnimationSprite;

    private Engine mEngine;

    IEntityModifierListener mFinishListener;

    public Animation(float startX, float startY, float endX, float endY) {
        mAnimationName = "slash_animation.png";
        mFrameWidth = 96;
        mFrameHeight = 96;
        mFrameIndex = -1;
        mFramesX = 3;
        mFramesY = 1;
        mStartX = startX;
        mStartY = startY;
        mPosX = startX - (mFrameWidth / 2);
        mPosY = startY - (mFrameHeight / 2);
        mDx = endX - startX;
        mDy = endY - startY;
        mDuration = 0.24f;
        mSpeed = mDuration;

        mKeyFrames = new ArrayList<KeyFrame>();
        mKeyFrames.add(new KeyFrame(0.0f, 0));
        mKeyFrames.add(new KeyFrame(0.04f, 1));
        mKeyFrames.add(new KeyFrame(0.08f, 2));
        mKeyFrames.add(new KeyFrame(0.16f, 1));
        mKeyFrames.add(new KeyFrame(0.2f, 0));

        mKeyFrameIndex = 0;
        if (mKeyFrames.size() > 0)
            mNextKeyFrameTime = mKeyFrames.get(0).mTime;
    }

    public TiledSprite loadAnimation() {
        mEngine = RoguelikeActivity.getContext().getEngine();

        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        mAnimationSprite = Graphics.createTiledSprite(mAnimationName, mFramesX, mFramesY, mStartX,
                mStartY);
        Graphics.endLoad("Animation " + mAnimationName);

        mAnimationSprite.setRotation(mRotatation);
        mAnimationSprite.setFlippedHorizontal(isFlippedHorizontal());
        mAnimationSprite.setFlippedVertical(isFlippedVertical());
        mAnimationSprite.setPosition(mPosX, mPosY);

        return mAnimationSprite;
    }

    public void start() {
        reset();
        mEngine.registerUpdateHandler(new UpdateHandler());
    }

    public void pause() {

    }

    public void stop() {

    }

    public void reset() {
        mCurrentTime = 0;
        mFrameIndex = -1;
        mKeyFrameIndex = 0;
        if (mKeyFrames.size() > 1) {
            mNextKeyFrameTime = mKeyFrames.get(1).mTime;
        }
        mPosX = mStartX - (mFrameWidth / 2);
        mPosY = mStartY - (mFrameHeight / 2);
        mAnimationSprite.setPosition(mPosX, mPosY);
    }

    public void attachOnFinishListener(IEntityModifierListener listener) {
        mFinishListener = listener;
    }

    private void nextKeyFrame() {
        mKeyFrameIndex++;
        if (mKeyFrameIndex < mKeyFrames.size() - 1) {
            mNextKeyFrameTime = mKeyFrames.get(mKeyFrameIndex + 1).mTime;
        }
        mFrameIndex = mKeyFrames.get(mKeyFrameIndex).mFrameIndex;
        mAnimationSprite.setCurrentTileIndex(mFrameIndex);
    }

    private class UpdateHandler implements IUpdateHandler {

        public void onUpdate(float pSecondsElapsed) {
            mCurrentTime += pSecondsElapsed;
            if (mKeyFrameIndex < mKeyFrames.size() - 1) {
                if (mCurrentTime >= mNextKeyFrameTime) {
                    nextKeyFrame();
                }
            }

            mPosX += mDx * (pSecondsElapsed / mDuration);
            mPosY += mDy * (pSecondsElapsed / mDuration);
            mAnimationSprite.setPosition(mPosX, mPosY);

            if (mCurrentTime > (mDuration)) {
                if (mFinishListener != null)
                    mFinishListener.onModifierFinished(null, mAnimationSprite);

                // mAnimationSprite.setVisible(false);
                mEngine.unregisterUpdateHandler(this);
                // mAnimationSprite.getParent().detachChild(mAnimationSprite);
                mAnimationSprite.detachSelf();
            }
        }

        public void reset() {
            // TODO Auto-generated method stub

        }

    }

    public class KeyFrame {
        public float mTime;

        public int mFrameIndex;

        public KeyFrame(float time, int frame) {
            mTime = time;
            mFrameIndex = frame;
        }
    }

    public TiledSprite getSprite() {
        return mAnimationSprite;
    }

    public float getRotatation() {
        return mRotatation;
    }

    public void setRotation(float Rotate) {
        this.mRotatation = Rotate;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float Scale) {
        this.mScale = Scale;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float Speed) {
        this.mSpeed = Speed;
        float multiplyer = Speed / mDuration;
        for (KeyFrame k : mKeyFrames) {
            k.mTime *= multiplyer;
        }
    }

    public boolean isFlippedHorizontal() {
        return mFlippedHorizontal;
    }

    public void setFlippedHorizontal(boolean FlippedHorizontal) {
        this.mFlippedHorizontal = FlippedHorizontal;
    }

    public boolean isFlippedVertical() {
        return mFlippedVertical;
    }

    public void setFlippedVertical(boolean FlippedVertical) {
        this.mFlippedVertical = FlippedVertical;
    }

    
    public static Animation create(float x1, float y1, float x2, float y2, float scale,
            IEntityModifierListener onAnimationDone) {
        return Animation.create(x1, y1, x2, y2, scale, 0f, false, false, onAnimationDone);
    }
    
    public static Animation create(float x1, float y1, float x2, float y2, float scale,
            float rotation, IEntityModifierListener onAnimationDone) {
        return Animation.create(x1, y1, x2, y2, scale, rotation, false, false, onAnimationDone);
    }
    
    public static Animation create(float x1, float y1, float x2, float y2, float scale,
            boolean flipX, boolean flipY, IEntityModifierListener onAnimationDone) {
        return Animation.create(x1, y1, x2, y2, scale, 0f, flipX, flipY, onAnimationDone);
    }

    /**
     * Creates an animation.
     * 
     * @param x1 the starting horizontal offset
     * @param y1 the starting vertical offset
     * @param x2 the ending horizontal offset
     * @param y2 the ending vertical offset
     * @param scale the image scale
     * @param rotation the rotation of the image in degrees
     * @param flipX whether the image is flipped horizontally
     * @param flipY whether the image is flipped vertically
     * @param onAnimationDone the listener to callback when animation completes
     * 
     * @return the defined animation
     */
    public static Animation create(float x1, float y1, float x2, float y2, float scale, float rotation,
            boolean flipX, boolean flipY, IEntityModifierListener onAnimationDone) {
        Animation a = new Animation(x1, y1, x2, y2);
        a.setScale(scale);
        a.setRotation(rotation);
        a.setFlippedHorizontal(flipX);
        a.setFlippedVertical(flipY);
        a.loadAnimation();
        a.attachOnFinishListener(onAnimationDone);
        return a;
    }
}
