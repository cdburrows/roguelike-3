/*
 * Copyright (c) 2012, Christopher Burrows
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

import java.util.ArrayList;
import java.util.Random;

import net.bytten.metazelda.Dungeon;
import net.bytten.metazelda.constraints.CountConstraints;
import net.bytten.metazelda.generators.DungeonGenerator;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import android.util.Log;
import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.map.Room.RoomState;
import com.cdburrows.android.roguelike.tmx.TmxMap;

public class GameMap {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    public static final int TILE_STAIRS_DOWN = 34;
    //private static final float MAX_DEPTH = 10;
    private static final int TILE_NW = 1;
    private static final int TILE_N = 1;
    private static final int TILE_NE = 2;
    private static final int TILE_W = 2;
    private static final int TILE_E = 4;
    private static final int TILE_SW = 4;
    private static final int TILE_S = 8;
    private static final int TILE_SE = 8;
    private static final float FEATURE_RATE = 0.02f;
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    public int mRoomWidth = 11;
    public int mRoomHeight = 9;
    public int mRoomPadding = 2;
    
    private TmxMap mMap;
    private XmlDungeonTileset mTileset;
    private ArrayList<int[]> mData;
    private ArrayList<Room> mRooms;
    
    private int mWidth;
    private int mHeight;
    private int mStartX;
    private int mStartY;
    
    private int mRoomCols;
    private int mRoomRows;
    private float mChestSpawnRate;
    private float mErodeRate;
    //private float mDepth;
    
    private int mMinHorPathSize;
    private int mMaxHorPathSize;
    private int mMinVertPathSize;
    private int mMaxVertPathSize;
    
    private static Random sRand = new Random(System.currentTimeMillis());
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    /*
    public GameMap(int cols, int rows, int roomWidth, int roomHeight, int roomPadding) {
        mMap = new TmxMap(cols, rows);

        mRoomWidth = roomWidth;
        mRoomHeight = roomHeight;
        mRoomPadding = roomPadding;
        mRoomCols = cols / mRoomWidth;
        mRoomRows = rows / mRoomHeight;
        
        mWidth = cols * roomWidth;
        mHeight = rows * roomHeight;
    }
    */
    
    public GameMap(XmlDungeonFloor floor, XmlDungeonTileset tileset) {
        mTileset = tileset;
        mMap = new TmxMap(floor.mCols, floor.mRows);
        mMap.addTileset(tileset.toTmx());

        mRoomWidth = floor.mRoomWidth;
        mRoomHeight = floor.mRoomHeight;
        mRoomPadding = floor.mRoomPadding;
        mRoomCols = floor.mCols / mRoomWidth;
        mRoomRows = floor.mRows / mRoomHeight;
        mChestSpawnRate = floor.mChestSpawnRate;
        mErodeRate = floor.getErode();
        //mDepth = floor.mDepth;
        
        mWidth = mRoomCols * mRoomWidth;
        mHeight = mRoomRows * mRoomHeight;
        
        mMinHorPathSize = floor.mMinHorizontalPathSize;
        mMaxHorPathSize = floor.mMaxHorizontalPathSize;
        mMinVertPathSize = floor.mMinVerticalPathSize;
        mMaxVertPathSize = floor.mMaxVerticalPathSize;
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    private int getTile(int x, int y) {
        if (y * mMap.getWidth() + x >= mData.get(0).length) {
            return mTileset.getWallTile();
        }
        
        if (x < 0) return mTileset.getWallTile();
        if (y < 0) return mTileset.getWallTile();
        if (x >= mWidth) return mTileset.getWallTile();
        if (y >= mHeight) return mTileset.getWallTile();
        return mData.get(0)[y * mMap.getWidth() + x];
    }
    
    private void setTile(int x, int y, int tile)
    {
        if (y * mMap.getWidth() + x >= mData.get(0).length) {
            Log.d("MAP", "WARNING! Attempt to set tile " + x + ", " + y + " exceeds array bound.");
            return;
        }
        
        mData.get(0)[y * mMap.getWidth() + x] = tile;
    }
    
    public boolean getRoomAccess(int roomX, int roomY, Direction direction) {
        if (roomY * mRoomCols + roomX >= mRooms.size()) return false;
        return mRooms.get(roomY * mRoomCols + roomX).isAccessable(direction);
    }
    
    public int getRoomCols() { return mRoomCols; }
    
    public int getRoomRows() { return mRoomRows; }
    
    public void setRoomState(int roomX, int roomY, RoomState roomState) {
        if (roomX < 0 || roomX >= mRoomCols || roomY < 0 || roomY > mRoomRows)
            return;
        mRooms.get(roomY * mRoomCols + roomX).setRoomState(roomState);
    }
    
    public RoomState getRoomState(int roomX, int roomY) {
        if (roomX < 0 || roomX >= mRoomCols || roomY < 0 || roomY > mRoomRows)
            return RoomState.ROOM_HIDDEN;
        if (roomY * mRoomCols + roomX >= mRooms.size()) return RoomState.ROOM_HIDDEN;
        return mRooms.get(roomY * mRoomCols + roomX).mRoomState;
    }
    
    public void occupyRoom(int x, int y) {
        Room room = mRooms.get(y * mRoomCols + x);
        setRoomState(x, y, RoomState.ROOM_OCCUPIED);
        if (x-1 >= 0 && room.isAccessable(Direction.DIRECTION_LEFT)) setRoomState(x-1, y, RoomState.ROOM_SPOTTED);
        if (x+1 < getRoomCols() && room.isAccessable(Direction.DIRECTION_RIGHT)) setRoomState(x+1, y, RoomState.ROOM_SPOTTED);
        if (y-1 >= 0 && room.isAccessable(Direction.DIRECTION_UP)) setRoomState(x, y-1, RoomState.ROOM_SPOTTED);
        if (y+1 < getRoomRows() && room.isAccessable(Direction.DIRECTION_DOWN)) setRoomState(x, y+1, RoomState.ROOM_SPOTTED);
    }
    
    public boolean hasChest(int roomX, int roomY) {
        return mRooms.get(roomY * mRoomCols + roomX).hasChest();
    }
    
    public void setChest(int roomX, int roomY, boolean value) {
        mRooms.get(roomY * mRoomCols + roomX).setChest(value);
    }
    
    public TMXTiledMap getTmxTiledMap() { return TmxMap.getTmxTiledMap(mMap); }
    
    public int getTileWidth() {
        return mMap.getTileWidth();
    }
    
    public int getTileHeight() {
        return mMap.getTileHeight();
    }
    
    public int getStartX() {
        return mStartX;
    }
    
    public int getStartY() {
        return mStartY;
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    public void generateMap() {
        
        
        DungeonGenerator dungeonGenerator = new DungeonGenerator(new Random().nextLong(), new CountConstraints(20, 1, 1));
        net.bytten.metazelda.Dungeon dungeon = null;
        
        boolean accepted = false;
        while (accepted == false) {
            accepted = true;
            dungeonGenerator.generate();
            dungeon = (Dungeon)dungeonGenerator.getDungeon();
            if (dungeon.getExtentBounds().height() < 7) accepted = false;
            if (dungeon.getExtentBounds().width() > 9) accepted = false;
        }
        
        mRoomCols = dungeon.getExtentBounds().width();
        mRoomRows = dungeon.getExtentBounds().height();
        mWidth = dungeon.getExtentBounds().width() * mRoomWidth;
        mHeight = dungeon.getExtentBounds().height() * mRoomHeight;
        mMap = new TmxMap(mWidth, mHeight);
        mMap.addTileset(mTileset.toTmx());

        mData = new ArrayList<int[]>();
        mData.add(new int[mMap.getWidth() * mMap.getHeight()]); // base
        mRooms = new ArrayList<Room>();
        
        for (int y = 0; y < mRoomRows; y++) {
            for (int x = 0; x < mRoomCols; x++) {
                mRooms.add(new Room(this, x * mRoomWidth, y * mRoomHeight));
            }
        }
        
        // Fill in the map with walls
        for (int y = 0; y < mMap.getHeight(); y++) {
            for (int x = 0; x < mMap.getWidth(); x++) {
                setTile(x, y, mTileset.getWallTile());
            }
        }
        
        int top = -1 * dungeon.getExtentBounds().top;
        int left = -1 * dungeon.getExtentBounds().left;
        
        mStartX = left + dungeon.findGoal().coords.x;
        mStartY = top + dungeon.findGoal().coords.y;
        
        for (net.bytten.metazelda.Room room : dungeon.getRooms()) {
            int x = left + room.coords.x;
            int y = top + room.coords.y;
            
            Room r = mRooms.get(y * mRoomCols + x);
            
            for (net.bytten.metazelda.Direction d: net.bytten.metazelda.Direction.values()) {
                if (!(room.getEdge(d) == null)) {
                    r.setAccessable(d, true);
                }
            }

            place(r);
        }
       
        // Connect rooms
        for (int y = 0; y < mRoomRows; y++) {
            for (int x = 0; x < mRoomCols; x++) {
                Room room = mRooms.get(y * mRoomCols + x);
                if (room.isAccessable(Direction.DIRECTION_DOWN)) buildPath(room, mRooms.get(((y+1) * mRoomCols) + x));
                if (room.isAccessable(Direction.DIRECTION_RIGHT)) buildPath(room, mRooms.get((y * mRoomCols) + x+1));
            }
        }
             
        dynamicTiles();
        
        mMap.build(mData);
    }
    
    /*
    private void generateRooms(int startX, int startY) {
        
        for (int y = 0; y < mRoomRows; y++) {
            for (int x = 0; x < mRoomCols; x++) {
                Room room = mRooms.get(y * mRoomCols + x); //new Room(x * mRoomWidth, y * mRoomHeight);
                if (sRand.nextFloat() < mChestSpawnRate) {
                    room.mHasChest = true;
                    Log.d("MAP", "Chest " + x + ", " + y);
                }
                
                if (y == 0) room.setAccessable(Direction.DIRECTION_UP, false);
                if (x == 0) room.setAccessable(Direction.DIRECTION_LEFT, false);
                if (y == mRoomRows-1) room.setAccessable(Direction.DIRECTION_DOWN, false); 
                if (x == mRoomCols-1) room.setAccessable(Direction.DIRECTION_RIGHT, false);
                                    
                //if (y * mRoomCols + x == stairDownIndex) room.mHasStairsDown = true;
                
                mRooms.add(room);
                room.place();
            }
        }
    }
    */
    
    private void dynamicTiles() {
     // Touch up wall edges
        int d = 0, c = 0;
        for (int y = 0; y < mMap.getHeight(); y++) {
            for (int x = 0; x < mMap.getWidth(); x++) {
                if (mTileset.isWallTile(mData.get(0)[y * mMap.getWidth() + x])) {
                    d = 0;
                    c = 0;
                    if (mTileset.isFloorTile(getTile(x, y-1)))   c = (c | TILE_N);
                    if (mTileset.isFloorTile(getTile(x-1, y)))   c = (c | TILE_W);
                    if (mTileset.isFloorTile(getTile(x+1, y)))   c = (c | TILE_E);
                    if (mTileset.isFloorTile(getTile(x, y+1)))   c = (c | TILE_S);
                    
                    if (mTileset.isFloorTile(getTile(x-1, y-1))) d = (d | TILE_NW);
                    if (mTileset.isFloorTile(getTile(x+1, y-1))) d = (d | TILE_NE);
                    if (mTileset.isFloorTile(getTile(x-1, y+1))) d = (d | TILE_SW);
                    if (mTileset.isFloorTile(getTile(x+1, y+1))) d = (d | TILE_SE);

                    if (c == 0) {                      
                        setTile(x, y, mTileset.getWallFillTile());
                    
  
                    } else if (c == (TILE_W | TILE_N | TILE_E) )  {
                        setTile(x, y, mTileset.getWallInNTile());
                        
                    } else if (c == (TILE_S | TILE_N | TILE_E) )  {
                        setTile(x, y, mTileset.getWallInETile());
                        
                    } else if (c == (TILE_W | TILE_S | TILE_E) )  {
                        setTile(x, y, mTileset.getWallInSTile());
                        
                    } else if (c == (TILE_W | TILE_N | TILE_S) )  {
                        setTile(x, y, mTileset.getWallInWTile());
                        
                        
                    } else if (c == (TILE_N | TILE_E) && (d & TILE_NE) == TILE_NE)  {
                        setTile(x, y, mTileset.getWallNETile());
                        
                    } else if (c == (TILE_S | TILE_E) && (d & TILE_SE) == TILE_SE)  {
                        setTile(x, y, mTileset.getWallSETile());
                    
                    } else if (c == (TILE_S | TILE_W) && (d & TILE_SW) == TILE_SW)  {
                        setTile(x, y, mTileset.getWallSWTile());
                        
                    } else if (c == (TILE_N | TILE_W) && (d & TILE_NW) == TILE_NW)  {
                        setTile(x, y, mTileset.getWallNWTile());
                        
                        
                    } else if (c == (TILE_N | TILE_E) )  {
                        setTile(x, y, mTileset.getWallNETile());
                        
                    } else if (c == (TILE_S | TILE_E) )  {
                        setTile(x, y, mTileset.getWallSETile());
                    
                    } else if (c == (TILE_S | TILE_W) )  {
                        setTile(x, y, mTileset.getWallSWTile());
                        
                    } else if (c == (TILE_N | TILE_W) )  {
                        setTile(x, y, mTileset.getWallNWTile());
                        
                        
                    } else if (c == TILE_N)  {
                        setTile(x, y, mTileset.getWallNTile());
                    
                    } else if (c == TILE_E)  {
                        setTile(x, y, mTileset.getWallETile());
                        
                    } else if (c == TILE_S)  {
                        setTile(x, y, mTileset.getWallSTile());
                        
                    } else if (c == TILE_W)  {
                        setTile(x, y, mTileset.getWallWTile());
                    
                        
                    } else if (d == TILE_SE)  {
                        setTile(x, y, mTileset.getWallWTile());
                    
                    } else if (d == TILE_SW)  {
                        setTile(x, y, mTileset.getWallETile());
                        
                    } else if (d == TILE_NE)  {
                        setTile(x, y, mTileset.getWallSTile());
                        
                    } else if (d == TILE_NW)  {
                        setTile(x, y, mTileset.getWallWTile());
                        
                    }
                }
            }
        }
    }
    
public void buildPath(Room src, Room dest) {
        
        int srcX = src.getX() + (mRoomWidth / 2);
        int srcY = src.getY() + (mRoomHeight / 2);
        int destX = dest.getX() + (mRoomWidth / 2);
        int destY = dest.getY() + (mRoomHeight / 2);
        
        for (int i = 0; i < Math.abs(destX - srcX); i++) {             
            int pathSize = 1;
            if (mMaxHorPathSize - mMinHorPathSize > 0) pathSize = sRand.nextInt(mMaxVertPathSize - mMinVertPathSize) + mMinVertPathSize;
            int offset = sRand.nextInt(pathSize);
                    
            if (getTile(srcX+i, srcY) != mTileset.getFloorTile()) {
                setTile(srcX+i, srcY-(pathSize + offset), mTileset.getWallTile());
                setTile(srcX+i, srcY + (offset+1), mTileset.getWallTile());
                for (int j = 0; j < pathSize; j++) {
                    setTile(srcX+i, srcY - (pathSize - offset - (j+1)), mTileset.getFloorTile());
                }  
            }
        }
        
        for (int i = 0; i < Math.abs(destY - srcY); i++) {     
            int pathSize = 1;
            if (mMaxVertPathSize - mMinVertPathSize > 0) pathSize = sRand.nextInt(mMaxVertPathSize - mMinVertPathSize) + mMinVertPathSize;
            int offset = sRand.nextInt(pathSize);
                    
            if (getTile(srcX, srcY+i) != mTileset.getFloorTile()) {
                setTile(srcX - (pathSize + offset), srcY+i, mTileset.getWallTile());
                setTile(srcX + (offset+1), srcY+i, mTileset.getWallTile());
                for (int j = 0; j < pathSize; j++) {
                    setTile(srcX - (pathSize - offset - (j+1)), srcY+i, mTileset.getFloorTile());
                }  
            }
        }
    }
    
    public void place(Room room) {
        
        // Build walls
        for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
            for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                if (i == mRoomPadding || i == mRoomHeight-1-mRoomPadding 
                        || j == mRoomPadding || j == mRoomWidth-1-mRoomPadding) {
                    setTile(room.getX()+j, room.getY()+i, mTileset.getWallTile());
                } else {
                    setTile(room.getX()+j, room.getY()+i, mTileset.getFloorTile());
                }
            }
        }
        
        // Degrade walls
        for (int k = 0; k < 2; k++) {
            for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
                for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                    if (i == mRoomPadding+k || i == mRoomHeight-1-mRoomPadding-k 
                            || j == mRoomPadding+k || j == mRoomWidth-1-mRoomPadding-k) {
                        if (sRand.nextFloat() < (mErodeRate / k)) {
                            setTile(room.getX()+j, room.getY()+i, mTileset.getWallTile());
                        }
                    }
                }
            }
        }
        
        // Place features
        int feature;
        for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
            for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                if (getTile(room.getX()+j, room.getY()+i) == mTileset.getFloorTile()) {
                    if (sRand.nextFloat() < FEATURE_RATE) {
                        feature = mTileset.getRandomFeature();
                        if (feature > 0) {
                            setTile(room.getX()+j, room.getY()+i, feature);
                        }
                    }
                }
            }
        }
        
        /*
        if (mStairsDown) {
            mData[(mY + (mRoomHeight / 2)) * width + (mX + (mRoomWidth / 2))] = TILE_STAIRS_DOWN;
        }
        */
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}