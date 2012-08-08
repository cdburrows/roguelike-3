package com.cburrows.android.roguelike.xml;

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

import com.cburrows.android.roguelike.TmxMap.Image;
import com.cburrows.android.roguelike.TmxMap.Tileset;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;

@Root(name="tileset")
public class DungeonTileset {
    
    static Random sRand = new Random();
    
    @Attribute(name="name")
    public String mName;
    
    @Attribute(name="path")
    public String mPath;
    
    @Attribute(name="tile_height")
    public int mTileHeight;
    
    @Attribute(name="tile_width")
    public int mTileWidth;   
    
    @Attribute(name="floor")
    public int mFloorTile;
    
    @Attribute(name="wall")
    public int mWallTile;
    
    @Attribute(name="wall_up")
    public int mWallUpTile;
    
    @Attribute(name="wall_right")
    public int mWallRightTile;
    
    @Attribute(name="wall_down")
    public int mWallDownTile;
    
    @Attribute(name="wall_left")
    public int mWallLeftTile;
    
    @Attribute(name="wall_downleft")
    public int mWallDownLeftTile;
    
    @Attribute(name="wall_downright")
    public int mWallDownRightTile;
    
    @Attribute(name="wall_downleftright")
    public int mWallDownLeftRightTile;
    
    @Attribute(name="wall_leftright")
    public int mWallLeftRightTile;
    
    @Attribute(name="feature_start", required=false)
    public int mFeatureStart;
    
    @Attribute(name="feature_end", required=false)
    public int mFeatureEnd;
    
    
    public int getFloorTile() {
        return mFloorTile;
    }

    public int getWallTile() {
        return mWallTile;
    }
    
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
    
    public int getRandomFeature() { return sRand.nextInt(mFeatureEnd - mFeatureStart) + mFeatureStart; }
    
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

    public boolean isFloorTile(int i) {
        return (i == mFloorTile || (i >= mFeatureStart && i <= mFeatureEnd));
    }
}
    