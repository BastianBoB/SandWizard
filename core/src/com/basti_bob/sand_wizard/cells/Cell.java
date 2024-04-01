package com.basti_bob.sand_wizard.cells;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cell_properties.CellProperty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.cells.util.ChunkBoarderState;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.Chunk;
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

    private boolean canBeHeated;
    private boolean canBeCooled;

    private float burningTemperature;
    private boolean canBurn;

    private int maxBurningTime;
    private int timeBurning;
    private float fireSpreadChance;

    private float temperature;
    private boolean burning;

    private float maxCorrosionHealth;
    private float corrosionHealth;
    private boolean canCorrode;

    public Cell(CellType cellType, World world, int posX, int posY) {
        this.world = world;
        this.setPosition(posX, posY);
        this.cellType = cellType;
        this.color = cellType.getCellColors().getColor(world, posX, posY);

        CellProperty cellProperty = cellType.getCellProperty();

        this.friction = cellProperty.friction;
        this.speedFactor = cellProperty.speedFactor;
        this.jumpFactor = cellProperty.jumpFactor;

        this.canBeHeated = cellProperty.canBeHeated;
        this.canBeCooled = cellProperty.canBeCooled;
        this.burningTemperature = cellProperty.burningTemperature;
        this.canBurn = cellProperty.canBurn;
        this.maxBurningTime = cellProperty.maxBurningTime;
        this.fireSpreadChance = cellProperty.fireSpreadChance;
        this.maxCorrosionHealth = cellProperty.maxCorrosionHealth;
        this.canCorrode = cellProperty.canCorrode;

        this.corrosionHealth = maxCorrosionHealth;
    }

    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        this.gotUpdated = true;

        updateMoving(chunkAccessor, updateDirection);

        if (isBurning()) {
            if (++timeBurning > getMaxBurningTime()) {
                if (finishedBurning(chunkAccessor, updateDirection)) return;
            } else {
                updateBurning(chunkAccessor, updateDirection);
            }
        }

        if (updateCorrosion(chunkAccessor, updateDirection)) return;
    }

    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {

    }

    public void updateBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        if (Math.random() > this.getFireSpreadChance()) return;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                if (Math.random() > this.getFireSpreadChance()) continue;

                chunkAccessor.setCellIfEmpty(CellType.FIRE, this.posX + i, this.posY + j);
            }
        }
    }

    public boolean updateCorrosion(ChunkAccessor chunkAccessor, boolean updateDirection) {
        if (this.corrosionHealth < 0) {
            return die(chunkAccessor);
        }
        return false;
    }

    public boolean finishedBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        return die(chunkAccessor);
    }

    public boolean die(ChunkAccessor chunkAccessor) {
        chunkAccessor.setCell(CellType.EMPTY, this.posX, this.posY);
        return true;
    }

    public boolean replace(CellType cellType, ChunkAccessor chunkAccessor) {
        chunkAccessor.setCell(cellType, this.posX, this.posY);
        return true;
    }

    private void changeTemperature(float temperatureChange) {
        this.temperature += temperatureChange;

        if (canBurn() && this.getTemperature() > getBurningTemperature()) {
            this.burning = true;
        }
    }

    public void applyHeating(float heat) {
        if (!canBeHeated) return;

        this.changeTemperature(heat);
    }

    public void applyCooling(float cooling) {
        if (!canBeCooled) return;

        this.changeTemperature(-cooling);
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;

        this.inChunkX = World.getInChunkPos(posX);
        this.inChunkY = World.getInChunkPos(posY);
    }

    public boolean applyCorrosion(float amount) {
        if(!this.canCorrode()) return false;

        this.corrosionHealth -= amount;
        return true;
    }

    public void swapWith(ChunkAccessor chunkAccessor, Cell target) {
        chunkAccessor.swapCells(this, target);
    }

    public Cell[] getDirectNeighbourCells(ChunkAccessor chunkAccessor, int posX, int posY) {
        Cell[] neighbourCells = new Cell[4];

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
                neighbourCells[0] = cellChunk.getCellFromInChunkPos(inChunkX, inChunkY + 1);
                neighbourCells[1] = cellChunk.getCellFromInChunkPos(inChunkX + 1, inChunkY);
                neighbourCells[2] = cellChunk.getCellFromInChunkPos(inChunkX, inChunkY - 1);
                neighbourCells[3] = cellChunk.getCellFromInChunkPos(inChunkX - 1, inChunkY);
            }
            case TOP_LEFT -> {
                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk != null) {
                    neighbourCells[0] = topChunk.getCellFromInChunkPos(0, 0);
                }

                neighbourCells[1] = cellChunk.getCellFromInChunkPos(1, boarderPos);
                neighbourCells[2] = cellChunk.getCellFromInChunkPos(0, boarderPos - 1);

                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk != null) {
                    neighbourCells[3] = leftChunk.getCellFromInChunkPos(boarderPos, boarderPos);
                }
            }
            case TOP_RIGHT -> {

                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk != null) {
                    neighbourCells[0] = topChunk.getCellFromInChunkPos(boarderPos, 0);
                }
                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk != null) {
                    neighbourCells[1] = rightChunk.getCellFromInChunkPos(0, boarderPos);
                }

                neighbourCells[2] = cellChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1);
                neighbourCells[3] = cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos);
            }

            case BOTTOM_LEFT -> {
                neighbourCells[0] = cellChunk.getCellFromInChunkPos(0, 1);
                neighbourCells[1] = cellChunk.getCellFromInChunkPos(1, 0);

                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk != null) {
                    neighbourCells[2] = (bottomChunk.getCellFromInChunkPos(0, boarderPos));
                }
                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk != null) {
                    neighbourCells[3] = (leftChunk.getCellFromInChunkPos(boarderPos, 0));
                }
            }

            case BOTTOM_RIGHT -> {
                neighbourCells[0] = (cellChunk.getCellFromInChunkPos(boarderPos, 1));

                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk != null) {
                    neighbourCells[1] = (rightChunk.getCellFromInChunkPos(0, 0));
                }

                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk != null) {
                    neighbourCells[2] = (bottomChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                }

                neighbourCells[3] = (cellChunk.getCellFromInChunkPos(boarderPos - 1, 0));
            }
            case LEFT -> {
                neighbourCells[0] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY + 1));
                neighbourCells[1] = (cellChunk.getCellFromInChunkPos(inChunkX + 1, inChunkY));
                neighbourCells[2] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY - 1));

                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk != null) {
                    neighbourCells[3] = (leftChunk.getCellFromInChunkPos(boarderPos, inChunkY));
                }
            }
            case TOP -> {

                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk != null) {
                    neighbourCells[0] = (topChunk.getCellFromInChunkPos(inChunkX, 0));
                }

                neighbourCells[1] = (cellChunk.getCellFromInChunkPos(inChunkX + 1, inChunkY));
                neighbourCells[2] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY - 1));
                neighbourCells[3] = (cellChunk.getCellFromInChunkPos(inChunkX - 1, inChunkY));

            }
            case BOTTOM -> {
                neighbourCells[0] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY + 1));
                neighbourCells[1] = (cellChunk.getCellFromInChunkPos(inChunkX + 1, inChunkY));

                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk != null) {
                    neighbourCells[2] = (bottomChunk.getCellFromInChunkPos(inChunkX, boarderPos));
                }

                neighbourCells[3] = (cellChunk.getCellFromInChunkPos(inChunkX - 1, inChunkY));
            }
            case RIGHT -> {
                neighbourCells[0] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY + 1));

                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk != null) {
                    neighbourCells[1] = (rightChunk.getCellFromInChunkPos(0, inChunkY));
                }

                neighbourCells[2] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY - 1));
                neighbourCells[3] = (cellChunk.getCellFromInChunkPos(inChunkX - 1, inChunkY));
            }
        }

        return neighbourCells;
    }

    //Very fucking large but hopefully more efficient than the other approach (here for each chunkboardering state there have to be retrieved less chunks and not repeatedly)
    public Cell[][] getNeighbourCells(ChunkAccessor chunkAccessor, int posX, int posY) {
        Cell[][] neighbourCells = new Cell[3][3];

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
                        neighbourCells[i + 1][j + 1] = cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j);
                    }
                }
            }
            case TOP_LEFT -> {
                neighbourCells[1][0] = cellChunk.getCellFromInChunkPos(0, boarderPos - 1);
                neighbourCells[2][0] = cellChunk.getCellFromInChunkPos(1, boarderPos - 1);
                neighbourCells[2][1] = cellChunk.getCellFromInChunkPos(1, boarderPos);

                Chunk topLeftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY + 1);
                if (topLeftChunk != null) {
                    neighbourCells[0][2] = topLeftChunk.getCellFromInChunkPos(boarderPos, 0);
                }
                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk != null) {
                    neighbourCells[1][2] = topChunk.getCellFromInChunkPos(0, 0);
                    neighbourCells[2][2] = topChunk.getCellFromInChunkPos(1, 0);
                }
                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk != null) {
                    neighbourCells[0][1] = leftChunk.getCellFromInChunkPos(boarderPos, boarderPos);
                    neighbourCells[0][0] = leftChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1);
                }
            }
            case TOP_RIGHT -> {
                neighbourCells[1][0] = cellChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1);
                neighbourCells[0][0] = cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos - 1);
                neighbourCells[0][1] = cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos);

                Chunk topRightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY + 1);
                if (topRightChunk != null) {
                    neighbourCells[2][2] = topRightChunk.getCellFromInChunkPos(0, 0);
                }
                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk != null) {
                    neighbourCells[1][2] = topChunk.getCellFromInChunkPos(boarderPos, 0);
                    neighbourCells[0][2] = topChunk.getCellFromInChunkPos(boarderPos - 1, 0);
                }
                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk != null) {
                    neighbourCells[2][1] = rightChunk.getCellFromInChunkPos(0, boarderPos);
                    neighbourCells[2][0] = rightChunk.getCellFromInChunkPos(0, boarderPos - 1);
                }
            }

            case BOTTOM_LEFT -> {
                neighbourCells[1][2] = (cellChunk.getCellFromInChunkPos(0, 1));
                neighbourCells[2][2] = (cellChunk.getCellFromInChunkPos(1, 1));
                neighbourCells[2][1] = (cellChunk.getCellFromInChunkPos(1, 0));

                Chunk bottomLeftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY - 1);
                if (bottomLeftChunk != null) {
                    neighbourCells[0][0] = (bottomLeftChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                }
                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk != null) {
                    neighbourCells[1][0] = (bottomChunk.getCellFromInChunkPos(0, boarderPos));
                    neighbourCells[2][0] = (bottomChunk.getCellFromInChunkPos(1, boarderPos));
                }
                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk != null) {
                    neighbourCells[0][1] = (leftChunk.getCellFromInChunkPos(boarderPos, 0));
                    neighbourCells[0][2] = (leftChunk.getCellFromInChunkPos(boarderPos, 1));
                }
            }

            case BOTTOM_RIGHT -> {
                neighbourCells[1][2] = (cellChunk.getCellFromInChunkPos(boarderPos, 1));
                neighbourCells[0][2] = (cellChunk.getCellFromInChunkPos(boarderPos - 1, 1));
                neighbourCells[0][1] = (cellChunk.getCellFromInChunkPos(boarderPos - 1, 0));

                Chunk bottomRightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY - 1);
                if (bottomRightChunk != null) {
                    neighbourCells[2][0] = (bottomRightChunk.getCellFromInChunkPos(0, boarderPos));
                }
                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk != null) {
                    neighbourCells[1][0] = (bottomChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                    neighbourCells[0][0] = (bottomChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos));
                }
                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk != null) {
                    neighbourCells[2][1] = (rightChunk.getCellFromInChunkPos(0, 0));
                    neighbourCells[2][2] = (rightChunk.getCellFromInChunkPos(0, 1));
                }
            }
            case LEFT -> {
                for (int i = 0; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        neighbourCells[i + 1][j + 1] = (cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }
                Chunk leftChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX - 1, chunkOffsetY);
                if (leftChunk == null) return neighbourCells;

                for (int j = -1; j <= 1; j++)
                    neighbourCells[0][j + 1] = (leftChunk.getCellFromInChunkPos(boarderPos, inChunkY + j));
            }
            case TOP -> {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 0; j++) {
                        if (i == 0 && j == 0) continue;
                        neighbourCells[i + 1][j + 1] = (cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }
                Chunk topChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY + 1);
                if (topChunk == null) return neighbourCells;

                for (int i = -1; i <= 1; i++)
                    neighbourCells[i + 1][2] = (topChunk.getCellFromInChunkPos(inChunkX + i, 0));

            }
            case BOTTOM -> {
                for (int i = -1; i <= 1; i++) {
                    for (int j = 0; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        neighbourCells[i + 1][j + 1] = (cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }

                Chunk bottomChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX, chunkOffsetY - 1);
                if (bottomChunk == null) return neighbourCells;

                for (int i = -1; i <= 1; i++)
                    neighbourCells[i + 1][0] = (bottomChunk.getCellFromInChunkPos(inChunkX + i, boarderPos));
            }
            case RIGHT -> {
                for (int i = -1; i <= 0; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        neighbourCells[i + 1][j + 1] = (cellChunk.getCellFromInChunkPos(inChunkX + i, inChunkY + j));
                    }
                }
                Chunk rightChunk = chunkAccessor.getNeighbourChunkWithOffset(chunkOffsetX + 1, chunkOffsetY);
                if (rightChunk == null) return neighbourCells;

                for (int j = -1; j <= 1; j++)
                    neighbourCells[2][j + 1] = (rightChunk.getCellFromInChunkPos(0, inChunkY + j));

            }
        }

        return neighbourCells;
    }

    private void trySetMoving(Cell cell) {
        if (cell instanceof MovableSolid movableSolidCell) {
            movableSolidCell.trySetMoving();
        }
    }

    public void trySetNeighboursMoving(ChunkAccessor chunkAccessor, int posX, int posY) {

        Cell[][] neighbourCells = getNeighbourCells(chunkAccessor, posX, posY);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                trySetMoving(neighbourCells[i + 1][j + 1]);
            }
        }
    }

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

    public boolean canMoveToOrSwap(Cell target) {
        return target instanceof Empty || canSwapWith(target);
    }

    public boolean canSwapWith(Cell target) {
        return false;
    }

    public World getWorld() {
        return world;
    }

    public boolean isCanBeHeated() {
        return canBeHeated;
    }

    public boolean isCanBeCooled() {
        return canBeCooled;
    }

    public float getBurningTemperature() {
        return burningTemperature;
    }

    public boolean canBurn() {
        return canBurn;
    }

    public float getTemperature() {
        return temperature;
    }

    public boolean isBurning() {
        return burning;
    }

    public int getMaxBurningTime() {
        return maxBurningTime;
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

    public float getFireSpreadChance() {
        return fireSpreadChance;
    }

    public float getMaxCorrosionHealth() {
        return maxCorrosionHealth;
    }

    public float getCorrosionHealth() {
        return corrosionHealth;
    }

    public boolean canCorrode() {
        return canCorrode;
    }
}
