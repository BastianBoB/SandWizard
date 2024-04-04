package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

public class ChunkAccessor {

    private final Chunk[][] surroundingChunks;
    public final int centerChunkX, centerChunkY;

    public ChunkAccessor(Chunk centerChunk) {
        this.surroundingChunks = new Chunk[3][3];
        this.surroundingChunks[1][1] = centerChunk;

        this.centerChunkX = centerChunk.posX;
        this.centerChunkY = centerChunk.posY;
    }

    public void setSurroundingChunk(Chunk chunk) {
        int gridX = chunk.posX - centerChunkX + 1;
        int gridY = chunk.posY - centerChunkY + 1;

        surroundingChunks[gridX][gridY] = chunk;
    }

    public void removeSurroundingChunk(Chunk chunk) {
        int gridX = chunk.posX - centerChunkX + 1;
        int gridY = chunk.posY - centerChunkY + 1;

        surroundingChunks[gridX][gridY] = null;
    }

    public Cell getCell(int targetX, int targetY) {
        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
        if (targetChunk == null) return null;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        return targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
    }

    public boolean moveToOrSwap(Cell cell, int targetX, int targetY) {
        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
        if (targetChunk == null) return false;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        Cell targetCell = targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);

        if (targetCell instanceof Empty) {

            cellChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
            targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
            return true;

        } else if (cell.canSwapWith(targetCell)) {

            cellChunk.setCell(targetCell, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
            targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
            return true;
        }
        return false;
    }

    public Chunk getNeighbourChunk(int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = targetChunkY - centerChunkY + 1;

        return surroundingChunks[gridX][gridY];
    }

    public Chunk getNeighbourChunkWithOffset(int offsetX, int offsetY) {
        return surroundingChunks[offsetX + 1][offsetY + 1];
    }

    public void cellActivatesChunk(int cellX, int cellY) {
        int inChunkX = World.getInChunkPos(cellX);
        int inChunkY = World.getInChunkPos(cellY);
        Chunk cellChunk = getNeighbourChunk(cellX, cellY);

        cellChunk.cellActivatesChunk(inChunkX, inChunkY);
    }

//    public Chunk getNeighbourChunk(ChunkBoarderState chunkBoarderState) {
//
//        return switch (chunkBoarderState) {
//            case CENTER -> surroundingChunks[1][1];
//
//            case TOP_LEFT -> surroundingChunks[0][0];
//            case TOP -> surroundingChunks[1][0];
//            case TOP_RIGHT -> surroundingChunks[2][0];
//
//            case BOTTOM_LEFT -> surroundingChunks[0][2];
//            case BOTTOM -> surroundingChunks[1][2];
//            case BOTTOM_RIGHT -> surroundingChunks[2][2];
//
//            case LEFT -> surroundingChunks[0][1];
//            case RIGHT -> surroundingChunks[2][1];
//        };
//    }

    public boolean moveToIfEmpty(Cell cell, int targetX, int targetY) {

        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
        if (targetChunk == null) return false;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        if (!(targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY) instanceof Empty)) return false;

        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);

        cellChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);

        return true;
    }

    public void swapCells(Cell cell, Cell other) {
        int targetX = other.posX;
        int targetY = other.posY;

        int targetInChunkX = other.inChunkX;
        int targetInChunkY = other.inChunkY;

        Chunk targetChunk = getNeighbourChunk(targetX, targetY);

        if (targetChunk == null) return;

        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);

        cellChunk.setCell(other, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
    }

    public void moveTo(Cell cell, int targetX, int targetY) {

        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);

        cellChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
    }

    public void setCellIfEmpty(CellType cellType, int posX, int posY) {
        Chunk targetChunk = getNeighbourChunk(posX, posY);

        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(posX);
        int targetInChunkY = World.getInChunkPos(posY);

        if (!(targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY) instanceof Empty)) return;

        targetChunk.setCell(cellType, posX, posY, targetInChunkX, targetInChunkY);
    }

    public void setCell(CellType cellType, int posX, int posY) {
        Chunk targetChunk = getNeighbourChunk(posX, posY);

        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(posX);
        int targetInChunkY = World.getInChunkPos(posY);

        targetChunk.setCell(cellType, posX, posY, targetInChunkX, targetInChunkY);
    }

    public void updateMeshColor(Cell cell) {
        Chunk targetChunk = getNeighbourChunk(cell.posX, cell.posY);

        if (targetChunk == null) return;

        targetChunk.updateMeshColor(cell);
    }


    public boolean isEmpty(int targetX, int targetY) {
        return getCell(targetX, targetY) instanceof Empty;
    }
}

