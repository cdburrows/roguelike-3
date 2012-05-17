package com.cburrows.android.roguelike.TmxMap;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class Tile {
    
    @Attribute
    private int gid;
    
    public Tile() {
        this.gid = 0; 
    }

    public int getGid() {
        return gid;
    }
    public void setGid(int gid) {
        this.gid = gid;
    }

}
