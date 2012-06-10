package com.cburrows.android.roguelike;

import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;


import com.cburrows.android.roguelike.GameMap.RoomState;
import com.cburrows.android.roguelike.TmxMap.Map;
import com.cburrows.android.roguelike.xml.DungeonDefinition;
import com.cburrows.android.roguelike.xml.DungeonFloor;
import com.cburrows.android.roguelike.xml.DungeonMonsterTemplate;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;

public class Dungeon {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static DungeonDefinition sDungeonDefinition;
    
    private static GameMap sGameMap;
    
    private static DungeonFloor sCurrentFloor;
    private static int sCurrentFloorLevel;

    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public Dungeon(String definitionPath) {
        try {
            sDungeonDefinition = DungeonDefinition.inflate(
                    RoguelikeActivity.getContext().getAssets().open("xml/dungeon_definition.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sCurrentFloorLevel = 1;
        sCurrentFloor = sDungeonDefinition.mFloors.get(sCurrentFloorLevel-1);
        sGameMap = generateMap(sCurrentFloor);
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public GameMap getGameMap() { 
        return sGameMap;
    }
    
    public TMXTiledMap getTmxMap() {
        return Map.getTmxTiledMap(sGameMap);
    }
    
    public DungeonFloor getCurrentFloor() { return sCurrentFloor; }
    
    public int getCurrentFloorLevel() { return sCurrentFloorLevel; }

    public TMXLayer getSprite() {
        TMXLayer layer = Map.getTmxTiledMap(sGameMap).getTMXLayers().get(0);
        layer.setScaleCenter(0, 0);
        layer.setScale(RoguelikeActivity.sScaleX, RoguelikeActivity.sScaleY);
        return layer;
    }
    
    public ArrayList<DungeonMonsterTemplate> getMonsterList() { return sCurrentFloor.mMonsters; }
    
    public static int getRoomWidth() { return sCurrentFloor.mRoomWidth; }

    public static int getRoomHeight() { return sCurrentFloor.mRoomHeight; }

    public int getRoomCols() { return sCurrentFloor.mCols / sCurrentFloor.mRoomWidth; }
    
    public int getRoomRows() { return sCurrentFloor.mRows / sCurrentFloor.mRoomHeight; }
    
    public boolean getRoomAccess(int roomX, int roomY, Direction direction) {
        return sGameMap.getRoomAccess(roomX, roomY, direction);
    }

    public RoomState getRoomState(int roomX, int roomY) {
        return sGameMap.getRoomState(roomX, roomY);
    }
    
    public float getTileWidth() { return 32 * RoguelikeActivity.sScaleX; }
    
    public float getTileHeight() { return 32 * RoguelikeActivity.sScaleY; }

    public boolean hasChest(int roomX, int roomY) {
        return sGameMap.hasChest(roomX, roomY);
    }
    
    public void setChest(int roomX, int roomY, boolean value) {
        sGameMap.setChest(roomX, roomY, value);
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private static GameMap generateMap(DungeonFloor floor) {
        GameMap map = new GameMap(floor);
        map.addTileset(sDungeonDefinition.getTileset(floor));
        map.generateMap();
        return map;
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
