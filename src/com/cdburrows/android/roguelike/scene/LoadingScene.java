package com.cdburrows.android.roguelike.scene;

import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;

public class LoadingScene extends BaseScene {

    public LoadingScene() {
        super();
    }
    
    @Override
    public void loadResources() {
        mLoaded = true;       
    }

    @Override
    public void prepare(IEntityModifierListener preparedListener) {
        mPrepared = true;   
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        
    }

}
