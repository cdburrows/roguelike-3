package com.cburrows.android.roguelike.components;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.ease.EaseLinear;

import android.view.MotionEvent;

import com.cburrows.android.roguelike.Item;
import com.cdburrows.android.roguelike.base.Graphics;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;
import com.cdburrows.android.roguelike.scenes.MainScene;

public class Chest {

    // ===========================================================
    // Constants
    // ===========================================================
    
    private static final int ATLAS_WIDTH = 512;
    private static final int ATLAS_HEIGHT = 128;
    private static final float CHEST_FADE_DURATION = 0.5f;
    private static final int CHEST_STEP = -32;
    private static final float ITEM_OFF_Y = 32;   
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private float mX;
    private float mY;
    private TiledSprite mChestSprite;
    private TiledSprite mItemSprite;
    private ChestState mChestState;
    
    private static float sTouchY;
    private static float sTouchOffsetY;
    private static float sTotalTouchOffsetY;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    public Chest(float x, float y) {
        mX = x;
        mY = y;
        
        Graphics.beginLoad("gfx/", ATLAS_WIDTH, ATLAS_HEIGHT);
        mChestSprite = Graphics.createTiledSprite("chest.png", 4, 1);
        Graphics.endLoad("CHEST");
        
        mChestSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mChestSprite.setPosition(x - (mChestSprite.getWidth() / 2),
                y - (mChestSprite.getHeight() / 2)); 
        mChestSprite.setCurrentTileIndex(0);
        mChestSprite.setAlpha(0f);
        mChestSprite.setVisible(false);
        mChestState = ChestState.CHEST_CLOSED;
        
        fadeInModifier.setRemoveWhenFinished(true);
        fadeOutModifier.setRemoveWhenFinished(true);
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public TiledSprite getSprite() {
        return mChestSprite;
    }
    
    public boolean isVisible() { return mChestSprite.isVisible(); }
    
    public void setVisible(boolean visible) { mChestSprite.setVisible(visible); }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    public boolean handleTouchEvent(TouchEvent pTouchEvent) {
        if (mChestSprite.getAlpha() < 1.0f) return true;
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            sTouchY = pTouchEvent.getMotionEvent().getY();
            sTotalTouchOffsetY = 0;
            
            /*
            else if (mItemSprite.getAlpha() > 0f && mItemSprite.getAlpha() > 0f && mItemSprite.getAlpha() < 1f) {
                mItemSprite.unregisterEntityModifier(chestFadeInModifier);
                mItemSprite.setAlpha(1f);
                for (int i = 0; i < mItemSprite.getChildCount(); i++) {
                    mItemSprite.getChild(i).setAlpha(1f);
                    mItemSprite.getChild(i).unregisterEntityModifier(chestFadeInModifier);
                }
            }
            */
                
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {    
            float newY = pTouchEvent.getMotionEvent().getY();
            sTouchOffsetY = (newY - sTouchY);
            sTotalTouchOffsetY += sTouchOffsetY;
            sTouchY = newY;
            updateChestState();
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            sTotalTouchOffsetY = 0;
            updateChestState();
            
            if (mChestState == ChestState.CHEST_OPENED) {
                mChestState = ChestState.CHEST_DONE; 
            } else if (mChestState == ChestState.CHEST_DONE) {
                finish();
            }  
        }
        return true;
    }
    
    public void show(TiledSprite item) {
        mChestSprite.setAlpha(0f);
        mChestSprite.setVisible(true);
        mChestSprite.registerEntityModifier(fadeInModifier);
        
        mItemSprite = item;
        mItemSprite.setAlpha(0f);
        for (int i = 0; i < item.getChildCount(); i++) {
            item.getChild(i).setAlpha(0f);
        }
        mItemSprite.setPosition(
                (mChestSprite.getWidth() / 2) - (item.getWidth() / 2), 
                (mChestSprite.getHeight() / 2) - (item.getHeight() / 2) + (ITEM_OFF_Y * RoguelikeActivity.sScaleY));

        mChestSprite.attachChild(mItemSprite);
    }
    
    private void showItem() {
        mItemSprite.setAlpha(1f);
        for (int i = 0; i < mItemSprite.getChildCount(); i++) {
            mItemSprite.getChild(i).setAlpha(1f);
        }
    }
    
    private void updateChestState() {
        if (mChestState == ChestState.CHEST_OPENED) return;
        if (sTotalTouchOffsetY < CHEST_STEP * 3) {
            mChestSprite.setCurrentTileIndex(3);
            mChestState= ChestState.CHEST_OPENED;
            showItem();
        } else if (sTotalTouchOffsetY < CHEST_STEP * 2) {
            mChestSprite.setCurrentTileIndex(2);
        } else if (sTotalTouchOffsetY < CHEST_STEP) {
            mChestSprite.setCurrentTileIndex(1);
        } else {
            mChestSprite.setCurrentTileIndex(0);
        }
    }
    
    private void finish() {
        mChestSprite.unregisterEntityModifier(fadeInModifier);
        mChestSprite.registerEntityModifier(fadeOutModifier);
        mItemSprite.registerEntityModifier(fadeOutModifier);
        for (int i = 0; i < mItemSprite.getChildCount(); i++) {
            mItemSprite.getChild(i).registerEntityModifier(fadeOutModifier);
        }
        
        //mChestSprite.setCurrentTileIndex(0);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private final IEntityModifierListener fadeOutListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            pItem.unregisterEntityModifier((IEntityModifier)pModifier);
            mChestSprite.setCurrentTileIndex(0);
            mChestState = ChestState.CHEST_CLOSED;
            fadeInModifier.reset();
            fadeOutModifier.reset();
            RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
                public void run() {
                    mItemSprite.detachSelf();
                }
            });
            mChestSprite.setVisible(false);
            MainScene.closeChest();
        }
    };
    
    private final AlphaModifier fadeInModifier = new AlphaModifier(CHEST_FADE_DURATION, 0f, 1f, EaseLinear.getInstance());
    
    private final AlphaModifier fadeOutModifier = new AlphaModifier(CHEST_FADE_DURATION, 1f, 1f,
            fadeOutListener, EaseLinear.getInstance());
    
    protected enum ChestState { CHEST_CLOSED, CHEST_OPENED, CHEST_DONE }
    
}
