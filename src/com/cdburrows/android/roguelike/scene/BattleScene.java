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

package com.cdburrows.android.roguelike.scene;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.util.modifier.IModifier;

import android.util.Log;

import com.cdburrows.android.roguelike.RoguelikeActivity;
import com.cdburrows.android.roguelike.item.Item;
import com.cdburrows.android.roguelike.item.ItemFactory;
import com.cdburrows.android.roguelike.monster.Monster;
import com.cdburrows.android.roguelike.monster.Monster.MonsterState;
import com.cdburrows.android.roguelike.monster.MonsterFactory;
import com.cdburrows.android.roguelike.player.Player;
import com.cdburrows.android.roguelike.skill.SkillDirection;
import com.cdburrows.android.roguelike.skill.SkillManager;

/**
 * Handles combat
 */
public class BattleScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private BattleSceneUI mUI;

    private Monster mMonster;

    private Player mPlayer;

    // TODO: Replace with scene status
    private boolean mSceneReady;

    private float mTime = 0f;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Constructs the scene and the BattleSceneUI
     */
    public BattleScene() {
        super();

        mUI = new BattleSceneUI(this);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public Monster getMonster() {
        return mMonster;
    }

    public boolean isSceneReady() {
        return mSceneReady;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setSceneReady(boolean ready) {
        mSceneReady = ready;
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    /**
     * Loads the player and the UI resources
     */
    @Override
    public void loadResources() {
        long timeStart = System.currentTimeMillis();

        mPlayer = RoguelikeActivity.getPlayer();

        mUI.loadResources();

        this.registerUpdateHandler(updateHandler);

        mLoaded = true;

        Log.d("BATTLE", "Load time: " + (System.currentTimeMillis() - timeStart));
    }

    /**
     * Creates the monster and prepares the UI
     */
    @Override
    public void prepare(IEntityModifierListener preparedListener) {
        mSceneReady = false;

        // The monster
        mMonster = MonsterFactory.generateMonster();
        mMonster.setMonsterState(MonsterState.MONSTER_TRANSITION_IN);
        mMonster.setDead(false);

        mTime = 0f;

        mUI.prepare();

        mPrepared = true;
        preparedListener.onModifierFinished(null, this);
    }

    public void pause() {
    }

    @Override
    public void resume() {
    }

    /**
     * Uninitializes the UI and destroys the monster
     */
    public void destroy() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                mUI.destory();
            }
        });

        mMonster = null;
    }

    /**
     * Sends input to the BattleSceneUI to process
     */
    public boolean onSceneTouchEvent(TouchEvent touchEvent) {
        return mUI.onSceneTouchEvent(touchEvent);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public boolean checkHit(int swipeDirection) {
        assert (swipeDirection >= 0 && swipeDirection < 8);

        if (!isSceneReady())
            return false;
        if (mMonster.isDead())
            return false;
        if (mUI.isAnimating())
            return false;

        // Queue skill
        SkillManager.queueAction(this, SkillDirection.getDirection(swipeDirection));

        int damage = mMonster.hit(mPlayer.getTotalAttack());

        mUI.playerAttack(mMonster, swipeDirection, damage);

        return true;
    }

    /**
     * Recreates the scene
     */
    public void reloadUI() {
        detachChildren();
        mUI.reloadUI();
    }

    /**
     * Changes the background image
     */
    public void reloadBattleBackground() {
        mUI.reloadBattleBackground();
    }

    /**
     * Displays the spoils, if any
     */
    public void displaySpoils() {
        SkillManager.reset();

        int spoils = RoguelikeActivity.nextInt(100);

        if (spoils < 10) {
            // No spoils!
            mUI.spoilsNone();

        } else if (spoils < 20) {
            // A potion
            mPlayer.increasePotions(1);
            mUI.spoilsPotion();

        } else {
            // An item!
            Item item = ItemFactory.createRandomItem(RoguelikeActivity.nextInt(3) + 1);
            mPlayer.addItem(item);
            mUI.spoilsItem(item.copySprite());
        }
    }

    /**
     * Increments the player's XP and prepares to transition out
     * 
     * @param xpGained the XP gained by the player
     */
    public void interruptXpFill(int xpGained) {
        mSceneReady = false;
        mPlayer.increaseXP(xpGained);
    }

    /**
     * Creates a new floating text to scroll up automatically
     * 
     * @param text the text to display
     * @param offX the horizontal offset
     */
    public void showFloatingText(String text, int offX) {
        mUI.showFloatingText(text, offX);

    }

    public void endCombat() {
        RoguelikeActivity.endScene();
    }

    public void takeDamage() {
        mPlayer.decreaseHP(RoguelikeActivity.nextInt(10) + 1);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    /**
     * Run the monster AI
     */
    private final IUpdateHandler updateHandler = new IUpdateHandler() {
        public void onUpdate(float secondsElapsed) {

            // Monster update
            mTime += secondsElapsed;

            if (mMonster.targetable() && mTime > 1.5f) {
                mTime = 0f;
                float attackSpeed = 1.2f - (mMonster.getSpeed() / 100);

                if (RoguelikeActivity.nextFloat() < 0.5f) {
                    mMonster.jumpForward(attackSpeed, monsterAttackListener);
                } else {
                    mMonster.jumpBackward(attackSpeed, monsterEvadeListener);
                }
            }

            mUI.update(secondsElapsed);

        }

        public void reset() {
        }
    };

    /**
     *  Listens for monster attack to shake camera and take damage
     */
    final IEntityModifierListener monsterAttackListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            takeDamage();
            mUI.monsterAttack();
        }
    };

    /**
     * Listens for monster evade events
     */
    final IEntityModifierListener monsterEvadeListener = new IEntityModifierListener() {
        public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
        }

        public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            mUI.monsterEvade();
        }
    };
}
