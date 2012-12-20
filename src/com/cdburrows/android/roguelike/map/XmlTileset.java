package com.cdburrows.android.roguelike.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.tmx.TmxImage;
import com.cdburrows.android.roguelike.tmx.TmxTileset;

@Root(name="tileset")
public class XmlTileset {
    
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
    
    @Attribute(name="simple", required=false)
    public boolean mSimple=false; 
    
    @Attribute(name="floor")
    public int mFloorTile;
    
    @Attribute(name="roof")
    public int mRoofTile;
    
    @Attribute(name="wall", required=false)
    public int mWallTile = 0;
    
    @Attribute(name="simple_tiles", required=false)
    public boolean mSimpleTiles=false;
    
    @Attribute(name="feature_start", required=false)
    public int mFeatureStart=0;
    
    @Attribute(name="feature_end", required=false)
    public int mFeatureEnd=0;
    
    @Attribute(name="stairs_up", required=false)
    public int mStairsUp;
    
    @Attribute(name="stairs_down", required=false)
    public int mStairsDown;
    
    public int getFloorTile() {
        return mFloorTile;
    }

    public int getStairsDown() {
        return mStairsDown;
    }
    
    public int getStairsUp() {
        return mStairsUp;
    }

    public int getRoofTile() {
        return mRoofTile;
    }
    
    public int getWallTile() {
        return mWallTile;
    }
    
    public boolean isSimple() {
        return mSimple;
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
        return (isWalkableTile(i) || (i >= mFeatureStart && i <= mFeatureEnd)) ;
    }
    
    public boolean isWalkableTile(int i) {
        return (i == getFloorTile() || i == mStairsDown || i == mStairsUp) ;
    }
    
    public boolean isRoofTile(int i) {
        return i == mRoofTile;
    }
}
    