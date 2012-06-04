package com.cburrows.android.roguelike.TmxMap;

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
    
    public void setData(int[] data) {
        this.data.setValue(data);
    }

}
