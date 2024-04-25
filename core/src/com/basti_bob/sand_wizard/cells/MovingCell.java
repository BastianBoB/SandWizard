package com.basti_bob.sand_wizard.cells;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public abstract class MovingCell extends Cell {

    public float velocityX, velocityY;

    public MovingCell(CellType cellType) {
        super(cellType);
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        updateMoving(chunkAccessor, updateDirection);

        super.update(chunkAccessor, updateDirection);
    }

    public abstract void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection);
    public abstract boolean moveWithVelocity(ChunkAccessor chunkAccessor, boolean updateDirection);

    public abstract boolean moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int lastValidX, int lastValidY, boolean updateDirection);


    public boolean moveOrSwapDownLeftRight(ChunkAccessor chunkAccessor, boolean updateDirection) {
        if (updateDirection) {
            if (chunkAccessor.moveToOrSwap(this, posX + 1, posY - 1)) return true;
            return chunkAccessor.moveToOrSwap(this, posX - 1, posY - 1);
        } else {
            if (chunkAccessor.moveToOrSwap(this, posX - 1, posY - 1)) return true;
            return chunkAccessor.moveToOrSwap(this, posX + 1, posY - 1);
        }
    }

    public void clampVelocity() {
        if (velocityX > WorldConstants.CHUNK_SIZE) velocityX = WorldConstants.CHUNK_SIZE;
        else if (velocityX < -WorldConstants.CHUNK_SIZE) velocityX = -WorldConstants.CHUNK_SIZE;

        if (velocityY > WorldConstants.CHUNK_SIZE) velocityY = WorldConstants.CHUNK_SIZE;
        else if (velocityY < -WorldConstants.CHUNK_SIZE) velocityY= -WorldConstants.CHUNK_SIZE;
    }
}
