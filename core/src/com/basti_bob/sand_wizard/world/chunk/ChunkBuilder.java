package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class ChunkBuilder {

    private final Chunk chunk;
    private final float[] vertices;

    public ChunkBuilder(World world, Chunk oldChunk, int posX, int posY) {
        this.chunk = oldChunk;

        int cs = WorldConstants.CHUNK_SIZE;

        this.chunk.world = world;
        this.chunk.posX = posX;
        this.chunk.posY = posY;
        this.vertices = new float[WorldConstants.NUM_MESH_VERTICES];

        for (int j = 0; j < cs; j++) {
            for (int i = 0; i < cs; i++) {

                int vertexI = (j * cs + i) * WorldConstants.NUM_MESH_VERTEX_VALUES;

                vertices[vertexI] = posX * cs + i;
                vertices[vertexI + 1] = posY * cs + j;
            }
        }
    }

//    public void setCell(CellType cellType, int inChunkPosX, int inChunkPosY) {
//        setCell(cellType, getCellPosX(inChunkPosX), getCellPosY(inChunkPosY), inChunkPosX, inChunkPosY);
//    }

    public int getCellPosX(int inChunkPosX) {
        return inChunkPosX + WorldConstants.CHUNK_SIZE * chunk.posX;
    }

    public int getCellPosY(int inChunkPosY) {
        return inChunkPosY + WorldConstants.CHUNK_SIZE * chunk.posY;
    }

    public void setCell(CellType cellType, int inChunkPosX, int inChunkPosY) {
        setCell(cellType.createCell(), getCellPosX(inChunkPosX), getCellPosY(inChunkPosY), inChunkPosX, inChunkPosY);
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        setCell(cellType.createCell(), cellPosX, cellPosY, inChunkPosX, inChunkPosY);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        cell.addedToWorld(chunk.world, chunk, cellPosX, cellPosY);

        chunk.grid.set(inChunkPosX, inChunkPosY, cell);

        int index = (inChunkPosY * WorldConstants.CHUNK_SIZE + inChunkPosX) * WorldConstants.NUM_MESH_VERTEX_VALUES;
        this.vertices[index + 2] = cell.getColorR();
        this.vertices[index + 3] = cell.getColorG();
        this.vertices[index + 4] = cell.getColorB();
        this.vertices[index + 5] = cell instanceof Empty ? 1f : 0f;
    }

    public Chunk buildChunk() {
        chunk.mesh.setVertices(vertices);

        return chunk;
    }


}
