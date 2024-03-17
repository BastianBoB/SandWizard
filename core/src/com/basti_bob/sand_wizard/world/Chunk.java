package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.ChunkBoarderState;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;

public class Chunk {


    private final Array2D<Cell> grid;
    public final World world;
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
        this.cellActivatesChunk(inChunkPosX, inChunkPosY);
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
        this.cellActivatesChunk(inChunkPosX, inChunkPosY);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY) {
        setCell(cell, cellPosX, cellPosY, World.getInChunkPos(cellPosX), World.getInChunkPos(cellPosY));
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY) {
        setCell(cellType, cellPosX, cellPosY, World.getInChunkPos(cellPosX), World.getInChunkPos(cellPosY));
    }

    public void setCellWithInChunkPos(CellType cellType, int inChunkPosX, int inChunkPosY) {
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

    public void activateNeighbourChunk(int offsetX, int offsetY) {
        Chunk neighbourChunk = chunkAccessor.getNeighbourChunkWithOffset(offsetX, offsetY);

        if(neighbourChunk == null) return;

        neighbourChunk.activateChunk();
    }

    public void cellActivatesChunk(int inChunkPosX, int inChunkPosY) {
        this.activateChunk();

        ChunkBoarderState chunkBoarderState = ChunkBoarderState.getStateWithInChunkPos(inChunkPosX, inChunkPosY);

        switch (chunkBoarderState) {
            case TOP_LEFT -> {
                activateNeighbourChunk(0, 1);
                activateNeighbourChunk(-1, 0);
                activateNeighbourChunk(-1, 1);
            }
            case TOP_RIGHT -> {
                activateNeighbourChunk(0, 1);
                activateNeighbourChunk(1, 0);
                activateNeighbourChunk(1, 1);
            }
            case BOTTOM_LEFT -> {
                activateNeighbourChunk(0, -1);
                activateNeighbourChunk(-1, 0);
                activateNeighbourChunk(-1, -1);
            }
            case BOTTOM_RIGHT -> {
                activateNeighbourChunk(0, -1);
                activateNeighbourChunk(1, 0);
                activateNeighbourChunk(1, -1);
            }
            case TOP -> activateNeighbourChunk(0, 1);
            case BOTTOM -> activateNeighbourChunk(0, -1);
            case LEFT -> activateNeighbourChunk(-1, 0);
            case RIGHT -> activateNeighbourChunk(1, 0);
        }

    }

    public boolean isActive() {
        return active;
    }

    public void updateActive() {
        this.active = activeNextFrame;
        this.activeNextFrame = false;
    }
}
