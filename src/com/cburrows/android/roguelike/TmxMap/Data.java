package com.cburrows.android.roguelike.TmxMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.anddev.andengine.util.Base64;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import android.util.Base64OutputStream;
import android.util.Log;

public class Data {
    
    @Attribute(required=false)
    private String encoding; 
    
    @Attribute(required=false)
    private String compression; 
    
    //@Text(required=false)
    //private String value;
    
    @ElementList(inline=true, required=false)
    private List<Tile> tile;
    
    public Data() {}
    
    public Data(int width, int height) {
        
        tile = new ArrayList<Tile>();
        for (int i = 0; i < width * height; i++) {
            tile.add(new Tile());
        }
        
        //encoding = "base64";
        //compression = "gzip";
    }

    /*
    public void setValue(String value) { this.value = value; }
    
    public void setValue(int[] data) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            GZIPOutputStream out = new GZIPOutputStream(byteOut);
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            out.finish();
            out.close();
            
            //ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            
            //byte[] zipped = byteOut.toByteArray();
            ByteArrayOutputStream byteOut2 = new ByteArrayOutputStream();
            Base64OutputStream base64out = new Base64OutputStream(byteOut2, 0);
            
            for (int i = 0; i < byteOut.size(); i++) {
                base64out.write(byteOut.toByteArray());
            }
            
            byte[] b = new byte[data.length * 4];
            Log.d("MAP", "Length: " + zipped.length);
            for (int i = 0; i < data.length; i++) {
                b[i*4] = (byte)zipped[i];
                b[(i*4)+1] = (byte)(zipped[i] >> 8);
                b[(i*4)+2] = (byte)(zipped[i] >> 16);
                b[(i*4)+3] = (byte)(zipped[i] >> 24);
            }
            byteOut.close();
            value = byteOut2.toString(); //Base64.encodeToString(b, 0);
            Log.d("MAP", value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }     
        
        
    }
    */
    
    public List<Tile> getTile() {
        return tile;
    }
    public void setTile(List<Tile> tile) {
        this.tile = tile;
    }
    public void setTile(int index, int gid) {
        tile.get(index).setGid(gid);
    }
    public void setTile(int width, int x, int y, int gid) {
        tile.get((y * width) + x).setGid(gid);
    }

}
