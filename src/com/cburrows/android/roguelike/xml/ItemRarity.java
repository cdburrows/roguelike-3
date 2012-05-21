package com.cburrows.android.roguelike.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.graphics.Color;

@Root(name="item_rarity")
public class ItemRarity {
    private static final Map<String, Integer> colourMap;   
    static {
        colourMap = new HashMap<String, Integer>();
        colourMap.put("white", Color.WHITE);
        colourMap.put("green", Color.GREEN);
        colourMap.put("blue", Color.BLUE);
        colourMap.put("yellow", Color.YELLOW);
        colourMap.put("gray", Color.LTGRAY);
    }
    
    @Attribute(name="name")
    public String mName;
    
    @Element (name="probability")
    public float mProbability;
    
    @Element(name="colour")
    private String mColour;
    
    @Element(name="num_properties")
    public int mNumProperties;
    
    @Element(name="stat_multiplier_min")
    public float mStatMultiplierMin;
    
    @Element(name="stat_multiplier_max")
    public float mStatMultiplierMax;
    
    public int getColour() { return colourMap.get(mColour); }
    
    public float getRandomMultiplier() {
        Random rand = new Random();
        return rand.nextFloat() * (mStatMultiplierMax - mStatMultiplierMin) + mStatMultiplierMin;
    }
}
