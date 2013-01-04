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

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;

import com.cdburrows.android.roguelike.graphics.Graphics;

public class TextPanel {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private float mX;

    private float mY;

    private float mWidth;

    private float mHeight;

    private Sprite mSprite;

    private Text mText;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TextPanel(float x, float y, float width, float height, String text) {
        mX = x - (width / 2);
        mY = y;
        mWidth = width;
        mHeight = height;
        mText = Graphics.createText(0, 0, Graphics.Font, text);
        buildSprite();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public Sprite getSprite() {
        return mSprite;
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void buildSprite() {
        Graphics.beginLoad("gfx/panels/", 128, 32);
        mSprite = Graphics.createSprite("popup.png", mX, mY);
        mSprite.setSize(mWidth, mHeight);
        Graphics.endLoad("Panel");
        mText.setPosition((mSprite.getWidth() / 2) - (mText.getWidth() / 2),
                (mSprite.getHeight() / 2) - (mText.getHeight() / 2));
        mSprite.attachChild(mText);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
