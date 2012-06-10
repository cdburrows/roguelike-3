package com.cburrows.android.roguelike.components;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.util.modifier.IModifier;

import com.cdburrows.android.roguelike.base.Graphics;


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
    
    public void activate(int text, float x, float y) { activate(String.valueOf(text), x, y); }
    
    public void activate(String text, float x, float y) {
        mAge = 0f;
        mEntity.setText(text);
        setPosition(x - (mEntity.getWidth() / 2), y);
        mEntity.registerEntityModifier(new ScaleModifier(0.10f, 1f, 1.5f, scaleUpOver));
        setVisible(true);
    }
    
    public ChangeableText getEntity() { return mEntity; }
    
    public void setPosition(float x, float y) {
        mX = x;
        mY = y;
        mEntity.setPosition(x, y);
    }
    
    public void setVisible(boolean visible) {
        mVisible = visible;
        mEntity.setVisible(visible);
    }
    
    final IEntityModifierListener scaleUpOver  = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {    }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mEntity.registerEntityModifier(new ScaleModifier(0.20f, 1.5f, 1.0f));
        }
    };
}
