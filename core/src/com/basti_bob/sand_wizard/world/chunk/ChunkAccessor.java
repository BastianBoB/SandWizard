package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;

public interface ChunkAccessor {

    Chunk getChunkFromCellPos(int targetX, int targetY);

    Chunk getChunkFromChunkPos(int targetChunkX, int targetChunkY);

    default Cell getCell(int targetX, int targetY) {
        Chunk targetChunk = getChunkFromCellPos(targetX, targetY);
        if (targetChunk == null) return null;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        return targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
    }

    default void finalMovedCell(Chunk previousChunk, Chunk targetChunk, Cell cell, int posX, int posY, int inChunkX, int inChunkY) {
        targetChunk.setCell(cell, posX, posY, inChunkX, inChunkY, CellPlaceFlag.MOVED);

        if (previousChunk != targetChunk)
            cell.movedInNewChunk(previousChunk, targetChunk);
    }

    default boolean moveToOrSwap(Cell cell, int targetX, int targetY) {
        Chunk targetChunk = getChunkFromCellPos(targetX, targetY);
        if (targetChunk == null) return false;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        Cell targetCell = targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY);
        Chunk cellChunk = getChunkFromCellPos(cell.getPosX(), cell.getPosY());

        if (cell.canMoveToOrSwap(targetCell)) {
            finalMovedCell(targetChunk, cellChunk, targetCell, cell.getPosX(), cell.getPosY(), cell.getInChunkX(), cell.getInChunkY());
            finalMovedCell(cellChunk, targetChunk, cell, targetX, targetY, targetInChunkX, targetInChunkY);
            return true;
        }
        return false;
    }

    default void swapCells(Cell cell, Cell other) {
        int targetX = other.getPosX();
        int targetY = other.getPosY();

        int targetInChunkX = other.getInChunkX();
        int targetInChunkY = other.getInChunkY();

        Chunk targetChunk = getChunkFromCellPos(targetX, targetY);

        if (targetChunk == null) return;

        Chunk cellChunk = getChunkFromCellPos(cell.getPosX(), cell.getPosY());

        finalMovedCell(targetChunk, cellChunk, other, cell.getPosX(), cell.getPosY(), cell.getInChunkX(), cell.getInChunkY());
        finalMovedCell(cellChunk, targetChunk, cell, targetX, targetY, targetInChunkX, targetInChunkY);
    }

    default void moveTo(Cell cell, int targetX, int targetY) {

        Chunk targetChunk = getChunkFromCellPos(targetX, targetY);
        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(targetX);
        int targetInChunkY = World.getInChunkPos(targetY);

        Chunk cellChunk = getChunkFromCellPos(cell.getPosX(), cell.getPosY());

        cellChunk.setCell(Empty.getInstance(), cell.getPosX(), cell.getPosY(), cell.getInChunkX(), cell.getInChunkY(), CellPlaceFlag.MOVED);
        finalMovedCell(cellChunk, targetChunk, cell, targetX, targetY, targetInChunkX, targetInChunkY);
    }

    default void setCellIfEmpty(CellType cellType, int posX, int posY) {
        Chunk targetChunk = getChunkFromCellPos(posX, posY);

        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(posX);
        int targetInChunkY = World.getInChunkPos(posY);

        if (!(targetChunk.getCellFromInChunkPos(targetInChunkX, targetInChunkY) instanceof Empty)) return;

        targetChunk.setCell(cellType, posX, posY, targetInChunkX, targetInChunkY);
    }

     default void cellActivatesChunk(int cellX, int cellY) {
        int inChunkX = World.getInChunkPos(cellX);
        int inChunkY = World.getInChunkPos(cellY);
        Chunk cellChunk = getChunkFromCellPos(cellX, cellY);

        cellChunk.cellActivatesChunk(inChunkX, inChunkY);
    }

    default void setCell(CellType cellType, int posX, int posY) {
        Chunk targetChunk = getChunkFromCellPos(posX, posY);

        if (targetChunk == null) return;

        int targetInChunkX = World.getInChunkPos(posX);
        int targetInChunkY = World.getInChunkPos(posY);

        targetChunk.setCell(cellType, posX, posY, targetInChunkX, targetInChunkY);
    }

    default void setCell(Cell cell, int posX, int posY, int inChunkX, int inChunkY) {
        Chunk targetChunk = getChunkFromCellPos(posX, posY);

        if (targetChunk == null) return;

        targetChunk.setCell(cell, posX, posY, inChunkX, inChunkY, CellPlaceFlag.NEW);
    }

    default void updateMeshColor(Cell cell) {
        Chunk targetChunk = getChunkFromCellPos(cell.getPosX(), cell.getPosY());

        if (targetChunk == null) return;

        targetChunk.updateMeshColor(cell);
    }

}

