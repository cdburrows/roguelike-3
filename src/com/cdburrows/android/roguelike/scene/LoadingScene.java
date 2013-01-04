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

package com.cdburrows.android.roguelike.scene;

import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.sprite.Sprite;

import com.cdburrows.android.roguelike.ILoadingTask;
import com.cdburrows.android.roguelike.graphics.Graphics;

public class LoadingScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int TEXTURE_ATLAS_WIDTH = 512;

    private static final int TEXTURE_ATLAS_HEIGHT = 512;

    // ===========================================================
    // Fields
    // ===========================================================

    private ILoadingTask[] mTasks;

    private Sprite sBackgroundSprite;

    // ===========================================================
    // Constructors
    // ===========================================================

    public LoadingScene(ILoadingTask[] tasks) {
        mFragile = true; // Make this scene fragile so it's destroyed once
                         // another scene is pushed

        mTasks = tasks;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    @Override
    public void loadResources() {

        // BG not loading on device for some reason...
        Graphics.beginLoad("gfx/", TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT);

        sBackgroundSprite = Graphics.createSprite("dungeon_bg_320.png", 0, 0);

        Graphics.endLoad("Loading");

        attachChild(sBackgroundSprite);

        mLoaded = true;
    }

    @Override
    public void prepare(IEntityModifierListener preparedListener) {
        new LoadingThread().run();
        mPrepared = true;

        preparedListener.onModifierFinished(null, this);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void destroy() {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private class LoadingThread implements Runnable {
        public void run() {
            for (ILoadingTask t : mTasks) {
                t.Load();
            }
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
