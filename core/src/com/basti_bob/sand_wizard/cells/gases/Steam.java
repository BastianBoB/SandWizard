package com.basti_bob.sand_wizard.cells.gases;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class Steam extends Gas {
    public Steam(CellType cellType) {
        super(cellType);
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

        MoveAlongState moveAlongState = MoveAlongState.STOP;

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

            if (moveAlongState == MoveAlongState.DIE_OR_REPLACE) {
                if (world.random.nextFloat() < 0.8)
                    replace(CellType.WATER, chunkAccessor);
                else
                    die(chunkAccessor);
            }

            return true;
        }

        return false;
    }

    @Override
    public MoveAlongState moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int targetX, int targetY, int lastValidX, int lastValidY, boolean updateDirection) {
        if (targetCell == null) return MoveAlongState.STOP;

        switch (targetCell.getPhysicalState()) {

            case OTHER -> {
                return MoveAlongState.CONTINUE;
            }

            case SOLID, LIQUID -> {
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    velocityY = 1;
                }
                return MoveAlongState.DIE_OR_REPLACE;
            }

            case GAS -> {
                if (!canSwapWith(targetCell)) return MoveAlongState.STOP;

                if (this.getPosX() != lastValidX || this.getPosY() != lastValidY) {
                    chunkAccessor.moveTo(this, lastValidX, lastValidY);
                }

                swapWith(chunkAccessor, targetCell);

                return MoveAlongState.CONTINUE;
            }
        }


        return MoveAlongState.STOP;
    }
}
