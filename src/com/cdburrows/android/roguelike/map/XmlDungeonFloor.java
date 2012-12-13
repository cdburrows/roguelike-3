package com.cdburrows.android.roguelike.map;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.cdburrows.android.roguelike.item.XmlDungeonRarityValue;
import com.cdburrows.android.roguelike.monster.XmlDungeonMonsterTemplate;

@Root(name="floor")
public class XmlDungeonFloor {
    @Attribute(name="depth")
    public int mDepth;
    
    @Attribute(name="tileset")
    public String mTilesetName;
    
    @Attribute(name="battle_bg")
    public String mBattleBackgroundName;
    
    @Attribute(name="monster_spawn_rate")
    public float mMonsterSpawnRate;
    
    @Attribute(name="chest_spawn_rate")
    public float mChestSpawnRate;
    
    @Attribute(name="cols")
    public int mCols;
    
    @Attribute(name="rows")
    public int mRows;
    
    @Attribute(name="room_width")
    public int mRoomWidth;
    
    @Attribute(name="room_height")
    public int mRoomHeight;
    
    @Attribute(name="room_padding")
    public int mRoomPadding;
    
    @Attribute(name="min_h_path")
    public int mMinHorizontalPathSize;
    
    @Attribute(name="max_h_path")
    public int mMaxHorizontalPathSize;
    
    @Attribute(name="min_v_path")
    public int mMinVerticalPathSize;
    
    @Attribute(name="max_v_path")
    public int mMaxVerticalPathSize;
    
    @Attribute(name="erode", required=false)
    public float mErode = 0.35f;
    
    public float getErode() { return mErode; }
    
    @ElementList(name="rarity", inline=true)
    public ArrayList<XmlDungeonRarityValue> mRarityValues;
    
    @ElementList(name="monster", inline=true)
    public ArrayList<XmlDungeonMonsterTemplate> mMonsters;
}