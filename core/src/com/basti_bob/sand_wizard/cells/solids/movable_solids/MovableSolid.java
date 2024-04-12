package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.cell_properties.property_types.MovableSolidProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class MovableSolid extends MovingCell {

    private boolean moving;
    private float movingResistance;
    private float sprayFactor;

    public MovableSolid(CellType cellType) {
        super(cellType);
        this.velocityY = -1;

        MovableSolidProperty cellProperty = (MovableSolidProperty) cellType.getCellProperty();

        this.movingResistance = cellProperty.movingResistance;
        this.sprayFactor = cellProperty.sprayFactor;
    }

    public float getMovingResistance() {
        return movingResistance;
    }

    public float getSprayFactor() {
        return sprayFactor;
    }

    public void trySetMoving() {
        this.moving = this.moving || MathUtils.random() > this.getMovingResistance();
    }

    public void trySetStationary() {
        this.moving = !(MathUtils.random() < this.getMovingResistance());
    }

    @Override
    public boolean canSwapWith(Cell target) {
        return target instanceof Liquid || target instanceof Gas;
    }

    @Override
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {

        Cell cellBelow = chunkAccessor.getCell(this.posX, this.posY - 1);
        boolean spaceBelow;

        if (cellBelow == null) {
            spaceBelow = false;
        } else {
            velocityX *= cellBelow.getFriction();
            spaceBelow = canMoveToOrSwap(cellBelow);
        }

        if (spaceBelow) {
            this.moving = true;
            this.velocityX += this.getGravity().x;
            this.velocityY += this.getGravity().y;
        }

        if (!moving) return;

        if (Math.abs(velocityX) >= 1 || Math.abs(velocityY) >= 1) {
            if (moveWithVelocity(chunkAccessor, updateDirection)) return;
        }

        moveOrSwapDownLeftRight(chunkAccessor, updateDirection);

        trySetStationary();

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

        int lastValidX = posX;
        int lastValidY = posY;

        int startPosX = posX;
        int startPosY = posY;

        for (int i = 1; i <= steps; i++) {

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

            case SOLID -> {
                if (targetCell.posY < this.posY) {
                    velocityX = getSidewaysVelocity(targetCell, updateDirection);
                    velocityY = -1;
                } else {
                    velocityX = 0;
                }
                return false;
            }

            case LIQUID, GAS -> {
                this.trySetNeighboursMoving(chunkAccessor, targetCell.posX, targetCell.posY);

                if (this.posX != lastValidX || this.posY != lastValidY) {
                    chunkAccessor.moveToOrSwap(this, lastValidX, lastValidY);
                }

                swapWith(chunkAccessor, targetCell);

                return true;
            }
        }

        return false;
    }

    public float getSidewaysVelocity(Cell hitCell, boolean updateDirection) {
        float sprayFactor = getSprayFactor();

        if (sprayFactor == 0 || Math.abs(velocityY) < 1.5) return 0;
//####################################################################################
        float strength = (float) (Math.random() + Math.pow(hitCell.getFriction(), 8) / 2f) * Math.abs(this.velocityY) * sprayFactor;

        if (velocityX < 0 || (velocityX == 0 && updateDirection)) {
            strength *= -1;
        }

        return strength;
    }

}
