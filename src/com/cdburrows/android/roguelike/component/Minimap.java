/*
 * Copyright (c) 2012, Christopher Burrows
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

package com.cdburrows.android.roguelike.component;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.cdburrows.android.roguelike.Direction;
import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.map.DungeonManager;
import com.cdburrows.android.roguelike.map.Room.RoomState;

import android.content.Context;
import android.graphics.Color;

public class Minimap {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    private static final int MINIMAP_ATLAS_WIDTH = 64;
    private static final int MINIMAP_ATLAS_HEIGHT = 32;
    private static final String MINIMAP_IMAGE_PATH = "minimap_icons.png";
    private static final int MINIMAP_IMAGE_COLS = 4;
    private static final int MINIMAP_IMAGE_ROWS = 2;
    private static final float FLASH_DURATION = 5f;
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;
    private static final float MINIMAP_ALPHA = 0.4f;
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static TiledTextureRegion IconRegion;
    private static TextureRegion sRoomTileRegion;
    private static TextureRegion sHorizontalPassageTileRegion;
    private static TextureRegion sVerticalPassageTileRegion;
    private static Entity sMinimapEntity;
    private static Sprite[] sRoomSprite;
    private static Sprite[] sHorizontalPassageSprite;
    private static Sprite[] sVerticalPassageSprite;
    private static float sMinimapWidth;
    private static float sMinimapHeight;
    private static float sMinimapScaleX;
    private static float sMinimapScaleY;
    private static float sCenterX;
    private static float sCenterY;
    
    private static DungeonManager sMap;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public static void initialize(DungeonManager map) {
        loadGraphics();
        loadMap(map);
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public static Entity getSprite() { 
        if (sMinimapEntity != null) {
            return sMinimapEntity;
        } 
        return null;
    }
    
    public static boolean isVisible() {
        return sMinimapEntity.isVisible();
    }

    public static void setVisible(boolean visible) {
        sMinimapEntity.setVisible(visible);
    }

    public static void setCenter(float posX, float posY) {
       sCenterX = (RoguelikeActivity.sCameraWidth / 2) - (posX * sMinimapScaleX) + 
               (sMap.getTileWidth() / 2) - (16 * RoguelikeActivity.sScaleX / 2);
       sCenterY = (RoguelikeActivity.sCameraHeight / 2) - (posY * sMinimapScaleY) + 
               (sMap.getTileHeight() / 2) - (16 * RoguelikeActivity.sScaleY / 2);
       sMinimapEntity.setPosition(sCenterX, sCenterY);
    }
    
    private static Sprite getRoomSprite(int x, int y) {
        return sRoomSprite[y * sMap.getRoomCols() + x];
    }
    
    private static void setRoomSprite(int x, int y, Sprite s) {
        sRoomSprite[y * sMap.getRoomCols() + x] = s;
    }
    
    private static Sprite getHorizontalPassageSprite(int x, int y) {
        return sHorizontalPassageSprite[y * sMap.getRoomCols() + x];
    }
    
    private static void setHorizontalPassageSprite(int x, int y, Sprite s) {
        sHorizontalPassageSprite[y * sMap.getRoomCols() + x] = s;
    }
    
    private static Sprite getVerticalPassageSprite(int x, int y) {
        return sVerticalPassageSprite[y * sMap.getRoomCols() + x];
    }
    
    private static void setVerticalPassageSprite(int x, int y, Sprite s) {
        sVerticalPassageSprite[y * sMap.getRoomCols() + x] = s;
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    private static void updatePassages(int x, int y, RoomState minActivationState, float activeAlpha, float activeColor, float inactiveAlpha) {
        updatePassages(x, y, minActivationState, activeAlpha, activeColor, inactiveAlpha, 0.0f);
    }
    
    private static void updatePassages(int x, int y, RoomState minActivationState, float activeAlpha, float activeColor, float inactiveAlpha, float inactiveColor) {
        if (x < sMap.getRoomCols()-1 && sMap.getRoomAccess(x, y, Direction.DIRECTION_RIGHT)) {
            if (sMap.getRoomState(x+1, y).getValue() >= minActivationState.getValue()) {
                getHorizontalPassageSprite(x, y).setAlpha(activeAlpha);
                getHorizontalPassageSprite(x, y).setColor(activeColor, activeColor, activeColor);
            } else {
                getHorizontalPassageSprite(x, y).setAlpha(inactiveAlpha);
                getHorizontalPassageSprite(x, y).setColor(inactiveColor, inactiveColor, inactiveColor);
            }
        }
        if (y < sMap.getRoomRows()-1 && sMap.getRoomAccess(x, y, Direction.DIRECTION_DOWN)) {
            if (sMap.getRoomState(x, y+1).getValue() >= minActivationState.getValue()) {
                getVerticalPassageSprite(x, y).setAlpha(activeAlpha);
                getVerticalPassageSprite(x, y).setColor(activeColor, activeColor, activeColor);
            } else {
                getVerticalPassageSprite(x, y).setAlpha(inactiveAlpha);
                getVerticalPassageSprite(x, y).setColor(inactiveColor, inactiveColor, inactiveColor);
            }
        }
    }
    
    public static void updateMinimap() {
        if (sMinimapEntity == null) return;
        
        for (int i = 0; i < sMap.getRoomRows(); i++) {
            for (int j = 0; j < sMap.getRoomCols(); j++) {
                switch (sMap.getRoomState(j, i)) {
                    case ROOM_HIDDEN:
                        getRoomSprite(j, i).setAlpha(0f);
                        updatePassages(j, i, RoomState.ROOM_VISITED, MINIMAP_ALPHA, 0.5f, 0f);
                        break;
                    case ROOM_SPOTTED:
                        getRoomSprite(j, i).setColor(0.5f, 0.5f, 0.5f);
                        getRoomSprite(j, i).setAlpha(MINIMAP_ALPHA);
                        updatePassages(j, i, RoomState.ROOM_SPOTTED, MINIMAP_ALPHA, 0.5f, 0f);
                        break;
                    case ROOM_VISITED:
                        getRoomSprite(j, i).setColor(1f, 1f, 1f);
                        getRoomSprite(j, i).setAlpha(MINIMAP_ALPHA);
                        updatePassages(j, i, RoomState.ROOM_VISITED, MINIMAP_ALPHA, 1f, MINIMAP_ALPHA, 0.5f);
                        break;
                    case ROOM_OCCUPIED:
                        getRoomSprite(j, i).setColor(Color.red(Color.YELLOW), Color.green(Color.YELLOW), Color.blue(Color.YELLOW));
                        getRoomSprite(j, i).setAlpha(MINIMAP_ALPHA);
                        updatePassages(j, i, RoomState.ROOM_VISITED, MINIMAP_ALPHA, 1f, MINIMAP_ALPHA, 0.5f);
                        break;
                }
            }
        }
    }  
    
    /**
     * Creates the minimap sprite for a map
     */
    public static void loadMap(DungeonManager map) {
        sMap = map;
        sMinimapWidth = sMap.getRoomCols() * ICON_WIDTH * RoguelikeActivity.sScaleX * 2;
        sMinimapHeight = sMap.getRoomRows() * ICON_HEIGHT * RoguelikeActivity.sScaleY * 2;
        sMinimapScaleX = sMinimapWidth / (sMap.getSprite(0).getWidth() * RoguelikeActivity.sScaleX); 
        sMinimapScaleY = sMinimapHeight / (sMap.getSprite(0).getHeight() * RoguelikeActivity.sScaleY);
        
        sRoomSprite = new Sprite[sMap.getRoomRows() * sMap.getRoomCols()];
        sHorizontalPassageSprite = new Sprite[sMap.getRoomRows() * sMap.getRoomCols()];
        sVerticalPassageSprite = new Sprite[sMap.getRoomRows() * sMap.getRoomCols()];
        
        for (int i = 0; i < sMap.getRoomRows(); i++) {
            for (int j = 0; j < sMap.getRoomCols(); j++) {
                // Create room sprite
                setRoomSprite(j, i, generateMinimapSprite(sRoomTileRegion, j, i, 0, 0));
                sMinimapEntity.attachChild(getRoomSprite(j, i));
                
                // Create horizontal passage sprite
                if (sMap.getRoomAccess(j, i, Direction.DIRECTION_RIGHT)) {
                    setHorizontalPassageSprite(j, i, generateMinimapSprite(sHorizontalPassageTileRegion, j, i, ICON_WIDTH, 0));
                    sMinimapEntity.attachChild(getHorizontalPassageSprite(j, i));
                }
                
                // Create vertical passage sprite
                if (sMap.getRoomAccess(j, i, Direction.DIRECTION_DOWN)) {
                    setVerticalPassageSprite(j, i, generateMinimapSprite(sVerticalPassageTileRegion, j, i, 0, ICON_HEIGHT));
                    sMinimapEntity.attachChild(getVerticalPassageSprite(j, i));
                }
            }
        }
        
        updateMinimap();
    }
    
    public static void scroll(float offsetX, float offsetY) {
        sMinimapEntity.setPosition(
                sCenterX + offsetX,  
                sCenterY + offsetY);
    }
    
    private static void loadGraphics() {
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(MINIMAP_ATLAS_WIDTH, MINIMAP_ATLAS_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        
        IconRegion =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, (Context)RoguelikeActivity.getContext(),
                MINIMAP_IMAGE_PATH, 0, 0, MINIMAP_IMAGE_COLS, MINIMAP_IMAGE_ROWS);
        
        RoguelikeActivity.getContext().getEngine().getTextureManager().loadTexture(bitmapTextureAtlas);
        
        sRoomTileRegion = TextureRegionFactory.extractFromTexture(IconRegion.getTexture(), 0, 0, 16, 16, true);
        sHorizontalPassageTileRegion = TextureRegionFactory.extractFromTexture(IconRegion.getTexture(), 0, 16, 16, 16, true);
        sVerticalPassageTileRegion = TextureRegionFactory.extractFromTexture(IconRegion.getTexture(), 16, 16, 16, 16, true);
        
        sMinimapEntity = new Entity(0, 0);
        sMinimapEntity.setVisible(false);
    }
    
    private static Sprite generateMinimapSprite(TextureRegion textureRegion, int x, int y, int offX, int offY) {
        Sprite s = new Sprite
                ((2 * ICON_WIDTH * x + offX) * RoguelikeActivity.sScaleX, 
                (2 * ICON_HEIGHT * y + offY) * RoguelikeActivity.sScaleY, 
                ICON_WIDTH * RoguelikeActivity.sScaleX, ICON_HEIGHT * RoguelikeActivity.sScaleY, 
                textureRegion);
        s.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        s.setColor(0, 0, 0);
        s.setAlpha(0f);
        return s;
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    
    
}
