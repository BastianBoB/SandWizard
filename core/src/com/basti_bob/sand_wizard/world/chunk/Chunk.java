package com.basti_bob.sand_wizard.world.chunk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.util.ChunkBoarderState;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class Chunk {

    private final Array2D<Cell> grid;
    public final World world;
    public final int posX, posY;
    private boolean active, activeNextFrame;
    public final ChunkAccessor chunkAccessor;
    public Mesh mesh;

    public boolean hasBeenModified = false;

    public Chunk(World world, int posX, int posY, Array2D<Cell> grid, Mesh mesh){
        int cs = WorldConstants.CHUNK_SIZE;

        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.grid = grid;
        this.chunkAccessor = new ChunkAccessor(this);
        this.active = true;
        this.activeNextFrame = true;
        this.mesh = mesh;
    }

//    public Chunk(World world, int posX, int posY) {
//        this(world, posX, posY, new Array2D<>(Cell.class, WorldConstants.CHUNK_SIZE, WorldConstants.CHUNK_SIZE));
//    }

    public void initializeMesh() {

    }

    public Array2D<Cell> getGrid() {
        return grid;
    }

    public void update(boolean updateDirection) {

        for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
            for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {

                int xIndex = updateDirection ? inChunkX : WorldConstants.CHUNK_SIZE - inChunkX - 1;

                Cell cell = grid.get(xIndex, inChunkY);

                if (cell == null || cell.gotUpdated) continue;

                cell.update(chunkAccessor, updateDirection);

                if (cell.shouldActiveChunk()) chunkAccessor.cellActivatesChunk(cell.posX, cell.posY);
            }
        }
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        Cell cell = cellType.createCell(world, cellPosX, cellPosY);

        setCellAndUpdate(cell, inChunkPosX, inChunkPosY);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        cell.setPosition(cellPosX, cellPosY);

        setCellAndUpdate(cell, inChunkPosX, inChunkPosY);
    }

    private void setCellAndUpdate(Cell cell, int inChunkPosX, int inChunkPosY) {
        grid.set(inChunkPosX, inChunkPosY, cell);
        this.cellActivatesChunk(inChunkPosX, inChunkPosY);

        updateMeshColor(inChunkPosX, inChunkPosY, cell.getColor());
        this.hasBeenModified = true;
    }


    public void updateMeshColor(int inChunkPosX, int inChunkPosY, Color color) {
        int index = (inChunkPosY * WorldConstants.CHUNK_SIZE + inChunkPosX) * 5;

        float[] vertices = new float[]{color.r, color.g, color.b};
        mesh.updateVertices(index + 2, vertices);
    }

    public void updateMeshColor(Cell cell) {
        updateMeshColor(cell.inChunkX, cell.inChunkY, cell.getColor());
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
}
