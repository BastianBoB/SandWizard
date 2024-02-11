package com.basti_bob.sand_wizard.coordinateSystems;

import java.util.Objects;

public class InChunkPos {

    private final int x, y;

    public InChunkPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
