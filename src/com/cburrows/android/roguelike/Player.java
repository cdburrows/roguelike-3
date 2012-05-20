package com.cburrows.android.roguelike;

import java.util.Random;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.util.Log;

public class Player {
    
    public static final float MOVE_SPEED = 768;
    public static final long[] ANIMATE_FRAME_DURATION = { 100, 100, 100, 100};
    public static final int[] ANIMATE_FACE_UP = { 0, 1, 2, 1 };
    public static final int[] ANIMATE_FACE_RIGHT = { 4, 5, 6, 5 };
    public static final int[] ANIMATE_FACE_DOWN = { 8, 9, 10, 9 };
    public static final int[] ANIMATE_FACE_LEFT = { 12, 13, 14, 13 };
    
    private TiledTextureRegion mPlayerTextureRegion;
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
    
    private int mAttack;
    private int mDefense;
    private int mMagic;
    
    private int mPotions;
    
    private Item mWeapon;
    private Item mArmour;

    public Player(TiledTextureRegion textureRegion, float scaleX, float scaleY) {
        mScaleX = scaleX;
        mScaleY = scaleY;
        mPlayerState = PlayerState.IDLE;
        mPlayerTextureRegion = textureRegion;
        mSprite = new AnimatedSprite(mPosX, mPosY, textureRegion);
        mSprite.setScale(scaleX, scaleY);
        mDirection = Direction.DIRECTION_DOWN;
        mSprite.setCurrentTileIndex(Direction.DIRECTION_DOWN.getValue() * 4 + 1);
        
        mName = "Leal";
        mLevel = 1;
        mMaxHP = 100;
        mCurHP = 100;
        mNextXP = 20;
        mCurXP = 0;
        
        mAttack = 8;
        mDefense = 4;
        mMagic = 4;
        
        mPotions = 10;
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
        
        mAttack *= 1.4;
        mDefense *= 1.4;
        mMagic *= 1.4;
    }
    
    public void usePotion() {
        Random rand = new Random(System.currentTimeMillis());
        if (mPotions > 0) {
            increaseCurHP(rand.nextInt(25) + 50);
            mPotions--;
        }
    }
    
    public void updatePositionFromRoom() {
        mPosX = ((mRoomX * mParentMap.ROOM_WIDTH) + (mParentMap.ROOM_WIDTH / 2)) * (mTileWidth); // / (mScaleX * 4));
        mPosY = ((mRoomY * mParentMap.ROOM_HEIGHT) + (mParentMap.ROOM_HEIGHT / 2)) * (mTileHeight); // / (mScaleY * 4));
        mSprite.setPosition(mPosX, mPosY);
        Log.d("SPRITE", "X: " + mPosX);
        Log.d("SPRITE", "Y: " + mPosY);
    }
    
    public void face(Direction direction) {
        mSprite.setCurrentTileIndex(direction.getValue() * 4 + 1);
    }
    
    public void equipWeapon(Item weapon) { mWeapon = weapon; }
    
    public void equipArmour(Item armour) { mArmour = armour; }
    
    public PlayerState getPlayerState() { return mPlayerState; }
    public void setPlayerState(PlayerState playerState) { mPlayerState = playerState; }
    
    public TiledTextureRegion getPlayerTextureRegion() { return mPlayerTextureRegion; }
    public void setPlayerTextureRegion(TiledTextureRegion playerTextureRegion) { mPlayerTextureRegion = playerTextureRegion; }
    
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
    public void setTileWidth(int tileWidth) { this.mTileWidth = mTileWidth; }

    public int getTileHeight() { return mTileHeight; }
    public void setTileHeight(int tileHeight) { this.mTileHeight = mTileHeight; }

    public void setRoom(int x, int y) {
        mRoomX = x;
        mRoomY = y;
        setRoomX(x);
        setRoomY(y);
    }
    
    public int getRoomX() { return mRoomX; }
    public void setRoomX(int roomX) { this.mRoomX = mRoomX; updatePositionFromRoom(); }

    public int getRoomY() { return mRoomY; }
    public void setRoomY(int roomY) { this.mRoomY = mRoomY; updatePositionFromRoom(); }

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
    
    public void setNumPotions(int potions) { mPotions = potions; }
    public int getNumPotions() { return mPotions; }
    
    public Item getWeapon() { return mWeapon; }
    
    public Item getArmour() { return mArmour; }

}
    