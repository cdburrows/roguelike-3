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

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.cdburrows.android.roguelike.item.XmlDungeonRarityValue;
import com.cdburrows.android.roguelike.monster.XmlDungeonMonsterTemplate;

@Root(name = "floor")
public class XmlFloor {
    @Attribute(name = "depth")
    public int mDepth;

    @Attribute(name = "tileset")
    public String mTilesetName;

    @Attribute(name = "battle_bg")
    public String mBattleBackgroundName;

    @Attribute(name = "has_walls", required = false)
    public boolean mHasWalls = false;

    @Attribute(name = "monster_spawn_rate")
    public float mMonsterSpawnRate;

    @Attribute(name = "chest_spawn_rate")
    public float mChestSpawnRate;

    @Attribute(name = "feature_rate", required = false)
    public float mFeatureRate = 0.02f;

    @Attribute(name = "num_rooms", required = false)
    public int mNumRooms;

    @Attribute(name = "room_width")
    public int mRoomWidth;

    @Attribute(name = "room_height")
    public int mRoomHeight;

    @Attribute(name = "room_padding")
    public int mRoomPadding;

    @Attribute(name = "min_h_path")
    public int mMinHorizontalPathSize;

    @Attribute(name = "max_h_path")
    public int mMaxHorizontalPathSize;

    @Attribute(name = "min_v_path")
    public int mMinVerticalPathSize;

    @Attribute(name = "max_v_path")
    public int mMaxVerticalPathSize;

    @Attribute(name = "erode", required = false)
    public float mErodeRate = 0.0f;

    @ElementList(name = "rarity", inline = true)
    public ArrayList<XmlDungeonRarityValue> mRarityValues;

    @ElementList(name = "monster", inline = true)
    public ArrayList<XmlDungeonMonsterTemplate> mMonsters;

    public float getErode() {
        return mErodeRate;
    }
}
