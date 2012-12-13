package com.cdburrows.android.roguelike.component;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;

import android.util.Log;

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
