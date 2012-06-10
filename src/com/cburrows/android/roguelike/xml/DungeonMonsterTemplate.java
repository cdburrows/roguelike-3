package com.cburrows.android.roguelike.xml;

import org.anddev.andengine.entity.sprite.Sprite;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.cdburrows.android.roguelike.base.Graphics;


@Root(name="monster")
public class DungeonMonsterTemplate {
    @Attribute(name="id")
    public String mName;
    
    @Attribute(name="rate")
    public float mRate;
    
    @Attribute(name="sprite_path")
    private String mSpritePath;
    
    @Attribute(name="offY", required=false)
    public float mOffY = 0f;
    
    @Element(name="min_level")
    private int mMinLevel;
    
    @Element(name="max_level")
    private int mMaxLevel;
    
    @Element(name="min_hp")
    private int mMinHP;
    
    @Element(name="max_hp")
    private int mMaxHP;
    
    @Element(name="min_attack")
    private int mMinAttack;
    
    @Element(name="max_attack")
    private int mMaxAttack;
    
    @Element(name="min_defense")
    private int mMinDefense;
    
    @Element(name="max_defense")
    private int mMaxDefense;
    
    @Element(name="min_speed")
    private float mMinSpeed;
    
    @Element(name="max_speed")
    private float mMaxSpeed;
    
    @Element(name="xp")
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
    
    public int getXp() { return mXp; }
}
