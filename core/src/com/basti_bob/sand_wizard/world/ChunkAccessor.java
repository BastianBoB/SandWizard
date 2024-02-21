package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.Empty;

public class ChunkAccessor {

    private final Chunk[][] surroundingChunks;
    private final int centerChunkX, centerChunkY;

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

    private Cell getCell(int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = targetChunkY - centerChunkY + 1;

        Chunk targetChunk = surroundingChunks[gridX][gridY];

        if (targetChunk == null) return null;

        return targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
    }

    public boolean moveToIfEmpty(Cell cell, int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        int gridX = targetChunkX - centerChunkX + 1;
        int gridY = targetChunkY - centerChunkY + 1;

        Chunk targetChunk = surroundingChunks[gridX][gridY];
        if (targetChunk == null) return false;

        if (!(targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY) instanceof Empty)) return false;

        surroundingChunks[1][1].setCell(CellType.EMPTY, cell.posX, cell.posY);
        surroundingChunks[gridX][gridY].setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
        cell.hasMoved = true;

        return true;
    }

//    public boolean isEmpty(int targetX, int targetY) {
//        return getCell(targetX, targetY) instanceof Empty;
//    }
}

