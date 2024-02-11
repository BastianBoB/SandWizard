package com.basti_bob.sand_wizard.cells;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.coordinateSystems.CellPos;
import com.basti_bob.sand_wizard.world.World;

public abstract class Cell {

    public final World world;
    public int x, y;
    public Vector2 velocity = new Vector2();
    public Color color;

    public boolean hasMoved;

    public Cell(World world, int x, int y) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.color = this.getCellType().randomColor();
        this.velocity.set(this.getGravity());
    }

    public abstract CellType getCellType();

    public Vector2 getGravity() {
        return World.GRAVITY;
    }

    public void update(){
    }

    protected boolean canSwapWith(Cell target) {
        return false;
    }

    public boolean canMoveToOrSwap(CellPos targetPos) {
        //if (!matrix.inBounds(targetX, targetY)) return false;

        if (world.isEmpty(targetPos)) return true;

        return canSwapWith(world.getCell(targetPos));
    }

    public CellPos getPos() {
        return new CellPos(this.x, this.y);
    }

    public void moveTo(CellPos newPos) {
        if(!world.hasChunk(newPos)) return;

        world.setCell(CellType.EMPTY, this.getPos());
        world.setCell(this, newPos);
    }
}
