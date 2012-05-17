package com.cburrows.android.roguelike;

public enum Direction {
    DIRECTION_UP(0), DIRECTION_RIGHT(1), DIRECTION_DOWN(2), DIRECTION_LEFT(3);
    
    private final int value;
    
    Direction(int value) { this.value = value; }
    public int getValue() { return value; }
}
