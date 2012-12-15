package com.cdburrows.android.roguelike.tmx;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="layer")
public class TmxLayer {
    
    @Attribute
    private String name;
    
    @Attribute
    private int width;
    
    @Attribute
    private int height;
    
    @Element
    private TmxData data;
    
    public TmxLayer() {}
    
    public TmxLayer(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.data = new TmxData(width, height);
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
    
    public void setData(int[] data) {
        this.data.setValue(data);
    }

    public void setData(int[][] data) {
        this.data.setValue(data);
        
    }

}
