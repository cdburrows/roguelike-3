package com.cburrows.android.roguelike.xml;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Root(name = "item_definitions")
public class ItemDefinitions {

    @Attribute(name="image_path")
    public String mImagePath;
    
    @Attribute(name="first_weapon")
    public int mFirstWeapon;
    
    @Attribute(name="first_armour")
    public int mFirstArmour;
    
    @ElementList(name="item_rarity", inline = true)
    public List<ItemRarity> mItemRarityList;
    
    @ElementList(name="base_item", inline = true)
    public List<ItemDefinition> mItemList;
    
    private float mTotalRarityProbability;
    
    public ItemRarity getRandomRarity() {
        Random rand = new Random();
        float result = rand.nextFloat() * mTotalRarityProbability;
        float totalProb = 0f;
        for (ItemRarity i : mItemRarityList) {
            if (result < i.mProbability + totalProb) return i;
            totalProb += i.mProbability;
        }
        return null;
    }
    
    private void build() {
        mTotalRarityProbability = 0;
        for (ItemRarity i : mItemRarityList) {
            mTotalRarityProbability += i.mProbability;
        }
    }
    
    public static ItemDefinitions inflate(InputStream stream) {
        Serializer serializer = new Persister();

        try {
            ItemDefinitions col = serializer.read(ItemDefinitions.class, stream);

            col.build();
            return col;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;  
    }
    
}
