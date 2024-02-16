package com.basti_bob.sand_wizard.cells;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public abstract class Cell {

    public final World world;
    public int posX, posY;
    public Vector2 velocity = new Vector2();
    public Color color;

    public boolean hasMoved;

    public Cell(World world, int posX, int posY) {
        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.color = this.getCellType().randomColor();
        this.velocity.set(this.getGravity());
    }

    public abstract CellType getCellType();

    public Vector2 getGravity() {
        return World.GRAVITY;
    }

    public void update(ChunkAccessor chunkAccessor, int inChunkX, int inChunkY, boolean updateDirection){

    }

    protected boolean canSwapWith(Cell target) {
        return false;
    }

}
