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

package com.cdburrows.android.roguelike.item;

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

    public Item(TiledSprite sprite, String name, int fontColor, int imageIndex, int itemType,
            int attack, int defense, int magic) {
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

    public TiledSprite getSprite() {
        return mSprite;
    }

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
        TiledSprite sprite = ItemFactory.createItem(mName, mFontColor, mImageIndex, mItemType,
                mAttack, mDefense, mMagic).getSprite();
        return sprite;
    }

}
