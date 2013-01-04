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

package com.cdburrows.android.roguelike.map;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;

import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.map.Room.RoomState;
import com.cdburrows.android.roguelike.tmx.TmxMap;

/**
 * @author cburrows
 */
public class GameMap {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int TILE_STAIRS_DOWN = 34;

    // private static final float MAX_DEPTH = 10;

    // ===========================================================
    // Fields
    // ===========================================================

    private TmxMap mTmxMap;

    private Room[][] mRooms;

    private long mSeed;

    private int mStartX;

    private int mStartY;

    // ===========================================================
    // Constructors
    // ===========================================================

    public GameMap(TmxMap map, Room[][] rooms, int startX, int startY) {
        // mSeed = seed;
        mTmxMap = map;
        mRooms = rooms;
        mStartX = startX;
        mStartY = startY;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void unload() {
        mTmxMap = null;
    }

    public TMXTiledMap getTmxTiledMap() {
        return TmxMap.getTmxTiledMap(mTmxMap);
    }

    public TMXLayer getSprite(int layer) {
        TMXLayer l = getTmxTiledMap().getTMXLayers().get(layer);
        l.setScaleCenter(0, 0);
        l.setScale(RoguelikeActivity.sScaleX, RoguelikeActivity.sScaleY);
        return l;
    }

    public boolean getRoomAccess(int roomX, int roomY, Direction direction) {
        if (roomY >= getRoomRows() || roomX >= getRoomCols())
            return false;
        return mRooms[roomY][roomX].isAccessable(direction);
    }

    public int getRoomCols() {
        return mRooms[0].length;
    }

    public int getRoomRows() {
        return mRooms.length;
    }

    public void setRoomState(int roomX, int roomY, RoomState roomState) {
        if (roomX < 0 || roomX >= getRoomCols() || roomY < 0 || roomY > getRoomRows())
            return;
        mRooms[roomY][roomX].setRoomState(roomState);
    }

    public RoomState getRoomState(int roomX, int roomY) {
        if (roomX < 0 || roomX >= getRoomCols() || roomY < 0 || roomY > getRoomRows())
            return RoomState.ROOM_HIDDEN;
        return mRooms[roomY][roomX].mRoomState;
    }

    public void occupyRoom(int x, int y) {
        Room room = mRooms[y][x];
        setRoomState(x, y, RoomState.ROOM_OCCUPIED);
        if (x - 1 >= 0 && room.isAccessable(Direction.DIRECTION_LEFT))
            setRoomState(x - 1, y, RoomState.ROOM_SPOTTED);
        if (x + 1 < getRoomCols() && room.isAccessable(Direction.DIRECTION_RIGHT))
            setRoomState(x + 1, y, RoomState.ROOM_SPOTTED);
        if (y - 1 >= 0 && room.isAccessable(Direction.DIRECTION_UP))
            setRoomState(x, y - 1, RoomState.ROOM_SPOTTED);
        if (y + 1 < getRoomRows() && room.isAccessable(Direction.DIRECTION_DOWN))
            setRoomState(x, y + 1, RoomState.ROOM_SPOTTED);
    }

    public boolean hasChest(int roomX, int roomY) {
        return mRooms[roomY][roomX].hasChest();
    }

    public void setChest(int roomX, int roomY, boolean value) {
        mRooms[roomY][roomX].setChest(value);
    }

    public int getTileWidth() {
        return mTmxMap.getTileWidth();
    }

    public int getTileHeight() {
        return mTmxMap.getTileHeight();
    }

    public int getStartX() {
        return mStartX;
    }

    public int getStartY() {
        return mStartY;
    }

    public boolean hasStairsUp(int roomX, int roomY) {
        return mRooms[roomY][roomX].hasStairsUp();
    }

    public boolean hasStairsDown(int roomX, int roomY) {
        return mRooms[roomY][roomX].hasStairsDown();
    }

    public Room[][] getRooms() {
        return mRooms;
    }

    public void setRooms(Room[][] rooms) {
        mRooms = rooms;
    }

    public float getSpriteWidth() {
        return mTmxMap.getWidth() * mTmxMap.getTileWidth();
    }

    public float getSpriteHeight() {
        return mTmxMap.getHeight() * mTmxMap.getTileHeight();
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
