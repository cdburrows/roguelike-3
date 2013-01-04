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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.anddev.andengine.entity.sprite.AnimatedSprite;

import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.Event;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.item.Item;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.map.GameMap;
import com.cdburrows.android.roguelike.map.Minimap;
import com.cdburrows.android.roguelike.skill.Skill;
import com.cdburrows.android.roguelike.skill.SkillDirection;
import com.cdburrows.android.roguelike.skill.SkillManager;

/**
 * The player character data
 * 
 * @author cburrows
 *
 */
public class Player {

    // ===========================================================
    // Constants
    // ===========================================================
    
    public static final float MOVE_SPEED = 768;

    public static final long[] ANIMATE_FRAME_DURATION = {
            100, 100, 100, 100
    };

    public static final int[] ANIMATE_FACE_UP = {
            0, 1, 2, 1
    };

    public static final int[] ANIMATE_FACE_RIGHT = {
            4, 5, 6, 5
    };

    public static final int[] ANIMATE_FACE_DOWN = {
            8, 9, 10, 9
    };

    public static final int[] ANIMATE_FACE_LEFT = {
            12, 13, 14, 13
    };
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    public final PlayerStats mStats = new PlayerStats();
    
    public final PlayerInventory mInventory = new PlayerInventory();
    
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

    private static ArrayList<Skill> sSkills;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    /**
     * Constructs the Player object by initializing stats to default values,
     * adding default equipment, and setting up sprite and skills.
     * 
     * @param sprite the graphical representation of the player
     */
    public Player(AnimatedSprite sprite) {
        mScaleX = RoguelikeActivity.sScaleX;
        mScaleY = RoguelikeActivity.sScaleY;
        mPlayerState = PlayerState.IDLE;
        mDirection = Direction.DIRECTION_DOWN;
        mSprite = sprite;
        mSprite.setCurrentTileIndex(mDirection.getValue() * 4 + 1);

        equipWeapon(ItemFactory.createRandomWeapon(6));
        equipArmour(ItemFactory.createRandomArmour(6));

        sSkills = new ArrayList<Skill>();
        SkillManager.setSkillList(sSkills);

        loadSkills();
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public PlayerState getPlayerState() {
        return mPlayerState;
    }

    public void setPlayerState(PlayerState playerState) {
        mPlayerState = playerState;
    }

    public void setAnimatedSprite(AnimatedSprite sprite) {
        mSprite = sprite;
    }

    public AnimatedSprite getAnimatedSprite() {
        return mSprite;
    }

    public void setParentMap(GameMap map) {
        mParentMap = map;
        mTileWidth = map.getTileWidth();
        mTileHeight = map.getTileHeight();
    }

    public GameMap getParentMap() {
        return mParentMap;
    }

    public float getTileX() {
        return mPosX;
    }

    public void setTileX(int tileX) {
        mPosX = tileX;
    }

    public float getTileY() {
        return mPosY;
    }

    public void setTileY(int tileY) {
        mPosY = tileY;
    }

    public int getTileWidth() {
        return mTileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.mTileWidth = tileWidth;
    }

    public int getTileHeight() {
        return mTileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.mTileHeight = tileHeight;
    }

    /**
     * Teleports the player to a room. No chest will be found
     * in the resulting room.
     * 
     * @param x the column value of the room
     * @param y the row value of the room
     */
    public void setRoom(int x, int y) {
        // mParentMap.setRoomState(mRoomX, mRoomY, RoomState.ROOM_VISITED);
        mRoomX = x;
        mRoomY = y;
        setRoomX(x);
        setRoomY(y);
        mParentMap.occupyRoom(x, y);
        mParentMap.setChest(x, y, false);
    }

    public int getRoomX() {
        return mRoomX;
    }

    public void setRoomX(int roomX) {
        this.mRoomX = roomX;
        updatePositionFromRoom();
    }

    public int getRoomY() {
        return mRoomY;
    }

    public void setRoomY(int roomY) {
        this.mRoomY = roomY;
        updatePositionFromRoom();
    }

    public ArrayList<Skill> getSkills() {
        return sSkills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        sSkills = skills;
        SkillManager.setSkillList(skills);
    }

    public float getX() {
        return mPosX;
    }

    public float getY() {
        return mPosY;
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    /**
     * Initializes player combat combo skills
     */
    private void loadSkills() {
        // TODO: Move these to XML?
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.add(new Skill("Stab", new ArrayList<SkillDirection>(Arrays.asList(
                SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_UP))));
        skills.add(new Skill("Slash", new ArrayList<SkillDirection>(Arrays.asList(
                SkillDirection.DIRECTION_DOWN_LEFT, SkillDirection.DIRECTION_DOWN_RIGHT))));
        skills.add(new Skill("Stab", new ArrayList<SkillDirection>(Arrays.asList(
                SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_UP,
                SkillDirection.DIRECTION_DOWN, SkillDirection.DIRECTION_UP_RIGHT))));
        skills.add(new Skill("Lunge", new ArrayList<SkillDirection>(Arrays.asList(
                SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_DOWN,
                SkillDirection.DIRECTION_RIGHT))));
        skills.add(new Skill("Flurry", new ArrayList<SkillDirection>(Arrays.asList(
                SkillDirection.DIRECTION_UP, SkillDirection.DIRECTION_DOWN,
                SkillDirection.DIRECTION_LEFT, SkillDirection.DIRECTION_RIGHT))));
        setSkills(skills);
    }

    /**
     * Handles the movement of the player on the game map.
     * 
     * @param elapsed the time elapsed since last update
     * 
     * @return the event state of a room
     */
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
                Minimap.updateMinimap();
            }

            switch (mDirection) {
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
            Minimap.setCenter(mPosX, mPosY);
        }
        return result;
    }

