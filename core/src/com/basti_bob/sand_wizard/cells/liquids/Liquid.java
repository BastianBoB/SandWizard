package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cell_properties.property_types.LiquidProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class Liquid extends MovingCell {

    private float dispersionRate;
    private float density;
    private boolean moving;

    public Liquid(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
        this.velocityY = -1;

        LiquidProperty cellProperty = (LiquidProperty) cellType.getCellProperty();

        this.dispersionRate = cellProperty.dispersionRate;
        this.density = cellProperty.density;
    }

    public float getDispersionRate() {
        return dispersionRate;
    }

    public float getDensity() {
        return density;
    }

    @Override
    public boolean canSwapWith(Cell target) {
        if (target instanceof Gas) return true;

        if (target instanceof Liquid liquidTarget) {
            return liquidTarget.getDensity() < this.getDensity();
        }
        return false;
    }

    @Override
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {
        clampVelocity();

        Cell cellBelow = chunkAccessor.getCell(this.posX, this.posY - 1);

        boolean spaceBelow = canMoveToOrSwap(cellBelow);

        if (spaceBelow) {
            this.velocityX += this.getGravity().x;
            this.velocityY += this.getGravity().y;
            this.moving = true;
        } else {
            if (moveOrSwapDownLeftRight(chunkAccessor, updateDirection)) return;

            this.velocityY = -1;

            boolean spaceRight = canMoveToOrSwap(chunkAccessor.getCell(this.posX + 1, this.posY));
            boolean spaceLeft = canMoveToOrSwap(chunkAccessor.getCell(this.posX - 1, this.posY));

            if (!spaceLeft && !spaceRight) {
                this.moving = false;
                this.velocityX = 0;
            } else {
                this.moving = true;
                this.velocityX = getXVelocityWhenBelowIsBlocked(updateDirection, spaceLeft, spaceRight);
            }
        }

        if (!moving) return;

        if (Math.abs(velocityX) >= 1 || Math.abs(velocityY) >= 1) {
            if (moveWithVelocity(chunkAccessor, updateDirection)) return;
        }
    }


    private float getXVelocityWhenBelowIsBlocked(boolean updateDirection, boolean spaceLeft, boolean spaceRight) {
        int velocityModifier = 1;

        if (spaceLeft && spaceRight) {
            if (velocityX == 0) {
                velocityModifier = updateDirection ? 1 : -1;
            } else {
                velocityModifier = velocityX > 0 ? 1 : -1;
            }
        } else if (spaceLeft) {
            velocityModifier = -1;
        }

        return (float) (Math.random() * (velocityModifier * this.getDispersionRate()));
    }

    @Override
    public boolean moveWithVelocity(ChunkAccessor chunkAccessor, boolean updateDirection) {
        float xDistance = Math.abs(velocityX);
        float yDistance = Math.abs(velocityY);

        boolean positiveX = velocityX > 0;
        boolean positiveY = velocityY > 0;

        float xSlope = Math.abs(velocityY / velocityX);
        float ySlope = Math.abs(velocityX / velocityY);

        int steps = (int) Math.max(xDistance, yDistance);

        int lastValidX = posX;
        int lastValidY = posY;

        int startPosX = posX;
        int startPosY = posY;

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

            if (moveAlong(chunkAccessor, targetCell, lastValidX, lastValidY, updateDirection)) {
                lastValidX = targetX;
                lastValidY = targetY;
            } else {
                break;
            }
        }

        if (lastValidX != posX || lastValidY != posY) {
            chunkAccessor.moveToOrSwap(this, lastValidX, lastValidY);
            return true;
        }

        return false;
    }

    @Override
    public boolean moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int lastValidX, int lastValidY, boolean updateDirection) {
        if(targetCell == null) return false;

        switch (targetCell.getPhysicalState()) {

            case OTHER -> {
                this.trySetNeighboursMoving(chunkAccessor, targetCell.posX, targetCell.posY);
                return true;
            }

            case SOLID -> {
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    velocityY = -1;
                }
                return false;
            }

            case LIQUID, GAS -> {
                if (!canSwapWith(targetCell)) return false;

                if (this.posX != lastValidX || this.posY != lastValidY) {
                    chunkAccessor.moveTo(this, lastValidX, lastValidY);
                }

                swapWith(chunkAccessor, targetCell);

                return true;
            }
        }

        return false;
    }
}
