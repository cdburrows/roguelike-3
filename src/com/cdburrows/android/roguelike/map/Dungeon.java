package com.cdburrows.android.roguelike.map;

import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;


import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.map.GameMap.RoomState;
import com.cdburrows.android.roguelike.monster.XmlDungeonMonsterTemplate;
import com.cdburrows.android.roguelike.tmx.TmxMap;

public class Dungeon {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static XmlDungeonDefinition sDungeonDefinition;
    
    private static GameMap sGameMap;
    
    private static XmlDungeonFloor sCurrentFloor;
    private static int sCurrentFloorLevel;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public Dungeon(String definitionPath) {
        try {
            sDungeonDefinition = XmlDungeonDefinition.inflate(
                    RoguelikeActivity.getContext().getAssets().open("xml/dungeon_definition.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sCurrentFloorLevel = 1;
        sCurrentFloor = sDungeonDefinition.getFloor(sCurrentFloorLevel-1);
        sGameMap = generateMap(sCurrentFloor);
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public GameMap getGameMap() { 
        return sGameMap;
    }
    
    public TMXTiledMap getTmxMap() {
        return sGameMap.getTmxTiledMap();
    }
    
    public XmlDungeonFloor getCurrentFloor() { return sCurrentFloor; }
    
    public int getCurrentFloorLevel() { return sCurrentFloorLevel; }

    public TMXLayer getSprite(int layer) {
        TMXLayer l = sGameMap.getTmxTiledMap().getTMXLayers().get(layer);
        l.setScaleCenter(0, 0);
        l.setScale(RoguelikeActivity.sScaleX, RoguelikeActivity.sScaleY);
        return l;
    }
    
    public ArrayList<XmlDungeonMonsterTemplate> getMonsterList() { return sCurrentFloor.mMonsters; }
    
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
    
    private static GameMap generateMap(XmlDungeonFloor floor) {
        GameMap map = new GameMap(floor, sDungeonDefinition.getDungeonTileset(floor));
        //map.addTileset(sDungeonDefinition.getTmxTileset(floor));
        map.generateMap();
        return map;
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