    /**
     * Animates and moves the player sprite
     * 
     * @param direction the direction to move
     * @param distance how many total pixels to move
     */
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
                        mSprite.animate(ANIMATE_FRAME_DURATION, 0, 3, true);
                        mRoomY--;
                        break;
                    case DIRECTION_RIGHT:
                        mSprite.animate(ANIMATE_FRAME_DURATION, 4, 7, true);
                        mRoomX++;
                        break;
                    case DIRECTION_DOWN:
                        mSprite.animate(ANIMATE_FRAME_DURATION, 8, 11, true);
                        mRoomY++;
                        break;
                    case DIRECTION_LEFT:
                        mSprite.animate(ANIMATE_FRAME_DURATION, 12, 15, true);
                        mRoomX--;
                        break;
                }
                mParentMap.occupyRoom(mRoomX, mRoomY);
            }
        }
    }

    /**
     * Sets player sprite position based on current room. Used when teleporting.
     */
    public void updatePositionFromRoom() {
        mPosX = ((mRoomX * DungeonManager.getRoomWidth()) + (DungeonManager.getRoomWidth() / 2))
                * (mTileWidth * mScaleX);
        mPosY = ((mRoomY * DungeonManager.getRoomHeight()) + (DungeonManager.getRoomHeight() / 2))
                * (mTileHeight * mScaleY);
        mSprite.setPosition(mPosX, mPosY);
    }

    /**
     * Sets player sprite facing.
     * 
     * @param direction the direction to face
     */
    public void face(Direction direction) {
        mSprite.setCurrentTileIndex(direction.getValue() * 4 + 1);
    }
    

    /**
     * Consumes a potion to recover HP
     */
    public void usePotion() {
        Random rand = new Random(System.currentTimeMillis());
        if (mInventory.getNumPotions() >= 0) {
            mStats.increaseCurHP(rand.nextInt(25) + 50);
            mInventory.changePotions(-1);
        }
    }
    
    /**
     * Unequips an item and returns it to the player inventory.
     * 
     * @param item the item to unequip
     */
    private void unequip(Item item) {
        // TODO: Check if equipped in the first place!
        mStats.unequip(item);
        mInventory.unequip(item);
    }

    public void equipWeapon(Item weapon) {
        mStats.equip(weapon);
        mInventory.equipWeapon(weapon);
    }

    public void equipArmour(Item armour) {
        mStats.equip(armour);
        mInventory.equipArmour(armour);
    }

    public void addItem(Item item) {
        mInventory.addItem(item); 
    }

    public static void end() {
        sSkills = null;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
