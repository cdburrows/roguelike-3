package com.cburrows.android.roguelike;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.util.modifier.IModifier;

import android.util.Log;

public class SceneManager {
    private static final float FADE_DURATION = 0.25f;
    
    private RoguelikeActivity mBase;
    private List<GameScene> mScenes;
 
    public SceneManager(RoguelikeActivity base) {
        // context to work with from the main game activity
        this.mBase = base;
        this.mScenes = new ArrayList<GameScene>();
    }
    
    public void pushScene(GameScene scene) { pushScene(scene, null); }
    public void pushScene(GameScene scene, IEntityModifierListener listener) {
        if (mScenes.size() == 0) {
            mScenes.add(scene);
            setScene(listener);
        } else {
            mScenes.add(scene);
            mScenes.get(getSize()-2).fadeOut(FADE_DURATION, transition);
        }
    }
    
    public GameScene popScene() { return popScene(null); }
    public GameScene popScene(IEntityModifierListener listener) {
        if (mScenes.size() > 0) {
            GameScene scene = getTopScene();
            scene.fadeOut(FADE_DURATION, transition);
            mScenes.remove(mScenes.get(getSize()-1)); 
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
    
    private void setScene(IEntityModifierListener listener) {
        GameScene scene = getTopScene();
        if (scene != null) {
            if (!scene.isLoaded()) scene.loadResources();
            
            scene.initialize();
            scene.fadeIn(FADE_DURATION, listener);
            mBase.getEngine().setScene(scene);
        }
    }   
    
    final IEntityModifierListener transition = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {       
            Log.d("SCENE", "FADE DONe");
            setScene(null);
        }
    };
}