package com.cdburrows.android.roguelike.skills;

import java.util.ArrayList;
import java.util.LinkedList;

import org.anddev.andengine.entity.sprite.TiledSprite;
import com.cdburrows.android.roguelike.base.Graphics;
import com.cdburrows.android.roguelike.base.RoguelikeActivity;

public class Skill {
    private static final int ATLAS_WIDTH = 128;
    private static final int ATLAS_HEIGHT = 128;
    private String mName;
    private TiledSprite mSprite;
    private TiledSprite[] mArrows;
    private ArrayList<SkillDirection> mComboList;
    
    public Skill(String name, ArrayList<SkillDirection> comboList) {
        mName = name;
        mComboList = comboList;
        //build();                
    }
    
    public boolean checkActionQueue(LinkedList<SkillDirection> actionQueue) {
        for (int i = 0; i < actionQueue.size(); i++) {
            if (actionQueue.get(i) == mComboList.get(i)) {
                mArrows[i].setColor(0, 0, 0.8f, 1.0f);
            } else {
                clearArrows();
                return false;
            }
        }
        return true;
    }
    
    public SkillDirection getFirstDirection() { return mComboList.get(0); }
    
    public SkillDirection getComboIndex(int index) { return mComboList.get(index); }
    
    public void clear() {
        clearArrows();
        mSprite.setVisible(false);
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                mSprite.detachSelf();
            }
        });
    }
    
    public TiledSprite createSprite() {
        Graphics.beginLoad("gfx/panels/", ATLAS_WIDTH, ATLAS_HEIGHT);
        TiledSprite sprite = Graphics.createTiledSprite("skill_bg.png", 1, 1);
        TiledSprite[] arrows = new TiledSprite[mComboList.size()];
        for (int i = 0; i < mComboList.size(); i++) {
            arrows[i] = Graphics.createTiledSprite("arrows.png", 8, 1, 
                    (4 + 15 * i) * RoguelikeActivity.sScaleX,
                    17 * RoguelikeActivity.sScaleY);
            arrows[i].setCurrentTileIndex(mComboList.get(i).getValue());
            sprite.attachChild(arrows[i]);
        }
        sprite.attachChild(Graphics.createText((4 * RoguelikeActivity.sScaleX), (4 * RoguelikeActivity.sScaleY), 
                Graphics.SmallFont, mName));
        Graphics.endLoad();
        return sprite;
    }
    
    private void clearArrows() {
        for (int i = 0; i < mComboList.size(); i++) {
            mArrows[i].setColor(1f, 1f, 1f, 1.0f);
        }
    }

    public int getSize() {
        return mComboList.size();
    }

    public String getName() {
        return mName;
    }
    
    //public TiledSprite getSprite() { return mSprite; }
}
