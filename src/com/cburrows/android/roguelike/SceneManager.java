package com.cburrows.android.roguelike;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.util.modifier.IModifier;

import android.util.Log;

public class SceneManager {
    private static final float FADE_DURATION = 0.25f;
    
    private List<GameScene> mScenes;
    private GameScene mNextScene = null;
 
    public SceneManager(RoguelikeActivity base) {
        mScenes = new ArrayList<GameScene>();
    }
    
    //public void pushScene(GameScene scene) { pushScene(scene, null); }
    public void pushScene(GameScene scene) {
        if (mScenes.size() == 0) {
            mScenes.add(scene);
            setScene();
        } else {
            mNextScene = scene;
            getTopScene().fadeOut(FADE_DURATION, transitionUp);
        }
    }
    
    public GameScene popScene() { return popScene(null); }
    public GameScene popScene(IEntityModifierListener listener) {
        if (mScenes.size() > 0) {
            GameScene scene = getTopScene();
            scene.fadeOut(FADE_DURATION, transitionDown);
            return scene;
        }
        return null;
    }
    
    public void load() { getTopScene().loadResources(); }
        
    public int getSize() {
        return mScenes.size();
    }
    
    public GameScene getTopScene() { 
        if (mScenes.size() > 0) { 
            return mScenes.get(mScenes.size()-1); 
        } else {
            return null;
        }
    }
    
    private void setScene() {
        GameScene scene = getTopScene();
        if (scene != null) {
            if (!scene.isLoaded()) scene.loadResources();
            scene.prepare(prepared); 
        } else {
            RoguelikeActivity.destroy();
        }
    }   
    
    final IEntityModifierListener prepared = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            Log.d("SCENE", "Scene prepared");
            GameScene scene = getTopScene();
            RoguelikeActivity.getContext().getEngine().setScene(scene);
            scene.fadeIn(FADE_DURATION, null);
        }
    };
    
    final IEntityModifierListener transitionOn = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            Log.d("SCENE", "Fade in complete");
            setScene();
        }
    };
    
    
    
    final IEntityModifierListener transitionUp = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
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
    
    final IEntityModifierListener transitionDown = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            mScenes.get(mScenes.size()-1).destroy();
            mScenes.remove(mScenes.get(getSize()-1));
            
            setScene();
        }
    };
}