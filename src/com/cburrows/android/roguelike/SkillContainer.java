package com.cburrows.android.roguelike;

import org.anddev.andengine.entity.sprite.TiledSprite;

public  class SkillContainer {
    public Skill mSkill;
    public TiledSprite mSprite;
    public int mComboIndex;
    
    public SkillContainer(Skill skill) {
        this.mSkill = skill;
        mSprite = skill.createSprite();
        mComboIndex = 0;
    }
    
    public void clear() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                mSprite.detachSelf();
            }
        });
    }
    
    public TiledSprite getSprite() {
        return mSprite;
    }
    
    public boolean checkAction(SkillDirection action) {
        if (mSkill.getComboIndex(mComboIndex) == action) {
            mSprite.getChild(mComboIndex).setColor(0, 0, 0.8f, 1.0f);
            mComboIndex++;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean complete() { return mComboIndex == mSkill.getSize(); }

    public void setPosition(float x, float y) {
        mSprite.setPosition(x, y);
        mSprite.setVisible(true);
    }

    public String getName() {
        return mSkill.getName();
    }
}
