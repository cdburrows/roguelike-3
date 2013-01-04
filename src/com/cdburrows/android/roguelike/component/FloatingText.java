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

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.util.modifier.IModifier;

import com.cdburrows.android.roguelike.graphics.Graphics;

public class FloatingText {
    private static final float DURATION = 2.0f;

    private static final float SPEED = 160.0f;

    private ChangeableText mEntity;

    private Font mFont;

    private int mColor;

    private float mX;

    private float mY;

    private String mText;

    private float mAge;

    private boolean mVisible;

    public FloatingText(Font font, int color, float x, float y, String text) {
        mFont = font;
        mColor = color;
        mX = x;
        mY = y;
        mText = text;
        mAge = 0f;
        mVisible = false;

        mEntity = Graphics.createChangeableText(x, y, font, text, color);
        mEntity.setVisible(false);
    }

    public boolean update(float secondsElapsed) {
        if (mVisible) {
            mAge += secondsElapsed;
            mY -= SPEED * secondsElapsed;
            setPosition(mX, mY);

            if (mAge >= DURATION) {
                setVisible(false);
                return true;
            }
        }
        return false;
    }

    public void activate(int text, float x, float y) {
        activate(String.valueOf(text), x, y);
    }

    public void activate(String text, float x, float y) {
        mAge = 0f;
        mEntity.setText(text);
        setPosition(x - (mEntity.getWidth() / 2), y);
        mEntity.registerEntityModifier(new ScaleModifier(0.10f, 1f, 1.5f, scaleUpOver));
        setVisible(true);
    }

    public ChangeableText getEntity() {
        return mEntity;
    }

    public void setPosition(float x, float y) {
        mX = x;
        mY = y;
        mEntity.setPosition(x, y);
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
        mEntity.setVisible(visible);
    }

    final IEntityModifierListener scaleUpOver = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mEntity.registerEntityModifier(new ScaleModifier(0.20f, 1.5f, 1.0f));
        }
    };
}
