package com.cburrows.android.roguelike;

import org.anddev.andengine.entity.sprite.TiledSprite;

public class Item {
    public static int ITEM_TYPE_WEAPON = 0;
    public static int ITEM_TYPE_ARMOUR = 1;
    
    private TiledSprite mSprite;

    private String mName;
    private float mX;
    private float mY;
    
    // Stats
    private int mAttack;
    private int mDefense;
    private int mMagic;
    
    private int mItemType;
    private int mFontColor;
    private int mImageIndex;
    
    
    public Item(TiledSprite sprite, String name, int fontColor, int imageIndex, int itemType, int attack, int defense, int magic) {
        mSprite = sprite;
        mName = name;
        mFontColor = fontColor;
        mImageIndex = imageIndex;
        mItemType = itemType;
        mAttack = attack;
        mDefense = defense;
        mMagic = magic;
    }
    
    public void handleTouchDown() {
        mSprite.setCurrentTileIndex(1);
    }
    
    public void handleTouchUp() {
        mSprite.setCurrentTileIndex(0);
    }
    
    public TiledSprite getSprite() { return mSprite; }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }
    
    public void setPosition(float x, float y) {
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
    
    public int getItemType() {
        return mItemType;
    }
    public void ItemType(int itemType) {
        this.mItemType = itemType;
    }
    
    public TiledSprite copySprite() {
        return ItemFactory.createItem(mName, mFontColor, mImageIndex, mItemType, mAttack, mDefense, mMagic).getSprite();
    }
        
}
