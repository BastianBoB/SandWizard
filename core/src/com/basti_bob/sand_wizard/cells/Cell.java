package com.basti_bob.sand_wizard.cells;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.cell_properties.CellProperty;
import com.basti_bob.sand_wizard.cells.cell_properties.PhysicalState;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.cells.util.ChunkBoarderState;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.ChunkLight;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.Light;

public abstract class Cell {

    public World world;
    protected final CellType cellType;

    private int posX;
    private int posY;
    private int inChunkX;
    private int inChunkY;

    private float colorR, colorG, colorB;
    private final float originalColorR, originalColorG, originalColorB;

    public boolean gotUpdated;

    private float friction;
    private float speedFactor;
    private float jumpFactor;

    private boolean canBeHeated;
    private boolean canBeCooled;

    private final float burningTemperature;
    private final int maxBurningTime;
    private final float fireSpreadChance;
    private final boolean canBurn;

    private int timeBurning;
    private float temperature;
    private boolean isBurning;

    private final boolean canCorrode;
    private final float corrosionResistance;
    private float corrosionHealth;

    private final boolean canExplode;
    private final float explosionResistance;
    private float explosionHealth;

    public boolean glowsWithCellColor;
    public boolean isLightSource;

    public Light light;

    public Cell(CellType cellType) {
        this.cellType = cellType;

        Color color = cellType.getCellColors().getColor(world);
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

        this.canCorrode = cellProperty.canCorrode;
        this.corrosionHealth = cellProperty.corrosionHealth;
        this.corrosionResistance = cellProperty.corrosionResistance;

        this.isLightSource = cellProperty.isLightSource;
        this.glowsWithCellColor = cellProperty.glowsWithCellColor;

        this.explosionResistance = cellProperty.explosionResistance;
        this.canExplode = cellProperty.canExplode;
        this.explosionHealth = cellProperty.explosionHealth;

        if (isLightSource) {
            Color lightColor = glowsWithCellColor ? color : cellProperty.lightColor;
            light = new ChunkLight(getPosX(), getPosY(), lightColor.r, lightColor.g, lightColor.b, cellProperty.lightRadius, cellProperty.lightIntensity);
        }

        cellProperty.createdCell(this);
    }

    public void removedFromChunk(Chunk chunk) {
        if (isLightSource) {
            light.removedFromChunk(chunk);
        }
    }

    public void addedToWorld(World world, Chunk chunk, int posX, int posY) {
        this.world = world;

        if (!(this instanceof Empty))
            this.setPosition(posX, posY);

        if (isLightSource) {
            light.placedInChunk(chunk);
        }
    }

    public void addedToWorld(World world, int posX, int posY) {
        this.world = world;

        if (!(this instanceof Empty))
            this.setPosition(posX, posY);
    }

    public void movedInNewChunk(Chunk previousChunk, Chunk newChunk) {
        if (isLightSource) {
            this.light.moveIntoNewChunk(previousChunk, newChunk);
        }
    }

    public void setPosition(int posX, int posY) {
        this.setPosX(posX);
        this.setPosY(posY);

        this.setInChunkX(World.getInChunkPos(posX));
        this.setInChunkY(World.getInChunkPos(posY));

        if (isLightSource) {
            light.setNewPosition(posX, posY);
        }
    }

