package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.cells.cell_properties.property_types.MovableSolidProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.MovingCell;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

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

        Cell cellBelow = chunkAccessor.getCell(this.getPosX(), this.getPosY() - 1);
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
        } else {
            velocityY = -1;
        }

        if (!moving) return;

        if (Math.abs(velocityX) >= 1 || Math.abs(velocityY) >= 1) {
            if (moveWithVelocity(chunkAccessor, updateDirection)) return;
        }

        moveOrSwapDownLeftRight(chunkAccessor, updateDirection);

        trySetStationary();

    }


    @Override
    public MoveAlongState moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int targetX, int targetY, int lastValidX, int lastValidY, boolean updateDirection) {
        if (targetCell == null) return MoveAlongState.STOP;

        switch (targetCell.getPhysicalState()) {

            case OTHER -> {
                this.trySetNeighboursMoving(chunkAccessor, targetX, targetY);
                return MoveAlongState.CONTINUE;
            }

            case SOLID -> {
                if (targetCell.getPosY() < this.getPosY()) {
                    velocityX = getSidewaysVelocity(targetCell, updateDirection);
                    velocityY = -1;
                } else {
                    velocityX = 0;
                }
                return MoveAlongState.STOP;
            }

            case LIQUID, GAS -> {
                this.trySetNeighboursMoving(chunkAccessor, targetCell.getPosX(), targetCell.getPosY());

                if (this.getPosX() != lastValidX || this.getPosY() != lastValidY) {
                    chunkAccessor.moveToOrSwap(this, lastValidX, lastValidY);
                }

                swapWith(chunkAccessor, targetCell);

                return MoveAlongState.CONTINUE;
            }
        }

        return MoveAlongState.STOP;
    }

    public float getSidewaysVelocity(Cell hitCell, boolean updateDirection) {
        float sprayFactor = getSprayFactor();

        if (sprayFactor == 0 || Math.abs(velocityY) < 1.5) return 0;
//####################################################################################
        float strength = (float) (world.random.nextFloat() + Math.pow(hitCell.getFriction(), 8) / 2f) * Math.abs(this.velocityY) * sprayFactor;

        if (velocityX < 0 || (velocityX == 0 && world.random.nextBoolean())) {
            strength *= -1;
        }

        return strength;
    }

}
