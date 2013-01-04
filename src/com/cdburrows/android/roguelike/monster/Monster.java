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

import java.util.Random;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.ease.EaseBackInOut;

public class Monster {
    private static final float MONSTER_FLASH_DURATION = 0.15f;

    private static final float JUMP_HEIGHT = 30;

    private static final float JUMP_SCALE = 1.4f;

    private MonsterState mMonsterState;

    private Sprite mSprite;

    private String mName = "";

    private int mLevel;

    private int mMaxHP;

    private int mCurHP;

    private int mAttack;

    private int mDefense;

    private float mSpeed;

    private int mXp;

    private boolean mDead;

    private float mOffY;

    private static Random sRand = new Random(System.currentTimeMillis());;

    public Monster(XmlDungeonMonsterTemplate template) {
        mSprite = template.getSprite();
        mName = template.getId();
        mLevel = (int)(sRand.nextFloat() * (template.getMaxLevel() - template.getMinLevel() + 1) + template
                .getMinLevel());
        mMaxHP = sRand.nextInt(template.getMaxHP() - template.getMinHP()) + template.getMinHP();
        mCurHP = mMaxHP;
        mAttack = sRand.nextInt(template.getMaxAttack() - template.getMinAttack())
                + template.getMinAttack();
        mDefense = sRand.nextInt(template.getMaxDefense() - template.getMinDefense())
                + template.getMinDefense();
        mSpeed = sRand.nextFloat() * (template.getMaxSpeed() - template.getMinSpeed())
                + template.getMinSpeed();
        mXp = template.getXp();
    }

    public Monster(Sprite sprite) {
        sRand = new Random(System.currentTimeMillis());
        mSprite = sprite;
    }

    public boolean targetable() {
        return (!mDead && mMonsterState == MonsterState.MONSTER_IDLE);
    }

    public int hit(int attack) {
        flash();

        int damage = attack + (int)(attack * sRand.nextFloat());
        damage -= mDefense * sRand.nextFloat();
        if (damage < 0)
            damage = 0;

        mCurHP -= damage;
        if (mCurHP <= 0) {
            mMonsterState = MonsterState.MONSTER_DEAD;
            mDead = true;
        }

        return damage;
    }

    public void fadeIn(float duration, final IEntityModifierListener listener) {
        mSprite.registerEntityModifier(new AlphaModifier(duration, 0f, 1f,
                new IEntityModifierListener() {
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                    }

                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        mMonsterState = MonsterState.MONSTER_IDLE;
                        listener.onModifierFinished(pModifier, pItem);
                    }
                }));
    }

    public void fadeOut(float duration, IEntityModifierListener listener) {
        mSprite.registerEntityModifier(new AlphaModifier(duration, 1f, 0f, listener));
    }

    public void flash() {
        mMonsterState = MonsterState.MONSTER_FLASH;
        mSprite.registerEntityModifier(new ColorModifier(MONSTER_FLASH_DURATION, mSprite.getRed(),
                1.5f, mSprite.getGreen(), 0.15f, mSprite.getBlue(), 0.15f, flashModifierListener));
    }

    public void jumpForward(final float duration) {
        jumpForward(duration, null);
    }

    public void jumpForward(final float duration, final IEntityModifierListener listener) {
        mSprite.registerEntityModifier(new MoveModifier(duration / 2, mSprite.getX(), mSprite
                .getX(), mSprite.getY(), mSprite.getY() + JUMP_HEIGHT,
                new IEntityModifierListener() {
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                        if (listener != null)
                            listener.onModifierStarted(pModifier, pItem);
                    }

                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        if (listener != null)
                            listener.onModifierFinished(pModifier, pItem);
                        pItem.registerEntityModifier(new MoveModifier(duration / 2, mSprite.getX(),
                                mSprite.getX(), mSprite.getY(), mSprite.getY() - JUMP_HEIGHT,
                                EaseBackInOut.getInstance()));

                        mSprite.registerEntityModifier(new ScaleModifier(duration / 2, JUMP_SCALE,
                                1.0f, EaseBackInOut.getInstance()));
                    }
                }, EaseBackInOut.getInstance()));

        mSprite.registerEntityModifier(new ScaleModifier(duration / 2, 1.0f, JUMP_SCALE,
                EaseBackInOut.getInstance()));
    }

    public void jumpBackward(final float duration, final IEntityModifierListener listener) {
        mSprite.registerEntityModifier(new MoveModifier(duration / 2, mSprite.getX(), mSprite
                .getX(), mSprite.getY(), mSprite.getY() - JUMP_HEIGHT,
                new IEntityModifierListener() {
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                        if (listener != null)
                            listener.onModifierStarted(pModifier, pItem);
                    }

                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        if (listener != null)
                            listener.onModifierFinished(pModifier, pItem);
                        pItem.registerEntityModifier(new MoveModifier(duration / 2, mSprite.getX(),
                                mSprite.getX(), mSprite.getY(), mSprite.getY() + JUMP_HEIGHT,
                                EaseBackInOut.getInstance()));

                        mSprite.registerEntityModifier(new ScaleModifier(duration / 2,
                                1 / JUMP_SCALE, 1.0f, EaseBackInOut.getInstance()));
                    }
                }, EaseBackInOut.getInstance()));

        mSprite.registerEntityModifier(new ScaleModifier(duration / 2, 1.0f, 1 / JUMP_SCALE,
                EaseBackInOut.getInstance()));
    }

    final IEntityModifierListener flashModifierListener = new IEntityModifierListener() {

        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            pItem.registerEntityModifier(new ColorModifier(MONSTER_FLASH_DURATION,
                    mSprite.getRed(), 1.0f, mSprite.getGreen(), 1.0f, mSprite.getBlue(), 1.0f,
                    new IEntityModifierListener() {
                        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                        }

                        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                            mMonsterState = MonsterState.MONSTER_IDLE;
                        }
                    }));
        }
    };

    public Sprite getSprite() {
        return mSprite;
    }

    public void setSprite(Sprite mMonsterSprite) {
        this.mSprite = mMonsterSprite;
    }

    public MonsterState getMonsterState() {
        return mMonsterState;
    }

    public void setMonsterState(MonsterState MonsterState) {
        this.mMonsterState = MonsterState;
    }

    public String getName() {
        return mName;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public int getMaxHP() {
        return mMaxHP;
    }

    public void setMaxHP(int MaxHP) {
        this.mMaxHP = MaxHP;
    }

    public int getCurHP() {
        return mCurHP;
    }

    public void setCurHP(int CurHP) {
        this.mCurHP = CurHP;
    }

    public int getAttack() {
        return mAttack;
    }

    public void setAttack(int Attack) {
        this.mAttack = Attack;
    }

    public int getDefense() {
        return mDefense;
    }

    public void setDefense(int Defense) {
        this.mDefense = Defense;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float Speed) {
        this.mSpeed = Speed;
    }

    public int getXp() {
        return mXp;
    }

    public boolean isDead() {
        return mDead;
    }

    public void setDead(boolean Dead) {
        this.mDead = Dead;
    }

    public float getOffY() {
        return mOffY;
    }

    public void setOffY(float offY) {
        mOffY = offY;
    }

    public static enum MonsterState {
        MONSTER_TRANSITION_IN, MONSTER_IDLE, MONSTER_ATTACK, MONSTER_DODGE, MONSTER_FLASH, MONSTER_DEAD, MONSTER_TRANSITION_OUT
    }

}
