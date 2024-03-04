package com.basti_bob.sand_wizard.cells;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public abstract class Cell {

    public final World world;
    public Color color;
    private final CellType cellType;

    public int posX, posY;
    public int inChunkX, inChunkY;
    public Vector2 velocity = new Vector2();
    public boolean gotUpdated;

    private float friction;
    private float speedFactor;
    private float jumpFactor;


    public Cell(CellType cellType, World world, int posX, int posY) {
        this.world = world;
        this.setPosition(posX, posY);
        this.cellType = cellType;
        this.color = cellType.randomColor();

        CellProperty cellProperty = cellType.getCellProperty();

        this.friction = cellProperty.friction;
        this.speedFactor = cellProperty.speedFactor;
        this.jumpFactor = cellProperty.jumpFactor;
    }

    public Color getColor() {
        return this.color;
    }

    public float getFriction() {
        return friction;
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public float getJumpFactor() {
        return jumpFactor;
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;

        this.inChunkX = World.getInChunkPos(posX);
        this.inChunkY = World.getInChunkPos(posY);
    }

    public void swapWith(ChunkAccessor chunkAccessor, Cell target) {
        chunkAccessor.swapCells(this, target);
    }

    public void trySetNeighboursMoving(ChunkAccessor chunkAccessor, int posX, int posY) {
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(i == 0 || i == j) continue;

                Cell cell = chunkAccessor.getCell(posX + i, posY + j);

                if(cell instanceof MovableSolid) {
                    ((MovableSolid) cell).trySetMoving();
                }
            }
        }
    }
    public void clampVelocity() {
        if (velocity.x > WorldConstants.CHUNK_SIZE) velocity.x = WorldConstants.CHUNK_SIZE;
        else if (velocity.x < -WorldConstants.CHUNK_SIZE) velocity.x = -WorldConstants.CHUNK_SIZE;

        if (velocity.y > WorldConstants.CHUNK_SIZE) velocity.y = WorldConstants.CHUNK_SIZE;
        else if (velocity.y < -WorldConstants.CHUNK_SIZE) velocity.y = -WorldConstants.CHUNK_SIZE;
    }

    public CellType getCellType(){
        return this.cellType;
    }

    public Vector2 getGravity() {
        return WorldConstants.GRAVITY;
    }

    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        this.gotUpdated = true;
    }

    public boolean canMoveToOrSwap(Cell target) {
        return target instanceof Empty || canSwapWith(target);
    }

    public boolean canSwapWith(Cell target) {
        return false;
    }

    public static class CellProperty {

        protected float friction = 0.9f;
        protected float speedFactor = 1f;
        protected float jumpFactor = 1f;

        public static final CellProperty EMPTY = new CellProperty();
        public static final CellProperty STONE = new CellProperty();
        public static final CellProperty GRASS = new CellProperty();
        public static final CellProperty ICE = new CellProperty().friction(0.98f);

        private CellProperty friction(float friction) {
            this.friction = friction;
            return this;
        }

        private CellProperty speedFactor(float speedFactor) {
            this.speedFactor = speedFactor;
            return this;
        }

        private CellProperty jumpFactor(float jumpFactor){
            this.jumpFactor = jumpFactor;
            return this;
        }

    }
}
