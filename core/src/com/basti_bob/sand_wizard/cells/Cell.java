package com.basti_bob.sand_wizard.cells;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cell_properties.CellProperty;
import com.basti_bob.sand_wizard.cell_properties.PhysicalState;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.cells.util.ChunkBoarderState;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.lighting.Light;

public abstract class Cell {

    public World world;
    private final CellType cellType;

    public int posX, posY;
    public int inChunkX, inChunkY;

    private float colorR, colorG, colorB;
    private final float originalColorR, originalColorG, originalColorB;

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

    private boolean isLightSource;
    private Light light;


    public Cell(CellType cellType) {
        this.cellType = cellType;

        Color color = cellType.getCellColors().getColor(world, posX, posY);
        this.colorR = color.r;
        this.colorG = color.g;
        this.colorB = color.b;
        this.originalColorR = colorR;
        this.originalColorG = colorG;
        this.originalColorB = colorB;

        CellProperty cellProperty = cellType.getCellProperty();

        this.friction = cellProperty.friction;
        this.speedFactor = cellProperty.speedFactor;
        this.jumpFactor = cellProperty.jumpFactor;

        this.temperature = WorldConstants.START_TEMPERATURE;
        this.canBeHeated = cellProperty.canBeHeated;
        this.canBeCooled = cellProperty.canBeCooled;
        this.burningTemperature = cellProperty.burningTemperature;
        this.canBurn = cellProperty.canBurn;
        this.maxBurningTime = cellProperty.maxBurningTime;
        this.fireSpreadChance = cellProperty.fireSpreadChance;

        this.maxCorrosionHealth = cellProperty.maxCorrosionHealth;
        this.canCorrode = cellProperty.canCorrode;
        this.corrosionHealth = cellProperty.maxCorrosionHealth;

        this.isLightSource = cellProperty.isLightSource;

        if (isLightSource) {
            Color lightColor = cellProperty.lightColor;

            light = new Light(posX, posY, lightColor.r, lightColor.g, lightColor.b, cellProperty.lightRadius, cellProperty.lightIntensity);
        }
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public void removedFromChunk(Chunk chunk) {
        if(isLightSource) {
            light.removedFromChunk(chunk);
        }
    }

    public void addedToWorld(World world, Chunk chunk, int posX, int posY) {
        this.world = world;
        this.setPosition(posX, posY);

        if(isLightSource) {
            light.placedInChunk(chunk);
        }
    }

    public void addedToWorld(World world, int posX, int posY) {
        this.world = world;
        this.setPosition(posX, posY);
    }

    public void movedInNewChunk(Chunk previousChunk, Chunk newChunk) {
        if (isLightSource) {
            this.light.moveIntoNewChunk(previousChunk, newChunk);
        }
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;

        this.inChunkX = World.getInChunkPos(posX);
        this.inChunkY = World.getInChunkPos(posY);

        if (isLightSource) {
            light.setNewPosition(posX, posY);
        }
    }

    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        this.gotUpdated = true;

        if (isBurning()) {
            if (++timeBurning > getMaxBurningTime()) {
                finishedBurning(chunkAccessor, updateDirection);
            } else {
                updateBurning(chunkAccessor, updateDirection);
            }
        }
    }

    public boolean applyCorrosion(ChunkAccessor chunkAccessor, float amount) {
        if (!this.canCorrode()) return false;

        this.corrosionHealth -= amount;

        if (this.corrosionHealth < 0) {
            die(chunkAccessor);
        }

        return true;
    }

    public void cleanColor(ChunkAccessor chunkAccessor) {
        updateColor(chunkAccessor, originalColorR, originalColorG, originalColorB);
    }

    public void cleanColor(ChunkAccessor chunkAccessor, float factor) {
        taintWithColor(chunkAccessor, originalColorR, originalColorG, originalColorB, factor);

        float newR = MathUtil.lerp(colorR, originalColorR, factor);
        float newG = MathUtil.lerp(colorG, originalColorG, factor);
        float newB = MathUtil.lerp(colorB, originalColorB, factor);

        updateColor(chunkAccessor, newR, newG, newB);
    }

    public void taintWithColor(ChunkAccessor chunkAccessor, float r, float g, float b, float factor) {
        float newR = MathUtil.lerp(colorR, r, factor);
        float newG = MathUtil.lerp(colorG, g, factor);
        float newB = MathUtil.lerp(colorB, b, factor);

        updateColor(chunkAccessor, newR, newG, newB);
    }

