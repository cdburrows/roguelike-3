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

package com.cdburrows.android.roguelike.item;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Root(name = "item_definitions")
public class XmlItemDefinitions {

    @Attribute(name = "image_path")
    public String mImagePath;

    @Attribute(name = "first_weapon")
    public int mFirstWeapon;

    @Attribute(name = "first_armour")
    public int mFirstArmour;

    @ElementList(name = "item_rarity", inline = true)
    public List<XmlItemRarity> mItemRarityList;

    @ElementList(name = "base_item", inline = true)
    public List<XmlItemDefinition> mItemList;

    private float mTotalRarityProbability;

    public XmlItemRarity getRandomRarity() {
        Random rand = new Random();
        float result = rand.nextFloat() * mTotalRarityProbability;
        float totalProb = 0f;
        for (XmlItemRarity i : mItemRarityList) {
            if (result < i.mProbability + totalProb)
                return i;
            totalProb += i.mProbability;
        }
        return null;
    }

    private void build() {
        mTotalRarityProbability = 0;
        for (XmlItemRarity i : mItemRarityList) {
            mTotalRarityProbability += i.mProbability;
        }
    }

    public static XmlItemDefinitions inflate(InputStream stream) {
        Serializer serializer = new Persister();

        try {
            XmlItemDefinitions col = serializer.read(XmlItemDefinitions.class, stream);

            col.build();
            return col;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

}