    public void setColor(float r, float g, float b) {
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
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

    public boolean explode(ChunkAccessor chunkAccessor, int originX, int originY, float strength) {

        if (!canExplode()) return false;

        this.explosionHealth -= strength * (1f - explosionResistance);

        if (this.explosionHealth > 0) return false;


        float r = world.random.nextFloat();
        if (r < 0.3) {
            replace(CellType.GAS.EXPLOSION_SPARK, chunkAccessor);
        } else {
            die(chunkAccessor);
        }

        return true;
    }

    public boolean wouldExplode(ChunkAccessor chunkAccessor, int originX, int originY, float strength) {

        if (!canExplode()) return false;

        return this.explosionHealth - strength * (1f - explosionResistance) < 0;
    }

    public boolean applyCorrosion(ChunkAccessor chunkAccessor, float amount) {
        if (!this.canCorrode()) return false;

        this.corrosionHealth -= amount * (1f - corrosionResistance);

        if (this.corrosionHealth < 0) {
            die(chunkAccessor);
        }

        return true;
    }

    public void cleanColor(ChunkAccessor chunkAccessor) {
        cleanColor(chunkAccessor, 1f);
    }

    public void cleanColor(ChunkAccessor chunkAccessor, float factor) {
        stainWithColor(chunkAccessor, originalColorR, originalColorG, originalColorB, factor);
    }

    public void stainWithColor(ChunkAccessor chunkAccessor, float r, float g, float b, float factor) {
        float newR = MathUtil.lerp(colorR, r, factor);
        float newG = MathUtil.lerp(colorG, g, factor);
        float newB = MathUtil.lerp(colorB, b, factor);

        updateColor(chunkAccessor, newR, newG, newB);
    }

    public void updateColor(ChunkAccessor chunkAccessor, float r, float g, float b) {
        if(this.colorR == r && this.colorG == g && this.colorB == b) return;

        this.colorR = r;
        this.colorB = b;
        this.colorG = g;
        chunkAccessor.updateMeshColor(this);
    }

    public void updateBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        if (world.random.nextFloat() > this.getFireSpreadChance()) return;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                if (world.random.nextFloat() > this.getFireSpreadChance()) continue;

                chunkAccessor.setCellIfEmpty(CellType.GAS.FIRE, this.getPosX() + i, this.getPosY() + j);
            }
        }
    }

    public boolean finishedBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        return die(chunkAccessor);
    }

    public void startedBurning(ChunkAccessor chunkAccessor) {
        this.isBurning = true;
    }

    public boolean die(ChunkAccessor chunkAccessor) {
        return replace(CellType.EMPTY, chunkAccessor);
    }

    public boolean replace(CellType cellType, ChunkAccessor chunkAccessor) {
        if (isLightSource) {
            light.removedFromChunk(chunkAccessor.getChunkFromCellPos(getPosX(), getPosY()));
        }
        chunkAccessor.setCell(cellType, getPosX(), getPosY());

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

        int cellChunkX = World.getChunkPos(posX);
        int cellChunkY = World.getChunkPos(posY);

        Chunk cellChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY);

        if(cellChunk == null) return neighbourCells;

        switch (chunkBoarderState) {

            case CENTER -> {
                neighbourCells[0] = cellChunk.getCellFromInChunkPos(inChunkX, inChunkY + 1);
                neighbourCells[1] = cellChunk.getCellFromInChunkPos(inChunkX + 1, inChunkY);
                neighbourCells[2] = cellChunk.getCellFromInChunkPos(inChunkX, inChunkY - 1);
                neighbourCells[3] = cellChunk.getCellFromInChunkPos(inChunkX - 1, inChunkY);
            }
            case TOP_LEFT -> {
                Chunk topChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY + 1);
                if (topChunk != null) {
                    neighbourCells[0] = topChunk.getCellFromInChunkPos(0, 0);
                }

                neighbourCells[1] = cellChunk.getCellFromInChunkPos(1, boarderPos);
                neighbourCells[2] = cellChunk.getCellFromInChunkPos(0, boarderPos - 1);

                Chunk leftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY);
                if (leftChunk != null) {
                    neighbourCells[3] = leftChunk.getCellFromInChunkPos(boarderPos, boarderPos);
                }
            }
            case TOP_RIGHT -> {

                Chunk topChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY + 1);
                if (topChunk != null) {
                    neighbourCells[0] = topChunk.getCellFromInChunkPos(boarderPos, 0);
                }
                Chunk rightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY);
                if (rightChunk != null) {
                    neighbourCells[1] = rightChunk.getCellFromInChunkPos(0, boarderPos);
                }

                neighbourCells[2] = cellChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1);
                neighbourCells[3] = cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos);
            }

            case BOTTOM_LEFT -> {
                neighbourCells[0] = cellChunk.getCellFromInChunkPos(0, 1);
                neighbourCells[1] = cellChunk.getCellFromInChunkPos(1, 0);

                Chunk bottomChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY - 1);
                if (bottomChunk != null) {
                    neighbourCells[2] = (bottomChunk.getCellFromInChunkPos(0, boarderPos));
                }
                Chunk leftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY);
                if (leftChunk != null) {
                    neighbourCells[3] = (leftChunk.getCellFromInChunkPos(boarderPos, 0));
                }
            }

            case BOTTOM_RIGHT -> {
                neighbourCells[0] = (cellChunk.getCellFromInChunkPos(boarderPos, 1));

                Chunk rightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY);
                if (rightChunk != null) {
                    neighbourCells[1] = (rightChunk.getCellFromInChunkPos(0, 0));
                }

                Chunk bottomChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY - 1);
                if (bottomChunk != null) {
                    neighbourCells[2] = (bottomChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                }

                neighbourCells[3] = (cellChunk.getCellFromInChunkPos(boarderPos - 1, 0));
            }
            case LEFT -> {
                neighbourCells[0] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY + 1));
                neighbourCells[1] = (cellChunk.getCellFromInChunkPos(inChunkX + 1, inChunkY));
                neighbourCells[2] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY - 1));

                Chunk leftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY);
                if (leftChunk != null) {
                    neighbourCells[3] = (leftChunk.getCellFromInChunkPos(boarderPos, inChunkY));
                }
            }
            case TOP -> {

                Chunk topChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY + 1);
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

                Chunk bottomChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY - 1);
                if (bottomChunk != null) {
                    neighbourCells[2] = (bottomChunk.getCellFromInChunkPos(inChunkX, boarderPos));
                }

                neighbourCells[3] = (cellChunk.getCellFromInChunkPos(inChunkX - 1, inChunkY));
            }
            case RIGHT -> {
                neighbourCells[0] = (cellChunk.getCellFromInChunkPos(inChunkX, inChunkY + 1));

                Chunk rightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY);
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

        int cellChunkX = World.getChunkPos(posX);
        int cellChunkY = World.getChunkPos(posY);

        Chunk cellChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY);

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

                Chunk topLeftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY + 1);
                if (topLeftChunk != null) {
                    neighbourCells[0][2] = topLeftChunk.getCellFromInChunkPos(boarderPos, 0);
                }
                Chunk topChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY + 1);
                if (topChunk != null) {
                    neighbourCells[1][2] = topChunk.getCellFromInChunkPos(0, 0);
                    neighbourCells[2][2] = topChunk.getCellFromInChunkPos(1, 0);
                }
                Chunk leftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY);
                if (leftChunk != null) {
                    neighbourCells[0][1] = leftChunk.getCellFromInChunkPos(boarderPos, boarderPos);
                    neighbourCells[0][0] = leftChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1);
                }
            }
            case TOP_RIGHT -> {
                neighbourCells[1][0] = cellChunk.getCellFromInChunkPos(boarderPos, boarderPos - 1);
                neighbourCells[0][0] = cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos - 1);
                neighbourCells[0][1] = cellChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos);

                Chunk topRightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY + 1);
                if (topRightChunk != null) {
                    neighbourCells[2][2] = topRightChunk.getCellFromInChunkPos(0, 0);
                }
                Chunk topChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY + 1);
                if (topChunk != null) {
                    neighbourCells[1][2] = topChunk.getCellFromInChunkPos(boarderPos, 0);
                    neighbourCells[0][2] = topChunk.getCellFromInChunkPos(boarderPos - 1, 0);
                }
                Chunk rightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY);
                if (rightChunk != null) {
                    neighbourCells[2][1] = rightChunk.getCellFromInChunkPos(0, boarderPos);
                    neighbourCells[2][0] = rightChunk.getCellFromInChunkPos(0, boarderPos - 1);
                }
            }

            case BOTTOM_LEFT -> {
                neighbourCells[1][2] = (cellChunk.getCellFromInChunkPos(0, 1));
                neighbourCells[2][2] = (cellChunk.getCellFromInChunkPos(1, 1));
                neighbourCells[2][1] = (cellChunk.getCellFromInChunkPos(1, 0));

                Chunk bottomLeftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY - 1);
                if (bottomLeftChunk != null) {
                    neighbourCells[0][0] = (bottomLeftChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                }
                Chunk bottomChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY - 1);
                if (bottomChunk != null) {
                    neighbourCells[1][0] = (bottomChunk.getCellFromInChunkPos(0, boarderPos));
                    neighbourCells[2][0] = (bottomChunk.getCellFromInChunkPos(1, boarderPos));
                }
                Chunk leftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY);
                if (leftChunk != null) {
                    neighbourCells[0][1] = (leftChunk.getCellFromInChunkPos(boarderPos, 0));
                    neighbourCells[0][2] = (leftChunk.getCellFromInChunkPos(boarderPos, 1));
                }
            }

            case BOTTOM_RIGHT -> {
                neighbourCells[1][2] = (cellChunk.getCellFromInChunkPos(boarderPos, 1));
                neighbourCells[0][2] = (cellChunk.getCellFromInChunkPos(boarderPos - 1, 1));
                neighbourCells[0][1] = (cellChunk.getCellFromInChunkPos(boarderPos - 1, 0));

                Chunk bottomRightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY - 1);
                if (bottomRightChunk != null) {
                    neighbourCells[2][0] = (bottomRightChunk.getCellFromInChunkPos(0, boarderPos));
                }
                Chunk bottomChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY - 1);
                if (bottomChunk != null) {
                    neighbourCells[1][0] = (bottomChunk.getCellFromInChunkPos(boarderPos, boarderPos));
                    neighbourCells[0][0] = (bottomChunk.getCellFromInChunkPos(boarderPos - 1, boarderPos));
                }
                Chunk rightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY);
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
                Chunk leftChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX - 1, cellChunkY);
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
                Chunk topChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY + 1);
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

                Chunk bottomChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX, cellChunkY - 1);
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
                Chunk rightChunk = chunkAccessor.getChunkFromChunkPos(cellChunkX + 1, cellChunkY);
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

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, posX, posY);
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
        return isBurning;
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

    public float getCorrosionHealth() {
        return corrosionHealth;
    }

    public boolean canCorrode() {
        return canCorrode;
    }

    public float getExplosionHealth() {
        return explosionHealth;
    }

    public float getExplosionResistance() {
        return explosionResistance;
    }

    public boolean canExplode() {
        return canExplode;
    }

    public boolean isLightSource() {
        return isLightSource;
    }

    public Light getLight() {
        return light;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getInChunkX() {
        return inChunkX;
    }

    public void setInChunkX(int inChunkX) {
        this.inChunkX = inChunkX;
    }

    public int getInChunkY() {
        return inChunkY;
    }

    public void setInChunkY(int inChunkY) {
        this.inChunkY = inChunkY;
    }


}
