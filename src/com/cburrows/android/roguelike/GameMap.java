package com.cburrows.android.roguelike;

import java.util.ArrayList;
import java.util.Random;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.simpleframework.xml.Root;

import android.util.Log;

import com.cburrows.android.roguelike.TmxMap.Map;
import com.cburrows.android.roguelike.components.Minimap;
import com.cburrows.android.roguelike.xml.DungeonFloor;
import com.cburrows.android.roguelike.xml.DungeonTileset;

@Root(name="map")
public class GameMap /*extends Map*/ {
    //private static final int TILE_WALL = 3;
    //private static final int TILE_FLOOR = 31;
    public static final int TILE_STAIRS_DOWN = 34;
    //private static final float MAX_DEPTH = 10;

    public int mRoomWidth = 11;
    public int mRoomHeight = 9;
    public int mRoomPadding = 2;
    
    private Map mMap;
    private DungeonTileset mTileset;
    private ArrayList<int[]> mData;
    
    private ArrayList<Room> mRooms;
    private int mNumRoomsX;
    private int mNumRoomsY;
    private float mChestSpawnRate;
    //private float mDepth;
    
    private int mMinHorPathSize;
    private int mMaxHorPathSize;
    private int mMinVertPathSize;
    private int mMaxVertPathSize;
    
    private static Random sRand = new Random(System.currentTimeMillis());
    
    public GameMap(int cols, int rows, int roomWidth, int roomHeight, int roomPadding) {
        mMap = new Map(cols, rows);

        mRoomWidth = roomWidth;
        mRoomHeight = roomHeight;
        mRoomPadding = roomPadding;
        mNumRoomsX = cols / mRoomWidth;
        mNumRoomsY = rows / mRoomHeight;
    }
    
    public GameMap(DungeonFloor floor, DungeonTileset tileset) {
        mTileset = tileset;
        mMap = new Map(floor.mCols, floor.mRows);
        mMap.addTileset(tileset.toTmx());

        mRoomWidth = floor.mRoomWidth;
        mRoomHeight = floor.mRoomHeight;
        mRoomPadding = floor.mRoomPadding;
        mNumRoomsX = floor.mCols / mRoomWidth;
        mNumRoomsY = floor.mRows / mRoomHeight;
        mChestSpawnRate = floor.mChestSpawnRate;
        //mDepth = floor.mDepth;
        
        mMinHorPathSize = floor.mMinHorizontalPathSize;
        mMaxHorPathSize = floor.mMaxHorizontalPathSize;
        mMinVertPathSize = floor.mMinVerticalPathSize;
        mMaxVertPathSize = floor.mMaxVerticalPathSize;
        
        Log.d("MAP", "FLOOR " + tileset.getFloorTile() + " WALL " + tileset.getWallTile());
    }
    
