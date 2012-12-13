package com.cdburrows.android.roguelike.tmx;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="tileset")
public class TmxTileset {
    
    @Attribute
    private int firstgid;
    
    @Attribute
    private String name;
    
    @Attribute
    private int tilewidth;
    
    @Attribute
    private int tileheight;
    
    @Element(required=false)
    private TmxImage image;
    
    @Attribute(required=false)
    private int spacing;
    
    @Attribute(required=false)
    private int margin;
    
    public TmxTileset() {
        this.firstgid = 1;
        this.name = null;
        this.tilewidth = 32;
        this.tileheight = 32;
        this.image = null;
        this.spacing = 0;
        this.margin = 0;
    }
    
    public TmxTileset(String name, TmxImage image, int firstGid, int tileWidth, int tileHeight) {
        this.name = name;
        this.image = image;
        this.firstgid = firstGid;
        this.tilewidth = tileWidth;
        this.tileheight = tileHeight;
    }
    
    public TmxTileset(String name, int tileWidth, int tileHeight) {
        this.name = name;
        
    }
    
    public int getFirstgid() {
        return firstgid;
    }
    public void setFirstgid(int firstgid) {
        this.firstgid = firstgid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getTilewidth() {
        return tilewidth;
    }
    public void setTilewidth(int tilewidth) {
        this.tilewidth = tilewidth;
    }

    public int getTileheight() {
        return tileheight;
    }
    public void setTileheight(int tileheight) {
        this.tileheight = tileheight;
    }

    public TmxImage getImage() {
        return image;
    }
    public void setImage(TmxImage image) {
        this.image = image;
    }

    public int getSpacing() {
        return spacing;
    }
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getMargin() {
        return margin;
    }
    public void setMargin(int margin) {
        this.margin = margin;
    }
    
    // properties, image, tile
    
    
}