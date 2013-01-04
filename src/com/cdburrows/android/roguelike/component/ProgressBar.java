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

import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.sprite.Sprite;

import android.graphics.Color;

import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.graphics.Graphics;

public class ProgressBar {

    private static final int TEXTURE_ATLAS_WIDTH = 128;

    private static final int TEXTURE_ATLAS_HEIGHT = 32;

    private Entity mSprite;

    private Sprite mBackground;

    private Sprite mFill;

    private float mX;

    private float mY;

    private int mWidth;

    private int mHeight;

    private int mColor;

    private float mAlpha;

    private int mMaxValue;

    private int mCurValue;

    public ProgressBar(float x, float y, int width, int height, int color, float alpha, int maxValue) {
        this(x, y, width, height, color, alpha, maxValue, maxValue);
    }

    public ProgressBar(float x, float y, int width, int height, int color, float alpha,
            int curValue, int maxValue) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mColor = color;
        mAlpha = alpha;
        mMaxValue = maxValue;
        mCurValue = curValue;

        Graphics.beginLoad("gfx/panels/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);
        mBackground = Graphics.createSprite("progress_bar.png", 0, 0, mAlpha);
        mFill = Graphics.createSprite("progress_bar_fill.png", 0, 0, mAlpha);
        Graphics.endLoad("Progress bar");

        mBackground.setSize(mWidth, mHeight);
        // mBackground.setScaleCenter(0f, 0f);
        // mBackground.setScale(RoguelikeActivity.sScaleX,
        // RoguelikeActivity.sScaleY);

        mFill.setSize(mWidth, mHeight);
        mFill.setScaleCenter(0f, 0f);
        // mFill.setScale(RoguelikeActivity.sScaleX, RoguelikeActivity.sScaleY);
        mFill.setColor(Color.red(mColor), Color.green(mColor), Color.blue(mColor));

        mSprite = new Entity(0, 0);
        mSprite.attachChild(mBackground);
        mSprite.attachChild(mFill);
        mSprite.setScaleCenter(0, 0);
        mSprite.setScale(RoguelikeActivity.sScaleX, RoguelikeActivity.sScaleY);
        mSprite.setPosition(mX, mY);
    }

    public Entity getEntity() {
        return mSprite;
    }

    private void adjustFill() {
        mFill.setWidth(((float)mBackground.getWidth()) * ((float)mCurValue / mMaxValue));
    }

    public void setPosition(float x, float y) {
        mX = x;
        mY = y;
        mSprite.setPosition(x, y);
    }

    public void setMaxValue(int max) {
        mMaxValue = max;
        adjustFill();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setCurValue(int cur) {
        mCurValue = cur;
        adjustFill();
    }

    public int getCurValue() {
        return mCurValue;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
        for (int i = 0; i < mSprite.getChildCount(); i++) {
            mSprite.getChild(i).setAlpha(alpha);
        }
    }

    public int getWidth() {
        return (int)(mWidth * RoguelikeActivity.sScaleX);
    }

    public int getHeight() {
        return (int)(mHeight * RoguelikeActivity.sScaleY);
    }

    public void registerEntityModifier(IEntityModifier modifier) {
        for (int i = 0; i < mSprite.getChildCount(); i++) {
            mSprite.getChild(i).registerEntityModifier(modifier);
        }
    }

}
