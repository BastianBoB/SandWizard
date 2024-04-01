package com.basti_bob.sand_wizard.cells.util;

import com.basti_bob.sand_wizard.world.WorldConstants;

public enum ChunkBoarderState {
    CENTER,

    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,

    TOP,
    BOTTOM,
    LEFT,
    RIGHT;

    public static ChunkBoarderState getStateWithInChunkPos(int x, int y) {
        int size = WorldConstants.CHUNK_SIZE;

        boolean top = y == size - 1;
        boolean bottom = y == 0;
        boolean right = x == size - 1;
        boolean left = x == 0;

        if (top) {
            if (right) return TOP_RIGHT;
            if (left) return TOP_LEFT;
            return TOP;
        }

        if (bottom) {
            if (right) return BOTTOM_RIGHT;
            if (left) return BOTTOM_LEFT;
            return BOTTOM;
        }

        if (right) return RIGHT;
        if (left) return LEFT;

        return CENTER;
    }


}