    public void generateMap() {
        mData = new ArrayList<int[]>();
        mData.add(new int[mMap.getWidth() * mMap.getHeight()]); // base
        mData.add(new int[mMap.getWidth() * mMap.getHeight()]); // top
        mRooms = new ArrayList<Room>();
        
        // Fill in the map with walls
        for (int y = 0; y < mMap.getHeight(); y++) {
            for (int x = 0; x < mMap.getWidth(); x++) {
                mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallTile();
            }
        }
        
        // Determine where the stairs will be
        int stairDownIndex = 1; //  (sRand.nextInt(mNumRoomsY) * mNumRoomsX) + sRand.nextInt(mNumRoomsX);
        //Log.d("MAP", "Index: " + stairDownIndex);
        
        // Place rooms
        for (int y = 0; y < mNumRoomsY; y++) {
            for (int x = 0; x < mNumRoomsX; x++) {
                Room room = new Room(x * mRoomWidth, y * mRoomHeight);
                if (sRand.nextFloat() < mChestSpawnRate) {
                    room.mHasChest = true;
                    Log.d("MAP", "Chest " + x + ", " + y);
                }
                
                if (y == 0) room.setAccessable(Direction.DIRECTION_UP, false);
                if (x == 0) room.setAccessable(Direction.DIRECTION_LEFT, false);
                if (y == mNumRoomsY-1) room.setAccessable(Direction.DIRECTION_DOWN, false); 
                if (x == mNumRoomsX-1) room.setAccessable(Direction.DIRECTION_RIGHT, false);
                                    
                //if (y * mNumRoomsX + x == stairDownIndex) room.mHasStairsDown = true;
                
                mRooms.add(room);
                room.place();
            }
        }
       
        // Connect rooms
        for (int y = 0; y < mNumRoomsY; y++) {
            for (int x = 0; x < mNumRoomsX; x++) {
                Room room = mRooms.get((y * mNumRoomsX) + x);
                if (y != mNumRoomsY-1) room.buildPath(mRooms.get(((y+1) * mNumRoomsX) + x));
                if (x != mNumRoomsX-1) room.buildPath(mRooms.get((y * mNumRoomsX) + x+1));
                                    
                mRooms.add(room);
            }
        }
        
        // Touch up wall edges
        /*
        for (int y = 1; y < mMap.getHeight()-1; y++) {
            for (int x = 1; x < mMap.getWidth()-1; x++) {
                if (mTileset.isFloorTile(mData.get(0)[y * mMap.getWidth() + x])) {
                    
                    if (mData.get(0)[(y+1) * mMap.getWidth() + x] == mTileset.getWallTile() &&
                            mData.get(0)[(y) * mMap.getWidth() + (x-1)] == mTileset.getWallTile() &&
                            mData.get(0)[(y) * mMap.getWidth() + (x+1)] == mTileset.getWallTile()) {
                        mData.get(1)[y * mMap.getWidth() + x] = mTileset.getWallDownLeftRightTile();
                        
                    } else if (mData.get(0)[(y+1) * mMap.getWidth() + x] == mTileset.getWallTile() &&
                            mData.get(0)[(y) * mMap.getWidth() + (x-1)] == mTileset.getWallTile()) {
                        mData.get(1)[y * mMap.getWidth() + x] = mTileset.getWallDownLeftTile();
                        
                    } else if (mData.get(0)[(y+1) * mMap.getWidth() + x] == mTileset.getWallTile() &&
                            mData.get(0)[(y) * mMap.getWidth() + (x+1)] == mTileset.getWallTile()) {
                        mData.get(1)[y * mMap.getWidth() + x] = mTileset.getWallDownRightTile();
                        
                    } else if (mData.get(0)[(y) * mMap.getWidth() + x-1] == mTileset.getWallTile() &&
                            mData.get(0)[(y) * mMap.getWidth() + (x+1)] == mTileset.getWallTile()) {
                        mData.get(1)[y * mMap.getWidth() + x] = mTileset.getWallLeftRightTile();
                        
                    } else if (mData.get(0)[(y) * mMap.getWidth() + (x+1)] == mTileset.getWallTile()) {
                        mData.get(1)[y * mMap.getWidth() + x] = mTileset.getWallLeftTile();
                        
                    } else if (mData.get(0)[(y) * mMap.getWidth() + (x-1)] == mTileset.getWallTile()) {
                        mData.get(1)[y * mMap.getWidth() + x] = mTileset.getWallRightTile();
                    } else if (mData.get(0)[(y+1) * mMap.getWidth() + x] == mTileset.getWallTile()) {
                        mData.get(1)[y * mMap.getWidth() + x] = mTileset.getWallDownTile();
                    }
                
                    if (mData.get(0)[(y-1) * mMap.getWidth() + x] == mTileset.getWallTile()) {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallUpTile();
                    }
                }
            }
        }
        */
        
        mMap.build(mData);
    }
    
