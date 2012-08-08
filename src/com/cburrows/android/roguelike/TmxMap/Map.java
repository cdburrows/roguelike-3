package com.cburrows.android.roguelike.TmxMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

import com.cdburrows.android.roguelike.base.RoguelikeActivity;

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
    
    @ElementList(inline = true, required=false)
    protected List<Tileset> tileset;
    
    @ElementList(inline = true, required=false)
    protected List<Layer> layer;
    
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
    
    public Map(int width, int height) {
        version = 1.0f;
        orientation = "orthogonal";
        this.width = width;
        this.height = height;
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
    public void setTileset(Tileset tileset) { 
        this.tileset = new ArrayList<Tileset>(); 
        this.tileset.add(tileset); 
    } 
    
    public List<Layer> getLayers() { return layer; }
    public void setLayers(List<Layer> layer) { this.layer = layer; }
    public void addLayer(String name) { this.layer.add(new Layer(name, width, height)); }
    public void addLayer(Layer layer) { this.layer.add(layer); }
    
    public void build(ArrayList<int[]> data) {
        this.layer = new ArrayList<Layer>();                         
        for (int l = 0; l < data.size(); l++) {                 
            Layer newLayer = new Layer("layer_" + l, width, height);
            newLayer.setData(data.get(l));
            layer.add(newLayer);
        }
        Log.d("MAP", "LAYERS: " + data.size());
    }
      
    public static TMXTiledMap getTmxTiledMap(Map map) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Persister();
        try {
            PrintWriter writer = new PrintWriter(out);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.flush();
            serializer.write(map, out);
            TMXLoader loader = new TMXLoader(RoguelikeActivity.getContext(), 
                    RoguelikeActivity.getContext().getTextureManager());
            TMXTiledMap result = loader.load(new ByteArrayInputStream(out.toByteArray()));
            out.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Map inflate(InputStream source) {
        
        Serializer serializer = new Persister();

        try {
            Map map = serializer.read(Map.class, source);
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
