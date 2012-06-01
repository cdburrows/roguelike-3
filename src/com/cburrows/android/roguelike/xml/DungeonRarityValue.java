package com.cburrows.android.roguelike.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="rarity")
public class DungeonRarityValue {
    @Attribute(name="id")
    public int mId;
    
    @Attribute(name="rate")
    public float mRate;
}
