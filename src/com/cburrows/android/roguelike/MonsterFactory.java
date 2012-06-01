package com.cburrows.android.roguelike;

import java.util.ArrayList;
import java.util.Random;

import junit.framework.Assert;

import com.cburrows.android.roguelike.xml.DungeonMonsterTemplate;

public class MonsterFactory {
    
    private static ArrayList<DungeonMonsterTemplate> sMonsterList;
    private static float sMaxRate = 0f;
    private static Random sRand = new Random(System.currentTimeMillis());
    
    public static void initialize(ArrayList<DungeonMonsterTemplate> monsters) {
        sMonsterList = monsters;
        sMaxRate = 0f;
        for (DungeonMonsterTemplate monster : sMonsterList) {
            sMaxRate += monster.getRate();
        }
    }
    
    public static Monster generateMonster() {
        float prob = sRand.nextFloat() * sMaxRate;
        float curProb = 0f;
        DungeonMonsterTemplate monster = null;
        for (DungeonMonsterTemplate m : sMonsterList) {
            if (prob <= m.mRate + curProb) {
                monster = m;
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
