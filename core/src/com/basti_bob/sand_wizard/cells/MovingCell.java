package com.basti_bob.sand_wizard.cells;

import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
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
    public boolean moveWithVelocity(ChunkAccessor chunkAccessor, boolean updateDirection) {
        clampVelocity();

        float xDistance = Math.abs(velocityX);
        float yDistance = Math.abs(velocityY);

        boolean positiveX = velocityX > 0;
        boolean positiveY = velocityY > 0;

        float xSlope = Math.abs(velocityY / velocityX);
        float ySlope = Math.abs(velocityX / velocityY);

        int steps = (int) Math.max(xDistance, yDistance);

        int lastValidX = getPosX();
        int lastValidY = getPosY();

        int startPosX = getPosX();
        int startPosY = getPosY();

        MoveAlongState moveAlongState;

        for (int i = 1; i <= steps; i++) {
            xDistance = Math.abs(velocityX);
            yDistance = Math.abs(velocityY);

            float x, y;

            if (xDistance > yDistance) {
                x = positiveX ? i : -i;
                y = positiveY ? i * xSlope : -i * xSlope;
            } else {
                x = positiveX ? i * ySlope : -i * ySlope;
                y = positiveY ? i : -i;
            }

            int targetX = startPosX + (int) x;
            int targetY = startPosY + (int) y;
            Cell targetCell = chunkAccessor.getCell(targetX, targetY);

            moveAlongState = moveAlong(chunkAccessor, targetCell, targetX, targetY, lastValidX, lastValidY, updateDirection);

            if (moveAlongState == MoveAlongState.CONTINUE) {
                lastValidX = targetX;
                lastValidY = targetY;
            } else {
                break;
            }
        }

        if (lastValidX != getPosX() || lastValidY != getPosY()) {
            chunkAccessor.moveToOrSwap(this, lastValidX, lastValidY);
            return true;
        }

        return false;
    }

    public abstract MoveAlongState moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int targetX, int targetY, int lastValidX, int lastValidY, boolean updateDirection);


    public boolean moveOrSwapDownLeftRight(ChunkAccessor chunkAccessor, boolean updateDirection) {
        if (updateDirection) {
            if (chunkAccessor.moveToOrSwap(this, getPosX() - 1, getPosY() - 1)) return true;
            return chunkAccessor.moveToOrSwap(this, getPosX() + 1, getPosY() - 1);
        } else {
            if (chunkAccessor.moveToOrSwap(this, getPosX() + 1, getPosY() - 1)) return true;
            return chunkAccessor.moveToOrSwap(this, getPosX() - 1, getPosY() - 1);
        }
    }

    public void clampVelocity() {
        if (velocityX > WorldConstants.CHUNK_SIZE) velocityX = WorldConstants.CHUNK_SIZE;
        else if (velocityX < -WorldConstants.CHUNK_SIZE) velocityX = -WorldConstants.CHUNK_SIZE;

        if (velocityY > WorldConstants.CHUNK_SIZE) velocityY = WorldConstants.CHUNK_SIZE;
        else if (velocityY < -WorldConstants.CHUNK_SIZE) velocityY= -WorldConstants.CHUNK_SIZE;
    }
}
