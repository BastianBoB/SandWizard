package com.basti_bob.sand_wizard.world.coordinates;

import com.basti_bob.sand_wizard.world.WorldConstants;

import java.util.Objects;

public class InChunkPos {

    public final int x;
    public final int y;

    private static final InChunkPos[][] positions;

    static {
        positions = new InChunkPos[WorldConstants.CHUNK_SIZE][WorldConstants.CHUNK_SIZE];

        for(int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
            for(int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                positions[i][j] = new InChunkPos(i, j);
            }
        }
    }

    private InChunkPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static InChunkPos get(int x, int y) {
        return positions[x][y];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InChunkPos that = (InChunkPos) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}