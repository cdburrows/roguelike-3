package com.cburrows.android.roguelike;

import java.util.ArrayList;

import org.simpleframework.xml.Root;

import com.cburrows.android.roguelike.TmxMap.Image;
import com.cburrows.android.roguelike.TmxMap.Tileset;
import com.cburrows.android.roguelike.TmxMap.Map;

@Root(name="map")
public class GameMap extends Map {
    private static final int TILE_WALL = 3;
    private static final int TILE_FLOOR = 31;
    
    public static final int ROOM_WIDTH = 11;
    public static final int ROOM_HEIGHT = 9;
    public static final int ROOM_PADDING = 2;
    
    private int[] data;
    
    private ArrayList<Room> mRooms;
    private int mNumRoomsX;
    private int mNumRoomsY;
    
    public GameMap(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        addTileset(new Tileset("dungeon_tiles", 
                new Image("gfx/dungeon_tiles.png", 960, 96),
                1, 32, 32));
        
        mNumRoomsX = width / ROOM_WIDTH;
        mNumRoomsY = height / ROOM_HEIGHT;
        
        data = new int[width * height];       
        mRooms = new ArrayList<Room>();
                
        for (int y = 0; y < mNumRoomsY; y++) {
            for (int x = 0; x < mNumRoomsX; x++) {
                Room room = new Room(x * ROOM_WIDTH, y * ROOM_HEIGHT);
                
                placeRoom(data, x * ROOM_WIDTH, y * ROOM_HEIGHT);
                
                if (y == 0) room.setAccessable(Direction.DIRECTION_UP, false);
                if (x == 0) room.setAccessable(Direction.DIRECTION_LEFT, false);
                if (y == mNumRoomsY-1) room.setAccessable(Direction.DIRECTION_DOWN, false); 
                if (x == mNumRoomsX-1) room.setAccessable(Direction.DIRECTION_RIGHT, false);
                                    
                mRooms.add(room);
            }
        }
        
        //Log.d("MAP", "Rooms Y " + mNumRoomsY);
        for (int y = 0; y < mNumRoomsY; y++) {
            for (int x = 0; x < mNumRoomsX; x++) {
                Room room = mRooms.get((y * mNumRoomsY) + x);
                
                //if (y != 0) room.buildPath(mRooms.get(((y-1) * mNumRoomsY) + x));
                //if (x != 0) room.buildPath(mRooms.get((y * mNumRoomsY) + x-1));
                if (y != mNumRoomsY-1) room.buildPath(mRooms.get(((y+1) * mNumRoomsX) + x));
                if (x != mNumRoomsX-1) room.buildPath(mRooms.get((y * mNumRoomsX) + x+1));
                                    
                mRooms.add(room);
            }
        }
        
        build(data);
    }
    
    public void placeRoom(int[] data, int x, int y) {
        for (int i = ROOM_PADDING; i < ROOM_HEIGHT - ROOM_PADDING; i++) {
            for (int j = ROOM_PADDING; j < ROOM_WIDTH - ROOM_PADDING; j++) {
                if (i == ROOM_PADDING || i == ROOM_HEIGHT-1-ROOM_PADDING 
                        || j == ROOM_PADDING || j == ROOM_WIDTH-1-ROOM_PADDING) {
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
    
    private class Room {
        
        public int mX = 0;
        public int mY = 0;
        public boolean[] mAccessable = new boolean[4];
        
        public Room(int x, int y) {
            mX = x;
            mY = y;
            for (int i = 0; i < 4; i++) mAccessable[i] = true;
        }
        
        public void buildPath(Room dest) {
            int srcX = mX + (ROOM_WIDTH / 2);
            int srcY = mY + (ROOM_HEIGHT / 2);
            int destX = dest.mX + (ROOM_WIDTH / 2);
            int destY = dest.mY + (ROOM_HEIGHT / 2);
            
            //Log.d("MAP", "Path from " + srcX + ", " + srcY + " to " + destX + ", " + destY);
            
            for (int i = 0; i < Math.abs(destX - srcX); i++) {                
                if (destX - srcX > 0) {
                    if (data[(srcY * width) + srcX + i] != TILE_FLOOR) {
                        data[(srcY * width) + srcX + i] = TILE_FLOOR;
                        data[((srcY-1) * width) + srcX + i] = TILE_WALL;
                        data[((srcY+1) * width) + srcX + i] = TILE_WALL;
                    }
                }
            }
            
            for (int i = 0; i < Math.abs(destY - srcY); i++) {                
                //if (destY - srcY > 0) {
                    if (data[((srcY+i) * width) + srcX] != TILE_FLOOR) {
                        data[((srcY+i) * width) + srcX] = TILE_FLOOR;
                        data[((srcY+i) * width) + srcX+1] = TILE_WALL;
                        data[((srcY+i) * width) + srcX-1] = TILE_WALL;
                    }
                //}
            }
        }
        
        //*************************************************************
        // Getters and setters;
        //*************************************************************

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
        
        public boolean isAccessable(Direction direction) {
            return mAccessable[direction.getValue()];
        }
        
        public void setAccessable(Direction direction, boolean value) {
            mAccessable[direction.getValue()] = value;
        }
    }

}
