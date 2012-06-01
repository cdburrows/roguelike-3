package com.cburrows.android.roguelike;

import java.util.Random;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.util.modifier.ease.EaseBackInOut;
import org.anddev.andengine.util.modifier.IModifier;

import com.cburrows.android.roguelike.xml.DungeonMonsterTemplate;

public class Monster {
    private static final float MONSTER_FLASH_DURATION = 0.15f;
    private static final float JUMP_HEIGHT = 24;
    private static final float JUMP_SCALE = 1.2f;
    
    private MonsterState mMonsterState;
    private Sprite mSprite;
    
    private String mName = "";
    private int mLevel;
    private int mMaxHP;
    private int mCurHP;
    private int mAttack;
    private int mDefense;
    private float mSpeed;
    private boolean mDead;
    private float mOffY;        
    private static Random sRand = new Random(System.currentTimeMillis());;
    
    public Monster(DungeonMonsterTemplate template) {
        mSprite = template.getSprite();
        mName = template.getId();
        mLevel = sRand.nextInt(template.getMaxLevel() - template.getMinLevel()) + template.getMinLevel();
        mMaxHP = sRand.nextInt(template.getMaxHP() - template.getMinHP()) + template.getMinHP();
        mAttack = sRand.nextInt(template.getMaxAttack() - template.getMinAttack()) + template.getMinAttack();
        mDefense = sRand.nextInt(template.getMaxDefense() - template.getMinDefense()) + template.getMinDefense();
        mSpeed = sRand.nextFloat() * (template.getMaxSpeed() - template.getMinSpeed()) + template.getMinSpeed();
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
    
    public void jumpForward(final float duration) { jumpForward(duration, null); }
    public void jumpForward(final float duration, final IEntityModifierListener listener) {
        mSprite.registerEntityModifier(new MoveModifier(duration / 2, 
                mSprite.getX(), mSprite.getX(), mSprite.getY(), mSprite.getY() + JUMP_HEIGHT, 
                new IEntityModifierListener() {
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                        if (listener != null) listener.onModifierStarted(pModifier, pItem);
                    }
                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        if (listener != null) listener.onModifierFinished(pModifier, pItem);
                        pItem.registerEntityModifier(new MoveModifier(duration / 2, 
                                mSprite.getX(), mSprite.getX(), 
                                mSprite.getY(), mSprite.getY() - JUMP_HEIGHT, EaseBackInOut.getInstance()));
                        
                        mSprite.registerEntityModifier(new ScaleModifier(duration/2, JUMP_SCALE, 
                                1.0f, EaseBackInOut.getInstance()));
                    }
                }, EaseBackInOut.getInstance()));
        
        mSprite.registerEntityModifier(new ScaleModifier(duration/2, 1.0f, 
                JUMP_SCALE, EaseBackInOut.getInstance()));
    }

    public void jumpBackward(final float duration) {
        mSprite.registerEntityModifier(new MoveModifier(duration / 2, 
                mSprite.getX(), mSprite.getX(), mSprite.getY(), mSprite.getY() - JUMP_HEIGHT, 
                new IEntityModifierListener() {
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        pItem.registerEntityModifier(new MoveModifier(duration / 2, 
                                mSprite.getX(), mSprite.getX(), 
                                mSprite.getY(), mSprite.getY() + JUMP_HEIGHT, EaseBackInOut.getInstance()));
                        
                        mSprite.registerEntityModifier(new ScaleModifier(duration / 2, 1 / JUMP_SCALE, 
                                1.0f, EaseBackInOut.getInstance()));
                    }
                }, EaseBackInOut.getInstance()));
        
        mSprite.registerEntityModifier(new ScaleModifier(duration/2, 1.0f, 
                1 / JUMP_SCALE, EaseBackInOut.getInstance()));
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
    
    /*
    public static Monster inflate(String path) {
        
        Serializer serializer = new Persister();
        try {
            Monster monster = serializer.read(Monster.class, path);
            Graphics.beginLoad("gfx/monsters/", 256, 256);
            monster.setSprite(Graphics.createSprite(monster.getSpritePath()));
            Graphics.endLoad("Monster loaded");
            return monster;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    */

    public static enum MonsterState {
        MONSTER_TRANSITION_IN, MONSTER_IDLE, MONSTER_ATTACK, MONSTER_DODGE, 
        MONSTER_FLASH, MONSTER_DEAD, MONSTER_TRANSITION_OUT
    }

}
