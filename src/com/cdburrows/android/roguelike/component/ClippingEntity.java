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

package com.cdburrows.android.roguelike.component;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.Entity;

import com.cdburrows.android.roguelike.RoguelikeActivity;

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
            pGL.glScissor(0 + (int)mPosX, (int)(RoguelikeActivity.sCameraHeight - mHeight - mPosY),
                    (int)mWidth, (int)mHeight);

            super.onManagedDraw(pGL, pCamera);

            pGL.glDisable(GL10.GL_SCISSOR_TEST);

        }
        pGL.glPopMatrix();

    }
}
