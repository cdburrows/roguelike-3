package com.cdburrows.android.roguelike.item;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Root(name = "item_definitions")
public class XmlItemDefinitions {

    @Attribute(name="image_path")
    public String mImagePath;
    
    @Attribute(name="first_weapon")
    public int mFirstWeapon;
    
    @Attribute(name="first_armour")
    public int mFirstArmour;
    
    @ElementList(name="item_rarity", inline = true)
    public List<XmlItemRarity> mItemRarityList;
    
    @ElementList(name="base_item", inline = true)
    public List<XmlItemDefinition> mItemList;
    
    private float mTotalRarityProbability;
    
    public XmlItemRarity getRandomRarity() {
        Random rand = new Random();
        float result = rand.nextFloat() * mTotalRarityProbability;
        float totalProb = 0f;
        for (XmlItemRarity i : mItemRarityList) {
            if (result < i.mProbability + totalProb) return i;
            totalProb += i.mProbability;
        }
        return null;
    }
    
    private void build() {
        mTotalRarityProbability = 0;
        for (XmlItemRarity i : mItemRarityList) {
            mTotalRarityProbability += i.mProbability;
        }
    }
    
    public static XmlItemDefinitions inflate(InputStream stream) {
        Serializer serializer = new Persister();

        try {
            XmlItemDefinitions col = serializer.read(XmlItemDefinitions.class, stream);

            col.build();
            return col;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;  
    }
    
}
