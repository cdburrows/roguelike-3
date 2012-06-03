package com.cburrows.android.roguelike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.anddev.andengine.entity.sprite.AnimatedSprite;

public class Player {
    
    public static final float MOVE_SPEED = 768;
    public static final long[] ANIMATE_FRAME_DURATION = { 100, 100, 100, 100};
    public static final int[] ANIMATE_FACE_UP = { 0, 1, 2, 1 };
    public static final int[] ANIMATE_FACE_RIGHT = { 4, 5, 6, 5 };
    public static final int[] ANIMATE_FACE_DOWN = { 8, 9, 10, 9 };
    public static final int[] ANIMATE_FACE_LEFT = { 12, 13, 14, 13 };
    
    private AnimatedSprite mSprite;
    private GameMap mParentMap;
    
    private PlayerState mPlayerState;
    private float mPosX = 0;
    private float mPosY = 0;
    private int mTileWidth = 32;
    private int mTileHeight = 32;
    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;
    private boolean mMoving = false;
    private Direction mDirection;
    private float mMoveDistance = 0;
    
    private int mRoomX = 0;
    private int mRoomY = 0;
    
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
    
    private int mPotions;
    private Item mWeapon;
    private Item mArmour;
    private ArrayList<Item> mWeaponList;
    private ArrayList<Item> mArmourList;
    
    private static ArrayList<Skill> sSkills;

    public Player(AnimatedSprite sprite) {
        mScaleX = RoguelikeActivity.sScaleX;
        mScaleY = RoguelikeActivity.sScaleY;
        mPlayerState = PlayerState.IDLE;
        mDirection = Direction.DIRECTION_DOWN;
        mSprite = sprite;
        mSprite.setCurrentTileIndex(mDirection.getValue() * 4 + 1);
        
        mName = "Leal";
        mLevel = 1;
        mMaxHP = 100;
        mCurHP = 100;
        mNextXP = 20;
        mCurXP = 0;
        
        mBaseAttack = 4;
        mBaseDefense = 4;
        mBaseMagic = 4;
        
        mPotions = 10;
        mWeaponList = new ArrayList<Item>();
        mArmourList = new ArrayList<Item>();
        
        for (int i = 0; i < 3; i++) {
            mWeaponList.add(ItemFactory.createRandomWeapon(1));
            mArmourList.add(ItemFactory.createRandomArmour(1));
        }
        
        sSkills = new ArrayList<Skill>();
        SkillManager.setSkillList(sSkills);
        
        sortWeapons();
        sortArmour();
    }
    
    public Event update(float elapsed) {
        Event result = Event.EVENT_NO_EVENT;
        if (mMoving) {
            
            float move = MOVE_SPEED * elapsed;
            mMoveDistance -= move;
            
            if (mMoveDistance <= 0) {
                move += mMoveDistance;
                mMoveDistance = 0;
                mMoving = false;
                mSprite.stopAnimation(mDirection.getValue() * 4 + 1);
                result = Event.EVENT_NEW_ROOM;
                mPlayerState = PlayerState.IDLE;
            }
            
            switch(mDirection) {
                case DIRECTION_UP:
                    mPosY -= move;
                    break;
                case DIRECTION_RIGHT:
                    mPosX += move;
                    break;
                case DIRECTION_DOWN:
                    mPosY += move;
                    break;
                case DIRECTION_LEFT:
                    mPosX -= move;
                    break;
            }
            mSprite.setPosition(mPosX, mPosY);
        }
        return result;
    }
       
    public void move(Direction direction, float distance) {
        if (mPlayerState == PlayerState.IDLE) {
            face(direction);
            boolean canMove = mParentMap.getRoomAccess(mRoomX, mRoomY, direction);
            if (canMove) {
                mPlayerState = PlayerState.MOVING;
                mMoving = true;
                mDirection = direction;
                mMoveDistance = distance * mScaleX;
                
                switch (direction) {
                    case DIRECTION_UP:
                        mSprite.animate (ANIMATE_FRAME_DURATION, 0, 3, true);
                        mRoomY--;
                        break;
                    case DIRECTION_RIGHT:
                        mSprite.animate (ANIMATE_FRAME_DURATION, 4, 7, true);
                        mRoomX++;
                        break;
                    case DIRECTION_DOWN:
                        mSprite.animate (ANIMATE_FRAME_DURATION, 8, 11, true);
                        mRoomY++;
                        break;
                    case DIRECTION_LEFT:
                        mSprite.animate (ANIMATE_FRAME_DURATION, 12, 15, true);
                        mRoomX--;
                        break;
                }
            }
        }
    }
    
