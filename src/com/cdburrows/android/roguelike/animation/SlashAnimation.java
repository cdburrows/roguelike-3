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

package com.cdburrows.android.roguelike.animation;

public enum SlashAnimation {

    RIGHT(      0, -48, 0,      48, 0,      45.0f, false, false),

    DOWN_RIGHT( 1, -48, -48,     48, 48,     0f, true, false),

    DOWN(       2, 0, -48,      0, 48,      -45.0f, false, false),

    DOWN_LEFT(  3, 48, -48,     -48, 48,    0f, false, false),

    LEFT(       4, 48, 0,       -48, 0,     45.0f, false, false),

    UP_LEFT(    5, 48, 48,      -48, -48,   0f, false, true),

    UP(         6, 0, 48,       0, -48,     135.0f, false, false),

    UP_RIGHT(   7, -48, 48,     48, -48,    0f, true, true);
    
    private int index;  // important that these coordinate with input calculation
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float rotation;
    private boolean flipX;
    private boolean flipY;
    
    SlashAnimation(int i, float startX, float startY, float endX, float endY, 
            float rotation, boolean flipX, boolean flipY) {
        this.index = i;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.rotation = rotation;
        this.flipX = flipX;
        this.flipY = flipY;
    }

    public int getIndex() {
        return index;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }

    public float getRotation() {
        return rotation;
    }

    public boolean isFlippedX() {
        return flipX;
    }

    public boolean isFlippedY() {
        return flipY;
    }

}
