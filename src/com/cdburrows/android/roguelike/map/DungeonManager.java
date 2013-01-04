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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;

import android.util.Log;

import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.map.Room.RoomState;
import com.cdburrows.android.roguelike.monster.XmlDungeonMonsterTemplate;

public class DungeonManager {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private static XmlDungeonDefinition sDungeonDefinition;

    private static GameMap sGameMap;

    private static long[] sMapSeeds;

    private static XmlFloor sCurrentFloor;

    private static int sCurrentFloorLevel = 0;

    private static XmlMap[] sSavedFloors;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public static GameMap getGameMap() {
        return sGameMap;
    }

    public static TMXTiledMap getTmxMap() {
        return sGameMap.getTmxTiledMap();
    }

    public static XmlFloor getCurrentFloor() {
        return sCurrentFloor;
    }

    public static int getCurrentDepth() {
        return sCurrentFloorLevel;
    }

    public static int getMaxDepth() {
        return sDungeonDefinition.getMaxDepth();
    }

    public static TMXLayer getSprite(int layer) {
        TMXLayer l = sGameMap.getSprite(0);
        l.setScaleCenter(0, 0);
        l.setScale(RoguelikeActivity.sScaleX, RoguelikeActivity.sScaleY);
        return l;
    }

    public static ArrayList<XmlDungeonMonsterTemplate> getMonsterList() {
        return sCurrentFloor.mMonsters;
    }

    public static int getRoomWidth() {
        return sCurrentFloor.mRoomWidth;
    }

    public static int getRoomHeight() {
        return sCurrentFloor.mRoomHeight;
    }

    public static int getRoomCols() {
        return sGameMap.getRoomCols();
    }

    public static int getRoomRows() {
        return sGameMap.getRoomRows() / sCurrentFloor.mRoomHeight;
    }

    public static boolean getRoomAccess(int roomX, int roomY, Direction direction) {
        return sGameMap.getRoomAccess(roomX, roomY, direction);
    }

    public static RoomState getRoomState(int roomX, int roomY) {
        return sGameMap.getRoomState(roomX, roomY);
    }

    public static float getTileWidth() {
        return 32 * RoguelikeActivity.sScaleX;
    }

    public static float getTileHeight() {
        return 32 * RoguelikeActivity.sScaleY;
    }

    public static boolean hasChest(int roomX, int roomY) {
        return sGameMap.hasChest(roomX, roomY);
    }

    public static void setChest(int roomX, int roomY, boolean value) {
        sGameMap.setChest(roomX, roomY, value);
    }

    public static XmlTileset getTileset(XmlFloor floor) {
        return sDungeonDefinition.getDungeonTileset(floor);
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public static void initialize(String definitionPath) {
        try {
            sDungeonDefinition = XmlDungeonDefinition.inflate(RoguelikeActivity.getContext()
                    .getAssets().open(definitionPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random rand = new Random(0); // put seed here?
        sSavedFloors = new XmlMap[sDungeonDefinition.getMaxDepth()];

        sMapSeeds = new long[sDungeonDefinition.getMaxDepth()];

        for (int i = 0; i < sDungeonDefinition.getMaxDepth(); i++) {
            sMapSeeds[i] = rand.nextLong();
        }

        loadFloor(sCurrentFloorLevel, true);
    }

    public static int getStartX() {
        return sGameMap.getStartX();
    }

    public static int getStartY() {
        return sGameMap.getStartY();
    }

    private static GameMap generateMap(XmlFloor floor, long seed, boolean goingDown) {
        return DungeonFactory.generateMap(floor, seed, goingDown);
    }

    public static boolean interact(int roomX, int roomY) {
        if (sGameMap.hasStairsUp(roomX, roomY)) {
            travelUpStairs();
            return true;
        } else if (sGameMap.hasStairsDown(roomX, roomY)) {
            travelDownStairs();
            return true;
        }
        return false;
    }

    private static void travelUpStairs() {
        if (sCurrentFloorLevel == 0)
            return;

        RoguelikeActivity.pause();
        saveMap();
        sCurrentFloorLevel--;
        loadFloor(sCurrentFloorLevel, false);
        RoguelikeActivity.resume();
    }

    private static void travelDownStairs() {
        if (sCurrentFloorLevel == sDungeonDefinition.getMaxDepth())
            return;

        RoguelikeActivity.pause();
        saveMap();
        sCurrentFloorLevel++;
        loadFloor(sCurrentFloorLevel, true);
        RoguelikeActivity.resume();
    }

    private static void saveMap() {
        if (sGameMap != null) {
            // FileOutputStream fos;
            // try {
            // Log.d("DUNGEON MANAGER", "Saving " + sCurrentFloorLevel);
            sSavedFloors[sCurrentFloorLevel] = new XmlMap(sGameMap.getRooms()); // "map_"+sCurrentFloorLevel+".map";
            // fos =
            // RoguelikeActivity.getOutputStream("map_"+sCurrentFloorLevel+".map");
            // XmlMap.deflate(sGameMap.getRooms(), fos);
            // } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
        }
    }

    private static void loadFloor(int floorLevel, boolean goingDown) {
        if (floorLevel < 0 || floorLevel >= sDungeonDefinition.getMaxDepth()) {
            return;
        }

        Log.d("DUNGEON MANAGER", "Current floor = " + sCurrentFloorLevel + " Loading '"
                + sSavedFloors[floorLevel] + "'");

        boolean firstLoad = sGameMap == null;

        if (!firstLoad) {
            // SceneManager.pushScene(new LoadingScene());
            sGameMap.unload();
            Log.d("DUNGEON MANAGER", "FIRST LOAD");
        }

        sCurrentFloorLevel = floorLevel;
        sCurrentFloor = sDungeonDefinition.getFloor(sCurrentFloorLevel);
        sGameMap = generateMap(sCurrentFloor, sMapSeeds[sCurrentFloorLevel], goingDown);

        if (sSavedFloors[floorLevel] != null) {
            sGameMap.setRooms(sSavedFloors[sCurrentFloorLevel].mRooms);
        }

        RoguelikeActivity.reloadBattleBackground();

        // if (!firstLoad) SceneManager.popScene();

    }

    public static String getBattleBackground() {
        return sCurrentFloor.mBattleBackgroundName;
    }

    public static XmlTileset getTileset(int depth) {
        return getTileset(sDungeonDefinition.getFloor(depth));
    }

    public static void end() {
        sDungeonDefinition = null;
        sGameMap = null;
        sMapSeeds = null;
        sCurrentFloor = null;
        sSavedFloors = null;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
