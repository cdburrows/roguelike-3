package com.cburrows.android.roguelike.TmxMap;

import org.simpleframework.xml.Attribute;

public class Image {
    
    @Attribute
    private String source;
    
    @Attribute
    private int width;
    
    @Attribute
    private int height;
    
    public Image() {}
    
    public Image(String source, int width, int height) {
        this.source = source;
        this.width = width;
        this.height = height;
    }

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
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
    
}
