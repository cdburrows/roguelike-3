package com.cburrows.android.roguelike.xml;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="floor")
public class DungeonFloor {
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
    
    @ElementList(name="rarity", inline=true)
    public ArrayList<DungeonRarityValue> mRarityValues;
    
    @ElementList(name="monster", inline=true)
    public ArrayList<DungeonMonsterTemplate> mMonsters;
}