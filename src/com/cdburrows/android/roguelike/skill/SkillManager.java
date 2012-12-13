package com.cdburrows.android.roguelike.skill;

import java.util.ArrayList;
import java.util.LinkedList;

import com.cdburrows.android.roguelike.scene.BattleScene;

public class SkillManager {
    
    private static final float SKILL_X = 16;
    private static final float SKILL_Y = 0;
    private static ArrayList<Skill> sSkills;
    private static LinkedList<SkillDirection> sActionQueue = new LinkedList<SkillDirection>();
    private static LinkedList<SkillContainer> sValidCombos = new LinkedList<SkillContainer>();
    private static LinkedList<SkillContainer> sToRemove = new LinkedList<SkillContainer>();

    public static void setSkillList(ArrayList<Skill> skills) {
        sSkills = skills;        
    }
    
    public static void queueAction(SkillDirection direction) {
        sActionQueue.addFirst(direction);
        checkActions();
    }
    
    public static void reset() {
        sActionQueue = new LinkedList<SkillDirection>();
        
        for (SkillContainer skill : sValidCombos) skill.clear();
        sValidCombos = new LinkedList<SkillContainer>();
    }
    
    private static void checkActions() {
        //for (SkillDirection s : sActionQueue) Log.d("Skill Manager", "Action " + s.toString());
        
        //for (SkillContainer s : sValidCombos) Log.d("Skill Manager", "Skill " + s.toString());
        
        for (Skill skill : sSkills) {
            if (skill.getFirstDirection() == sActionQueue.getFirst()) {
                queueSkill(new SkillContainer(skill));
            }
        }
        
        for (int i = 0; i < sValidCombos.size(); i++) {
            SkillContainer skill = sValidCombos.get(i);
            if (skill.checkAction(sActionQueue.getFirst()) == false) {
                // remove
                sToRemove.add(skill);
            } else {
                if (skill.complete()) {
                    BattleScene.showFloatingText(skill.getName(), 96);
                    sToRemove.add(skill);
                }
            }
        }
        
        for (SkillContainer skill : sToRemove) {
            sValidCombos.remove(skill);
            //skill.clear();
        }
        
        alignSkills();
        
        /*
        for (Skill skill : sSkills) {
            if (skill.checkActionQueue(sActionQueue)) {
                if (!sValidCombos.contains(skill)) {
                    queueSprite(skill);
                } 
            } else {
                skill.clear();
                sValidCombos.remove(skill);
                
            }
        }
        */
    }
    
    private static void queueSkill(SkillContainer skill) {
        sValidCombos.addFirst(skill);
        /*
        final TiledSprite sprite = skill.getSprite();
        sprite.setVisible(false);
        
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                BattleScene.getScene().attachChild(sprite);
            }
        });
        */
    }
    
    private static void alignSkills() {
        /*
        for (int i = 0; i < sValidCombos.size(); i++) {
            sValidCombos.get(i).setPosition(SKILL_X * RoguelikeActivity.sScaleX, 
                (BattleScene.getScene().getHeight() - (36 * i) - 40) * RoguelikeActivity.sScaleY );
        }
        */
    }
}
