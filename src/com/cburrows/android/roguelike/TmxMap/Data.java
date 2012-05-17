package com.cburrows.android.roguelike.TmxMap;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

public class Data {
    
    @ElementList(inline=true)
    private List<Tile> tile;
    
    public Data() {}
    
    public Data(int width, int height) {
        tile = new ArrayList<Tile>();
        for (int i = 0; i < width * height; i++) {
            tile.add(new Tile());
        }
    }

    public List<Tile> getTile() {
        return tile;
    }
    public void setTile(List<Tile> tile) {
        this.tile = tile;
    }
    public void setTile(int index, int gid) {
        tile.get(index).setGid(gid);
    }
    public void setTile(int width, int x, int y, int gid) {
        tile.get((y * width) + x).setGid(gid);
    }

}
