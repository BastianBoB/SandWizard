package com.basti_bob.sand_wizard.entities;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.util.Direction;
import com.basti_bob.sand_wizard.world.World;

import java.util.function.Predicate;

public class EntityHitBox {

    private final float width, height;

    public EntityHitBox(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean collidesWithCell(World world, float entityX, float entityY, Direction direction, Predicate<Cell> cellPredicate) {

        int centerOffsetX = (int) (direction.getXOff() * (direction.isHorizontal() ? Math.ceil(width/2f) : 0));
        int centerOffsetY = (int) (direction.getYOff() * (direction.isVertical() ? Math.ceil(height/2f) : 0));

        int iterationRange = (int) (direction.isHorizontal() ? Math.ceil(height/2f) : Math.ceil(width/2f));

        for (int i = -iterationRange; i <= iterationRange; i++) {
            int xOff = direction.getXOff() * i;
            int yOff = direction.getYOff() * i;

            Cell cell = world.getCell((int) (entityX + centerOffsetX + xOff), (int) (entityY + centerOffsetY + yOff));

            if (cell == null) continue;

            if (cellPredicate.test(cell)) return true;
        }

        return false;

    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
