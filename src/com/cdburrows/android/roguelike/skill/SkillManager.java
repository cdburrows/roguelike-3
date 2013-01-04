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

package com.cdburrows.android.roguelike.skill;

import java.util.ArrayList;
import java.util.LinkedList;

import com.cdburrows.android.roguelike.scene.BattleScene;

public class SkillManager {

    private static ArrayList<Skill> sSkills;

    private static LinkedList<SkillDirection> sActionQueue = new LinkedList<SkillDirection>();

    private static LinkedList<SkillContainer> sValidCombos = new LinkedList<SkillContainer>();

    private static LinkedList<SkillContainer> sToRemove = new LinkedList<SkillContainer>();

    public static void setSkillList(ArrayList<Skill> skills) {
        sSkills = skills;
    }

    public static void queueAction(BattleScene battleScene, SkillDirection direction) {
        sActionQueue.addFirst(direction);
        checkActions(battleScene);
    }

    public static void reset() {
        sActionQueue = new LinkedList<SkillDirection>();

        for (SkillContainer skill : sValidCombos)
            skill.clear();
        sValidCombos = new LinkedList<SkillContainer>();
    }

    private static void checkActions(BattleScene battleScene) {
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
                    battleScene.showFloatingText(skill.getName(), 96);
                    sToRemove.add(skill);
                }
            }
        }

        for (SkillContainer skill : sToRemove) {
            sValidCombos.remove(skill);
        }

        alignSkills();
    }

    private static void queueSkill(SkillContainer skill) {
        sValidCombos.addFirst(skill);
    }

    private static void alignSkills() {
    }
}
