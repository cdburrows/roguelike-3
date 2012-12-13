package com.cdburrows.android.roguelike.skill;

public enum SkillDirection {
        DIRECTION_UP(6), DIRECTION_RIGHT(0), DIRECTION_DOWN(2), DIRECTION_LEFT(4),
        DIRECTION_UP_RIGHT(7), DIRECTION_UP_LEFT(5), DIRECTION_DOWN_RIGHT(1), DIRECTION_DOWN_LEFT(3);
        
        private final int value;
        
        SkillDirection(int value) { this.value = value; }
        public int getValue() { return value; }
        
        public static SkillDirection getDirection(int direction) {
            for (SkillDirection d : SkillDirection.values()) {
                if (direction == d.value) return d;
            }
            return null;
        }
}
