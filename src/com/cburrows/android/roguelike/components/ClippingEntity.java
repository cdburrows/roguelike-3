package com.cburrows.android.roguelike.components;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.Entity;

import com.cdburrows.android.roguelike.base.RoguelikeActivity;


public class ClippingEntity extends Entity {
    
    protected float mWidth;
    protected float mHeight;
    private float mPosX;
    private float mPosY;
 
    public ClippingEntity(float pX, float pY, int pWidth, int pHeight) {
        super(pX, pY);
        mWidth = pWidth;
        mHeight = pHeight;
        mPosX = pX;
        mPosY = pY;
    }
 
    public ClippingEntity() {
        super();
    }
 
    @Override
    protected void doDraw(GL10 pGL, Camera pCamera) {
        super.doDraw(pGL, pCamera);
    }
 
    @Override
    protected void onManagedDraw(GL10 pGL, Camera pCamera) {
 
        pGL.glPushMatrix();
        {
            pGL.glEnable(GL10.GL_SCISSOR_TEST);
            pGL.glScissor(0+(int)mPosX, (int)(RoguelikeActivity.sCameraHeight - mHeight - mPosY), (int)mWidth, (int)mHeight);
 
            super.onManagedDraw(pGL, pCamera);
 
            pGL.glDisable(GL10.GL_SCISSOR_TEST);
           
        }
        pGL.glPopMatrix();
 
    }
}