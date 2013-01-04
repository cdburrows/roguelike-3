/*
 * Copyright (c) 2012-2013, Christopher Burrows
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

@Root(name = "tileset")
public class XmlTileset {

    public static final int TILE_COLS = 8;

    static Random sRand = new Random();

    @Attribute(name = "name")
    public String mName;

    @Attribute(name = "path")
    public String mPath;

    @Attribute(name = "tile_height")
    public int mTileHeight;

    @Attribute(name = "tile_width")
    public int mTileWidth;

    @Attribute(name = "simple", required = false)
    public boolean mSimple = false;

    @Attribute(name = "floor")
    public int mFloorTile;

    @Attribute(name = "roof")
    public int mRoofTile;

    @Attribute(name = "wall", required = false)
    public int mWallTile = 0;

    @Attribute(name = "simple_tiles", required = false)
    public boolean mSimpleTiles = false;

    @Attribute(name = "feature_start", required = false)
    public int mFeatureStart = 0;

    @Attribute(name = "feature_end", required = false)
    public int mFeatureEnd = 0;

    @Attribute(name = "stairs_up", required = false)
    public int mStairsUp;

    @Attribute(name = "stairs_down", required = false)
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
        if (mFeatureEnd < 1)
            return 0;
        return sRand.nextInt(mFeatureEnd - mFeatureStart) + mFeatureStart;
    }

    public TmxTileset toTmx() {
        try {
            AssetManager assetManager = RoguelikeActivity.getContext().getAssets();
            InputStream istr;
            istr = assetManager.open(mPath);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            TmxTileset t = new TmxTileset(mName, new TmxImage(mPath, bitmap.getWidth(),
                    bitmap.getHeight()), 1, mTileWidth, mTileHeight);
            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isFloorTile(int i) {
        return (isWalkableTile(i) || (i >= mFeatureStart && i <= mFeatureEnd));
    }

    public boolean isWalkableTile(int i) {
        return (i == getFloorTile() || i == mStairsDown || i == mStairsUp);
    }

    public boolean isRoofTile(int i) {
        return i == mRoofTile;
    }
}
