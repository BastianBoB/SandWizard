package com.basti_bob.sand_wizard.cells;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public abstract class Cell {


    public static final CellProperty EMPTY = new CellProperty();
    public static final CellProperty STONE = new CellProperty();
    public static final CellProperty GRASS = new CellProperty();
    public static final CellProperty ICE = new CellProperty().friction(0.98f);
    public static final CellProperty WOOD = new CellProperty();
    public static final CellProperty LEAF = new CellProperty();


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

    //Very fucking large but hopefully more efficient than the other approach (here for each chunkboardering state there have to be retrieved less chunks and not repeatedly)
    public void trySetNeighboursMoving(ChunkAccessor chunkAccessor, int posX, int posY) {
        final int boarderPos = WorldConstants.CHUNK_SIZE - 1;

        int inChunkX = World.getInChunkPos(posX);
        int inChunkY = World.getInChunkPos(posY);

        ChunkBoarderState chunkBoarderState = ChunkBoarderState.getStateWithInChunkPos(inChunkX, inChunkY);

        int targetChunkX = World.getChunkPos(posX);
        int targetChunkY = World.getChunkPos(posY);

        int chunkOffsetX = targetChunkX - chunkAccessor.centerChunkX;
        int chunkOffsetY = targetChunkY - chunkAccessor.centerChunkY;

        Chunk cellChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY);

        switch (chunkBoarderState) {

            case CENTER -> {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        trySetMoving(cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }
            }
            case TOP_LEFT -> {
                trySetMoving(cellChunk.getCellFromInChunkPos(0, boarderPos - 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(1, boarderPos - 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(1, boarderPos));

                Chunk topLeftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY + 1);
                if (topLeftChunk != null) {
                    trySetMoving(topLeftChunk.getCellFromInChunkPos(boarderPos, 0));
                }
                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk != null) {
                    trySetMoving(topChunk.getCellFromInChunkPos(0, 0));
                    trySetMoving(topChunk.getCellFromInChunkPos(1, 0));
                }
                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk != null) {
                    trySetMoving(leftChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                    trySetMoving(leftChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1));
                }
            }
            case TOP_RIGHT -> {
                trySetMoving(cellChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos - 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos));

                Chunk topRightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY + 1);
                if (topRightChunk != null) {
                    trySetMoving(topRightChunk.getCellFromInChunkPos(0, 0));
                }
                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk != null) {
                    trySetMoving(topChunk.getCellFromInChunkPos(boarderPos, 0));
                    trySetMoving(topChunk.getCellFromInChunkPos(boarderPos - 1, 0));
                }
                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk != null) {
                    trySetMoving(rightChunk.getCellFromInChunkPos(0, boarderPos));
                    trySetMoving(rightChunk.getCellFromInChunkPos(0, boarderPos - 1));
                }
            }

            case BOTTOM_LEFT -> {
                trySetMoving(cellChunk.getCellFromInChunkPos(0, 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(1, 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(1, 0));

                Chunk bottomLeftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY - 1);
                if (bottomLeftChunk != null) {
                    trySetMoving(bottomLeftChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                }
                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk != null) {
                    trySetMoving(bottomChunk.getCellFromInChunkPos(0, boarderPos));
                    trySetMoving(bottomChunk.getCellFromInChunkPos(1, boarderPos));
                }
                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk != null) {
                    trySetMoving(leftChunk.getCellFromInChunkPos(boarderPos, 0));
                    trySetMoving(leftChunk.getCellFromInChunkPos(boarderPos, 1));
                }
            }

            case BOTTOM_RIGHT -> {
                trySetMoving(cellChunk.getCellFromInChunkPos(boarderPos, 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(boarderPos - 1, 1));
                trySetMoving(cellChunk.getCellFromInChunkPos(boarderPos - 1, 0));

                Chunk bottomRightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY - 1);
                if (bottomRightChunk != null) {
                    trySetMoving(bottomRightChunk.getCellFromInChunkPos(0, boarderPos));
                }
                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk != null) {
                    trySetMoving(bottomChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                    trySetMoving(bottomChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos));
                }
                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk != null) {
                    trySetMoving(rightChunk.getCellFromInChunkPos(0, 0));
                    trySetMoving(rightChunk.getCellFromInChunkPos(0, 1));
                }
            }
            case LEFT -> {
                for (int i = 0; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        trySetMoving(cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }
                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk == null) return;

                for (int j = -1; j <= 1; j++)
                    trySetMoving(leftChunk.getCellFromInChunkPos(boarderPos, inChunkY + j));
            }
            case TOP -> {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 0; j++) {
                        if (i == 0 && j == 0) continue;
                        trySetMoving(cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }
                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk == null) return;

                for (int i = -1; i <= 1; i++)
                    trySetMoving(topChunk.getCellFromInChunkPos(inChunkX + i, 0));

            }
            case BOTTOM -> {
                for (int i = -1; i <= 1; i++) {
                    for (int j = 0; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        trySetMoving(cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }

                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk == null) return;

                for (int i = -1; i <= 1; i++)
                    trySetMoving(bottomChunk.getCellFromInChunkPos(inChunkX + i, boarderPos));
            }
            case RIGHT -> {
                for (int i = -1; i <= 0; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        trySetMoving(cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }
                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk == null) return;

                for (int j = -1; j <= 1; j++)
                    trySetMoving(rightChunk.getCellFromInChunkPos(0, inChunkY + j));

            }
        }
    }

    private void trySetMoving(Cell cell) {
        if (cell instanceof MovableSolid movableSolidCell) {
            movableSolidCell.trySetMoving();
        }
    }

//    public void trySetNeighboursMoving2(ChunkAccessor chunkAccessor, int posX, int posY) {
//
//        for (int i = -1; i <= 1; i++) {
//            for (int j = -1; j <= 1; j++) {
//                if (i == 0 && j == 0) continue;
//
//                trySetMoving(chunkAccessor.getCell(posX + i, posY + j));
//            }
//        }
//    }

    public void clampVelocity() {
        if (velocity.x > WorldConstants.CHUNK_SIZE) velocity.x = WorldConstants.CHUNK_SIZE;
        else if (velocity.x < -WorldConstants.CHUNK_SIZE) velocity.x = -WorldConstants.CHUNK_SIZE;

        if (velocity.y > WorldConstants.CHUNK_SIZE) velocity.y = WorldConstants.CHUNK_SIZE;
        else if (velocity.y < -WorldConstants.CHUNK_SIZE) velocity.y = -WorldConstants.CHUNK_SIZE;
    }

    public boolean moveOrSwapDownLeftRight(ChunkAccessor chunkAccessor, boolean updateDirection) {
        if (updateDirection) {
            if (chunkAccessor.moveToOrSwap(this, posX + 1, posY - 1)) return true;
            return chunkAccessor.moveToOrSwap(this, posX - 1, posY - 1);
        } else {
            if (chunkAccessor.moveToOrSwap(this, posX - 1, posY - 1)) return true;
            return chunkAccessor.moveToOrSwap(this, posX + 1, posY - 1);
        }
    }


    public CellType getCellType() {
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

        private CellProperty friction(float friction) {
            this.friction = friction;
            return this;
        }

        private CellProperty speedFactor(float speedFactor) {
            this.speedFactor = speedFactor;
            return this;
        }

        private CellProperty jumpFactor(float jumpFactor) {
            this.jumpFactor = jumpFactor;
            return this;
        }

    }
}
