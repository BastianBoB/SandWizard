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
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = centerChunkY - targetChunkY + 1;

        Chunk targetChunk = surroundingChunks[gridX][gridY];

        if (targetChunk == null) return null;

        return targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
    }

    public boolean moveToOrSwap(Cell cell, int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = centerChunkY - targetChunkY + 1;

        Chunk targetChunk = surroundingChunks[gridX][gridY];

        if (targetChunk == null) return false;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        Cell targetCell = targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);

        if(targetCell instanceof Empty) {

            centerChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
            targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
            return true;

        } else if(cell.canSwapWith(targetCell)) {

            centerChunk.setCell(targetCell, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
            targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
            return true;
        }
        return false;
    }

    public boolean moveToIfEmpty(Cell cell, int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = centerChunkY - targetChunkY + 1;

        Chunk targetChunk = surroundingChunks[gridX][gridY];
        if (targetChunk == null) return false;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        if (!(targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY) instanceof Empty)) return false;


        PROBLEM IST HIER!
            CELL Muss nicht mehr im center chunk sein also auch für die cell den chunk neu berechnen
                vllt auch in der set position den chunk berechnen

        centerChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);

        return true;
    }

    public void swapCells(Cell cell, Cell other) {
        int targetX = other.posX;
        int targetY = other.posY;

        int targetChunkX = World.getChunkPos(other.posX);
        int targetChunkY = World.getChunkPos(other.posY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = centerChunkY - targetChunkY + 1;

        Chunk targetChunk = surroundingChunks[gridX][gridY];

        if (targetChunk == null) return;

        centerChunk.setCell(other, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        targetChunk.setCell(cell, targetX, targetY, other.inChunkX, other.inChunkY);
    }

    public void moveTo(Cell cell, int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = centerChunkY - targetChunkY + 1;

        Chunk targetChunk = surroundingChunks[gridX][gridY];
        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        centerChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
    }

    public boolean isEmpty(int targetX, int targetY) {
        return getCell(targetX, targetY) instanceof Empty;
    }
}

