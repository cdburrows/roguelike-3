package com.cdburrows.android.roguelike.tmx;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="tile")
public class TmxTile {
    
    @Attribute
    private int gid;
    
    public TmxTile() {
        this.gid = 0; 
    }

    public int getGid() {
        return gid;
    }
    public void setGid(int gid) {
        this.gid = gid;
    }

}
