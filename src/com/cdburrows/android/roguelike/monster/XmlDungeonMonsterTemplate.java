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

package com.cdburrows.android.roguelike.monster;

import org.anddev.andengine.entity.sprite.Sprite;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "monster")
public class XmlDungeonMonsterTemplate {
    @Attribute(name = "id")
    public String mName;

    @Attribute(name = "rate")
    public float mRate;

    @Attribute(name = "sprite_path")
    private String mSpritePath;

    @Attribute(name = "offY", required = false)
    public float mOffY = 0f;

    @Element(name = "min_level")
    private int mMinLevel;

    @Element(name = "max_level")
    private int mMaxLevel;

    @Element(name = "min_hp")
    private int mMinHP;

    @Element(name = "max_hp")
    private int mMaxHP;

    @Element(name = "min_attack")
    private int mMinAttack;

    @Element(name = "max_attack")
    private int mMaxAttack;

    @Element(name = "min_defense")
    private int mMinDefense;

    @Element(name = "max_defense")
    private int mMaxDefense;

    @Element(name = "min_speed")
    private float mMinSpeed;

    @Element(name = "max_speed")
    private float mMaxSpeed;

    @Element(name = "xp")
    private int mXp;

    private Sprite mSprite;

    public Sprite getSprite() {
        return mSprite;
    }

    public void setSprite(Sprite sprite) {
        this.mSprite = sprite;
    }

    public String getId() {
        return mName;
    }

    public void setId(String id) {
        mName = id;
    }

    public float getRate() {
        return mRate;
    }

    public String getSpritePath() {
        return mSpritePath;
    }

    public float getOffY() {
        return mOffY;
    }

    public void setSpritePath(String mSpritePath) {
        this.mSpritePath = mSpritePath;
    }

    public int getMinLevel() {
        return mMinLevel;
    }

    public void setMinLevel(int mMinLevel) {
        this.mMinLevel = mMinLevel;
    }

    public int getMaxLevel() {
        return mMaxLevel;
    }

    public void setMaxLevel(int mMaxLevel) {
        this.mMaxLevel = mMaxLevel;
    }

    public int getMinHP() {
        return mMinHP;
    }

    public void setMinHP(int mMinHP) {
        this.mMinHP = mMinHP;
    }

    public int getMaxHP() {
        return mMaxHP;
    }

    public void setMaxHP(int mMaxHP) {
        this.mMaxHP = mMaxHP;
    }

    public int getMinAttack() {
        return mMinAttack;
    }

    public void setMinAttack(int mMinAttack) {
        this.mMinAttack = mMinAttack;
    }

    public int getMaxAttack() {
        return mMaxAttack;
    }

    public void setMaxAttack(int mMaxAttack) {
        this.mMaxAttack = mMaxAttack;
    }

    public int getMinDefense() {
        return mMinDefense;
    }

    public void setMinDefense(int mMinDefense) {
        this.mMinDefense = mMinDefense;
    }

    public int getMaxDefense() {
        return mMaxDefense;
    }

    public void setMaxDefense(int mMaxDefense) {
        this.mMaxDefense = mMaxDefense;
    }

    public float getMinSpeed() {
        return mMinSpeed;
    }

    public void setMinSpeed(float mMinSpeed) {
        this.mMinSpeed = mMinSpeed;
    }

    public float getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(float mMaxSpeed) {
        this.mMaxSpeed = mMaxSpeed;
    }

    public int getXp() {
        return mXp;
    }
}
