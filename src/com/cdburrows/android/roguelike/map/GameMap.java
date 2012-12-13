package com.cdburrows.android.roguelike.map;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.simpleframework.xml.Root;

import android.util.Log;
import android.util.Pair;

import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.component.Minimap;
import com.cdburrows.android.roguelike.tmx.TmxMap;

public class GameMap /*extends Map*/ {

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

    public int mRoomWidth = 11;
    public int mRoomHeight = 9;
    public int mRoomPadding = 2;
    
    private TmxMap mMap;
    private XmlDungeonTileset mTileset;
    private ArrayList<int[]> mData;
    
    private ArrayList<Room> mRooms;
    private int mNumRoomsX;
    private int mNumRoomsY;
    private float mChestSpawnRate;
    private float mErodeRate;
    //private float mDepth;
    
    private int mMinHorPathSize;
    private int mMaxHorPathSize;
    private int mMinVertPathSize;
    private int mMaxVertPathSize;
    
    private static Random sRand = new Random(System.currentTimeMillis());
    
    public GameMap(int cols, int rows, int roomWidth, int roomHeight, int roomPadding) {
        mMap = new TmxMap(cols, rows);

        mRoomWidth = roomWidth;
        mRoomHeight = roomHeight;
        mRoomPadding = roomPadding;
        mNumRoomsX = cols / mRoomWidth;
        mNumRoomsY = rows / mRoomHeight;
    }
    
    public GameMap(XmlDungeonFloor floor, XmlDungeonTileset tileset) {
        mTileset = tileset;
        mMap = new TmxMap(floor.mCols, floor.mRows);
        mMap.addTileset(tileset.toTmx());

        mRoomWidth = floor.mRoomWidth;
        mRoomHeight = floor.mRoomHeight;
        mRoomPadding = floor.mRoomPadding;
        mNumRoomsX = floor.mCols / mRoomWidth;
        mNumRoomsY = floor.mRows / mRoomHeight;
        mChestSpawnRate = floor.mChestSpawnRate;
        mErodeRate = floor.getErode();
        //mDepth = floor.mDepth;
        
        mMinHorPathSize = floor.mMinHorizontalPathSize;
        mMaxHorPathSize = floor.mMaxHorizontalPathSize;
        mMinVertPathSize = floor.mMinVerticalPathSize;
        mMaxVertPathSize = floor.mMaxVerticalPathSize;
    }
    
    public void generateMap() {
        mData = new ArrayList<int[]>();
        mData.add(new int[mMap.getWidth() * mMap.getHeight()]); // base
        //mData.add(new int[mMap.getWidth() * mMap.getHeight()]); // top
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
        generateRooms(0, 0);
        /*
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
        */
       
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
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallFillTile();
                    
  
                    } else if (c == (TILE_W | TILE_N | TILE_E) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallInNTile();
                        
                    } else if (c == (TILE_S | TILE_N | TILE_E) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallInETile();
                        
                    } else if (c == (TILE_W | TILE_S | TILE_E) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallInSTile();
                        
                    } else if (c == (TILE_W | TILE_N | TILE_S) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallInWTile();
                        
                        
                    } else if (c == (TILE_N | TILE_E) && (d & TILE_NE) == TILE_NE)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallNETile();
                        
                    } else if (c == (TILE_S | TILE_E) && (d & TILE_SE) == TILE_SE)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallSETile();
                    
                    } else if (c == (TILE_S | TILE_W) && (d & TILE_SW) == TILE_SW)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallSWTile();
                        
                    } else if (c == (TILE_N | TILE_W) && (d & TILE_NW) == TILE_NW)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallNWTile();
                        
                        
                    } else if (c == (TILE_N | TILE_E) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallNETile();
                        
                    } else if (c == (TILE_S | TILE_E) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallSETile();
                    
                    } else if (c == (TILE_S | TILE_W) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallSWTile();
                        
                    } else if (c == (TILE_N | TILE_W) )  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallNWTile();
                        
                        
                    } else if (c == TILE_N)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallNTile();
                    
                    } else if (c == TILE_E)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallETile();
                        
                    } else if (c == TILE_S)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallSTile();
                        
                    } else if (c == TILE_W)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallWTile();
                    
                        
                    } else if (d == TILE_SE)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallWTile();
                    
                    } else if (d == TILE_SW)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallETile();
                        
                    } else if (d == TILE_NE)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallSTile();
                        
                    } else if (d == TILE_NW)  {
                        mData.get(0)[y * mMap.getWidth() + x] = mTileset.getWallWTile();
                        
                    }
                }
            }
        }
                        
                     
        
        mMap.build(mData);
    }
    
    private void generateRooms(int startX, int startY) {
        
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
        
        /*
        Stack<Pair<Integer, Integer>> roomStack = new Stack<Pair<Integer, Integer>>();
        roomStack.push(new Pair<Integer, Integer>(startX, startY));
        
        Pair<Integer, Integer> curRoom;
        //LinkedList<>
        while (!roomStack.empty()) {
             curRoom = roomStack.pop();
             
            
            // Randomize room type
            
            // Randomize number of connections
            
            
            // Get list of valid rooms to connect to
            
            // Connect to rooms
        }
        */
    }
    
    private int getTile(int x, int y) {
        if (x < 0) return mTileset.getWallTile();
        if (y < 0) return mTileset.getWallTile();
        if (x >= mMap.getWidth()) return mTileset.getWallTile();
        if (y >= mMap.getHeight()) return mTileset.getWallTile();
        return mData.get(0)[y * mMap.getWidth() + x];
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
    
    public TMXTiledMap getTmxTiledMap() { return TmxMap.getTmxTiledMap(mMap); }
    
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
                int pathSize = 1;
                if (mMaxHorPathSize - mMinHorPathSize > 0) pathSize = sRand.nextInt(mMaxVertPathSize - mMinVertPathSize) + mMinVertPathSize;
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
                
                int pathSize = 1;
                if (mMaxVertPathSize - mMinVertPathSize > 0) pathSize = sRand.nextInt(mMaxVertPathSize - mMinVertPathSize) + mMinVertPathSize;
                int offset = sRand.nextInt(pathSize);
                        
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
            for (int k = 0; k < 2; k++) {
                for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
                    for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                        if (i == mRoomPadding+k || i == mRoomHeight-1-mRoomPadding-k 
                                || j == mRoomPadding+k || j == mRoomWidth-1-mRoomPadding-k) {
                            if (sRand.nextFloat() < (mErodeRate / k)) {
                                mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] = mTileset.getWallTile();
                            }
                        }
                    }
                }
            }
            
            // Place features
            int feature;
            for (int i = mRoomPadding; i < mRoomHeight - mRoomPadding; i++) {
                for (int j = mRoomPadding; j < mRoomWidth - mRoomPadding; j++) {
                    if (mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] == mTileset.getFloorTile() &&
                            mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] == 0) {
                        if (sRand.nextFloat() < 0.2f) {
                            //ArrayList<Integer> features = mTileset.getFeatures();
                            feature = mTileset.getRandomFeature();
                            if (feature > 0)
                                mData.get(0)[(mY+i) * mMap.getWidth() + (mX+j)] = feature;
                        }
                    }
                }
            }
            
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
