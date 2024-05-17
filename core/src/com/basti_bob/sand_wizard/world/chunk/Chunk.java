package com.basti_bob.sand_wizard.world.chunk;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.util.ChunkBoarderState;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.ChunkLight;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Chunk implements Supplier<Chunk> {

    public World world;
    public Array2D<Cell> grid;
    public NeighbourChunkAccessor chunkAccessor;
    public Mesh mesh;
    public int posX, posY;
    private int numActiveFrames;
    public boolean hasBeenModified;

    private boolean loaded;

    public final List<ChunkLight> affectedLights = new ArrayList<>();
    public final List<ChunkLight> lightsInChunk = new ArrayList<>();

    public Chunk() {
        mesh = new Mesh(true, WorldConstants.NUM_MESH_VERTICES, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 3, "a_vertexColor"),
                new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_empty"));

        this.chunkAccessor = new NeighbourChunkAccessor(this);
        this.grid = new Array2D<>(Cell.class, WorldConstants.CHUNK_SIZE, WorldConstants.CHUNK_SIZE);
    }

    public void gotAddedToWorld() {
        this.hasBeenModified = false;
        this.numActiveFrames = 5;
    }

    public void gotRemovedFromWorld() {
        affectedLights.clear();
        lightsInChunk.clear();

        for (Cell cell : grid.getArray()) {
            cell.removedFromChunk(this);
        }
    }

    public void update(boolean updateDirection) {

        //setCell(updateDirection ? CellType.FLOWER_PETAL.BLUE_GLOW : CellType.FLOWER_PETAL.RED_GLOW, getCellPosX(16), getCellPosY(16), 16, 16);

        for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
            for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {

                int xIndex = updateDirection ? inChunkX : WorldConstants.CHUNK_SIZE - inChunkX - 1;

                Cell cell = grid.get(xIndex, inChunkY);

                if (cell instanceof Empty || cell.gotUpdated) continue;

                cell.update(chunkAccessor, updateDirection);

                if (cell.shouldActiveChunk()) chunkAccessor.cellActivatesChunk(cell.getPosX(), cell.getPosY());
            }
        }
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        Cell cell = cellType.createCell();

        setCellAndUpdate(cell, cellPosX, cellPosY, inChunkPosX, inChunkPosY, CellPlaceFlag.NEW);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY, CellPlaceFlag flag) {
        if (!(cell instanceof Empty) && flag != CellPlaceFlag.NEW)
            cell.setPosition(cellPosX, cellPosY);

        setCellAndUpdate(cell, cellPosX, cellPosY, inChunkPosX, inChunkPosY, flag);

    }

    private void setCellAndUpdate(Cell cell, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY, CellPlaceFlag flag) {
        if (flag == CellPlaceFlag.NEW) {
            Cell oldCell = grid.get(inChunkPosX, inChunkPosY);
            oldCell.removedFromChunk(this);

            cell.addedToWorld(world, this, cellPosX, cellPosY);
        }

        grid.set(inChunkPosX, inChunkPosY, cell);

        this.cellActivatesChunk(inChunkPosX, inChunkPosY);

        updateMeshData(cell, inChunkPosX, inChunkPosY);

        this.hasBeenModified = true;
    }

    public void updateMeshColor(Cell cell) {
        updateMeshColor(cell.getInChunkX(), cell.getInChunkY(), cell.getColorR(), cell.getColorG(), cell.getColorB());
    }

    public void updateMeshColor(int inChunkPosX, int inChunkPosY, float r, float g, float b) {
        int index = (inChunkPosY * WorldConstants.CHUNK_SIZE + inChunkPosX) * WorldConstants.NUM_MESH_VERTEX_VALUES;

        mesh.updateVertices(index + 2, new float[]{r, g, b});
    }

    public void updateMeshData(Cell cell, int inChunkX, int inChunkY) {
        updateMeshData(inChunkX, inChunkY, cell.getColorR(), cell.getColorG(), cell.getColorB(), cell instanceof Empty);
    }

    public void updateMeshData(int inChunkPosX, int inChunkPosY, float r, float g, float b, boolean isEmpty) {
        int index = (inChunkPosY * WorldConstants.CHUNK_SIZE + inChunkPosX) * WorldConstants.NUM_MESH_VERTEX_VALUES;

        mesh.updateVertices(index + 2, new float[]{r, g, b, isEmpty ? 1f : 0f});
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

    public void setCellWithInChunkPos(Cell cell, int inChunkPosX, int inChunkPosY, CellPlaceFlag flag) {
        setCell(cell, getCellPosX(inChunkPosX), getCellPosY(inChunkPosY), inChunkPosX, inChunkPosY, flag);
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
        this.numActiveFrames = 3;
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
        return numActiveFrames > 0;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;

        if(loaded) {
            this.activateChunk();
        }
    }

    public void updateActive() {
        numActiveFrames--;
    }


    public Array2D<Cell> getGrid() {
        return grid;
    }

    @Override
    public Chunk get() {
        return this;
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
