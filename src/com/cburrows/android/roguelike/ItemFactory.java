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

import com.cburrows.android.roguelike.xml.BaseItemCollection;

public class ItemFactory {
    private static final int ITEM_TYPE_WEAPON = 0;
    private static final int ITEM_TYPE_ARMOUR = 1;
    
    private static final float TEXT_X = 32;
    private static final float TEXT_Y = 2.9f;
    
    private static final int ICON_SIZE = 11;
    private static final int MAIN_ICON_X = 32;
    private static final int MAIN_ICON_Y = 18;
    private static final float MAIN_VALUE_TEXT_X = 48;
    private static final float MAIN_VALUE_TEXT_Y = 18;
    private static final float SPRITE_ICON_X = 4;
    private static final float SPRITE_ICON_Y = 4;
    private static final int ITEM_ICON_WIDTH = 24;
    private static final int ITEM_ICON_HEIGHT = 24;
    
    // Image data
    private static TiledTextureRegion mItemIconsTextureRegion;
    private static TiledTextureRegion mItemAttributesTextureRegion;
    private static TextureRegion mEquipmentBackgroundTextureRegion;
    private static TextureRegion mPotionTextureRegion;
    
    static BaseItemCollection mItemDefinitions;
    
    private static Random rand = new Random(System.currentTimeMillis());
    
    public static void loadResources(RoguelikeActivity context) {
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
            mItemDefinitions = BaseItemCollection.inflate(context.getAssets().open("xml/item_definitions.xml"));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Item createItem(RoguelikeActivity context, String name, int imageIndex, int itemType, int attack, int defense, int magic) {
        Sprite sprite = new Sprite(0, 0, 
                mEquipmentBackgroundTextureRegion.getWidth() * context.getGameScaleX(),
                mEquipmentBackgroundTextureRegion.getHeight() * context.getGameScaleY(), 
                mEquipmentBackgroundTextureRegion.deepCopy());
        
        TiledSprite icon = new TiledSprite(SPRITE_ICON_X * context.getGameScaleX(), SPRITE_ICON_Y * context.getGameScaleY(), 
                ITEM_ICON_WIDTH * context.getGameScaleX(), ITEM_ICON_HEIGHT * context.getGameScaleY(), mItemIconsTextureRegion.deepCopy());
        icon.setCurrentTileIndex(imageIndex);
        
        TiledSprite mainIcon = new TiledSprite(MAIN_ICON_X * context.getGameScaleX(), MAIN_ICON_Y * context.getGameScaleY(), 
                ICON_SIZE * context.getGameScaleX(), ICON_SIZE * context.getGameScaleY(), mItemAttributesTextureRegion.deepCopy());
        mainIcon.setCurrentTileIndex(itemType);
        
        int value = 0;
        if (itemType == 0) value = attack;
        if (itemType == 1) value = defense;
        
        Text itemName = new Text(TEXT_X * context.getGameScaleX(), TEXT_Y * context.getGameScaleY(), 
                context.SmallFont, name);
        Text mainValue = new Text(MAIN_VALUE_TEXT_X * context.getGameScaleX(), MAIN_VALUE_TEXT_Y * context.getGameScaleY(), 
                context.SmallFont, String.valueOf(value));
        
        sprite.attachChild(itemName);
        sprite.attachChild(icon);
        sprite.attachChild(mainValue);
        sprite.attachChild(mainIcon);
        return new Item(sprite, name, itemType, attack, defense, magic);
    }
    
    public static Item createRandomItem(RoguelikeActivity context, int level) {
        if (rand.nextInt(100) < 50) {
            return createRandomWeapon(context, level);
        } else {
            return createRandomArmour(context, level);
        }
    }
    
    public static Item createRandomWeapon(RoguelikeActivity context, int level) {
        int i = rand.nextInt(5);
        String name = mItemDefinitions.base_item.get(i).name;
        int imageIndex = mItemDefinitions.base_item.get(i).index;
        int attack = mItemDefinitions.base_item.get(i).attack; //rand.nextInt(8) * level + level;
        int defense = mItemDefinitions.base_item.get(i).defense;
        int magic = mItemDefinitions.base_item.get(i).magic;
        return createItem(context, name, imageIndex, ITEM_TYPE_WEAPON, attack, defense, magic);
    }
    
    public static Item createRandomArmour(RoguelikeActivity context, int level) {
        String name = "Buckler";
        int imageIndex = 25;
        int attack = 0;
        int defense = rand.nextInt(8) * level + level;;
        int magic = 0;
        return createItem(context, name, imageIndex, ITEM_TYPE_ARMOUR, attack, defense, magic);
    }
    
    public static Sprite getPotionSprite() {
        return new Sprite(0, 0, mPotionTextureRegion);
    }
}
