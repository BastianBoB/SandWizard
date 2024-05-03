package com.basti_bob.sand_wizard.cells;

import com.basti_bob.sand_wizard.cell_properties.PhysicalState;
import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
import com.basti_bob.sand_wizard.world.chunk.CellPlaceFlag;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class CellParticle extends MovingCell {

    private final Cell containedCell;

    public CellParticle(Cell containedCell) {
        super(CellType.PARTICLE);

        this.containedCell = containedCell;
        this.setColor(containedCell.getColorR(), containedCell.getColorG(), containedCell.getColorB());
    }

    @Override
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {
        this.velocityX *= 1;

        this.velocityX += getGravity().x;
        this.velocityY += getGravity().y;

        moveWithVelocity(chunkAccessor, updateDirection);
    }

    @Override
    public boolean shouldActiveChunk() {
        return true;
    }

    @Override
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

        boolean replace = false;

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

            if (moveAlong(chunkAccessor, targetCell, targetX, targetY, lastValidX, lastValidY, updateDirection) == MoveAlongState.CONTINUE) {
                lastValidX = targetX;
                lastValidY = targetY;
            } else {
                replace = true;
                break;
            }
        }

        if (lastValidX != getPosX() || lastValidY != getPosY()) {
            chunkAccessor.moveTo(this, lastValidX, lastValidY);
        }

        if (replace) dieAndReplace(chunkAccessor, containedCell);

        return false;
    }

    public void dieAndReplace(ChunkAccessor chunkAccessor, Cell cell) {
        trySetNeighboursMoving(chunkAccessor, this.getPosX(), this.getPosY());

        chunkAccessor.setCell(cell, this.getPosX(), this.getPosY(), this.getInChunkX(), this.getInChunkY());
    }

    @Override
    public MoveAlongState moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int targetX, int targetY, int lastValidX, int lastValidY, boolean updateDirection) {
        if (targetCell == null) return MoveAlongState.STOP;

        if(targetCell.getPhysicalState() == PhysicalState.OTHER) {
            return MoveAlongState.CONTINUE;
        } else {
            return MoveAlongState.STOP;
        }
    }

}
