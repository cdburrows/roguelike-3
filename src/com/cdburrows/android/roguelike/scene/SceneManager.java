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

package com.cdburrows.android.roguelike.scene;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.util.modifier.IModifier;

import com.cdburrows.android.roguelike.RoguelikeActivity;

import android.util.Log;

/**
 * Handles display of scenes, fading in and out when necessary
 * @author cburrows
 *
 */
public class SceneManager {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    private static final float FADE_DURATION = 0.25f;
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static List<BaseScene> mScenes  = new ArrayList<BaseScene>();
    
    // Holds a scene to add to queue as soon as current one fades out
    private static BaseScene mNextScene = null;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public static int getSize() {
        return mScenes.size();
    }
    
    public static BaseScene getTopScene() { 
        if (mScenes.size() > 0) { 
            return mScenes.get(mScenes.size()-1); 
        } else {
            return null;
        }
    }
    
    // ===========================================================
    // Inherited Methods
    // ===========================================================
    
    // ===========================================================
    // Methods
    // ===========================================================
    
    public static void load() { 
        getTopScene().loadResources(); 
    }
    
    /**
     * Fade in a new scene, or queue it and fade out the current one
     * if the scene stack isn't empty
     */
    public static void pushScene(BaseScene scene) {
        if (mScenes.size() == 0) {
            mScenes.add(scene);
            setScene();
        } else {
            mNextScene = scene;
            getTopScene().fadeOut(FADE_DURATION, transitionUp);
        }
    }
    
    //public static BaseScene popScene() { 
    //    return popScene(null); 
    //}
    
    /**
     * Fades out and returns top scene
     */
    public static BaseScene popScene(/*IEntityModifierListener listener*/) {
        if (mScenes.size() > 0) {
            BaseScene scene = getTopScene();
            scene.fadeOut(FADE_DURATION, transitionDown);
            return scene;
        }
        return null;
    }
    
    public static void pauseScene() {
        if (getTopScene() == null) return;
        getTopScene().pause();
    }
    
    public static void resumeScene() {
        if (getTopScene() == null) return;
        getTopScene().resume();
    }
    
    /**
     * Loads the top scene and displays it, or ends application if 
     * scene stack is empty.
     */
    private static void setScene() {
        BaseScene scene = getTopScene();
        if (scene == null) {
            // End activity
            RoguelikeActivity.destroy();
        } else {
            if (!scene.isLoaded()) scene.loadResources();
            scene.prepare(prepared);
            scene.resume();
        }
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    /**
     * Listener called after a scene is prepared; fades in scene
     */
    final static IEntityModifierListener prepared = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            Log.d("SCENE", "Scene prepared");
            BaseScene scene = getTopScene();
            RoguelikeActivity.getContext().getEngine().setScene(scene);
            scene.fadeIn(FADE_DURATION, null);
        }
    };
    
    /**
     * Pushes a scene    
     */
    final static IEntityModifierListener transitionUp = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            getTopScene().pause();
            if (mNextScene != null) {
                mScenes.add(mNextScene);
                mNextScene = null;
                //Log.d("SCENE", "Load next scene");
            } else {
                
            }
            setScene();
        }
    };
    
    /**
     * Pops a scene
     */
    final static IEntityModifierListener transitionDown = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {  
            if (mScenes.size() > 0) {
                mScenes.get(mScenes.size()-1).destroy();
                mScenes.remove(mScenes.get(getSize()-1));
            }
            
            setScene();
        }
    };

    public static void fadeSceneOut(float f, IEntityModifierListener listener) {
        BaseScene scene = getTopScene();
        if (scene == null) return;
        
        scene.fadeOut(f, listener);
    }
    
    public static void fadeSceneIn(float f, IEntityModifierListener listener) {
        BaseScene scene = getTopScene();
        if (scene == null) return;
        
        scene.fadeIn(f, listener);
    }
    
}