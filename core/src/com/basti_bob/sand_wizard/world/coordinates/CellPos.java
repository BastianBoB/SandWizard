package com.basti_bob.sand_wizard.world.coordinates;

import java.util.Objects;

public class CellPos {

    public final int x;
    public final int y;

    public CellPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellPos that = (CellPos) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
