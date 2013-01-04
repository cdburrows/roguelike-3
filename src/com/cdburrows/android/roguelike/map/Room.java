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

package com.cdburrows.android.roguelike.map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import com.cdburrows.android.roguelike.Direction;

@Root(name = "room")
public class Room {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @Attribute(name = "x")
    protected int mX = 0;

    @Attribute(name = "y")
    protected int mY = 0;

    @ElementArray(name = "accessable")
    protected boolean[] mAccessable = new boolean[Direction.values().length];

    @Attribute(name = "chest")
    protected boolean mChest;

    @Attribute(name = "stairs_up")
    protected boolean mStairsUp;

    @Attribute(name = "stairs_down")
    protected boolean mStairsDown;

    @Attribute(name = "room_state")
    protected RoomState mRoomState;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Room(int x, int y) {
        mX = x;
        mY = y;
        for (int i = 0; i < 4; i++)
            mAccessable[i] = false;
        mRoomState = RoomState.ROOM_HIDDEN;
        mChest = false;
        mStairsUp = false;
        mStairsDown = false;
        mRoomState = RoomState.ROOM_HIDDEN;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public int getX() {
        return mX;
    }

    public void setX(int X) {
        this.mX = X;
    }

    public int getY() {
        return mY;
    }

    public void setY(int Y) {
        this.mY = Y;
    }

    public boolean isAccessable(Direction direction) {
        return mAccessable[direction.getValue()];
    }

    public void setAccessable(net.bytten.metazelda.Direction d, boolean value) {
        if (d == net.bytten.metazelda.Direction.N)
            setAccessable(Direction.DIRECTION_UP, value);
        if (d == net.bytten.metazelda.Direction.E)
            setAccessable(Direction.DIRECTION_RIGHT, value);
        if (d == net.bytten.metazelda.Direction.S)
            setAccessable(Direction.DIRECTION_DOWN, value);
        if (d == net.bytten.metazelda.Direction.W)
            setAccessable(Direction.DIRECTION_LEFT, value);
    }

    public void setAccessable(Direction direction, boolean value) {
        mAccessable[direction.getValue()] = value;
    }

    public boolean hasChest() {
        return mChest;
    }

    public void setChest(boolean value) {
        mChest = value;
    }

    public boolean hasStairsUp() {
        return mStairsUp;
    }

    public void setStairsUp(boolean value) {
        mStairsUp = value;
    }

    public boolean hasStairsDown() {
        return mStairsDown;
    }

    public void setStairsDown(boolean value) {
        mStairsDown = value;
    }

    public RoomState getRoomState() {
        return mRoomState;
    }

    public void setRoomState(RoomState roomState) {
        if (mRoomState == RoomState.ROOM_OCCUPIED
                && roomState.value != RoomState.ROOM_OCCUPIED.value) {
            mRoomState = RoomState.ROOM_VISITED;
            return;
        }
        if (roomState.value < mRoomState.getValue())
            return;

        mRoomState = roomState;
    }

    // ===========================================================
    // Inherited Methods
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public void translateMZRoom(net.bytten.metazelda.Room room) {
        if (room.isStart())
            setStairsUp(true);

        if (room.isGoal())
            setStairsDown(true);

        // Set room accessibility
        for (net.bytten.metazelda.Direction d : net.bytten.metazelda.Direction.values()) {
            if (!(room.getEdge(d) == null)) {
                setAccessable(d, true);
            }
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public enum RoomState {
        ROOM_HIDDEN(0), ROOM_SPOTTED(1), ROOM_VISITED(2), ROOM_OCCUPIED(3);

        private final int value;

        RoomState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
