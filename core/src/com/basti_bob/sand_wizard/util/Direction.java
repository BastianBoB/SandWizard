package com.basti_bob.sand_wizard.util;

public enum Direction {

    UP, DOWN, LEFT, RIGHT;

    public int getXOff() {
        return switch (this) {
            case UP, DOWN -> 0;
            case LEFT -> -1;
            case RIGHT -> 1;
        };
    }

    public int getYOff() {
        return switch (this) {
            case LEFT, RIGHT -> 0;
            case DOWN -> -1;
            case UP -> 1;
        };
    }

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
}