    public void levelUp() {
        mCurXP -= mNextXP;
        mNextXP *= 1.5f;
        mLevel++;
        
        mMaxHP *= 1.2f;
        //mCurHP = mMaxHP;
        
        mBaseAttack *= 1.4;
        mBaseDefense *= 1.4;
        mBaseMagic *= 1.4;
        
        //updateStats();
    }
    
    public void usePotion() {
        Random rand = new Random(System.currentTimeMillis());
        if (mPotions > 0) {
            increaseCurHP(rand.nextInt(25) + 50);
            mPotions--;
        }
    }
    
    public void updatePositionFromRoom() {
        mPosX = ((mRoomX * Dungeon.getRoomWidth()) + (Dungeon.getRoomWidth() / 2)) * (mTileWidth * mScaleX); 
        mPosY = ((mRoomY * Dungeon.getRoomHeight()) + (Dungeon.getRoomHeight() / 2)) * (mTileHeight * mScaleY);
        mSprite.setPosition(mPosX, mPosY);
    }
    
    public void face(Direction direction) {
        mSprite.setCurrentTileIndex(direction.getValue() * 4 + 1);
    }
    
    private void sortWeapons() {
        Collections.sort(mWeaponList, new Comparator<Item>() {
            public int compare(Item lhs, Item rhs) {
                if (lhs.getAttack() < rhs.getAttack()) return 1;
                if (lhs.getAttack() == rhs.getAttack()) return 0;
                return -1;
            }
            
        });
    }
    
    private void sortArmour() {
        Collections.sort(mArmourList, new Comparator<Item>() {
            public int compare(Item lhs, Item rhs) {
                if (lhs.getDefense() < rhs.getDefense()) return 1;
                if (lhs.getDefense() == rhs.getDefense()) return 0;
                return -1;
            }
            
        });
    }
    
    public PlayerState getPlayerState() { return mPlayerState; }
    public void setPlayerState(PlayerState playerState) { mPlayerState = playerState; }
    
    public void setAnimatedSprite(AnimatedSprite sprite) { mSprite = sprite; }
    public AnimatedSprite getAnimatedSprite() { return mSprite; }
    
    public void setParentMap(GameMap map) { 
        mParentMap = map;
        mTileWidth = map.getTileWidth();
        mTileHeight = map.getTileHeight();
    }
    public GameMap getParentMap() { return mParentMap; }

    public float getTileX() { return mPosX; }
    public void setTileX(int tileX) { mPosX = tileX; }

    public float getTileY() { return mPosY; }
    public void setTileY(int tileY) { mPosY = tileY; }
    
    public int getTileWidth() { return mTileWidth; }
    public void setTileWidth(int tileWidth) { this.mTileWidth = tileWidth; }

    public int getTileHeight() { return mTileHeight; }
    public void setTileHeight(int tileHeight) { this.mTileHeight = tileHeight; }

    public void setRoom(int x, int y) {
        mRoomX = x;
        mRoomY = y;
        setRoomX(x);
        setRoomY(y);
    }
    
    public int getRoomX() { return mRoomX; }
    public void setRoomX(int roomX) { this.mRoomX = roomX; updatePositionFromRoom(); }

    public int getRoomY() { return mRoomY; }
    public void setRoomY(int roomY) { this.mRoomY = roomY; updatePositionFromRoom(); }
    
  //********************************
    // Equipment accessors
    //********************************
    
    public void setNumPotions(int potions) { mPotions = potions; }
    public int getNumPotions() { return mPotions; }
    public void increasePotions(int i) {
        mPotions += i;
    }
    
