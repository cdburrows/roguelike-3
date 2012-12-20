package com.cdburrows.android.roguelike.tmx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.map.XmlTileset;

@Root(name="map")
public class TmxMap {
    
    private static final int TILE_NW = 16;
    private static final int TILE_N = 1;
    private static final int TILE_NE = 32;
    private static final int TILE_W = 2;
    private static final int TILE_E = 4;
    private static final int TILE_SW = 64;
    private static final int TILE_S = 8;
    private static final int TILE_SE = 128;
    
    public static int TILESET_COLS = 8;
    
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

    public int getWidth() { return width / 2; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height / 2; }
    public void setHeight(int height) { this.height = height; }
    
    public int getTileWidth() { return tilewidth * 2; }
    public void setTileWidth(int tilewidth) { this.tilewidth = tilewidth; }
    
    public int getTileHeight() { return tileheight * 2; }
    public void setTileHeight(int tileheight) { this.tileheight = tileheight; }
    
    public void addTileset(TmxTileset tileset) { this.tileset.add(tileset); }

    
    public List<TmxLayer> getLayers() { return layer; }
    public void setLayers(List<TmxLayer> layer) { this.layer = layer; }
    public void addLayer(String name) { this.layer.add(new TmxLayer(name, width, height)); }
    public void addLayer(TmxLayer layer) { this.layer.add(layer); }
      
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
    
    public void buildDynamicMap(int[][] data, int depth) {
        tileset.get(0).setTileheight(16);
        tileset.get(0).setTilewidth(16);
        width = width * 2;
        height = height * 2;
        tileheight = tileheight / 2;
        tilewidth = tilewidth / 2;

        int[][] dynamicData = constructDynamicTiles(data, depth);

        this.layer = new ArrayList<TmxLayer>();                         
        for (int l = 0; l < 1; l++) {                 
            TmxLayer newLayer = new TmxLayer("layer_" + l, width, height);
            newLayer.setData(dynamicData);
            layer.add(newLayer);
        }
    }
    
    private int[][] constructDynamicTiles(int[][] data, int depth) {
        
        Log.d("DYNAMIC", "Start");
        
        int[][] tiles = new int[height][width];
        int[][] tileType = new int[height/2][width/2];
        
        for (int y = 0; y < height/2; y++) {
            for (int x = 0; x < width/2; x++) {
                int tile = data[y][x];
                tileType[y][x] = tile;
                
                int col = (tile-1) % (TILESET_COLS);
                int row = ((tile - 1) / TILESET_COLS);
                
                tiles[y*2][x*2] = (row*32)+(2*col)+1;
                tiles[y*2][x*2+1] = (row*32)+(2*col)+2;
                tiles[y*2+1][x*2] = (row*32)+(2*col)+1 + (2*TILESET_COLS);
                tiles[y*2+1][x*2+1] = (row*32)+(2*col)+2 + (2*TILESET_COLS); 
            }
        }
        
        
        XmlTileset t = DungeonManager.getTileset(depth);
        
        for (int y = 0; y < height/2; y++) {
            for (int x = 0; x < width/2; x++) {
                int type = tileType[y][x];
                if (!t.isRoofTile(type)) continue;

                int c = 0; // cardinal
                
                if (compareType(tileType, type, x, y-1)) c = (c | TILE_N);
                if (compareType(tileType, type, x-1, y)) c = (c | TILE_W);
                if (compareType(tileType, type, x, y+1)) c = (c | TILE_S);
                if (compareType(tileType, type, x+1, y)) c = (c | TILE_E);
                
                if (compareType(tileType, type, x-1, y-1)) c = (c | TILE_NW);
                if (compareType(tileType, type, x+1, y-1)) c = (c | TILE_NE);
                if (compareType(tileType, type, x-1, y+1)) c = (c | TILE_SW);
                if (compareType(tileType, type, x+1, y+1)) c = (c | TILE_SE);
                
                if (t.isSimple()) {
                    simpleTile(c, x, y, type, tileType, tiles);
                } else {
                    standardTile(c, x, y, type, tileType, tiles);
                }
            }
        }
        
        Log.d("DYNAMIC", "End");
        
        return tiles;
    }
    
