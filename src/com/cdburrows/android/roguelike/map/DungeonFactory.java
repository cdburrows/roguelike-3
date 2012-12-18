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

import java.util.Random;

import net.bytten.metazelda.Dungeon;
import net.bytten.metazelda.constraints.CountConstraints;
import net.bytten.metazelda.generators.DungeonGenerator;

import android.util.Log;

import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.tmx.TmxMap;

public class DungeonFactory {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
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
    
    private static Random sRand;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    public static GameMap generateMap(XmlFloor floor, long seed, boolean startAtStairsUp) {
        sRand = new Random(seed);
        
        XmlTileset tileset = DungeonManager.getTileset(floor);
        
        DungeonGenerator dungeonGenerator = new DungeonGenerator(seed, new CountConstraints(floor.mNumRooms, 1, 1));
        net.bytten.metazelda.Dungeon dungeon = null;
        
        boolean accepted = false;
        while (accepted == false) {
            accepted = true;
            dungeonGenerator.generate();
            dungeon = (Dungeon)dungeonGenerator.getDungeon();
            //if (dungeon.getExtentBounds().height() < 7) accepted = false;
            //if (dungeon.getExtentBounds().width() > 9) accepted = false;
        }
        
        int roomCols = dungeon.getExtentBounds().width();
        int roomRows = dungeon.getExtentBounds().height();
        int width = dungeon.getExtentBounds().width() * floor.mRoomWidth;
        int height = dungeon.getExtentBounds().height() * floor.mRoomHeight;

        int[][] tiles = new int[height][width];
        Room[][] rooms = new Room[roomRows][roomCols];
        
        for (int y = 0; y < roomRows; y++) {
            for (int x = 0; x < roomCols; x++) {
                rooms[y][x] = new Room(x * floor.mRoomWidth, y * floor.mRoomHeight);
            }
        }
        
        // Fill in the map with walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               tiles[y][x] = tileset.getWallTile();
            }
        }
        
        int top = -1 * dungeon.getExtentBounds().top;
        int left = -1 * dungeon.getExtentBounds().left;
        
        
        int startX = 0;
        int startY = 0;
        if (startAtStairsUp) {
            startX = left + dungeon.findStart().coords.x;
            startY = top + dungeon.findStart().coords.y;
        } else {
            startX = left + dungeon.findGoal().coords.x;
            startY = top + dungeon.findGoal().coords.y;
        }
        
        for (net.bytten.metazelda.Room room : dungeon.getRooms()) {
            int x = left + room.coords.x;
            int y = top + room.coords.y;
            
            rooms[y][x].translateMZRoom(room);
            if (floor.mDepth == 0) rooms[y][x].setStairsUp(false);
            if (floor.mDepth == DungeonManager.getMaxDepth()) rooms[y][x].setStairsDown(false); 
            place(tiles, floor, rooms[y][x]);
        }
       
        // Connect rooms
        for (int y = 0; y < roomRows; y++) {
            for (int x = 0; x < roomCols; x++) {
                Room room = rooms[y][x];
                if (room.isAccessable(Direction.DIRECTION_DOWN)) buildPath(tiles, floor, room, rooms[y+1][x]);
                if (room.isAccessable(Direction.DIRECTION_RIGHT)) buildPath(tiles, floor, room, rooms[y][x+1]);
            }
        }
             
        dynamicTiles(tiles, tileset);
        
        TmxMap tmxMap = new TmxMap(width, height);
        tmxMap.addTileset(DungeonManager.getTileset(floor).toTmx());
        tmxMap.build(tiles);
        
        sRand = null;
        return new GameMap(tmxMap, rooms, startX, startY);
    }
    
    /**
     * Applies dynamic tiling methods to a map.
     * 
     * @param tiles
     * @param tileset
     */
    private static void dynamicTiles(int[][] tiles, XmlTileset tileset) {
            // Touch up wall edges
           int d = 0, c = 0;
           for (int y = 0; y < tiles.length; y++) {
               for (int x = 0; x < tiles[0].length; x++) {
                   if (tileset.isWallTile(tiles[y][x])) {
                       d = 0;
                       c = 0;
                       if (y > 0 && tileset.isFloorTile(tiles[y-1][x]))  c = (c | TILE_N);
                       if (x > 0 && tileset.isFloorTile(tiles[y][x-1]))  c = (c | TILE_W);
                       if (y < tiles.length-1 && tileset.isFloorTile(tiles[y+1][x]))  c = (c | TILE_S);
                       if (x < tiles[0].length-1 && tileset.isFloorTile(tiles[y][x+1]))  c = (c | TILE_E);
                       
                       if (x > 0 && y > 0 && tileset.isFloorTile(tiles[y-1][x-1])) d = (d | TILE_NW);
                       if (x < tiles[0].length-1 && y > 0 && tileset.isFloorTile(tiles[y-1][x+1])) d = (d | TILE_NE);
                       if (x < 0 && y < tiles.length-1 && tileset.isFloorTile(tiles[y+1][x-1])) d = (d | TILE_SW);
                       if (x < tiles[0].length-1 && y < tiles.length-1 && tileset.isFloorTile(tiles[y+1][x+1])) d = (d | TILE_SE);

                       if (c == 0) {                      
                           tiles[y][x] = tileset.getWallFillTile();
                       
     
                       } else if (c == (TILE_W | TILE_N | TILE_E) )  {
                           tiles[y][x] = tileset.getWallInNTile();
                           
                       } else if (c == (TILE_S | TILE_N | TILE_E) )  {
                           tiles[y][x] = tileset.getWallInETile();
                           
                       } else if (c == (TILE_W | TILE_S | TILE_E) )  {
                           tiles[y][x] = tileset.getWallInSTile();
                           
                       } else if (c == (TILE_W | TILE_N | TILE_S) )  {
                           tiles[y][x] = tileset.getWallInWTile();
                           
                           
                       } else if (c == (TILE_N | TILE_E) && (d & TILE_NE) == TILE_NE)  {
                           tiles[y][x] = tileset.getWallNETile();
                           
                       } else if (c == (TILE_S | TILE_E) && (d & TILE_SE) == TILE_SE)  {
                           tiles[y][x] = tileset.getWallSETile();
                       
                       } else if (c == (TILE_S | TILE_W) && (d & TILE_SW) == TILE_SW)  {
                           tiles[y][x] = tileset.getWallSWTile();
                           
                       } else if (c == (TILE_N | TILE_W) && (d & TILE_NW) == TILE_NW)  {
                           tiles[y][x] = tileset.getWallNWTile();
                           
                           
                       } else if (c == (TILE_N | TILE_E) )  {
                           tiles[y][x] = tileset.getWallNETile();
                           
                       } else if (c == (TILE_S | TILE_E) )  {
                           tiles[y][x] = tileset.getWallSETile();
                       
                       } else if (c == (TILE_S | TILE_W) )  {
                           tiles[y][x] = tileset.getWallSWTile();
                           
                       } else if (c == (TILE_N | TILE_W) )  {
                           tiles[y][x] = tileset.getWallNWTile();
                           
                           
                       } else if (c == TILE_N)  {
                           tiles[y][x] = tileset.getWallNTile();
                       
                       } else if (c == TILE_E)  {
                           tiles[y][x] = tileset.getWallETile();
                           
                       } else if (c == TILE_S)  {
                           tiles[y][x] = tileset.getWallSTile();
                           
                       } else if (c == TILE_W)  {
                           tiles[y][x] = tileset.getWallWTile();
                       
                           
                       } else if (d == TILE_SE)  {
                           tiles[y][x] = tileset.getWallWTile();
                       
                       } else if (d == TILE_SW)  {
                           tiles[y][x] = tileset.getWallETile();
                           
                       } else if (d == TILE_NE)  {
                           tiles[y][x] = tileset.getWallSTile();
                           
                       } else if (d == TILE_NW)  {
                           tiles[y][x] = tileset.getWallWTile();
                           
                       }
                   }
               }
           }
       }    
       
   /**
    * Creates a path between two adjacent Rooms.
    * 
    * @param src the first room to connect
    * @param dest the second room to connect
    */
   private static void buildPath(int[][] tiles, XmlFloor floor, Room src, Room dest) {
       XmlTileset tileset = DungeonManager.getTileset(floor);
       
       // Find the center of each room
       int srcX = src.getX() + (floor.mRoomWidth / 2);
       int srcY = src.getY() + (floor.mRoomHeight / 2);
       int destX = dest.getX() + (floor.mRoomWidth / 2);
       int destY = dest.getY() + (floor.mRoomHeight / 2);
       
       // Build a horizontal path
       for (int i = 0; i < Math.abs(destX - srcX); i++) {             
           int pathSize = 1;                       // how wide the path is
           if (floor.mMaxHorizontalPathSize - floor.mMinHorizontalPathSize > 0) pathSize = 
                   sRand.nextInt(floor.mMaxVerticalPathSize - floor.mMinVerticalPathSize) + floor.mMinVerticalPathSize;
           int offset = sRand.nextInt(pathSize);   // how offset the path is
                   
           if (!tileset.isWalkableTile(tiles[srcY][srcX+i])) {
               tiles[srcY-(pathSize + offset)][srcX+i] = tileset.getWallTile();
               tiles[srcY + (offset+1)][srcX+i] = tileset.getWallTile();
               for (int j = 0; j < pathSize; j++) {
                  tiles[srcY - (pathSize - offset - (j+1))][srcX+i] = tileset.getFloorTile();
               }  
           }
       }
       
       // Build a vertical path
       for (int i = 0; i < Math.abs(destY - srcY); i++) {     
           int pathSize = 1;
           if (floor.mMaxVerticalPathSize - floor.mMinVerticalPathSize > 0) pathSize = 
                   sRand.nextInt(floor.mMaxVerticalPathSize - floor.mMinVerticalPathSize) + floor.mMinVerticalPathSize;
           int offset = sRand.nextInt(pathSize);
                   
           if (!tileset.isWalkableTile(tiles[srcY+i][srcX])) {
               tiles[srcY+i][srcX - (pathSize + offset)] = tileset.getWallTile();
               tiles[srcY+i][srcX + (offset+1)] = tileset.getWallTile();
               for (int j = 0; j < pathSize; j++) {
                   tiles[srcY+i][srcX - (pathSize - offset - (j+1))] = tileset.getFloorTile();
               }  
           }
       }
   }
   
   /**
    * Adds a Room to the map and customizes its appearance.
    * 
    * @param room the Room to add
    */
   private static void place(int[][] tiles, XmlFloor floor, Room room) {
       XmlTileset tileset = DungeonManager.getTileset(floor);
       
       // Build floor
       for (int i = floor.mRoomPadding; i < floor.mRoomHeight - floor.mRoomPadding; i++) {
           for (int j = floor.mRoomPadding; j < floor.mRoomWidth - floor.mRoomPadding; j++) {
               if (i == floor.mRoomPadding || i == floor.mRoomHeight-1-floor.mRoomPadding 
                       || j == floor.mRoomPadding || j == floor.mRoomWidth-1-floor.mRoomPadding) {
                   tiles[room.getY()+i][room.getX()+j] = tileset.getWallTile();
               } else {
                   tiles[room.getY()+i][room.getX()+j] = tileset.getFloorTile();
               }
           }
       }
       
       // Degrade walls
       for (int k = 0; k < 2; k++) {   // iterations
           for (int i = floor.mRoomPadding; i < floor.mRoomHeight - floor.mRoomPadding; i++) {
               for (int j = floor.mRoomPadding; j < floor.mRoomWidth - floor.mRoomPadding; j++) {
                   if (i == floor.mRoomPadding+k || i == floor.mRoomHeight-1-floor.mRoomPadding-k 
                           || j == floor.mRoomPadding+k || j == floor.mRoomWidth-1-floor.mRoomPadding-k) {
                       if (sRand.nextFloat() < (floor.mErodeRate / k)) {
                           tiles[room.getY()+i][room.getX()+j] = tileset.getWallTile();
                       }
                   }
               }
           }
       }
       
       // Place features
       int feature;
       for (int i = floor.mRoomPadding; i < floor.mRoomHeight - floor.mRoomPadding; i++) {
           for (int j = floor.mRoomPadding; j < floor.mRoomWidth - floor.mRoomPadding; j++) {
               if (tiles[room.getY()+i][room.getX()+j] == tileset.getFloorTile()) {
                   if (sRand.nextFloat() < FEATURE_RATE) {
                       feature = tileset.getRandomFeature();
                       if (feature > 0) {
                           tiles[room.getY()+i][room.getX()+j] = feature;
                       }
                   }
               }
           }
       }
       
       if (room.hasStairsDown() && floor.mDepth < DungeonManager.getMaxDepth()-1) {
           tiles[room.getY()+floor.mRoomHeight/2][room.getX()+floor.mRoomWidth/2] = tileset.getStairsDown();
           Log.d("MAP", "stairs down " + (room.getX()/floor.mRoomWidth)  + ", " + (room.getY()/floor.mRoomHeight));
       }
       
       if (room.hasStairsUp() && floor.mDepth > 0) {
           tiles[room.getY()+floor.mRoomHeight/2][room.getX()+floor.mRoomWidth/2] = tileset.getStairsUp();
           Log.d("MAP", "stairs up " + (room.getX()/floor.mRoomWidth)  + ", " + (room.getY()/floor.mRoomHeight));
       }

   }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
}