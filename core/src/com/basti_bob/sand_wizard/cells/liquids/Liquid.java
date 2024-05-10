package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.cell_properties.property_types.LiquidProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class Liquid extends MovingCell {

    private float dispersionRate;
    private float density;
    protected boolean moving;

    public Liquid(CellType cellType) {
        super(cellType);
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

        Cell cellBelow = chunkAccessor.getCell(this.getPosX(), this.getPosY() - 1);

        boolean spaceBelow = canMoveToOrSwap(cellBelow);

        if (spaceBelow) {
            this.velocityX += this.getGravity().x;
            this.velocityY += this.getGravity().y;
            this.moving = true;
        } else {
            if (moveOrSwapDownLeftRight(chunkAccessor, updateDirection)) return;

            this.velocityY = -1;

            boolean spaceRight = canMoveToOrSwap(chunkAccessor.getCell(this.getPosX() + 1, this.getPosY()));
            boolean spaceLeft = canMoveToOrSwap(chunkAccessor.getCell(this.getPosX() - 1, this.getPosY()));

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

        return world.random.nextFloat() * (velocityModifier * this.getDispersionRate());
    }

    @Override
    public MoveAlongState moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int targetX, int targetY, int lastValidX, int lastValidY, boolean updateDirection) {
        if(targetCell == null) return MoveAlongState.STOP;

        switch (targetCell.getPhysicalState()) {

            case OTHER -> {
                this.trySetNeighboursMoving(chunkAccessor, targetX, targetY);
                return MoveAlongState.CONTINUE;
            }

            case SOLID -> {
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    velocityY = -1;
                } else {
                    velocityX = 0;
                }
                return MoveAlongState.STOP;
            }

            case LIQUID, GAS -> {
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