    private boolean compareType(int[][] tileType, int type, int x, int y) {
        if (y < 0) return true;
        if (y >= tileType.length-1) return true;
        if (x < 0) return true;
        if (x >= tileType[0].length-1) return true;
        
        return tileType[y][x] == type;
    }

    private void standardTile(int c, int x, int y, int type, int[][] tileType, int[][] tiles) {
        int nw = 0;
        int ne = 0;
        int sw = 0;
        int se = 0;
        
        if (contains(c, TILE_N | TILE_W | TILE_NW)){
            nw = 7;
        } else if (contains(c, TILE_N | TILE_W )) {
            nw = 5;
        } else if (contains(c, TILE_W )) {
            nw = 3;
        } else if (contains(c, TILE_N)) {
            nw = 4;
        } else {
            nw = 1;
        } 
        
        if (contains(c, TILE_N | TILE_E | TILE_NE)){
            ne = 8;
        } else if (contains(c, TILE_N | TILE_E)) {
            ne = 6;
        } else if (contains(c, TILE_E )) {
            ne = 3;
        } else if (contains(c, TILE_N)) {
            ne = 20;
        } else {
            ne = 2;
        } 
        
        if (contains(c, TILE_S| TILE_W | TILE_SW)){
            sw = 23;
        } else if (contains(c, TILE_S | TILE_W )) {
            sw = 21;
        } else if (contains(c, TILE_W )) {
            sw = 19;
        } else if (contains(c, TILE_S)) {
            sw = 4;
        } else {
            sw = 17;
        } 
        
        if (contains(c, TILE_S| TILE_E | TILE_SE)){
            se = 24;
        } else if (contains(c, TILE_S | TILE_E )) {
            se = 22;
        } else if (contains(c, TILE_E)) {
            se = 19;
        } else if (contains(c, TILE_S)) {
            se = 20;
        } else {
            se = 18;
        } 
        
        tiles[y*2][x*2] = nw;
        tiles[y*2][x*2+1] = ne;
        tiles[y*2+1][x*2] = sw;
        tiles[y*2+1][x*2+1] = se;
    }

    private void simpleTile(int c, int x, int y, int type, int[][] tileType, int[][] tiles) {
        int nw = 0;
        int ne = 0;
        int sw = 0;
        int se = 0;
        
        if (contains(c, TILE_N | TILE_W)) {
            nw = 3;
        } else if (contains(c, TILE_N)) {
            nw = 17;
        } else if (contains(c, TILE_W)) {
            nw = 2;
        } else if (c == 0) {
            nw = 1;
        } else {
            nw = 15;
        }
        
        if (contains(c, TILE_N | TILE_E)) {
            ne = 4;
        } else if (contains(c, TILE_N)) {
            ne = 18;
        } else if (contains(c, TILE_E)) {
            ne = 1;
        } else if (c == 0) {
            ne = 2;
        } else {
            ne = 15;
        }
        
        if (contains(c, TILE_S | TILE_W)) {
            sw = 19;
        } else if (contains(c, TILE_S)) {
            sw = 1;
        } else if (contains(c, TILE_W)) {
            sw = 18;
        } else if (c == 0) {
            sw = 17;
        } else {
            sw = 15;
        }
        
        if (contains(c, TILE_S | TILE_E)) {
            se = 20;
        } else if (contains(c, TILE_S)) {
            se = 2;
        } else if (contains(c, TILE_E)) {
            se = 17;
        } else if (c == 0) {
            se = 18;
        } else {
            se = 15;
        }
        
        tiles[y*2][x*2] = nw;
        tiles[y*2][x*2+1] = ne;
        tiles[y*2+1][x*2] = sw;
        tiles[y*2+1][x*2+1] = se;
        
        if (nw <0 || nw <0 || sw<0 || sw<0) {
            Log.d("ERROR!", " LEss than 0! " + x + ", " + y);
        }
    }

    private boolean contains(int p, int q) {
        return (p & q) == q;
    }
}
