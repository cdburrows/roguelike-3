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

import org.anddev.andengine.entity.sprite.TiledSprite;

import com.cdburrows.android.roguelike.RoguelikeActivity;

public class SkillContainer {
    public Skill mSkill;

    public TiledSprite mSprite;

    public int mComboIndex;

    public SkillContainer(Skill skill) {
        this.mSkill = skill;
        // mSprite = skill.createSprite();
        mComboIndex = 0;
    }

    public void clear() {
        RoguelikeActivity.getContext().runOnUpdateThread(new Runnable() {
            public void run() {
                // mSprite.detachSelf();
            }
        });
    }

    public TiledSprite getSprite() {
        return mSprite;
    }

    public boolean checkAction(SkillDirection action) {
        if (mSkill.getComboIndex(mComboIndex) == action) {
            // mSprite.getChild(mComboIndex).setColor(0, 0, 0.8f, 1.0f);
            mComboIndex++;
            return true;
        } else {
            return false;
        }
    }

    public boolean complete() {
        return mComboIndex == mSkill.getSize();
    }

    public void setPosition(float x, float y) {
        // mSprite.setPosition(x, y);
        // mSprite.setVisible(true);
    }

    public String getName() {
        return mSkill.getName();
    }
}
