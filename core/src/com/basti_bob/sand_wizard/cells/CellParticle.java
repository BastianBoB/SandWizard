package com.basti_bob.sand_wizard.cells;

import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class CellParticle extends MovingCell {

    private Cell containedCell;

    public CellParticle(CellType cellType) {
        super(cellType);
    }

    @Override
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {

    }

    @Override
    public boolean moveWithVelocity(ChunkAccessor chunkAccessor, boolean updateDirection) {
        return false;
    }

    @Override
    public boolean moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int lastValidX, int lastValidY, boolean updateDirection) {
        return false;
    }

}