    /*
    protected void placeRoom(int[] data, int x, int y) {
        for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
            for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                if (i == mRoomPadding || i == mRoomHeight-1-mRoomPadding 
                        || j == mRoomPadding || j == mRoomWidth-1-mRoomPadding) {
                    data[(y+i) * width + (x+j)] = TILE_WALL;
                } else {
                    data[(y+i) * width + (x+j)] = TILE_FLOOR;
                }
            }
        }
    }
    */
    
    public boolean getRoomAccess(int roomX, int roomY, Direction direction) {
        return mRooms.get((roomY * mNumRoomsX) + roomX).isAccessable(direction);
    }
    
    public int getRoomCols() { return mNumRoomsX; }
    
    public int getRoomRows() { return mNumRoomsY; }
    
    public void setRoomState(int roomX, int roomY, RoomState roomState) {
        if (roomX < 0 || roomX >= mNumRoomsX || roomY < 0 || roomY > mNumRoomsY)
            return;
        mRooms.get(roomY * mNumRoomsX + roomX).setRoomState(roomState);
    }
    
    public RoomState getRoomState(int roomX, int roomY) {
        if (roomX < 0 || roomX >= mNumRoomsX || roomY < 0 || roomY > mNumRoomsY)
            return RoomState.ROOM_HIDDEN;
        return mRooms.get(roomY * mNumRoomsX + roomX).mRoomState;
    }
    
    public void occupyRoom(int x, int y) {
        setRoomState(x, y, RoomState.ROOM_OCCUPIED);
        if (x-1 >= 0) setRoomState(x-1, y, RoomState.ROOM_SPOTTED);
        if (x+1 < getRoomCols()) setRoomState(x+1, y, RoomState.ROOM_SPOTTED);
        if (y-1 >= 0) setRoomState(x, y-1, RoomState.ROOM_SPOTTED);
        if (y+1 < getRoomRows()) setRoomState(x, y+1, RoomState.ROOM_SPOTTED);
    }
    
    public boolean hasChest(int roomX, int roomY) {
        return mRooms.get(roomY * mNumRoomsX + roomX).mHasChest;
    }
    
    public void setChest(int roomX, int roomY, boolean value) {
        mRooms.get(roomY * mNumRoomsX + roomX).mHasChest = value;
    }
    
    public TMXTiledMap getTmxTiledMap() { return Map.getTmxTiledMap(mMap); }
    
    private class Room {
        
        public int mX = 0;
        public int mY = 0;
        public boolean[] mAccessable = new boolean[4];
        public boolean mHasChest;
        //public boolean mHasStairsUp;
        //public boolean mHasStairsDown;
        private RoomState mRoomState;
        
        public Room(int x, int y) {
            mX = x;
            mY = y;
            for (int i = 0; i < 4; i++) mAccessable[i] = true;
            mRoomState = RoomState.ROOM_HIDDEN;
            //mHasStairsUp = false;
            //mHasStairsDown = false;
            mHasChest = false;
        }
        
