package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.Empty;

public class ChunkAccessor {

    private final Chunk[][] surroundingChunks;
    private final Chunk centerChunk;
    private final int centerChunkX, centerChunkY;

    public ChunkAccessor(Chunk centerChunk) {
        this.surroundingChunks = new Chunk[3][3];
        this.surroundingChunks[1][1] = centerChunk;
        this.centerChunk = centerChunk;

        this.centerChunkX = centerChunk.posX;
        this.centerChunkY = centerChunk.posY;
    }

    public void setSurroundingChunk(Chunk chunk) {
        int gridX = chunk.posX - centerChunkX + 1;
        int gridY = centerChunkY - chunk.posY + 1;

        surroundingChunks[gridX][gridY] = chunk;
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
        int gridY = centerChunkY - targetChunkY + 1;

        return surroundingChunks[gridX][gridY];
    }

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

    public boolean isEmpty(int targetX, int targetY) {
        return getCell(targetX, targetY) instanceof Empty;
    }
}

