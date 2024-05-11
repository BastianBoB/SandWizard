package com.basti_bob.sand_wizard.cells.gases;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.cell_properties.property_types.GasProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class Gas extends MovingCell {

    private static final Vector2 GAS_GRAVITY = new Vector2(WorldConstants.GRAVITY.x, WorldConstants.GRAVITY.y * -0.2f);
    private static final float MAX_SIDEWARDS_ACC = 2;

    private float dispersionRate;
    private float density;
    public int lifeTime;

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

        int yDir = velocityY > 0 ? 1 : -1;
        boolean spaceVertical = canMoveToOrSwap(chunkAccessor.getCell(this.getPosX(), this.getPosY() + yDir));

        boolean spaceLeft = canMoveToOrSwap(chunkAccessor.getCell(this.getPosX() - 1, this.getPosY()));
        boolean spaceRight = canMoveToOrSwap(chunkAccessor.getCell(this.getPosX() + 1, this.getPosY()));

        if (spaceVertical) {
            this.velocityY += this.getGravity().y;
            this.velocityY *= 0.98f;
        }

        if (spaceLeft || spaceRight) {
            this.velocityX += this.getGravity().x;
            this.velocityX += getSidewardsVelocity(updateDirection, spaceLeft, spaceRight);
            this.velocityX *= 0.98f;
        }

        if (!(spaceLeft || spaceRight || spaceVertical)) return;

        if (Math.abs(velocityX) >= 1 || Math.abs(velocityY) >= 1) {
            if (moveWithVelocity(chunkAccessor, updateDirection)) return;
        }
    }

    private float getSidewardsVelocity(boolean updateDirection, boolean spaceLeft, boolean spaceRight) {

        float velocityModifier = getSidewardsVelocityModifier(updateDirection, spaceLeft, spaceRight);

        return velocityModifier * this.getDispersionRate() * world.random.nextFloat();
    }

    private float getSidewardsVelocityModifier(boolean updateDirection, boolean spaceLeft, boolean spaceRight) {

        if (velocityX > MAX_SIDEWARDS_ACC) return world.random.nextBoolean() ? 0 : -0.3f;
        if (velocityX < -MAX_SIDEWARDS_ACC) return world.random.nextBoolean() ? 0 : 0.3f;

        if (spaceLeft && spaceRight) {
            return world.random.nextBoolean() ? 1 : -1;
        }

        if (spaceLeft) {
            return -1;
        }

        return 1;
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
                    velocityY = 0;
                } else {
                    velocityX = 0;
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
