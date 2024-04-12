package com.basti_bob.sand_wizard.world.chunk;

import com.badlogic.gdx.graphics.Mesh;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.util.ChunkBoarderState;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.lighting.Light;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    public final World world;
    private final Array2D<Cell> grid;
    public final ChunkAccessor chunkAccessor;
    public final Mesh mesh;
    public final int posX, posY;
    private boolean active, activeNextFrame;
    public boolean hasBeenModified;

    public final List<Light> affectedLights = new ArrayList<>();

    public Chunk(World world, int posX, int posY, Array2D<Cell> grid, Mesh mesh) {
        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.grid = grid;
        this.chunkAccessor = new ChunkAccessor(this);
        this.mesh = mesh;

        this.hasBeenModified = false;
        this.active = true;
        this.activeNextFrame = true;
    }

    public static float compressedVertexData(float r, float g, float b) {
        return Float.intBitsToFloat(((int) (r * 127) | ((int) (g * 127) << 8) | (int) (b * 127) << 16));
    }

    public void gotRemoved() {
        mesh.dispose();
        affectedLights.clear();

        for (Cell cell : grid.getArray()) {
            cell.removedFromChunk(this);
        }
    }

    public void update(boolean updateDirection) {

        for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
            for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {

                int xIndex = updateDirection ? inChunkX : WorldConstants.CHUNK_SIZE - inChunkX - 1;

                Cell cell = grid.get(xIndex, inChunkY);

                if (cell instanceof Empty || cell.gotUpdated) continue;

                cell.update(chunkAccessor, updateDirection);

                if (cell.shouldActiveChunk()) chunkAccessor.cellActivatesChunk(cell.posX, cell.posY);
            }
        }
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        Cell cell = cellType.createCell(world, cellPosX, cellPosY);

        setCellAndUpdate(cell, inChunkPosX, inChunkPosY, CellPlaceFlag.NEW);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY, CellPlaceFlag flag) {
        cell.setPosition(cellPosX, cellPosY);

        setCellAndUpdate(cell, inChunkPosX, inChunkPosY, flag);
    }

    private void setCellAndUpdate(Cell cell, int inChunkPosX, int inChunkPosY, CellPlaceFlag flag) {
        if (flag == CellPlaceFlag.NEW) {
            Cell oldCell = grid.get(inChunkPosX, inChunkPosY);
            oldCell.removedFromChunk(this);

            cell.addedToWorld(world, this, getCellPosX(inChunkPosX), getCellPosY(inChunkPosY));
        }

        grid.set(inChunkPosX, inChunkPosY, cell);

        this.cellActivatesChunk(inChunkPosX, inChunkPosY);
        updateMeshColor(inChunkPosX, inChunkPosY, cell.getColorR(), cell.getColorG(), cell.getColorB());

        this.hasBeenModified = true;
    }

    public void updateMeshColor(int inChunkPosX, int inChunkPosY, float r, float g, float b) {
        int index = (inChunkPosY * WorldConstants.CHUNK_SIZE + inChunkPosX) * WorldConstants.NUM_MESH_VERTEX_VALUES;

        mesh.updateVertices(index + 2, new float[]{r, g, b});
    }

    public void updateMeshColor(Cell cell) {
        updateMeshColor(cell.inChunkX, cell.inChunkY, cell.getColorR(), cell.getColorG(), cell.getColorB());
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY, CellPlaceFlag flag) {
        setCell(cell, cellPosX, cellPosY, World.getInChunkPos(cellPosX), World.getInChunkPos(cellPosY), flag);
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

    public void activateChunk() {
        this.activeNextFrame = true;
    }

    public void activateNeighbourChunk(int offsetX, int offsetY) {
        Chunk neighbourChunk = chunkAccessor.getNeighbourChunkWithOffset(offsetX, offsetY);

        if (neighbourChunk == null) return;

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


    public Array2D<Cell> getGrid() {
        return grid;
    }


//    public void updateLighting() {
//        lightValues.clear();
//
//        int combineSize = WorldConstants.LIGHT_COMBINING_SIZE;
//
//        for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY += combineSize) {
//            for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX += combineSize) {
//
//                float totalFactor = 0;
//                float totalR = 0, totalG = 0, totalB = 0;
//                int lights = 0;
//                for (int i = 0; i < combineSize; i++) {
//                    for (int j = 0; j < combineSize; j++) {
//
//                        Cell cell = grid.get(inChunkX + i, inChunkY + j);
//
//                        if (cell.isLightSource()) {
//                            totalFactor += cell.getLightEmittingValue();
//                            totalR += cell.getLightColorR();
//                            totalG += cell.getLightColorG();
//                            totalB += cell.getLightColorB();
//
//                            lights++;
//                        }
//                    }
//                }
//
//                float posX = (float) getCellPosX(inChunkX + combineSize / 2);
//                float posY = (float) getCellPosY(inChunkY + combineSize / 2);
//
//                if (lights > 0) {
//                    lightValues.addAll(Arrays.asList(posX, posY, totalR / lights, totalG / lights, totalB / lights, totalFactor / lights));
//                }
//
//            }
//
//   }
}