    public void updateColor(ChunkAccessor chunkAccessor, float r, float g, float b) {
        this.colorR = r;
        this.colorB = b;
        this.colorG = g;
        chunkAccessor.updateMeshColor(this);
    }

    public void updateBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        if (Math.random() > this.getFireSpreadChance()) return;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                if (Math.random() > this.getFireSpreadChance()) continue;

                chunkAccessor.setCellIfEmpty(CellType.FIRE, this.getX() + i, this.getY() + j);
            }
        }
    }

    public boolean finishedBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        return die(chunkAccessor);
    }

    public void startedBurning(ChunkAccessor chunkAccessor) {
        this.burning = true;
    }

    public boolean die(ChunkAccessor chunkAccessor) {
        if(isLightSource) {
            light.removedFromChunk(chunkAccessor.getNeighbourChunk(this.getX(), this.getY()));
        }

        return replace(CellType.EMPTY, chunkAccessor);
    }

    public boolean replace(CellType cellType, ChunkAccessor chunkAccessor) {
        chunkAccessor.setCell(cellType, this.getX(), this.getY());
        return true;
    }

    public boolean shouldActiveChunk() {
        return false;
    }

    public void changeTemperature(ChunkAccessor chunkAccessor, float temperatureChange) {
        setTemperature(chunkAccessor, temperature + temperatureChange);
    }

    public void setTemperature(ChunkAccessor chunkAccessor, float temperature) {
        this.temperature = temperature;

        if (canBurn() && this.getTemperature() > getBurningTemperature()) {
            startedBurning(chunkAccessor);
        }
    }

    public void applyHeating(ChunkAccessor chunkAccessor, float heat) {
        if (!canBeHeated()) return;

        this.changeTemperature(chunkAccessor, heat);
    }

    public void applyCooling(ChunkAccessor chunkAccessor, float cooling) {
        if (!canBeCooled()) return;

        this.changeTemperature(chunkAccessor, -cooling);
    }

    public void transferTemperature(ChunkAccessor chunkAccessor, float targetTemperature, float factor) {
        if (targetTemperature > getTemperature() && !canBeHeated()) return;
        if (targetTemperature < getTemperature() && !canBeCooled()) return;

        this.setTemperature(chunkAccessor, MathUtil.lerp(this.temperature, targetTemperature, factor));
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

        int chunkOffsetX = targetChunkX - chunkAccessor.centerChunk.posX;
        int chunkOffsetY = targetChunkY - chunkAccessor.centerChunk.posY;

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


    public Cell[][] getNeighbourCells(ChunkAccessor chunkAccessor, int posX, int posY) {
        Cell[][] neighbourCells = new Cell[3][3];

        final int boarderPos = WorldConstants.CHUNK_SIZE - 1;

        int inChunkX = World.getInChunkPos(posX);
        int inChunkY = World.getInChunkPos(posY);

        ChunkBoarderState chunkBoarderState = ChunkBoarderState.getStateWithInChunkPos(inChunkX, inChunkY);

        int targetChunkX = World.getChunkPos(posX);
        int targetChunkY = World.getChunkPos(posY);

        int chunkOffsetX = targetChunkX - chunkAccessor.centerChunk.posX;
        int chunkOffsetY = targetChunkY - chunkAccessor.centerChunk.posY;

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

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.getX(), this.getY());
        for (Cell cell : directNeighbourCells) {
            trySetMoving(cell);
        }
    }

    public PhysicalState getPhysicalState() {
        return this.getCellType().getPhysicalState();
    }

    public boolean isSolid() {
        return getPhysicalState() == PhysicalState.SOLID;
    }

    public boolean isLiquid() {
        return getPhysicalState() == PhysicalState.LIQUID;
    }

    public boolean isGas() {
        return getPhysicalState() == PhysicalState.GAS;
    }

    public boolean canMoveToOrSwap(Cell target) {
        return target instanceof Empty || canSwapWith(target);
    }

    public boolean canSwapWith(Cell target) {
        return false;
    }

    public CellType getCellType() {
        return this.cellType;
    }

    public Vector2 getGravity() {
        return WorldConstants.GRAVITY;
    }

    public World getWorld() {
        return world;
    }

    public boolean canBeHeated() {
        return canBeHeated;
    }

    public boolean canBeCooled() {
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

    public float getColorR() {
        return colorR;
    }

    public float getColorG() {
        return colorG;
    }

    public float getColorB() {
        return colorB;
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

    public boolean isLightSource() {
        return isLightSource;
    }

    public Light getLight() {
        return light;
    }
}
