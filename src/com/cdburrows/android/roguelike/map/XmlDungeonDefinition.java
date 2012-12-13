package com.cdburrows.android.roguelike.map;

import java.io.InputStream;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.tmx.TmxMap;
import com.cdburrows.android.roguelike.tmx.TmxTileset;

@Root(name="dungeon_definition")
public class XmlDungeonDefinition {
    
    @ElementList(name="tileset", inline=true)
    private ArrayList<XmlDungeonTileset> mTilesets;
    
    @ElementList(name="floor", inline=true)
    private ArrayList<XmlDungeonFloor> mFloors;
    
    public XmlDungeonDefinition() {
        mTilesets = new ArrayList<XmlDungeonTileset>();
        mFloors = new ArrayList<XmlDungeonFloor>();
    }
    
    public static XmlDungeonDefinition inflate(InputStream source) {
        Serializer serializer = new Persister();
        try {
            XmlDungeonDefinition dungeon = serializer.read(XmlDungeonDefinition.class, source);
            
            return dungeon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;        
    }
    
    public ArrayList<XmlDungeonTileset> getDungeonTilesets() {
        return mTilesets;
    }
    
    public XmlDungeonTileset getDungeonTileset(XmlDungeonFloor floor) {
        String id = floor.mTilesetName;
        for (XmlDungeonTileset t : mTilesets) {
            if (t.mName.equals(id)) return t;
        }
        return null;
    }
    
    public TmxTileset getTmxTileset(XmlDungeonFloor floor) {
        String id = floor.mTilesetName;
        for (XmlDungeonTileset t : mTilesets) {
            if (t.mName.equals(id)) return t.toTmx();
        }
        return null;
    }

    public XmlDungeonFloor getFloor(int floor) {
       return mFloors.get(floor);
    }
}


