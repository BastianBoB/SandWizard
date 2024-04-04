package com.basti_bob.sand_wizard.cells.gases;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cell_properties.property_types.GasProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class Gas extends MovingCell {

    private static final Vector2 GAS_GRAVITY = new Vector2(WorldConstants.GRAVITY.x, WorldConstants.GRAVITY.y * -0.1f);

    private float dispersionRate;
    private float density;
    private boolean moving;

    public Gas(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
        this.velocityY = 1;

        GasProperty cellProperty = (GasProperty) cellType.getCellProperty();

        this.dispersionRate = cellProperty.dispersionRate;
        this.density = cellProperty.density;
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
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {
        clampVelocity();

        Cell cellAbove = chunkAccessor.getCell(this.posX, this.posY + 1);

        boolean spaceAbove = canMoveToOrSwap(cellAbove);

        if (spaceAbove) {
            this.velocityX += this.getGravity().x;
            this.velocityY += this.getGravity().y;
            this.moving = true;
        } else {
            this.velocityY = 1;
        }

        boolean spaceLeft = canMoveToOrSwap(chunkAccessor.getCell(this.posX - 1, this.posY));
        boolean spaceRight = canMoveToOrSwap(chunkAccessor.getCell(this.posX + 1, this.posY));

        if (!spaceLeft && !spaceRight && !spaceAbove) {
            this.moving = false;
        } else {
            this.moving = true;
            this.velocityX += getXVelocityWhenBelowIsBlocked(updateDirection, spaceLeft, spaceRight);
            this.velocityX *= WorldConstants.AIR_FRICTION;
        }

        if (!moving) return;

        if (Math.abs(velocityX) >= 1 || Math.abs(velocityX) >= 1) {
            if (moveWithVelocity(chunkAccessor, updateDirection)) return;
        }
    }

    private float getXVelocityWhenBelowIsBlocked(boolean updateDirection, boolean spaceLeft, boolean spaceRight) {
        int velocityModifier = 1;

        if (spaceLeft && spaceRight) {
            velocityModifier = (Math.random() > 0.5 ? 1 : -1);
        } else if (spaceLeft) {
            velocityModifier = -1;
        }

        return (float) (velocityModifier * this.getDispersionRate() * Math.random());
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

        switch (targetCell.getPhysicalState()) {

            case OTHER -> {
                this.trySetNeighboursMoving(chunkAccessor, targetCell.posX, targetCell.posY);
                return true;
            }

            case SOLID, LIQUID -> {
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    velocityY = 1;
                }
                return false;
            }

            case GAS -> {
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
