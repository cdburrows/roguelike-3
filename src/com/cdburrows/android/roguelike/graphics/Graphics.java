/*
 * Copyright (c) 2012-2013, Christopher Burrows
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cdburrows.android.roguelike.graphics;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;

import com.cdburrows.android.roguelike.RoguelikeActivity;

public class Graphics {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int FONT_LARGE_SIZE = 20;

    public static final int FONT_SIZE = 14;

    public static final int FONT_SMALL_SIZE = 10;

    // ===========================================================
    // Fields
    // ===========================================================

    private static BaseGameActivity sContext;

    private static float sScaleX;

    private static float sScaleY;

    public static Font Font;

    public static Font SmallFont;

    public static Font LargeFont;

    private static BitmapTextureAtlas sBitmapTextureAtlas;

    private static int sCurrentAtlasX;

    private static int sCurrentAtlasY;

    private static TextureRegion sLastTextureRegion;

    private static TiledTextureRegion sLastTiledTextureRegion;

    private static String sLastImagePath;

    private static boolean sLoaded = false;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public static void initialize(BaseGameActivity context, int desiredWidth, int desiredHeight) {
        sContext = context;
        final Display display = context.getWindowManager().getDefaultDisplay();
        sScaleX = display.getWidth() / (float)desiredWidth;
        sScaleY = display.getHeight() / (float)desiredHeight;

        // Setup fonts
        Typeface t = Typeface.createFromAsset(context.getAssets(), "fonts/prstart.ttf");
        Font = createFont(t, FONT_SIZE, Color.WHITE);
        LargeFont = createFont(t, FONT_LARGE_SIZE, Color.WHITE);
        SmallFont = createFont(t, FONT_SMALL_SIZE, Color.WHITE);
        sLoaded = true;
    }

    public static void end() {
        Font = null;
        SmallFont = null;
        LargeFont = null;
        sContext = null;
        sBitmapTextureAtlas = null;
        sLastTextureRegion = null;
        sLastTiledTextureRegion = null;
        sLastImagePath = null;
        sLoaded = false;
    }

    public static void beginLoad(String atlasPath, int atlasWidth, int atlasHeight) {
        sCurrentAtlasX = 0;
        sCurrentAtlasY = 0;
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(atlasPath);
        sBitmapTextureAtlas = new BitmapTextureAtlas(atlasWidth, atlasHeight,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    }

    public static void endLoad() {
        endLoad("");
    }

    public static void endLoad(String debug) {
        sContext.getTextureManager().loadTexture(sBitmapTextureAtlas);
    }

    // Font

    public static Font createFont(Typeface typeface, int size, int color) {
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(256, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        RoguelikeActivity.loadTexture(atlas);
        Font font = FontFactory.create(atlas, typeface, size * sScaleX, true, color);
        sContext.getEngine().getFontManager().loadFonts(font);
        return font;
    }

    // Sprite

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
            if (texture.getWidth() > sCurrentAtlasX)
                sCurrentAtlasX = texture.getWidth();
            sCurrentAtlasY += texture.getHeight();
        }

        Sprite sprite = new Sprite(x, y, texture.getWidth() * sScaleX, texture.getHeight()
                * sScaleY, texture);
        sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        sprite.setAlpha(opacity);

        sLastImagePath = imagePath;
        sLastTextureRegion = texture;

        return sprite;
    }

    // TiledSprite

    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows) {
        return createTiledSprite(imagePath, cols, rows, 0, 0, 1.0f);
    }

    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows, float opacity) {
        return createTiledSprite(imagePath, cols, rows, 0, 0, opacity);
    }

    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows, float x,
            float y) {
        return createTiledSprite(imagePath, cols, rows, x, y, 1.0f);
    }

    public static TiledSprite createTiledSprite(String imagePath, int cols, int rows, float x,
            float y, float opacity) {
        TiledTextureRegion texture;
        if (imagePath == sLastImagePath) {
            texture = sLastTiledTextureRegion.deepCopy();
        } else {
            texture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    sBitmapTextureAtlas, sContext, imagePath, 0, sCurrentAtlasY, cols, rows);
            if (texture.getWidth() > sCurrentAtlasX)
                sCurrentAtlasX = texture.getWidth();
            sCurrentAtlasY += texture.getHeight();
        }

        TiledSprite sprite = new TiledSprite(x, y, texture.getTileWidth() * sScaleX,
                texture.getTileHeight() * sScaleY, texture);
        sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        sprite.setAlpha(opacity);

        sLastImagePath = imagePath;
        sLastTiledTextureRegion = texture;

        return sprite;
    }

    // AnimatedSprite

    public static AnimatedSprite createAnimatedSprite(String imagePath, int cols, int rows) {
        return createAnimatedSprite(imagePath, cols, rows, 0, 0, 1.0f);
    }

    public static AnimatedSprite createAnimatedSprite(String imagePath, int cols, int rows,
            float x, float y) {
        return createAnimatedSprite(imagePath, cols, rows, x, y, 1.0f);
    }

    public static AnimatedSprite createAnimatedSprite(String imagePath, int cols, int rows,
            float x, float y, float opacity) {
        TiledTextureRegion texture;
        if (imagePath == sLastImagePath) {
            texture = sLastTiledTextureRegion.deepCopy();
        } else {
            texture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                    sBitmapTextureAtlas, sContext, imagePath, 0, sCurrentAtlasY, cols, rows);
            if (texture.getWidth() > sCurrentAtlasX)
                sCurrentAtlasX = texture.getWidth();
            sCurrentAtlasY += texture.getHeight();
        }

        AnimatedSprite sprite = new AnimatedSprite(x, y, texture.getTileWidth() * sScaleX,
                texture.getTileHeight() * sScaleY, texture);
        sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        sprite.setAlpha(opacity);

        sLastImagePath = imagePath;
        sLastTiledTextureRegion = texture;

        return sprite;
    }

    // Text

    public static Text createText(float x, float y, Font font, String caption) {
        return createText(x, y, font, caption, Color.WHITE, 1.0f);
    }

    public static Text createText(float x, float y, Font font, String caption, int fontColor) {
        return createText(x, y, font, caption, fontColor, 1.0f);
    }

    public static Text createText(float x, float y, Font font, String caption, int fontColor,
            float alpha) {
        Text text = new Text(x, y, font, caption);
        text.setColor(Color.red(fontColor) / 255, Color.green(fontColor) / 255,
                Color.blue(fontColor) / 255, alpha);
        text.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        return text;
    }

    // ChangeableText

    public static ChangeableText createChangeableText(float x, float y, Font font, String caption) {
        return createChangeableText(x, y, font, caption, Color.WHITE, 1.0f);
    }

    public static ChangeableText createChangeableText(float x, float y, Font font, String caption,
            int fontColor) {
        return createChangeableText(x, y, font, caption, fontColor, 1.0f);
    }

    public static ChangeableText createChangeableText(float x, float y, Font font, String caption,
            float alpha) {
        return createChangeableText(x, y, font, caption, Color.WHITE, alpha);
    }

    public static ChangeableText createChangeableText(float x, float y, Font font, String caption,
            int fontColor, float alpha) {
        ChangeableText text = new ChangeableText(x, y, font, caption, 24);
        text.setColor(Color.red(fontColor) / 255, Color.green(fontColor) / 255,
                Color.blue(fontColor) / 255, alpha);
        text.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        return text;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
