package com.cburrows.android.roguelike;

import org.anddev.andengine.entity.sprite.Sprite;

public class Item {
    private Sprite mSprite;

    private String mName;
    private float mX;
    private float mY;
    
    // Stats
    private int mAttack;
    private int mDefense;
    private int mMagic;
    
    
    public Item(Sprite sprite, String name, int itemType, int attack, int defense, int magic) {
        mSprite = sprite;
        mName = name;
        mAttack = attack;
        mDefense = defense;
        mMagic = magic;
    }
    
    public Sprite getSprite() { return mSprite; }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }
    
    public void setPosition(int x, int y) {
        mX = x;
        mY = y;
        mSprite.setPosition(mX, mY);
    }

    public float getX() {
        return mX;
    }
    public void setX(float mX) {
        this.mX = mX;
        mSprite.setPosition(mX, mY);
    }

    public float getY() {
        return mY;
    }
    public void setY(float mY) {
        this.mY = mY;
        mSprite.setPosition(mX, mY);
    }

    public int getAttack() {
        return mAttack;
    }
    public void setAttack(int mAttack) {
        this.mAttack = mAttack;
    }

    public int getDefense() {
        return mDefense;
    }
    public void setDefense(int mDefense) {
        this.mDefense = mDefense;
    }

    public int getMagic() {
        return mMagic;
    }
    public void setMagic(int mMagic) {
        this.mMagic = mMagic;
    }
        
}
