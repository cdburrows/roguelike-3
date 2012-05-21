package com.cburrows.android.roguelike;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.graphics.Color;
import android.util.Log;

import com.cburrows.android.roguelike.xml.ItemDefinitions;
import com.cburrows.android.roguelike.xml.ItemRarity;

public class ItemFactory {
    private static final int ITEM_TYPE_WEAPON = 0;
    private static final int ITEM_TYPE_ARMOUR = 1;
    
    private static final float TEXT_X = 32;
    private static final float TEXT_Y = 2.9f;
    
    private static final int ICON_SIZE = 11;
    private static final int ICON_Y = 18;
    private static final int FIRST_ICON_X = 32;
    private static final int SECOND_ICON_X = 72;
    private static final int THIRD_ICON_X = 112;
    private static final int VALUE_TEXT_Y = 19;
    private static final int FIRST_VALUE_TEXT_X = 46;
    private static final int SECOND_VALUE_TEXT_X = 86;
    private static final int THIRD_VALUE_TEXT_X = 124;
    private static final int SPRITE_ICON_X = 4;
    private static final int SPRITE_ICON_Y = 4;
    private static final int ITEM_ICON_WIDTH = 24;
    private static final int ITEM_ICON_HEIGHT = 24;
    
    private static float mScaleX;
    private static float mScaleY;
    
    // Image data
    private static TiledTextureRegion mItemIconsTextureRegion;
    private static TiledTextureRegion mItemAttributesTextureRegion;
    private static TextureRegion mEquipmentBackgroundTextureRegion;
    private static TextureRegion mPotionTextureRegion;
    
    static ItemDefinitions mItemDefinitions;
    
    private static Random rand = new Random(System.currentTimeMillis());
    
