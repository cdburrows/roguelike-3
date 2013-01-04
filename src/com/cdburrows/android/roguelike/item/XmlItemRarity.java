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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.graphics.Color;

@Root(name = "item_rarity")
public class XmlItemRarity {
    private static final Map<String, Integer> colourMap;
    static {
        colourMap = new HashMap<String, Integer>();
        colourMap.put("white", Color.WHITE);
        colourMap.put("green", Color.GREEN);
        colourMap.put("blue", Color.BLUE);
        colourMap.put("yellow", Color.YELLOW);
        colourMap.put("gray", Color.LTGRAY);
    }

    @Attribute(name = "name")
    public String mName;

    @Element(name = "probability")
    public float mProbability;

    @Element(name = "colour")
    private String mColour;

    @Element(name = "num_properties")
    public int mNumProperties;

    @Element(name = "stat_multiplier_min")
    public float mStatMultiplierMin;

    @Element(name = "stat_multiplier_max")
    public float mStatMultiplierMax;

    public int getColour() {
        return colourMap.get(mColour);
    }

    public float getRandomMultiplier() {
        Random rand = new Random();
        return rand.nextFloat() * (mStatMultiplierMax - mStatMultiplierMin) + mStatMultiplierMin;
    }
}
