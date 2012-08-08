package com.cdburrows.android.roguelike.scenes;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.util.modifier.IModifier;

import com.cdburrows.android.roguelike.base.RoguelikeActivity;


import android.util.Log;

public class SceneManager {
    
    // ===========================================================
    // Constants
    // ===========================================================
    
    private static final float FADE_DURATION = 0.25f;
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private static List<BaseScene> mScenes  = new ArrayList<BaseScene>();;
    
    private static BaseScene mNextScene = null;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    /*
    public SceneManager(RoguelikeActivity base) {
        mScenes = new ArrayList<GameScene>();
    }
    */
    
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
    
    public static void load() { getTopScene().loadResources(); }
    
    public static void pushScene(BaseScene scene) {
        if (mScenes.size() == 0) {
            mScenes.add(scene);
            setScene();
        } else {
            mNextScene = scene;
            getTopScene().fadeOut(FADE_DURATION, transitionUp);
        }
    }
    
    public static BaseScene popScene() { return popScene(null); }
    
    public static BaseScene popScene(IEntityModifierListener listener) {
        if (mScenes.size() > 0) {
            BaseScene scene = getTopScene();
            scene.fadeOut(FADE_DURATION, transitionDown);
            return scene;
        }
        return null;
    }
    
    private static void setScene() {
        BaseScene scene = getTopScene();
        if (scene != null) {
            if (!scene.isLoaded()) scene.loadResources();
            scene.prepare(prepared); 
        } else {
            RoguelikeActivity.destroy();
        }
    }
    
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    final static IEntityModifierListener prepared = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            Log.d("SCENE", "Scene prepared");
            BaseScene scene = getTopScene();
            RoguelikeActivity.getContext().getEngine().setScene(scene);
            scene.fadeIn(FADE_DURATION, null);
        }
    };
    
    final static IEntityModifierListener transitionUp = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            getTopScene().pause();
            //Log.d("SCENE", "FADE DONe");
            if (mNextScene != null) {
                mScenes.add(mNextScene);
                mNextScene = null;
                Log.d("SCENE", "Load next scene");
            } else {
                
            }
            setScene();
        }
    };
    
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
    
}