    public static void loadResources(RoguelikeActivity context) {
        mScaleX = context.getGameScaleX();
        mScaleY = context.getGameScaleY();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(256, 512, 
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mItemIconsTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                bitmapTextureAtlas, context, "panels/weapon_icons.png", 0, 0, 5, 10);
        mItemAttributesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                bitmapTextureAtlas, context, "panels/icons.png", 0, 
                mItemIconsTextureRegion.getTexturePositionY() + mItemIconsTextureRegion.getHeight(), 4, 4);     
        mEquipmentBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                bitmapTextureAtlas, context, "panels/equipment_bg.png", 
                0, mItemAttributesTextureRegion.getTexturePositionY() + mItemAttributesTextureRegion.getHeight());
        mPotionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                bitmapTextureAtlas, context, "panels/potion_icon.png", 
                0, mEquipmentBackgroundTextureRegion.getTexturePositionY() + mEquipmentBackgroundTextureRegion.getHeight());
        context.getTextureManager().loadTexture(bitmapTextureAtlas);
        
        try {
            mItemDefinitions = ItemDefinitions.inflate(context.getAssets().open("xml/item_definitions.xml"));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Item createItem(RoguelikeActivity context, String name, int fontColor, int imageIndex, int itemType, int attack, int defense, int magic) {
        Sprite sprite = new Sprite(0, 0, 
                mEquipmentBackgroundTextureRegion.getWidth() * context.getGameScaleX(),
                mEquipmentBackgroundTextureRegion.getHeight() * context.getGameScaleY(), 
                mEquipmentBackgroundTextureRegion.deepCopy());
        
        TiledSprite icon = new TiledSprite(SPRITE_ICON_X * context.getGameScaleX(), SPRITE_ICON_Y * context.getGameScaleY(), 
                ITEM_ICON_WIDTH * context.getGameScaleX(), ITEM_ICON_HEIGHT * context.getGameScaleY(), mItemIconsTextureRegion.deepCopy());
        icon.setCurrentTileIndex(imageIndex);
        
        Text itemName = new Text(TEXT_X * context.getGameScaleX(), TEXT_Y * context.getGameScaleY(), 
                context.SmallFont, name);
        itemName.setColor(Color.red(fontColor)/255, Color.green(fontColor)/255, Color.blue(fontColor)/255);
        
        attack = 88;
        defense = 88;
        magic = 88;
        
        int curValueIndex = -1;
        for (int i = 0; i < 3; i++) {
            int attributeValue = 0;
            switch (i) {
                case(0):        // attack value
                    if (attack > 0) {
                        attributeValue = attack;
                        curValueIndex++;
                    }
                    break;
                case(1):        // defense value
                    if (defense > 0) {
                        attributeValue = defense;
                        curValueIndex++;
                    }
                    break;
                case(2):        // magic value
                    if (magic > 0) {
                        attributeValue = magic;
                        curValueIndex++;
                    }
                    break;
            }
            
            if (attributeValue != 0) {
                int posX = 0;
                int textPosX = 0;
                int posY = ICON_Y;
                if (curValueIndex == 0) {
                    posX = FIRST_ICON_X;
                    textPosX = FIRST_VALUE_TEXT_X;
                } else if (curValueIndex == 1) {
                    posX = SECOND_ICON_X;
                    textPosX = SECOND_VALUE_TEXT_X;
                } else {
                    posX = THIRD_ICON_X;
                    textPosX = THIRD_VALUE_TEXT_X;
                }
                
                TiledSprite attributeIcon = new TiledSprite(posX * context.getGameScaleX(), posY * context.getGameScaleY(), 
                        ICON_SIZE * context.getGameScaleX(), ICON_SIZE * context.getGameScaleY(), mItemAttributesTextureRegion.deepCopy());
                attributeIcon.setCurrentTileIndex(i);
                
                Text attributeText = new Text(textPosX * context.getGameScaleX(), VALUE_TEXT_Y * context.getGameScaleY(), 
                        context.SmallFont, String.valueOf(attributeValue));
                
                sprite.attachChild(attributeIcon);
                sprite.attachChild(attributeText);
                attributeValue = 0;
            }
        }
        
        sprite.attachChild(itemName);
        sprite.attachChild(icon);
        
        return new Item(sprite, name, itemType, attack, defense, magic);
    }
    
    public static Item createRandomItem(RoguelikeActivity context, int level) {
        if (rand.nextInt(100) < 50) {
            return createRandomWeapon(context, level);
        } else {
            return createRandomArmour(context, level);
        }
    }
    
    public static Item createRandomItem(RoguelikeActivity context, int level, int itemType) {
        int i = 0;
        if (itemType == ITEM_TYPE_WEAPON) i = rand.nextInt(5); //(25);
        if (itemType == ITEM_TYPE_ARMOUR) i = rand.nextInt(5);; //rand.nextInt(25) + 25;
        
        ItemRarity rarity = mItemDefinitions.getRandomRarity();
        int textColour = rarity.getColour();
        
        String name;
        if (rarity.mName.equals("")) {
            name = mItemDefinitions.mItemList.get(i).name;
        } else {
            name = rarity.mName + " " + mItemDefinitions.mItemList.get(i).name;
        }
        int imageIndex = mItemDefinitions.mItemList.get(i).index;
        int attack = (int)(mItemDefinitions.mItemList.get(i).attack * rarity.getRandomMultiplier());
        int defense = (int)(mItemDefinitions.mItemList.get(i).defense * rarity.getRandomMultiplier());
        int magic = (int)(mItemDefinitions.mItemList.get(i).magic * rarity.getRandomMultiplier());
        return createItem(context, name, textColour, imageIndex, itemType, attack, defense, magic);
    }
    
    public static Item createRandomWeapon(RoguelikeActivity context, int level) {
        return createRandomItem(context, level, ITEM_TYPE_WEAPON);
    }
    
    public static Item createRandomArmour(RoguelikeActivity context, int level) {
        return createRandomItem(context, level, ITEM_TYPE_ARMOUR);
    }
    
    public static Sprite getPotionSprite() {
        return new Sprite(0, 0, ITEM_ICON_WIDTH * mScaleX, ITEM_ICON_HEIGHT * mScaleY, mPotionTextureRegion);
    }
}
