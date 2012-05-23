package com.cburrows.android.roguelike;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;

/**
 * Automatically scales and sets up alpha blends for sprites it creates.
 * @author cburrows
 *
 */
public class Graphics {

    private static BaseGameActivity sContext;
    //private static int sDesiredWidth;
    //private static int sDesiredHeight;
    private static float sScaleX;
    private static float sScaleY;
    
    private static BitmapTextureAtlas sBitmapTextureAtlas;
    private static int sCurrentAtlasX;
    private static int sCurrentAtlasY;
    //private static String sAtlasPath;
    
    private static TextureRegion sLastTextureRegion;
    private static TiledTextureRegion sLastTiledTextureRegion;
    private static String sLastImagePath;
    
    public static void initialize(BaseGameActivity context, int desiredWidth, int desiredHeight) {
        sContext = context;
        final Display display = context.getWindowManager().getDefaultDisplay();
        sScaleX = display.getWidth() / (float)desiredWidth;
        sScaleY = display.getHeight() / (float)desiredHeight;
    }
    
    public static void beginLoad(String atlasPath, int atlasWidth, int atlasHeight) {
        sCurrentAtlasX = 0;
        sCurrentAtlasY = 0;
        //sAtlasPath = atlasPath;
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(atlasPath);
        sBitmapTextureAtlas = new BitmapTextureAtlas(atlasWidth, atlasHeight,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    }
    
    public static Sprite createSprite(String imagePath) { 
        return createSprite(imagePath, 0, 0, 1.0f);
    }
    public static Sprite createSprite(String imagePath, float x, float y) {
        return createSprite(imagePath, x, y, 1.0f);
    }
    public static Sprite createSprite(String imagePath, float x, float y, float opacity) {
        TextureRegion texture;
        if (imagePath == sLastImagePath) {
            texture = sLastTextureRegion.deepCopy();
        } else {
            texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(sBitmapTextureAtlas,
                        sContext, imagePath, 0, sCurrentAtlasY);
            if (texture.getWidth() > sCurrentAtlasX) sCurrentAtlasX = texture.getWidth();
            sCurrentAtlasY += texture.getHeight();
        }
        
        Sprite sprite = new Sprite(x, y, 
                texture.getWidth() * sScaleX, texture.getHeight() * sScaleY, texture);
        sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        sprite.setAlpha(opacity);
        
        sLastImagePath = imagePath;
        sLastTextureRegion = texture;
        
        //Log.d("GRAPHICS", "Image: " + imagePath + " Width: " + texture.getWidth() + ", Height: " + texture.getHeight());
        return sprite;
    }
    
    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows) {
        return createTiledSprite(imagePath, cols, rows, 0, 0, 1.0f);
    }
    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows, float opacity) {
        return createTiledSprite(imagePath, cols, rows, 0, 0, opacity);
    }
    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows, float x, float y) {
        return createTiledSprite(imagePath, cols, rows, x, y, 1.0f);
    }
    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows, float x, float y, float opacity) {
        TiledTextureRegion texture;
        if (imagePath == sLastImagePath) {
            texture = sLastTiledTextureRegion.deepCopy();
        } else {
            texture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(sBitmapTextureAtlas,
                    sContext, imagePath, 0, sCurrentAtlasY, cols, rows);
            if (texture.getWidth() > sCurrentAtlasX) sCurrentAtlasX = texture.getWidth();
            sCurrentAtlasY += texture.getHeight();
        }
        
        TiledSprite sprite = new TiledSprite(x, y, 
                texture.getTileWidth() * sScaleX, texture.getTileHeight() * sScaleY, texture);
        sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        sprite.setAlpha(opacity);
        
        sLastImagePath = imagePath;
        sLastTiledTextureRegion = texture;
        
        //Log.d("GRAPHICS", "Image: " + imagePath + " Width: " + texture.getWidth() + ", Height: " + texture.getHeight());
        return sprite;
    }
    
    public static AnimatedSprite createAnimatedSprite(String imagePath, int cols, int rows) {
        return createAnimatedSprite(imagePath, cols, rows, 0, 0, 1.0f);
    }
    public static AnimatedSprite createAnimatedSprite(String imagePath, int cols, int rows, float x, float y) {
        return createAnimatedSprite(imagePath, cols, rows, x, y, 1.0f);
    }
    public static AnimatedSprite createAnimatedSprite(String imagePath, int cols, int rows, float x, float y, float opacity) {
        TiledTextureRegion texture;
        if (imagePath == sLastImagePath) {
            texture = sLastTiledTextureRegion.deepCopy();
        } else {
            texture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(sBitmapTextureAtlas,
                    sContext, imagePath, 0, sCurrentAtlasY, cols, rows);
            if (texture.getWidth() > sCurrentAtlasX) sCurrentAtlasX = texture.getWidth();
            sCurrentAtlasY += texture.getHeight();
        }
        
        AnimatedSprite sprite = new AnimatedSprite(x, y, 
                texture.getTileWidth() * sScaleX, texture.getTileHeight() * sScaleY, texture);
        sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        sprite.setAlpha(opacity);
        
        sLastImagePath = imagePath;
        sLastTiledTextureRegion = texture;
        
        //Log.d("GRAPHICS", "Image: " + imagePath + " Width: " + texture.getWidth() + ", Height: " + texture.getHeight());
        return sprite;
    }
    
    public static Font createFont(Typeface typeface, int size, int color) {
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sContext.getTextureManager().loadTextures(atlas);
        Font font = FontFactory.create(atlas, typeface, size * sScaleX, true, color);
        sContext.getEngine().getFontManager().loadFonts(font);
        //Log.d("GRAPHICS", "Font: " + font.getTexture().getWidth());
        return font;
    }
    
    public static void endLoad() { endLoad(""); }
    public static void endLoad(String debug) {
        sContext.getTextureManager().loadTexture(sBitmapTextureAtlas);
        Log.d("GRAPHICS", debug + " Width: " + sCurrentAtlasX + ", Height: " + sCurrentAtlasY);
    }

}
