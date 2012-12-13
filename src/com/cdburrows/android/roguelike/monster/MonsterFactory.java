package com.cdburrows.android.roguelike.monster;

import java.util.ArrayList;
import java.util.Random;

import junit.framework.Assert;

import android.util.Log;

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
