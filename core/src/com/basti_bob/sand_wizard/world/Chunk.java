package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.util.Array2D;

public class Chunk {


    private final Array2D<Cell> grid;
    private final World world;
    public final int posX, posY;
    private boolean active = true;
    public final ChunkAccessor chunkAccessor;

    public Chunk(World world, int posX, int posY) {
        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.grid = new Array2D<>(Cell.class, WorldConstants.CHUNK_SIZE, WorldConstants.CHUNK_SIZE);
        this.chunkAccessor = new ChunkAccessor(this);
    }

    public Array2D<Cell> getGrid() {
        return grid;
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        grid.set(inChunkPosX, inChunkPosY, cellType.createCell(world, cellPosX, cellPosY));
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        cell.posX = cellPosX;
        cell.posY = cellPosY;

        grid.set(inChunkPosX, inChunkPosY, cell);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY) {
        setCell(cell, cellPosX, cellPosY, World.getInChunkPos(cellPosX), World.getInChunkPos(cellPosY));
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY) {
        setCell(cellType, cellPosX, cellPosY, World.getInChunkPos(cellPosX), World.getInChunkPos(cellPosY));
    }

    private void setCellWithChunkPos(CellType cellType, int inChunkPosX, int inChunkPosY) {
        setCell(cellType, getCellPosX(inChunkPosX), getCellPosY(inChunkPosY), inChunkPosX, inChunkPosY);
    }

    public int getCellPosX(int inChunkPosX) {
        return inChunkPosX + WorldConstants.CHUNK_SIZE * this.posX;
    }

    public int getCellPosY(int inChunkPosY) {
        return inChunkPosY + WorldConstants.CHUNK_SIZE * this.posY;
    }

    public Cell getCellFromInChunkPos(int inChunkPosX, int inChunkPosY) {
        return grid.get(inChunkPosX, inChunkPosY);
    }

    public void update(boolean updateDirection) {
//        Chunk[][] neighborChunks = new Chunk[3][3];
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (i == 1 && j == 1) {
//                    neighborChunks[i][j] = this;
//                } else {
//                    neighborChunks[i][j] = world.getChunkFromChunkPos(this.posX + i - 1, this.posY + j - 1);
//                }
//            }
//        }
//
//        ChunkAccessor chunkAccessor = new ChunkAccessor(neighborChunks);

        for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
            for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {


                int xIndex = updateDirection ? inChunkX : WorldConstants.CHUNK_SIZE - inChunkX - 1;

                Cell cell = grid.get(xIndex, inChunkY);

                if (cell.hasMoved || cell instanceof Empty) continue;

                cell.update(chunkAccessor, xIndex, inChunkY, updateDirection);
            }
        }
    }

    public static Chunk loadOrCreate(World world, int chunkPosX, int chunkPosY) {
        return generateNew(world, chunkPosX, chunkPosY);
    }

    public static Chunk generateNew(World world, int chunkPosX, int chunkPosY) {
        Chunk chunk = new Chunk(world, chunkPosX, chunkPosY);

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                chunk.setCellWithChunkPos(CellType.SAND, i, j);
            }
        }

        for (int i = 4; i < WorldConstants.CHUNK_SIZE - 4; i++) {
            chunk.setCellWithChunkPos(CellType.SAND, i, 16);
        }

       //chunk.setCellWithChunkPos(CellType.STONE, 0, 0);

        return chunk;
    }

//    public Chunk load(int chunkX, int chunkY) {
//
//    }
//
//    public void unload() {
//
//    }
//
//    public void save() {
//
//    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
