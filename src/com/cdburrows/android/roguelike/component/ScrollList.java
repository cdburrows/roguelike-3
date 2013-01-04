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

package com.cdburrows.android.roguelike.component;

import java.util.ArrayList;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ScrollDetector;

import android.view.MotionEvent;

import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.audio.Audio;
import com.cdburrows.android.roguelike.graphics.Graphics;
import com.cdburrows.android.roguelike.item.Item;

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

    // private static float sItemHeight = 32;
    private static float sTouchX;

    private static float sTouchY;

    // private static float sTouchOffsetX;
    // private static float sTouchOffsetY;

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

        mClipEntity = new ClippingEntity(mX + (ITEM_OFFSET_X * RoguelikeActivity.sScaleX), mY
                + (ITEM_OFFSET_Y * RoguelikeActivity.sScaleY),
                (int)(mWidth - (ITEM_OFFSET_X * RoguelikeActivity.sScaleX)),
                (int)(mHeight - (ITEM_OFFSET_Y * RoguelikeActivity.sScaleY)));
        mSprite.attachChild(mClipEntity);
    }

    public void setData(ArrayList<Item> data) {
        mData = data;

        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                if (mFrame != null)
                    mClipEntity.detachChild(mFrame);
                mFrame = new Scene();
                mFrame.setBackgroundEnabled(false);
                mFrame.setPosition(mX + (ITEM_OFFSET_X * RoguelikeActivity.sScaleX), mOffY
                        + (ITEM_OFFSET_Y * RoguelikeActivity.sScaleY));
                mClipEntity.attachChild(mFrame);

                for (int i = 0; i < mFrame.getChildCount(); i++)
                    mFrame.getChild(i).setParent(null);
                int y = (int)ITEM_OFFSET_Y;
                for (Item e : mData) {
                    e.getSprite().setPosition(0, y);
                    y += ITEM_HEIGHT * RoguelikeActivity.sScaleY;
                    if (e.getSprite().hasParent())
                        e.getSprite().getParent().detachChild(e.getSprite());
                    mFrame.attachChild(e.getSprite());
                }
            }
        });

        mData = data;
        render();
    }

    private void render() {
        mMaxOffY = mY + (ITEM_OFFSET_Y * RoguelikeActivity.sScaleX);
        mMinOffY = mY - (32 * RoguelikeActivity.sScaleY * (mData.size() - 1));
    }

    public Sprite getSprite() {
        return mSprite;
    }

    public void handleTouchEvent(TouchEvent pTouchEvent) {
        if (pTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
            sTouchX = pTouchEvent.getMotionEvent().getX();
            sTouchY = pTouchEvent.getMotionEvent().getY();

            for (Item i : mData) {
                if (i.getSprite().contains(sTouchX, sTouchY)) {
                    i.handleTouchDown();
                    mSelectedItem = i;
                    break;
                }
            }
        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_MOVE) {

        } else if (pTouchEvent.getAction() == MotionEvent.ACTION_UP) {
            float mTouchUpX = pTouchEvent.getMotionEvent().getX();
            float mTouchUpY = pTouchEvent.getMotionEvent().getY();

            for (Item i : mData) {
                if (i.getSprite().contains(mTouchUpX, mTouchUpY)) {
                    if (mSelectedItem != null && mSelectedItem == i) {
                        if (mSelectListener != null)
                            mSelectListener.itemSelected(mSelectedItem);
                        Audio.playClick();
                    }
                    break;
                }
            }
            if (mSelectedItem != null)
                mSelectedItem.handleTouchUp();
        }
    }

    public void handleScrollEvent(final ScrollDetector pScollDetector,
            final TouchEvent pTouchEvent, final float pDistanceX, final float pDistanceY) {
        if (mSelectedItem != null)
            mSelectedItem.handleTouchUp();

        mOffY += pDistanceY;

        if (mOffY > mMaxOffY)
            mOffY = mMaxOffY;
        if (mOffY < mMinOffY)
            mOffY = mMinOffY;

        mFrame.setPosition(mFrame.getX(), mOffY);
    }

    public interface ISelectListener {
        void itemSelected(Item item);
    }

}
