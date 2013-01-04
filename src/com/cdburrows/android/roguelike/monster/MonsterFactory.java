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

package com.cdburrows.android.roguelike.monster;

import java.util.ArrayList;
import java.util.Random;

import com.cdburrows.android.roguelike.graphics.Graphics;

public class MonsterFactory {

    private static ArrayList<XmlDungeonMonsterTemplate> sMonsterList;

    private static float sMaxRate = 0f;

    private static Random sRand = new Random(System.currentTimeMillis());

    public static void initialize(ArrayList<XmlDungeonMonsterTemplate> monsters) {
        sMonsterList = monsters;
        sMaxRate = 0f;
        for (XmlDungeonMonsterTemplate monster : sMonsterList) {
            sMaxRate += monster.getRate();
        }
    }

    public static Monster generateMonster() {
        float prob = sRand.nextFloat() * sMaxRate;
        float curProb = 0f;
        XmlDungeonMonsterTemplate monster = null;
        for (XmlDungeonMonsterTemplate m : sMonsterList) {
            if (prob <= (m.mRate + curProb)) {
                monster = m;
                break;
            } else {
                curProb += m.mRate;
            }
        }

        assert monster != null;

        Monster result = new Monster(monster);
        result.setOffY(monster.getOffY());
        Graphics.beginLoad("gfx/monsters/", 256, 256);
        result.setSprite(Graphics.createSprite(monster.getSpritePath()));
        Graphics.endLoad("Monster loaded");

        return result;
    }

}
