package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

public class ChunkAccessor {

    private final Array2D<Chunk> surroundingChunks;
    public final Chunk centerChunk;

    public ChunkAccessor(Chunk centerChunk) {
        this.surroundingChunks = new Array2D<>(Chunk.class, 3, 3);
        this.surroundingChunks.set(1, 1, centerChunk);
        this.centerChunk = centerChunk;
    }

//    public void resetSurroundingChunks() {
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (i == 1 && j == 1) continue;
//
//                surroundingChunks.set(i, j, null);
//            }
//        }
//    }

    public Chunk[] getSurroundingChunks() {
        return surroundingChunks.getArray();
    }

    public Chunk getNeighbourChunkWithOffset(int offsetX, int offsetY) {
        return surroundingChunks.get(offsetX + 1, offsetY + 1);
    }

    public void setSurroundingChunk(Chunk chunk) {
        int gridX = chunk.posX - centerChunk.posX + 1;
        int gridY = chunk.posY - centerChunk.posY + 1;

        surroundingChunks.set(gridX, gridY, chunk);
    }

    public void removeSurroundingChunk(Chunk chunk) {
        int gridX = chunk.posX - centerChunk.posX + 1;
        int gridY = chunk.posY - centerChunk.posY + 1;

        surroundingChunks.set(gridX, gridY, null);
    }

    public Chunk getNeighbourChunk(int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        int gridX = targetChunkX - centerChunk.posX + 1;
        int gridY = targetChunkY - centerChunk.posY + 1;

        return surroundingChunks.get(gridX, gridY);
    }

    public void cellActivatesChunk(int cellX, int cellY) {
        int inChunkX = World.getInChunkPos(cellX);
        int inChunkY = World.getInChunkPos(cellY);
        Chunk cellChunk = getNeighbourChunk(cellX, cellY);

        cellChunk.cellActivatesChunk(inChunkX, inChunkY);
    }

    public Cell getCell(int targetX, int targetY) {
        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
        if (targetChunk == null) return null;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        return targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
    }

    private void finalMovedCell(Chunk previousChunk, Chunk targetChunk, Cell cell, int posX, int posY, int inChunkX, int inChunkY) {
        targetChunk.setCell(cell, posX, posY, inChunkX, inChunkY, CellPlaceFlag.MOVED);

        if (previousChunk != targetChunk)
            cell.movedInNewChunk(previousChunk, targetChunk);
    }

    public boolean moveToOrSwap(Cell cell, int targetX, int targetY) {
        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
        if (targetChunk == null) return false;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        Cell targetCell = targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);

        if (cell.canMoveToOrSwap(targetCell)) {
            finalMovedCell(targetChunk, cellChunk, targetCell, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
            finalMovedCell(cellChunk, targetChunk, cell, targetX, targetY, targetInChunkX, targetInChunkY);
            return true;
        }
        return false;
    }

    public void swapCells(Cell cell, Cell other) {
        int targetX = other.posX;
        int targetY = other.posY;

        int targetInChunkX = other.inChunkX;
        int targetInChunkY = other.inChunkY;

        Chunk targetChunk = getNeighbourChunk(targetX, targetY);

        if (targetChunk == null) return;

        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);

        finalMovedCell(targetChunk, cellChunk, other, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        finalMovedCell(cellChunk, targetChunk, cell, targetX, targetY, targetInChunkX, targetInChunkY);
    }

    public void moveTo(Cell cell, int targetX, int targetY) {

        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);

        cellChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
        finalMovedCell(cellChunk, targetChunk, cell, targetX, targetY, targetInChunkX, targetInChunkY);
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

//    public boolean moveToIfEmpty(Cell cell, int targetX, int targetY) {
//
//        Chunk targetChunk = getNeighbourChunk(targetX, targetY);
//        if (targetChunk == null) return false;
//
//        int targetInChunkX = World.getInChunkPos(targetX);
//        int targetInChunkY = World.getInChunkPos(targetY);
//
//        if (!(targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY) instanceof Empty)) return false;
//
//        Chunk cellChunk = getNeighbourChunk(cell.posX, cell.posY);
//
//        cellChunk.setCell(CellType.EMPTY, cell.posX, cell.posY, cell.inChunkX, cell.inChunkY);
//        targetChunk.setCell(cell, targetX, targetY, targetInChunkX, targetInChunkY);
//
//        return true;
//    }
}

