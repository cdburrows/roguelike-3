package com.cburrows.android.roguelike;

import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;

import base.RoguelikeActivity;

import com.cburrows.android.roguelike.TmxMap.Map;
import com.cburrows.android.roguelike.xml.DungeonDefinition;
import com.cburrows.android.roguelike.xml.DungeonFloor;
import com.cburrows.android.roguelike.xml.DungeonMonsterTemplate;

public class Dungeon {
    
    private static DungeonDefinition sDungeonDefinition;
    
    private static GameMap sGameMap;
    
    private static DungeonFloor sCurrentFloor;
    private static int sCurrentFloorLevel;
    //private static String sTilesetName;
    //private static String sBattleBackgroundName;
    
    
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
    
    public GameMap getGameMap() { 
        return sGameMap;
    }
    
    public TMXTiledMap getTmxMap() {
        return Map.getTmxTiledMap(sGameMap);
    }
    
    public DungeonFloor getCurrentFloor() { return sCurrentFloor; }

    private static GameMap generateMap(DungeonFloor floor) {
        GameMap map = new GameMap(floor);
        map.addTileset(sDungeonDefinition.getTileset(floor));
        map.generateMap();
        return map;
    }
    
    public TMXLayer getSprite() {
        TMXLayer layer = Map.getTmxTiledMap(sGameMap).getTMXLayers().get(0);
        layer.setScaleCenter(0, 0);
        layer.setScale(RoguelikeActivity.sScaleX, RoguelikeActivity.sScaleY);
        return layer;
    }
    
    public ArrayList<DungeonMonsterTemplate> getMonsterList() {
        return sCurrentFloor.mMonsters;
    }
    
    public static int getRoomWidth() {
        return sCurrentFloor.mRoomWidth;
    }

    public static int getRoomHeight() {
        return sCurrentFloor.mRoomHeight;
    }

}