    private void unequip(Item item) {
        mAttackBonus -= item.getAttack();
        mDefenseBonus -= item.getDefense();
        mMagicBonus -= item.getMagic();
        if (item.getItemType() == Item.ITEM_TYPE_WEAPON) {
            mWeaponList.add(item);
            sortWeapons();
        } else if (item.getItemType() == Item.ITEM_TYPE_ARMOUR) {
            mArmourList.add(item);
            sortArmour();
        }
        
    }
    
    public Item getWeapon() { return mWeapon; }
    
    public void equipWeapon(Item weapon) {
        if (mWeapon != null) unequip(mWeapon);
        
        mWeaponList.remove(weapon);
        mWeapon = weapon;
        mAttackBonus += weapon.getAttack();
        mDefenseBonus += weapon.getDefense();
        mMagicBonus += weapon.getMagic();
    }
    
    public ArrayList<Item> getWeaponList() { return mWeaponList; }
    

    public Item getArmour() { return mArmour; }
    
    public void equipArmour(Item armour) { 
        if (mArmour != null) unequip(mArmour);
        
        mArmourList.remove(armour);
        mArmour = armour;
        mAttackBonus += armour.getAttack();
        mDefenseBonus += armour.getDefense();
        mMagicBonus += armour.getMagic();
    }
    
    public ArrayList<Item> getArmourList() { return mArmourList; }
    
    public void addItem(Item item) {
        if (item.getItemType() == Item.ITEM_TYPE_WEAPON) {
            mWeaponList.add(item);
            sortWeapons();
        } else if (item.getItemType() == Item.ITEM_TYPE_ARMOUR) {
            mArmourList.add(item);
        }
    }

    //********************************
    // Stat accessors
    //********************************
    
    public String getName() {
        return mName;
    }
    public void setName(String mName) {
        this.mName = mName;
    }

    public int getLevel() {
        return mLevel;
    }
    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public int getMaxHP() {
        return mMaxHP;
    }
    public void setMaxHP(int mMaxHP) {
        this.mMaxHP = mMaxHP;
    }

    public int getCurHP() {
        return mCurHP;
    }
    public void setCurHP(int mCurHP) {
        this.mCurHP = mCurHP;
        if (mCurHP > mMaxHP) mCurHP = mMaxHP;
    }
    public void increaseCurHP(int value) {
        mCurHP += value;
        if (mCurHP > mMaxHP) mCurHP = mMaxHP;
    }

    public int getNextXP() {
        return mNextXP;
    }
    public void setNextXP(int mNextXP) {
        this.mNextXP = mNextXP;
    }
    
    public int getCurXP() {
        return mCurXP;
    }
    public void setCurXP(int mCurXP) {
        this.mCurXP = mCurXP;
    }
    public void increaseXP(int value) {
        mCurXP += value;
        if (mCurXP >= mNextXP) levelUp();           
    }

    public float getHPFraction() {
        return (float)mCurHP / mMaxHP;
    }

    public void decreaseHP(int damage) {
       mCurHP -= damage;
       if (mCurHP < 0) mCurHP = 0;
    }

    public float getXPFraction() {
        return (float)mCurXP / mNextXP;
    }

    public int getBaseAttack() {
        return mBaseAttack;
    }
    public void setBaseAttack(int mAttack) {
        this.mBaseAttack = mAttack;
    }
    public int getTotalAttack() {
        return mBaseAttack + mAttackBonus;
    }
    
    public int getBaseDefense() {
        return mBaseDefense;
    }
    public void setBaseDefense(int mDefense) {
        this.mBaseDefense = mDefense;
    }
    public int getTotalDefense() {
        return mBaseDefense + mDefenseBonus;
    }

    public int getBaseMagic() {
        return mBaseMagic;
    }
    public void setBaseMagic(int mMagic) {
        this.mBaseMagic = mMagic;
    }
    public int getTotalMagic() {
        return mBaseMagic + mMagicBonus;
    }
    
    public ArrayList<Skill> getSkills() { return sSkills; }
    public void setSkills(ArrayList<Skill> skills) {
        sSkills = skills;
        SkillManager.setSkillList(skills);
    }
}
    