package com.cdburrows.android.roguelike.map;

import java.io.InputStream;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.cdburrows.android.roguelike.tmx.TmxTileset;

@Root(name="dungeon_definition")
public class XmlDungeonDefinition {
    
    @ElementList(name="tileset", inline=true)
    private ArrayList<XmlTileset> mTilesets;
    
    @ElementList(name="floor", inline=true)
    private ArrayList<XmlFloor> mFloors;
    
    public XmlDungeonDefinition() {
        mTilesets = new ArrayList<XmlTileset>();
        mFloors = new ArrayList<XmlFloor>();
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
    
    public ArrayList<XmlTileset> getDungeonTilesets() {
        return mTilesets;
    }
    
    public XmlTileset getDungeonTileset(XmlFloor floor) {
        String id = floor.mTilesetName;
        for (XmlTileset t : mTilesets) {
            if (t.mName.equals(id)) return t;
        }
        return null;
    }
    
    public TmxTileset getTmxTileset(XmlFloor floor) {
        String id = floor.mTilesetName;
        for (XmlTileset t : mTilesets) {
            if (t.mName.equals(id)) return t.toTmx();
        }
        return null;
    }

    public XmlFloor getFloor(int floor) {
       return mFloors.get(floor);
    }

    public int getMaxDepth() {
        return mFloors.size();
    }
}