        public void buildPath(Room dest) {
            int srcX = mX + (mRoomWidth / 2);
            int srcY = mY + (mRoomHeight / 2);
            int destX = dest.mX + (mRoomWidth / 2);
            int destY = dest.mY + (mRoomHeight / 2);
            
            for (int i = 0; i < Math.abs(destX - srcX); i++) {             
                int pathSize = sRand.nextInt(mMaxHorPathSize - mMinHorPathSize)+mMinHorPathSize;
                int offset = sRand.nextInt(pathSize);
                        
                if (mData.get(0)[(srcY * mMap.getWidth()) + srcX + i] != mTileset.getFloorTile()) {
                    mData.get(0)[((srcY- (pathSize + offset)) * mMap.getWidth()) + srcX + i] = mTileset.getWallTile();
                    mData.get(0)[(srcY + (offset+1)) * mMap.getWidth() + srcX + i] = mTileset.getWallTile();
                    for (int j = 0; j < pathSize; j++) {
                        mData.get(0)[((srcY - (pathSize - offset - (j+1))) * mMap.getWidth()) + srcX + i ] = mTileset.getFloorTile();
                    }  
                }
            }
            
            for (int i = 0; i < Math.abs(destY - srcY); i++) {             
                int pathSize = sRand.nextInt(mMaxVertPathSize - mMinVertPathSize)+mMinVertPathSize;
                int offset = 1;
                if (pathSize > mMinVertPathSize)
                    offset = sRand.nextInt(pathSize - mMinVertPathSize);
                        
                if (mData.get(0)[((srcY+i) * mMap.getWidth()) + srcX] != mTileset.getFloorTile()) {
                    mData.get(0)[((srcY+i) * mMap.getWidth()) + srcX - (pathSize + offset)] = mTileset.getWallTile();
                    mData.get(0)[((srcY+i) * mMap.getWidth()) + srcX + (offset+1)] = mTileset.getWallTile();
                    for (int j = 0; j < pathSize; j++) {
                        mData.get(0)[((srcY+i) * mMap.getWidth()) + srcX - (pathSize - offset - (j+1))] = mTileset.getFloorTile();
                    }  
                }
            }
        }
        
        public void place() {
            
            // Build walls
            for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
                for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                    if (i == mRoomPadding || i == mRoomHeight-1-mRoomPadding 
                            || j == mRoomPadding || j == mRoomWidth-1-mRoomPadding) {
                        mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] = mTileset.getWallTile();
                    } else {
                        mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] = mTileset.getFloorTile();
                    }
                }
            }
            
            // Degrade walls
            /*
            for (int k = 0; k < 2; k++) {
                for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
                    for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                        if (i == mRoomPadding+k || i == mRoomHeight-1-mRoomPadding-k 
                                || j == mRoomPadding+k || j == mRoomWidth-1-mRoomPadding-k) {
                            if (sRand.nextFloat() < (0.5f / k)) {
                                mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] = mTileset.getWallTile();
                            }
                        }
                    }
                }
            }
            */
            
            // Place features
            /*
            for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
                for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                    if (mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] == mTileset.getFloorTile() &&
                            mData.get(1)[(mY+i) * mMap.getWidth() + (mX+j)] == 0) {
                        if (sRand.nextFloat() < 0.2f) {
                            //ArrayList<Integer> features = mTileset.getFeatures();
                            mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] = mTileset.getRandomFeature();
                        }
                    }
                }
            }
            */
            
            /*
            if (mHasStairsDown) {
                mData[(mY + (mRoomHeight / 2)) * width + (mX + (mRoomWidth / 2))] = TILE_STAIRS_DOWN;
            }
            */
        }
        
        //*************************************************************
        // Getters and setters;
        //*************************************************************
/*
        public int getX() {
            return mX;
        }

        public void setX(int X) {
            this.mX = X;
        }

        public int getY() {
            return mY;
        }

        public void setY(int Y) {
            this.mY = Y;
        }
*/      
        public boolean isAccessable(Direction direction) {
            return mAccessable[direction.getValue()];
        }
        
        public void setAccessable(Direction direction, boolean value) {
            mAccessable[direction.getValue()] = value;
        }
        
        public void setRoomState(RoomState roomState) {
            if (mRoomState == RoomState.ROOM_OCCUPIED && roomState.value != RoomState.ROOM_OCCUPIED.value) {
                mRoomState = RoomState.ROOM_VISITED;
                return;
            }
            if (roomState.value < mRoomState.getValue()) return;
            
            mRoomState = roomState;
        }
    }
    
    public enum RoomState {
        ROOM_HIDDEN(0), ROOM_SPOTTED(1), ROOM_VISITED(2), ROOM_OCCUPIED(3);
        
        private final int value;
        
        RoomState(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public int getTileWidth() {
        return mMap.getTileWidth();
    }
    
    public int getTileHeight() {
        return mMap.getTileHeight();
    }
}
