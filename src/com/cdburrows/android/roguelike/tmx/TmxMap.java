package com.cdburrows.android.roguelike.tmx;

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

import com.cdburrows.android.roguelike.RoguelikeActivity;

@Root(name="map")
public class TmxMap {
    
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
    protected List<TmxTileset> tileset;
    
    @ElementList(inline = true, required=false)
    protected List<TmxLayer> layer;
    
    public TmxMap() {
        version = 1.0f;
        orientation = "orthogonal";
        width = 40;
        height = 30;
        tileheight = 32;
        tilewidth = 32;
        tileset = new ArrayList<TmxTileset>();
        layer = new ArrayList<TmxLayer>();
    }
    
    public TmxMap(int width, int height) {
        version = 1.0f;
        orientation = "orthogonal";
        this.width = width;
        this.height = height;
        tileheight = 32;
        tilewidth = 32;
        tileset = new ArrayList<TmxTileset>();
        layer = new ArrayList<TmxLayer>();
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
    
    public List<TmxTileset> getTilesets() { return tileset; }
    public void setTilesets(List<TmxTileset> tileset) { this.tileset = tileset; }
    public void addTileset(TmxTileset tileset) { this.tileset.add(tileset); }
    public void setTileset(TmxTileset tileset) { 
        this.tileset = new ArrayList<TmxTileset>(); 
        this.tileset.add(tileset); 
    } 
    
    public List<TmxLayer> getLayers() { return layer; }
    public void setLayers(List<TmxLayer> layer) { this.layer = layer; }
    public void addLayer(String name) { this.layer.add(new TmxLayer(name, width, height)); }
    public void addLayer(TmxLayer layer) { this.layer.add(layer); }
    
    public void build(ArrayList<int[]> data) {
        this.layer = new ArrayList<TmxLayer>();                         
        for (int l = 0; l < data.size(); l++) {                 
            TmxLayer newLayer = new TmxLayer("layer_" + l, width, height);
            newLayer.setData(data.get(l));
            layer.add(newLayer);
        }
        Log.d("MAP", "LAYERS: " + data.size());
    }
      
    public static TMXTiledMap getTmxTiledMap(TmxMap map) {
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
    
    public static TmxMap inflate(InputStream source) {
        
        Serializer serializer = new Persister();

        try {
            TmxMap map = serializer.read(TmxMap.class, source);
            return map;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;        
    }
    
    public static void deflate(TmxMap map, OutputStream dest) {
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
