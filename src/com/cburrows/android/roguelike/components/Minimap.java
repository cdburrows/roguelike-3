package com.cburrows.android.roguelike.components;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.cburrows.android.roguelike.Direction;
import com.cburrows.android.roguelike.Dungeon;
import com.cburrows.android.roguelike.GameMap.RoomState;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

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
    
    private static TiledTextureRegion sMapIconRegion;
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
    private static Dungeon sDungeon;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public static void initialize(Dungeon dungeon) {
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(MINIMAP_ATLAS_WIDTH, MINIMAP_ATLAS_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        
        sMapIconRegion =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, (Context)RoguelikeActivity.getContext(),
                MINIMAP_IMAGE_PATH, 0, 0, MINIMAP_IMAGE_COLS, MINIMAP_IMAGE_ROWS);
        
        RoguelikeActivity.getContext().getEngine().getTextureManager().loadTexture(bitmapTextureAtlas);
        
        sRoomTileRegion = TextureRegionFactory.extractFromTexture(sMapIconRegion.getTexture(), 0, 0, 16, 16, true);
        sHorizontalPassageTileRegion = TextureRegionFactory.extractFromTexture(sMapIconRegion.getTexture(), 0, 16, 16, 16, true);
        sVerticalPassageTileRegion = TextureRegionFactory.extractFromTexture(sMapIconRegion.getTexture(), 16, 16, 16, 16, true);
        
        sMinimapEntity = new Entity(0, 0);
        sMinimapEntity.setVisible(false);
        
        sDungeon = dungeon;
        loadFloor();
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
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    public static void updateFloor() {
        if (sMinimapEntity == null) return;
        
        for (int i = 0; i < sDungeon.getRoomRows(); i++) {
            for (int j = 0; j < sDungeon.getRoomCols(); j++) {
                switch (sDungeon.getRoomState(j, i)) {
                    case ROOM_HIDDEN:
                        sRoomSprite[i * RoguelikeActivity.getDungeon().getRoomCols() 
                                    + j].setAlpha(0f);
                        
                        if (j < sDungeon.getRoomCols()-1 && sDungeon.getRoomAccess(j, i, Direction.DIRECTION_RIGHT)) {

                            if (sDungeon.getRoomState(j+1, i) == RoomState.ROOM_VISITED || sDungeon.getRoomState(j+1, i) == RoomState.ROOM_OCCUPIED) {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(0f);
                            }
                            
                        }
                        
                        if (i < sDungeon.getRoomRows()-1 && sDungeon.getRoomAccess(j, i, Direction.DIRECTION_DOWN)) {
                            if (sDungeon.getRoomState(j, i+1) == RoomState.ROOM_VISITED || sDungeon.getRoomState(j, i+1) == RoomState.ROOM_OCCUPIED) {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(0f);
                            }
                        }
                        break;
                    case ROOM_SPOTTED:
                        sRoomSprite[i * RoguelikeActivity.getDungeon().getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                        sRoomSprite[i * RoguelikeActivity.getDungeon().getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                        
                        if (j < sDungeon.getRoomCols()-1 && sDungeon.getRoomAccess(j, i, Direction.DIRECTION_RIGHT)) {

                            if (sDungeon.getRoomState(j+1, i) == RoomState.ROOM_SPOTTED || 
                                    sDungeon.getRoomState(j+1, i) == RoomState.ROOM_VISITED || sDungeon.getRoomState(j+1, i) == RoomState.ROOM_OCCUPIED) {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(0f);
                            }
                            
                        }
                        
                        if (sDungeon.getRoomAccess(j, i, Direction.DIRECTION_DOWN)) {
                            if (sDungeon.getRoomState(j, i+1) == RoomState.ROOM_SPOTTED || 
                                    sDungeon.getRoomState(j, i+1) == RoomState.ROOM_VISITED || sDungeon.getRoomState(j, i+1) == RoomState.ROOM_OCCUPIED) {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(0f);
                            }
                        }
                        break;
                    case ROOM_VISITED:
                        sRoomSprite[i * RoguelikeActivity.getDungeon().getRoomCols() + j].setColor(1f, 1f, 1f);
                        sRoomSprite[i * RoguelikeActivity.getDungeon().getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                        
                        if (j < sDungeon.getRoomCols()-1 && sDungeon.getRoomAccess(j, i, Direction.DIRECTION_RIGHT)) {
                            if (sDungeon.getRoomState(j+1, i) == RoomState.ROOM_SPOTTED) {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(1f, 1f, 1f);
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            }
                        }
                        
                        if (sDungeon.getRoomAccess(j, i, Direction.DIRECTION_DOWN)) {
                            if (sDungeon.getRoomState(j, i+1) == RoomState.ROOM_SPOTTED) {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(1f, 1f, 1f);
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            }
                        }
                        break;
                    case ROOM_OCCUPIED:
                        sRoomSprite[i * RoguelikeActivity.getDungeon().getRoomCols() 
                                    + j].setColor(Color.red(Color.YELLOW), 
                                            Color.green(Color.YELLOW), Color.blue(Color.YELLOW));
                        sRoomSprite[i * RoguelikeActivity.getDungeon().getRoomCols() 
                                    + j].setAlpha(MINIMAP_ALPHA);
                        
                        if (j < sDungeon.getRoomCols()-1 && sDungeon.getRoomAccess(j, i, Direction.DIRECTION_RIGHT)) {
                            if (sDungeon.getRoomState(j+1, i) == RoomState.ROOM_VISITED) {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(1f, 1f, 1f);
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            }
                        }
                        
                        if (sDungeon.getRoomAccess(j, i, Direction.DIRECTION_DOWN)) {
                            if (sDungeon.getRoomState(j, i+1) == RoomState.ROOM_VISITED) {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(1f, 1f, 1f);
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            } else {
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0.5f, 0.5f, 0.5f);
                                sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(MINIMAP_ALPHA);
                            }
                            
                        } 
                        break;
                }
            }
        }
    }
    
    private static void loadFloor() {
        sRoomSprite = new Sprite[sDungeon.getRoomRows() * sDungeon.getRoomCols()];
        sHorizontalPassageSprite = new Sprite[sDungeon.getRoomRows() * sDungeon.getRoomCols()];
        sVerticalPassageSprite = new Sprite[sDungeon.getRoomRows() * sDungeon.getRoomCols()];
        
        for (int i = 0; i < sDungeon.getRoomRows(); i++) {
            for (int j = 0; j < sDungeon.getRoomCols(); j++) {
                Sprite sprite = new Sprite(2 * ICON_WIDTH * j * RoguelikeActivity.sScaleX,
                        2 * ICON_HEIGHT * i * RoguelikeActivity.sScaleY, 
                        ICON_WIDTH * RoguelikeActivity.sScaleX, ICON_HEIGHT * RoguelikeActivity.sScaleY, 
                        sRoomTileRegion);
                sprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                sprite.setAlpha(0f);
                sRoomSprite[i * sDungeon.getRoomCols() + j] = sprite;
                sMinimapEntity.attachChild(sRoomSprite[i * sDungeon.getRoomCols() + j]);
                
                if (sDungeon.getRoomAccess(j, i, Direction.DIRECTION_RIGHT)) {
                    sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j] = new Sprite(
                            (2 * ICON_WIDTH * j + ICON_WIDTH) * RoguelikeActivity.sScaleX,
                            2 * ICON_HEIGHT * i * RoguelikeActivity.sScaleY, 
                            ICON_WIDTH * RoguelikeActivity.sScaleX, ICON_HEIGHT * RoguelikeActivity.sScaleY, 
                            sHorizontalPassageTileRegion);
                    sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                    sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0, 0, 0);
                    sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(0f);
                    sMinimapEntity.attachChild(sHorizontalPassageSprite[i * sDungeon.getRoomCols() + j]);
                }
                
                if (sDungeon.getRoomAccess(j, i, Direction.DIRECTION_DOWN)) {
                    sVerticalPassageSprite[i * sDungeon.getRoomCols() + j] = new Sprite
                            (2 * ICON_WIDTH * j * RoguelikeActivity.sScaleX, 
                            (2 * ICON_HEIGHT * i + ICON_HEIGHT) * RoguelikeActivity.sScaleY, 
                            ICON_WIDTH * RoguelikeActivity.sScaleX, ICON_HEIGHT * RoguelikeActivity.sScaleY, 
                            sVerticalPassageTileRegion);
                    sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                    sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setColor(0, 0, 0);
                    sVerticalPassageSprite[i * sDungeon.getRoomCols() + j].setAlpha(0f);
                    sMinimapEntity.attachChild(sVerticalPassageSprite[i * sDungeon.getRoomCols() + j]);
                }
            }
        }
        
        sMinimapWidth = sDungeon.getRoomCols() * ICON_WIDTH * RoguelikeActivity.sScaleX * 2;
        sMinimapHeight = sDungeon.getRoomRows() * ICON_HEIGHT * RoguelikeActivity.sScaleY * 2;
        sMinimapScaleX = sMinimapWidth / (sDungeon.getSprite().getWidth() * RoguelikeActivity.sScaleX); 
        sMinimapScaleY = sMinimapHeight / (sDungeon.getSprite().getHeight() * RoguelikeActivity.sScaleY);
        
        updateFloor();
    }

    public static boolean isVisible() {
        return sMinimapEntity.isVisible();
    }

    public static void setVisible(boolean visible) {
        sMinimapEntity.setVisible(visible);
    }

    public static void setCenter(float posX, float posY) {
       sMinimapEntity.setPosition((RoguelikeActivity.sCameraWidth / 2) - (posX * sMinimapScaleX) + (sDungeon.getTileWidth() / 2) - (16 * RoguelikeActivity.sScaleX / 2),  
               (RoguelikeActivity.sCameraHeight / 2) - (posY * sMinimapScaleY) + (sDungeon.getTileHeight() / 2) - (16 * RoguelikeActivity.sScaleY / 2));
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    
    
}
