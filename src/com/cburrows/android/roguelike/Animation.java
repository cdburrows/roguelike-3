package com.cburrows.android.roguelike;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import base.Graphics;

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
    //private float mEndX; // should be handled in keyframes
    //private float mEndY;
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
        mDy = endY - startY ;
        mDuration = 0.24f;
        mSpeed = mDuration;
        
        mKeyFrames = new ArrayList<KeyFrame>();
        mKeyFrames.add(new KeyFrame(0.0f, 0));
        mKeyFrames.add(new KeyFrame(0.04f, 1));
        mKeyFrames.add(new KeyFrame(0.08f, 2));
        mKeyFrames.add(new KeyFrame(0.16f, 1));
        mKeyFrames.add(new KeyFrame(0.2f, 0));
        
        mKeyFrameIndex = 0;
        if (mKeyFrames.size() > 0) mNextKeyFrameTime = mKeyFrames.get(0).mTime;
    }
    
    public TiledSprite loadAnimation(BaseGameActivity context) {
        mEngine = context.getEngine();

        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        mAnimationSprite = Graphics.createTiledSprite(mAnimationName, mFramesX, mFramesY, mStartX, mStartY);
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
        if (mKeyFrames.size() >1 ) { 
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
        if (mKeyFrameIndex < mKeyFrames.size()-1) {
            mNextKeyFrameTime = mKeyFrames.get(mKeyFrameIndex+1).mTime;
        }
        mFrameIndex = mKeyFrames.get(mKeyFrameIndex).mFrameIndex;
        mAnimationSprite.setCurrentTileIndex(mFrameIndex);
    }

    private class UpdateHandler implements IUpdateHandler {

        public void onUpdate(float pSecondsElapsed) {
            mCurrentTime += pSecondsElapsed;
            if (mKeyFrameIndex < mKeyFrames.size()-1) {
                if (mCurrentTime >= mNextKeyFrameTime) {
                    nextKeyFrame();
                }
            }
            
            mPosX += mDx * (pSecondsElapsed / mDuration);
            mPosY += mDy * (pSecondsElapsed / mDuration);
            mAnimationSprite.setPosition(mPosX, mPosY);
            
            if (mCurrentTime > (mDuration)) {
                if (mFinishListener != null) mFinishListener.onModifierFinished(
                        null, mAnimationSprite);
                
                //mAnimationSprite.setVisible(false);
                mEngine.unregisterUpdateHandler(this);
                //mAnimationSprite.getParent().detachChild(mAnimationSprite);
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
    
    public TiledSprite getSprite() { return mAnimationSprite; }

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
    
}
