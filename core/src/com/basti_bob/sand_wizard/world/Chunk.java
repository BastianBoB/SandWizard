package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.util.Array2D;

public class Chunk {


    private final Array2D<Cell> grid;
    private final World world;
    public final int posX, posY;
    private boolean active, activeNextFrame;
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
//        if(inChunkPosX >= 32 || inChunkPosX < 0 || inChunkPosY >= 32 || inChunkPosY < 0) System.out.println("EROOROORORORORORO");
/////*
////        int cellChunkX = World.getChunkPos(cellPosX);
////        int cellChunkY = World.getChunkPos(cellPosY);
////
////        if(cellChunkX != this.posX || cellChunkY != this.posY) {
////            System.out.println("______________CELL POS NOT MATCH CHUNK1");
////        }*/

        grid.set(inChunkPosX, inChunkPosY, cellType.createCell(world, cellPosX, cellPosY));
        this.activateChunk();
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
//        if(inChunkPosX >= 32 || inChunkPosX < 0 || inChunkPosY >= 32 || inChunkPosY < 0) System.out.println("EROOROORORORORORO");
//
//        int cellChunkX = World.getChunkPos(cellPosX);
//        int cellChunkY = World.getChunkPos(cellPosY);
//
//        if(cellChunkX != this.posX || cellChunkY != this.posY) {
//            System.out.println(cell.getCellType() + ": CELL POS NOT MATCH CHUNK2");
//        }
//
        cell.setPosition(cellPosX, cellPosY);
        grid.set(inChunkPosX, inChunkPosY, cell);
        this.activateChunk();
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

        for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
            for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {

                int xIndex = updateDirection ? inChunkX : WorldConstants.CHUNK_SIZE - inChunkX - 1;

                Cell cell = grid.get(xIndex, inChunkY);

                if (cell instanceof Empty || cell.gotUpdated) continue;

                cell.update(chunkAccessor, updateDirection);
            }
        }

    }

    public static Chunk loadOrCreate(World world, int chunkPosX, int chunkPosY) {
        return generateNew(world, chunkPosX, chunkPosY);
    }

    public static Chunk generateNew(World world, int chunkPosX, int chunkPosY) {
        Chunk chunk = new Chunk(world, chunkPosX, chunkPosY);

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunk.getCellPosX(i);
            double terrainHeight = world.openSimplexNoise.eval(cellPosX / 100f, 0, 0) * 0;

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                chunk.setCellWithChunkPos(CellType.EMPTY, i, j);

                int cellPosY = chunk.getCellPosY(j);

                CellType cellType = CellType.EMPTY;

                //if (cellPosY < terrainHeight) cellType = CellType.SAND;
                //if (cellPosY < terrainHeight - 10) cellType = CellType.DIRT;
                if (cellPosY < terrainHeight - 30) cellType = CellType.STONE;

                chunk.setCellWithChunkPos(cellType, i, j);
            }
        }

        //chunk.setCell(CellType.GRASS, 0, 0);

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

    public void activateChunk() {
        this.activeNextFrame = true;
    }

    public boolean isActive() {
        return active;
    }

    public void updateActive() {
        this.active = activeNextFrame;
        this.activeNextFrame = false;
    }
}
