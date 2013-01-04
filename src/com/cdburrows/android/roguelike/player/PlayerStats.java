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

package com.cdburrows.android.roguelike.player;

import com.cdburrows.android.roguelike.item.Item;

public class PlayerStats {

    // ===========================================================
    // Constants
    // ===========================================================
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private String mName;

    private int mLevel;

    private int mMaxHP;

    private int mCurHP;

    private int mNextXP;

    private int mCurXP;

    private int mBaseAttack;

    private int mAttackBonus;

    private int mBaseDefense;

    private int mDefenseBonus;

    private int mBaseMagic;

    private int mMagicBonus;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public PlayerStats() {
        mName = "Leal";
        mLevel = 1;
        mMaxHP = 100;
        mCurHP = 100;
        mNextXP = 20;
        mCurXP = 0;

        mBaseAttack = 4;
        mAttackBonus = 0;
        
        mBaseDefense = 4;
        mDefenseBonus = 0;
        
        mBaseMagic = 4;
        mMagicBonus = 0;
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public String getName() { return mName; }
    public void setName(String name) { this.mName = name; }

    public int getLevel() { return mLevel; }
    public void setLevel(int level) { this.mLevel = level; }

    public int getMaxHP() { return mMaxHP; }
    public void setMaxHP(int maxHP) { this.mMaxHP = maxHP; }

    public int getCurHP() { return mCurHP; }
    public void setCurHP(int curHP) {
        this.mCurHP = curHP;
        if (curHP > mMaxHP)
            curHP = mMaxHP;
    }
    
    public float getHPFraction() {
        return (float)mCurHP / mMaxHP;
    }
    
    public void increaseCurHP(int value) {
        mCurHP += value;
        if (mCurHP > mMaxHP)
            mCurHP = mMaxHP;
    }
    
    public void decreaseCurHP(int damage) {
        mCurHP -= damage;
        if (mCurHP < 0)
            mCurHP = 0;
    }

    public int getNextXP() { return mNextXP; }
    public void setNextXP(int nextXP) { this.mNextXP = nextXP; }

    public int getCurXP() { return mCurXP; }
    public void setCurXP(int curXP) { this.mCurXP = curXP; }

    public float getXPFraction() {
        return (float)mCurXP / mNextXP;
    }
    
    public void increaseXP(int value) {
        mCurXP += value;
        if (mCurXP >= mNextXP) {
            levelUp();
        }
    }

    public int getBaseAttack() { return mBaseAttack; }
    public void setBaseAttack(int attack) { this.mBaseAttack = attack; }
    public int getTotalAttack() { return mBaseAttack + mAttackBonus; }

    public int getBaseDefense() { return mBaseDefense; }
    public void setBaseDefense(int defense) { this.mBaseDefense = defense; }
    public int getTotalDefense() { return mBaseDefense + mDefenseBonus; }

    public int getBaseMagic() { return mBaseMagic; }
    public void setBaseMagic(int magic) { this.mBaseMagic = magic; }
    public int getTotalMagic() { return mBaseMagic + mMagicBonus; }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    /**
     * Levels up the player
     */
    public void levelUp() {
        mCurXP -= mNextXP;
        mNextXP *= 1.5f;
        mLevel++;

        mMaxHP *= 1.2f;
        // mCurHP = mMaxHP;

        mBaseAttack *= 1.4;
        mBaseDefense *= 1.4;
        mBaseMagic *= 1.4;
    }
    
    /**
     * Adds the stat bonuses provided by passed item
     * 
     * @param item the item to equip
     */
    public void equip(Item item) {
        mAttackBonus += item.getAttack();
        mDefenseBonus += item.getDefense();
        mMagicBonus += item.getMagic();
    }

    /**
     * Removes the stat bonuses provided by passed item
     * 
     * @param item the item to unequip
     */
    public void unequip(Item item) {
        mAttackBonus -= item.getAttack();
        mDefenseBonus -= item.getDefense();
        mMagicBonus -= item.getMagic();
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
}
