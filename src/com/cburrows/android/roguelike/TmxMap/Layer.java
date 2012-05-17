package com.cburrows.android.roguelike.TmxMap;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class Layer {
    
    @Attribute
    private String name;
    
    @Attribute
    private int width;
    
    @Attribute
    private int height;
    
    @Element
    private Data data;
    
    public Layer() {}
    
    public Layer(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.data = new Data(width, height);
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public List<Tile> getData() { return data.getTile(); }
    public void setData(Data data) {
        this.data = data;
    }
    public void setData(int index, int gid) {
        this.data.setTile(index, gid);
    }
    public void setData(int x, int y, int gid) {
        this.data.setTile(width, x, y, gid);
    }

}
