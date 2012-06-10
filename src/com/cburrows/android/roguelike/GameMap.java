package com.cburrows.android.roguelike;

import java.util.ArrayList;
import java.util.Random;

import org.simpleframework.xml.Root;

import android.util.Log;

import com.cburrows.android.roguelike.TmxMap.Map;
import com.cburrows.android.roguelike.components.Minimap;
import com.cburrows.android.roguelike.xml.DungeonFloor;

@Root(name="map")
public class GameMap extends Map {
    private static final int TILE_WALL = 3;
    private static final int TILE_FLOOR = 31;
    
    public int mRoomWidth = 11;
    public int mRoomHeight = 9;
    public int mRoomPadding = 2;
    
    private int[] mData;
    
    private ArrayList<Room> mRooms;
    private int mNumRoomsX;
    private int mNumRoomsY;
    private float mChestSpawnRate;
    
    private static Random sRand = new Random(System.currentTimeMillis());
    
    public GameMap(int cols, int rows, int roomWidth, int roomHeight, int roomPadding) {
        super(cols, rows);

        mRoomWidth = roomWidth;
        mRoomHeight = roomHeight;
        mRoomPadding = roomPadding;
        mNumRoomsX = cols / mRoomWidth;
        mNumRoomsY = rows / mRoomHeight;
    }
    
    public GameMap(DungeonFloor floor) {
        super(floor.mCols, floor.mRows);

        mRoomWidth = floor.mRoomWidth;
        mRoomHeight = floor.mRoomHeight;
        mRoomPadding = floor.mRoomPadding;
        mNumRoomsX = floor.mCols / mRoomWidth;
        mNumRoomsY = floor.mRows / mRoomHeight;
        mChestSpawnRate = floor.mChestSpawnRate;
        
    }
    
    public void generateMap() {
        mData = new int[width * height];       
        mRooms = new ArrayList<Room>();
                
        for (int y = 0; y < mNumRoomsY; y++) {
            for (int x = 0; x < mNumRoomsX; x++) {
                Room room = new Room(x * mRoomWidth, y * mRoomHeight);
                if (sRand.nextFloat() < mChestSpawnRate) {
                    room.mHasChest = true;
                    Log.d("MAP", "Chest " + x + ", " + y);
                }
                
                placeRoom(mData, x * mRoomWidth, y * mRoomHeight);
                
                if (y == 0) room.setAccessable(Direction.DIRECTION_UP, false);
                if (x == 0) room.setAccessable(Direction.DIRECTION_LEFT, false);
                if (y == mNumRoomsY-1) room.setAccessable(Direction.DIRECTION_DOWN, false); 
                if (x == mNumRoomsX-1) room.setAccessable(Direction.DIRECTION_RIGHT, false);
                                    
                mRooms.add(room);
            }
        }
        
        for (int y = 0; y < mNumRoomsY; y++) {
            for (int x = 0; x < mNumRoomsX; x++) {
                Room room = mRooms.get((y * mNumRoomsX) + x);
                if (y != mNumRoomsY-1) room.buildPath(mRooms.get(((y+1) * mNumRoomsX) + x));
                if (x != mNumRoomsX-1) room.buildPath(mRooms.get((y * mNumRoomsX) + x+1));
                                    
                mRooms.add(room);
            }
        }
        
        build(mData);
    }
    
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
    
    private class Room {
        
        public int mX = 0;
        public int mY = 0;
        public boolean[] mAccessable = new boolean[4];
        public boolean mHasChest;
        private RoomState mRoomState;
        
        public Room(int x, int y) {
            mX = x;
            mY = y;
            for (int i = 0; i < 4; i++) mAccessable[i] = true;
            mRoomState = RoomState.ROOM_HIDDEN;
            mHasChest = false;
        }
        
        public void buildPath(Room dest) {
            int srcX = mX + (mRoomWidth / 2);
            int srcY = mY + (mRoomHeight / 2);
            int destX = dest.mX + (mRoomWidth / 2);
            int destY = dest.mY + (mRoomHeight / 2);
            
            //Log.d("MAP", "Path from " + srcX + ", " + srcY + " to " + destX + ", " + destY);
            
            for (int i = 0; i < Math.abs(destX - srcX); i++) {                
                if (destX - srcX > 0) {
                    if (mData[(srcY * width) + srcX + i] != TILE_FLOOR) {
                        mData[(srcY * width) + srcX + i] = TILE_FLOOR;
                        mData[((srcY-1) * width) + srcX + i] = TILE_WALL;
                        mData[((srcY+1) * width) + srcX + i] = TILE_WALL;
                    }
                }
            }
            
            for (int i = 0; i < Math.abs(destY - srcY); i++) {                
                //if (destY - srcY > 0) {
                    if (mData[((srcY+i) * width) + srcX] != TILE_FLOOR) {
                        mData[((srcY+i) * width) + srcX] = TILE_FLOOR;
                        mData[((srcY+i) * width) + srcX+1] = TILE_WALL;
                        mData[((srcY+i) * width) + srcX-1] = TILE_WALL;
                    }
                //}
            }
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
}
