package com.cburrows.android.roguelike.xml;

import java.io.IOException;
import java.io.InputStream;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import base.RoguelikeActivity;

import com.cburrows.android.roguelike.TmxMap.Image;
import com.cburrows.android.roguelike.TmxMap.Tileset;

@Root(name="tileset")
public class DungeonTileset {
    @Attribute(name="name")
    public String mName;
    
    @Attribute(name="path")
    public String mPath;
    
    @Attribute(name="tileheight")
    public int mTileHeight;
    
    @Attribute(name="tilewidth")
    public int mTileWidth;         
    
    public Tileset toTmx() {
        try {
            AssetManager assetManager = RoguelikeActivity.getContext().getAssets();
            InputStream istr;
            istr = assetManager.open(mPath);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            Tileset t = new Tileset(mName, new Image(mPath, bitmap.getWidth(), bitmap.getHeight()),
                    1, mTileWidth, mTileHeight);
            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
