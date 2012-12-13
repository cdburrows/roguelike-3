package com.cdburrows.android.roguelike.item;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="rarity")
public class XmlDungeonRarityValue {
    @Attribute(name="id")
    public int mId;
    
    @Attribute(name="rate")
    public float mRate;
}
