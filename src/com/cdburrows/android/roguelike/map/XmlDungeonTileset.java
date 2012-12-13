package com.cdburrows.android.roguelike.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.tmx.TmxImage;
import com.cdburrows.android.roguelike.tmx.TmxTileset;

@Root(name="tileset")
public class XmlDungeonTileset {
    
    public static final int TILE_COLS = 8;
    
    static Random sRand = new Random();
    
    @Attribute(name="name")
    public String mName;
    
    @Attribute(name="path")
    public String mPath;
    
    @Attribute(name="tile_height")
    public int mTileHeight;
    
    @Attribute(name="tile_width")
    public int mTileWidth;   
    
    //@Attribute(name="floor")
    //public int mFloorTile;
    
    @Attribute(name="wall")
    public int mWallTile;
    
    @Attribute(name="feature_start", required=false)
    public int mFeatureStart;
    
    @Attribute(name="feature_end", required=false)
    public int mFeatureEnd;
    
    public int getFloorTile() {
        return mWallTile + (5 * TILE_COLS) + 1; 
    }

    public int getWallTile() {
        return mWallTile;
    }
    
    public int getWallNWTile() {
        return mWallTile + (1 * TILE_COLS) + 0;
    }
    
    public int getWallNTile() {
        return mWallTile + (1 * TILE_COLS) + 1;
    }
    
    public int getWallNETile() {
        return mWallTile + (1 * TILE_COLS) + 2;
    }
    
    public int getWallWTile() {
        return mWallTile + (2 * TILE_COLS) + 0;
    }
    
    public int getWallFillTile() {
        return mWallTile + (2 * TILE_COLS) + 1;
    }
    
    public int getWallETile() {
        return mWallTile + (2 * TILE_COLS) + 2;
    }
    
    public int getWallSWTile() {
        return mWallTile + (3 * TILE_COLS) + 0;
    }
    
    public int getWallSTile() {
        return mWallTile + (3 * TILE_COLS) + 1;
    }
    
    public int getWallSETile() {
        return mWallTile + (3 * TILE_COLS) + 2;
    }
    
    public int getWallInNTile() {
        return mWallTile + (4 * TILE_COLS) + 1;
    }
    
    public int getWallInETile() {
        return mWallTile + (5 * TILE_COLS) + 2;
    }
    
    public int getWallInSTile() {
        return mWallTile + (6 * TILE_COLS) + 1;
    }
    
    public int getWallInWTile() {
        return mWallTile + (5 * TILE_COLS) + 0;
    }
    
    /*
    public int getWallUpTile() {
        return mWallUpTile;
    }
    
    public int getWallRightTile() {
        return mWallRightTile;
    }
    
    public int getWallDownTile() {
        return mWallDownTile;
    }
    
    public int getWallLeftTile() {
        return mWallLeftTile;
    }
    
    public int getWallDownLeftTile() {
        return mWallDownLeftTile;
    }
    
    public int getWallDownRightTile() {
        return mWallDownRightTile;
    }
    
    public int getWallDownLeftRightTile() {
        return mWallDownLeftRightTile;
    }
    
    public int getWallLeftRightTile() {
        return mWallLeftRightTile;
    }
    */
    
    public void halveTileSize() {
        mTileHeight /= 2;
        mTileWidth /= 2;
    }
    
    public int getRandomFeature() { 
        if (mFeatureEnd < 1) return 0; 
        return sRand.nextInt(mFeatureEnd - mFeatureStart) + mFeatureStart; 
    }
    
    public TmxTileset toTmx() {
        try {
            AssetManager assetManager = RoguelikeActivity.getContext().getAssets();
            InputStream istr;
            istr = assetManager.open(mPath);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            TmxTileset t = new TmxTileset(mName, new TmxImage(mPath, bitmap.getWidth(), bitmap.getHeight()),
                    1, mTileWidth, mTileHeight);
            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }    
    
    public boolean isFloorTile(int i) {
        return (i == getFloorTile() || (i >= mFeatureStart && i <= mFeatureEnd));
    }
    
    public boolean isWallTile(int i) {
        return (i == mWallTile || i == getWallNWTile() || i == getWallNTile() || i == getWallNETile()
                               || i == getWallWTile() || i == getWallFillTile() || i == getWallETile()
                               || i == getWallSWTile() || i == getWallSTile() || i == getWallSETile());
    }
}
    