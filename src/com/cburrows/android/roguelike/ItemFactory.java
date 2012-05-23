package com.cburrows.android.roguelike;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.graphics.Color;
import com.cburrows.android.roguelike.xml.ItemDefinitions;
import com.cburrows.android.roguelike.xml.ItemRarity;

public class ItemFactory {
    private static final int TEXTURE_ATLAS_WIDTH = 512;
    private static final int TEXTURE_ATLAS_HEIGHT = 512;
    
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
    
    private static float sScaleX;
    private static float sScaleY;
    
    // Image data
    private static TiledTextureRegion sItemIconsTextureRegion;
    private static TiledTextureRegion sItemAttributesTextureRegion;
    private static TiledTextureRegion sEquipmentBackgroundTextureRegion;
    private static TextureRegion sPotionTextureRegion;
    
    static ItemDefinitions mItemDefinitions;
    
    private static Random rand = new Random(System.currentTimeMillis());
    
    public static void loadResources() {
        RoguelikeActivity context = RoguelikeActivity.getContext();
        sScaleX = RoguelikeActivity.sScaleX;
        sScaleY = RoguelikeActivity.sScaleY;
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT, 
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sItemIconsTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                bitmapTextureAtlas, context, "panels/weapon_icons.png", 0, 0, 5, 10);
        sItemAttributesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                bitmapTextureAtlas, context, "panels/icons.png", 0, 
                sItemIconsTextureRegion.getTexturePositionY() + sItemIconsTextureRegion.getHeight(), 4, 4);     
        sEquipmentBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                bitmapTextureAtlas, context, "panels/equipment_bg.png", 
                0, sItemAttributesTextureRegion.getTexturePositionY() + sItemAttributesTextureRegion.getHeight(), 1, 2);
        sPotionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                bitmapTextureAtlas, context, "panels/potion_icon.png", 
                0, sEquipmentBackgroundTextureRegion.getTexturePositionY() + sEquipmentBackgroundTextureRegion.getHeight());
        context.getTextureManager().loadTexture(bitmapTextureAtlas);
        
        try {
            mItemDefinitions = ItemDefinitions.inflate(context.getAssets().open("xml/item_definitions.xml"));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Item createItem(String name, int fontColor, int imageIndex, int itemType, int attack, int defense, int magic) {
        RoguelikeActivity context = RoguelikeActivity.getContext();
        TiledSprite sprite = new TiledSprite(0, 0, 
                sEquipmentBackgroundTextureRegion.getTileWidth() * sScaleX,
                sEquipmentBackgroundTextureRegion.getTileHeight() * sScaleY, 
                sEquipmentBackgroundTextureRegion.deepCopy());
        
        TiledSprite icon = new TiledSprite(SPRITE_ICON_X * sScaleX, SPRITE_ICON_Y * sScaleY, 
                ITEM_ICON_WIDTH * sScaleX, ITEM_ICON_HEIGHT * sScaleY, sItemIconsTextureRegion.deepCopy());
        icon.setCurrentTileIndex(imageIndex);
        icon.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        Text itemName = new Text(TEXT_X * context.sScaleX, TEXT_Y * sScaleY, 
                context.SmallFont, name);
        itemName.setColor(Color.red(fontColor)/255, Color.green(fontColor)/255, Color.blue(fontColor)/255);
        itemName.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
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
                
                TiledSprite attributeIcon = new TiledSprite(posX * sScaleX, posY * sScaleY, 
                        ICON_SIZE * sScaleX, ICON_SIZE * sScaleY, sItemAttributesTextureRegion.deepCopy());
                attributeIcon.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                attributeIcon.setCurrentTileIndex(i);
                
                Text attributeText = new Text(textPosX * sScaleX, VALUE_TEXT_Y * sScaleY, 
                        context.SmallFont, String.valueOf(attributeValue));
                attributeText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                
                sprite.attachChild(attributeIcon);
                sprite.attachChild(attributeText);
                attributeValue = 0;
            }
        }
        
        sprite.attachChild(itemName);
        sprite.attachChild(icon);
        sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        return new Item(sprite, name, fontColor, imageIndex, itemType, attack, defense, magic);
    }
    
    public static Item createRandomItem(int level) {
        if (rand.nextInt(100) < 50) {
            return createRandomWeapon(level);
        } else {
            return createRandomArmour(level);
        }
    }
    
    public static Item createRandomItem(int level, int itemType) {
        int i = 0;
        if (itemType == Item.ITEM_TYPE_WEAPON) i = rand.nextInt(mItemDefinitions.mFirstArmour-1); 
        if (itemType == Item.ITEM_TYPE_ARMOUR) i = rand.nextInt(25) + mItemDefinitions.mFirstArmour;
        
        ItemRarity rarity = mItemDefinitions.getRandomRarity();
        int textColour = rarity.getColour();
        
        String name;
        if (rarity.mName.equals("")) {
            name = mItemDefinitions.mItemList.get(i).name;
        } else {
            name = rarity.mName + " " + mItemDefinitions.mItemList.get(i).name;
        }
        int imageIndex = mItemDefinitions.mItemList.get(i).index;
        int attack = (int)((mItemDefinitions.mItemList.get(i).attack + level) * rarity.getRandomMultiplier());
        int defense = (int)((mItemDefinitions.mItemList.get(i).defense + level) * rarity.getRandomMultiplier());
        int magic = (int)((mItemDefinitions.mItemList.get(i).magic + level) * rarity.getRandomMultiplier());
        return createItem(name, textColour, imageIndex, itemType, attack, defense, magic);
    }
    
    public static Item createRandomWeapon(int level) {
        return createRandomItem(level, Item.ITEM_TYPE_WEAPON);
    }
    
    public static Item createRandomArmour(int level) {
        return createRandomItem(level, Item.ITEM_TYPE_ARMOUR);
    }
    
    public static Sprite getPotionSprite() {
        return new Sprite(0, 0, ITEM_ICON_WIDTH * sScaleX, ITEM_ICON_HEIGHT * sScaleY, sPotionTextureRegion);
    }
}
