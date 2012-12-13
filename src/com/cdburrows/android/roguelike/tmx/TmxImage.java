package com.cdburrows.android.roguelike.tmx;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="image")
public class TmxImage {
    
    @Attribute
    private String source;
    
    @Attribute
    private int width;
    
    @Attribute
    private int height;
    
    public TmxImage() {}
    
    public TmxImage(String source, int width, int height) {
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
