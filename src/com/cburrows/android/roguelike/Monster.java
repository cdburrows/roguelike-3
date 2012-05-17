package com.cburrows.android.roguelike;

import java.util.Random;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.util.modifier.IModifier;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.util.Log;

@Root(name="monster")
public class Monster {
    private static final float MONSTER_FLASH_DURATION = 0.15f;
    
    private MonsterState mMonsterState;
    
    @Element(name="sprite")
    private Sprite mSprite;
    
    @Element(name="hp")
    private int mMaxHP;
    
    private int mCurHP;
    
    @Element(name="attack")
    private int mAttack;
    
    @Element(name="defense")
    private int mDefense;
    
    @Element(name="speed")
    private float mSpeed;
    
    private boolean mDead;
    
    private Random rand;
    
    public Monster() {
         rand = new Random(System.currentTimeMillis());
    }
    
    public boolean targetable() {
        return (!mDead && mMonsterState == MonsterState.MONSTER_IDLE);
    }
    
    public int hit(int attack) {
        flash();
        
        int damage = attack + (int)(attack * rand.nextFloat());
        damage -= mDefense * rand.nextFloat();
        if (damage < 0) damage = 0;
        
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
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                mMonsterState = MonsterState.MONSTER_IDLE;
                listener.onModifierFinished(pModifier, pItem);
            } }));
    }
    
    public void fadeOut(float duration, IEntityModifierListener listener) {
        mSprite.registerEntityModifier(new AlphaModifier(duration, 1f, 0f, listener));
    }
    
    public void flash() {
        mMonsterState = MonsterState.MONSTER_FLASH;
        mSprite.registerEntityModifier(new ColorModifier(MONSTER_FLASH_DURATION,
                mSprite.getRed(), 1.5f,
                mSprite.getGreen(), 0.15f,
                mSprite.getBlue(), 0.15f, flashModifierListener) );
            
    }

    final IEntityModifierListener flashModifierListener = new IEntityModifierListener() {
        
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }
        
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            pItem.registerEntityModifier(new ColorModifier(MONSTER_FLASH_DURATION,
                    mSprite.getRed(), 1.0f,
                    mSprite.getGreen(), 1.0f,
                    mSprite.getBlue(), 1.0f, new IEntityModifierListener() {
                        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
                        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                            mMonsterState = MonsterState.MONSTER_IDLE;
                        }
                    } ) );
        }
    };
    
    public Sprite getSprite() {
        return mSprite;
    }
    public void setSprite(Sprite Sprite) {
        this.mSprite = Sprite;
    }
    
    public MonsterState getMonsterState() {
        return mMonsterState;
    }
    public void setMonsterState(MonsterState MonsterState) {
        this.mMonsterState = MonsterState;
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
        this.mAttack = mAttack;
    }
    public int getDefense() {
        return mDefense;
    }
    public void setDefense(int Defense) {
        this.mDefense = mDefense;
    }

    public float getSpeed() {
        return mSpeed;
    }
    public void setSpeed(float Speed) {
        this.mSpeed = Speed;
    }
    
    public boolean isDead() {
        return mDead;
    }
    public void setDead(boolean Dead) {
        this.mDead = Dead;
    }

    public enum MonsterState {
        MONSTER_TRANSITION_IN, MONSTER_IDLE, MONSTER_ATTACK, MONSTER_FLASH, 
        MONSTER_DEAD, MONSTER_TRANSITION_OUT
    }

}
