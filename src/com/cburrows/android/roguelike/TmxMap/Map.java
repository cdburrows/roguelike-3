package com.cburrows.android.roguelike.TmxMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import android.content.Context;
import android.util.Log;

@Root
public class Map {
    
    @Attribute
    protected float version;
    
    @Attribute
    protected String orientation;
    
    @Attribute
    protected int width;
    
    @Attribute
    protected int height;
    
    @Attribute
    protected int tilewidth;
    
    @Attribute
    protected int tileheight;
    
    //@ElementList(required=false)
    //private List<Properties> properties;
    
    @ElementList(inline = true, required=false)
    protected List<Tileset> tileset;
    
    @ElementList(inline = true, required=false)
    protected List<Layer> layer;
    
    //@ElementList(required=false)
    //private List<ObjectGroup> objectgroup;
    
    public Map() {
        version = 1.0f;
        orientation = "orthogonal";
        width = 40;
        height = 30;
        tileheight = 32;
        tilewidth = 32;
        tileset = new ArrayList<Tileset>();
        layer = new ArrayList<Layer>();
    }

    public float getVersion() { return version; }
    public void setVersion(float version) { this.version = version; }
    
    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) { this.orientation = orientation; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public int getTileWidth() { return tilewidth; }
    public void setTileWidth(int tilewidth) { this.tilewidth = tilewidth; }
    
    public int getTileHeight() { return tileheight; }
    public void setTileHeight(int tileheight) { this.tileheight = tileheight; }
    
    public List<Tileset> getTilesets() { return tileset; }
    public void setTilesets(List<Tileset> tileset) { this.tileset = tileset; }
    public void addTileset(Tileset tileset) { this.tileset.add(tileset); }
    
    public List<Layer> getLayers() { return layer; }
    public void setLayers(List<Layer> layer) { this.layer = layer; }
    public void addLayer(String name) { this.layer.add(new Layer(name, width, height)); }
    public void addLayer(Layer layer) { this.layer.add(layer); }
    
    public boolean setTile(int layer, int x, int y, int gid) {
        if (layer >= 0 && layer < this.layer.size() && 
                x >= 0 && x < width && y > 0 && y < height) {
            this.layer.get(layer).setData(x, y, gid);
            return true;
        }
        return false;
    }
    
    public void build(int[] data) {
        this.layer = new ArrayList<Layer>();                         
        for (int l = 0; l < 1; l++) {                 // layer
            Layer newLayer = new Layer("layer_" + l, width, height);
            for (int i = 0; i < width * height; i++) {
                newLayer.setData(i, data[i]);
            }
            layer.add(newLayer);
        }
    }
      
    public TMXTiledMap getTmxTiledMap(Context context, TextureManager textureManager) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Persister();
        try {
            PrintWriter writer = new PrintWriter(out);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.flush();
            serializer.write(this, out);
            TMXLoader loader = new TMXLoader(context, textureManager);
            TMXTiledMap map = loader.load(new ByteArrayInputStream(out.toByteArray()));
            out.close();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Map inflate(InputStream source) {
        
        Serializer serializer = new Persister();

        try {
            Map map = serializer.read(Map.class, source);
                        
            Log.d("MAP", map.version + ", " + map.orientation + ", " + map.width + ", " + map.height + ", " + map.layer.get(0).getData().size());
            
            return map;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;        
    }
    
    public static void deflate(Map map, OutputStream dest) {
        Serializer serializer = new Persister();
        try {
            PrintWriter writer = new PrintWriter(dest);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.flush();
            serializer.write(map, dest);
            writer.close();
            dest.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
