package com.cdburrows.android.roguelike.component;

import java.util.ArrayList;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ScrollDetector;


import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.Item;

import android.view.MotionEvent;

public class ScrollList {
    
    // The offset of the item buttons into the scroll list
    protected static final float ITEM_OFFSET_X = 2;
    protected static final float ITEM_OFFSET_Y = 2;
    protected static final float ITEM_HEIGHT = 32;
    private ISelectListener mSelectListener;
    private Sprite mSprite;
    private float mX;
    private float mY;
    private float mWidth;
    private float mHeight;
    private ArrayList<Item> mData;
    private ClippingEntity mClipEntity;
    private Scene mFrame;
    
    //private static float sItemHeight = 32;
    private static float sTouchX;
    private static float sTouchY;
    //private static float sTouchOffsetX;
    //private static float sTouchOffsetY;
    
    private float mOffY;
    private float mMinOffY;
    private float mMaxOffY;
    private Item mSelectedItem;
    
    public ScrollList(float x, float y, float width, float height, ISelectListener listener) {
        mSelectListener = listener;
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mOffY = y;
        
        Graphics.beginLoad("gfx/panels/", 512, 512);
        mSprite = Graphics.createSprite("scroll_list.png", mX, mY);
        Graphics.endLoad();
        
        mClipEntity = new ClippingEntity(mX + (ITEM_OFFSET_X * RoguelikeActivity.sScaleX), 
                mY + (ITEM_OFFSET_Y * RoguelikeActivity.sScaleY), 
                (int)(mWidth - (ITEM_OFFSET_X * RoguelikeActivity.sScaleX)), 
                (int)(mHeight - (ITEM_OFFSET_Y * RoguelikeActivity.sScaleY)));
        mSprite.attachChild(mClipEntity);
    }
    
    public void setData(ArrayList<Item> data) {
        mData = data;
        
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {     
            public void run() {
                if (mFrame != null) mClipEntity.detachChild(mFrame);
                mFrame = new Scene();
                mFrame.setBackgroundEnabled(false);
                mFrame.setPosition(mX + (ITEM_OFFSET_X * RoguelikeActivity.sScaleX), 
                        mOffY + (ITEM_OFFSET_Y * RoguelikeActivity.sScaleY));
                mClipEntity.attachChild(mFrame);
        
                for (int i = 0; i < mFrame.getChildCount(); i++) mFrame.getChild(i).setParent(null);
                int y = (int)ITEM_OFFSET_Y; 
                for (Item e : mData) {
                    e.getSprite().setPosition(0, y);
                    y += ITEM_HEIGHT * RoguelikeActivity.sScaleY;
                    if (e.getSprite().hasParent()) e.getSprite().getParent().detachChild(e.getSprite());
                    mFrame.attachChild(e.getSprite());
                }
            }
        });
        
        mData = data;
        render();
    }
    
    private void render() {   
        mMaxOffY = mY + (ITEM_OFFSET_Y * RoguelikeActivity.sScaleX); 
        mMinOffY = mY - (32 * RoguelikeActivity.sScaleY * (mData.size()-1));  
    }
    
    public Sprite getSprite() { return mSprite; }

    public void handleTouchEvent(TouchEvent pTouchEvent) {
        if(pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            sTouchX = pTouchEvent.getMotionEvent().getX();
            sTouchY = pTouchEvent.getMotionEvent().getY();
            
            for (Item i : mData) {
                if (i.getSprite().contains(sTouchX, sTouchY)) {
                    i.handleTouchDown();
                    mSelectedItem = i;
                    break;
                }
            }
        }
        else if(pTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {     
            
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            float mTouchUpX = pTouchEvent.getMotionEvent().getX();
            float mTouchUpY = pTouchEvent.getMotionEvent().getY();
            
            for (Item i : mData) {
                if (i.getSprite().contains(mTouchUpX, mTouchUpY)) {
                    if (mSelectedItem != null && mSelectedItem == i) {
                        if (mSelectListener != null) mSelectListener.itemSelected(mSelectedItem);
                        Audio.playClick();
                    }
                    break;
                }
            }
            if (mSelectedItem != null) mSelectedItem.handleTouchUp();
        }
    }

    public void handleScrollEvent(final ScrollDetector pScollDetector, final TouchEvent pTouchEvent, final float pDistanceX, final float pDistanceY) {
        if (mSelectedItem != null) mSelectedItem.handleTouchUp();
        
        mOffY += pDistanceY;
        
        if (mOffY > mMaxOffY) mOffY = mMaxOffY;
        if (mOffY < mMinOffY) mOffY = mMinOffY;

        mFrame.setPosition(mFrame.getX(), mOffY);
    }
    
    public interface ISelectListener {
        void itemSelected(Item item);
    }

}
