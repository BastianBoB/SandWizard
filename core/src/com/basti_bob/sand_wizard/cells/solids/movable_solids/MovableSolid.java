package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.cells.solids.Solid;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class MovableSolid extends Solid implements MovingCell {

    public static final MovableSolidProperty SAND = new MovableSolidProperty().movingResistance(0.1f).sprayFactor(0.6f);
    public static final MovableSolidProperty DIRT = new MovableSolidProperty().movingResistance(0.3f).sprayFactor(0.3f);
    public static final MovableSolidProperty COAL = new MovableSolidProperty().movingResistance(0.8f).sprayFactor(0.2f);

    private boolean moving;
    private float movingResistance;
    private float sprayFactor;

    public MovableSolid(CellType cellType, World world, int x, int y) {
        super(cellType, world, x, y);
        this.velocity.y = -1;

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
        return target instanceof Liquid;
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        clampVelocity();

        Cell cellBelow = chunkAccessor.getCell(this.posX, this.posY - 1);

        velocity.x *= cellBelow.getFriction();

        boolean spaceBelow = canMoveToOrSwap(cellBelow);

        if (spaceBelow) {
            this.moving = true;
            this.velocity.add(this.getGravity());
        }

        if (!moving) return;

        if (Math.abs(velocity.x) > 1 || Math.abs(velocity.y) > 1) {
            if (moveWithVelocity(chunkAccessor, updateDirection)) return;
        }

        moveOrSwapDownLeftRight(chunkAccessor, updateDirection);

        trySetStationary();
    }

    @Override
    public boolean moveWithVelocity(ChunkAccessor chunkAccessor, boolean updateDirection) {
        float xDistance = Math.abs(velocity.x);
        float yDistance = Math.abs(velocity.y);

        boolean positiveX = velocity.x > 0;
        boolean positiveY = velocity.y > 0;

        float xSlope = Math.abs(velocity.y / velocity.x);
        float ySlope = Math.abs(velocity.x / velocity.y);

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

        if (targetCell instanceof Empty) {
            this.trySetNeighboursMoving(chunkAccessor, targetCell.posX, targetCell.posY);
            return true;
        }

        if (targetCell instanceof Solid) {
            if (targetCell.posY < this.posY) {
                velocity.x = getSidewaysVelocity(targetCell, updateDirection);
                velocity.y = -1;
            } else {
                velocity.x = 0;
            }
            return false;
        }

        if(targetCell instanceof Liquid) {
            this.trySetNeighboursMoving(chunkAccessor, targetCell.posX, targetCell.posY);

            if(this.posX != lastValidX || this.posY != lastValidY) {
                chunkAccessor.moveToIfEmpty(this, lastValidX, lastValidY);
            }

            swapWith(chunkAccessor, targetCell);

            return true;
        }

        return false;
    }

    public float getSidewaysVelocity(Cell hitCell, boolean updateDirection) {
        float sprayFactor = getSprayFactor();

        if (sprayFactor == 0 || Math.abs(velocity.y) < 1.5) return 0;
//####################################################################################
        float strength = (float) (Math.random() + Math.pow(hitCell.getFriction(), 8) / 2f) * Math.abs(this.velocity.y) * sprayFactor;

        if (velocity.x < 0 || (velocity.x == 0 && updateDirection)) {
            strength *= -1;
        }

        return strength;
    }

    public static class MovableSolidProperty extends CellProperty {

        protected float movingResistance = 0f;
        protected float sprayFactor = 0f;

        public MovableSolidProperty movingResistance(float movingResistance) {
            this.movingResistance = movingResistance;
            return this;
        }

        public MovableSolidProperty sprayFactor(float sprayFactor) {
            this.sprayFactor = sprayFactor;
            return this;
        }
    }

}
