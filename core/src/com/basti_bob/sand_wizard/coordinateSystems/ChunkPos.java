package com.basti_bob.sand_wizard.coordinateSystems;

import java.util.Objects;

public class ChunkPos {

    private final int x, y;
    private final int hashCode;

    public ChunkPos(int x, int y) {
        this.x = x;
        this.y = y;
        this.hashCode = Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ChunkPos offset(int xOff, int yOff) {
        return new ChunkPos(this.x + xOff, this.y + yOff);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        ChunkPos that = (ChunkPos) obj;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
