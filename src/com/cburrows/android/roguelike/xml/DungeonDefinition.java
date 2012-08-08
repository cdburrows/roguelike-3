package com.cburrows.android.roguelike.xml;

import java.io.InputStream;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

import com.cburrows.android.roguelike.TmxMap.Map;
import com.cburrows.android.roguelike.TmxMap.Tileset;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;

@Root(name="dungeon_definition")
public class DungeonDefinition {
    
    @ElementList(name="tileset", inline=true)
    private ArrayList<DungeonTileset> mTilesets;
    
    @ElementList(name="floor", inline=true)
    private ArrayList<DungeonFloor> mFloors;
    
    public DungeonDefinition() {
        mTilesets = new ArrayList<DungeonTileset>();
        mFloors = new ArrayList<DungeonFloor>();
    }
    
    public static DungeonDefinition inflate(InputStream source) {
        Serializer serializer = new Persister();
        try {
            DungeonDefinition dungeon = serializer.read(DungeonDefinition.class, source);
            return dungeon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;        
    }
    
    public ArrayList<DungeonTileset> getDungeonTilesets() {
        return mTilesets;
    }
    
    public DungeonTileset getDungeonTileset(DungeonFloor floor) {
        String id = floor.mTilesetName;
        for (DungeonTileset t : mTilesets) {
            if (t.mName.equals(id)) return t;
        }
        return null;
    }
    
    public Tileset getTmxTileset(DungeonFloor floor) {
        String id = floor.mTilesetName;
        for (DungeonTileset t : mTilesets) {
            if (t.mName.equals(id)) return t.toTmx();
        }
        return null;
    }

    public DungeonFloor getFloor(int floor) {
       return mFloors.get(floor);
    }
}


