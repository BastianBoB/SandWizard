package com.basti_bob.sand_wizard.cells.gases;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cell_properties.property_types.GasProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class Gas extends MovingCell {

    private static final Vector2 GAS_GRAVITY = new Vector2(WorldConstants.GRAVITY.x, WorldConstants.GRAVITY.y * -0.1f);

    private float dispersionRate;
    private float density;
    protected int lifeTime;
    private boolean moving;

    public Gas(CellType cellType) {
        super(cellType);
        this.velocityY = 1;

        GasProperty cellProperty = (GasProperty) cellType.getCellProperty();

        this.dispersionRate = cellProperty.dispersionRate;
        this.density = cellProperty.density;
        this.lifeTime = cellProperty.lifeTime;
    }

    @Override
    public Vector2 getGravity() {
        return GAS_GRAVITY;
    }

    public float getDispersionRate() {
        return dispersionRate;
    }

    public float getDensity() {
        return density;
    }

    @Override
    public boolean canSwapWith(Cell target) {
        if (target instanceof Gas gasTarget) {
            return gasTarget.getDensity() < this.getDensity();
        }
        return false;
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        if (--lifeTime <= 0) die(chunkAccessor);
    }

    @Override
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {
        Cell cellAbove = chunkAccessor.getCell(this.getPosX(), this.getPosY() + 1);

        boolean spaceAbove = canMoveToOrSwap(cellAbove);

        if (spaceAbove) {
            this.velocityX += this.getGravity().x;
            this.velocityY += this.getGravity().y;
            this.moving = true;
        } else {
            this.velocityY = 1;
        }

        boolean spaceLeft = canMoveToOrSwap(chunkAccessor.getCell(this.getPosX() - 1, this.getPosY()));
        boolean spaceRight = canMoveToOrSwap(chunkAccessor.getCell(this.getPosX() + 1, this.getPosY()));

        if (!spaceLeft && !spaceRight && !spaceAbove) {
            this.moving = false;
        } else {
            this.moving = true;
            this.velocityX += getXVelocityWhenBelowIsBlocked(updateDirection, spaceLeft, spaceRight);
            this.velocityX *= 0.7f;
        }

        if (!moving) return;

        if (Math.abs(velocityX) >= 1 || Math.abs(velocityY) >= 1) {
            if (moveWithVelocity(chunkAccessor, updateDirection)) return;
        }
    }

    private float getXVelocityWhenBelowIsBlocked(boolean updateDirection, boolean spaceLeft, boolean spaceRight) {
        int velocityModifier = 1;

        if (spaceLeft && spaceRight) {
            velocityModifier = (world.random.nextFloat() > 0.5 ? 1 : -1);
        } else if (spaceLeft) {
            velocityModifier = -1;
        }

        return velocityModifier * this.getDispersionRate() * world.random.nextFloat();
    }

    @Override
    public MoveAlongState moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int targetX, int targetY, int lastValidX, int lastValidY, boolean updateDirection) {
        if(targetCell == null) return MoveAlongState.STOP;

        switch (targetCell.getPhysicalState()) {

            case OTHER -> {
                return MoveAlongState.CONTINUE;
            }

            case SOLID, LIQUID -> {
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    velocityY = 1;
                }
                return MoveAlongState.STOP;
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

    @Override
    public boolean shouldActiveChunk() {
        return true;
    }

}
