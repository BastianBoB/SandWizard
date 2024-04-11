package com.basti_bob.sand_wizard.world.chunk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class ChunkBuilder {

    private final World world;
    public final int posX, posY;
    private final float[] vertices;
    private final Array2D<Cell> grid;

    public ChunkBuilder(World world, int posX, int posY) {
        int cs = WorldConstants.CHUNK_SIZE;

        this.world = world;
        this.posX = posX;
        this.posY = posY;
        this.grid = new Array2D<>(Cell.class, cs, cs);
        this.vertices = new float[WorldConstants.NUM_MESH_VERTICES];

        for (int j = 0; j < cs; j++) {
            for (int i = 0; i < cs; i++) {

                int vertexI = (j * cs + i) * WorldConstants.NUM_MESH_VERTEX_VALUES;

                vertices[vertexI] = posX * cs + i;
                vertices[vertexI + 1] = posY * cs + j;
            }
        }
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

    public void setCell(CellType cellType, int cellPosX, int cellPosY, int inChunkPosX, int inChunkPosY) {
        Cell cell = cellType.createCell(world, cellPosX, cellPosY);
        grid.set(inChunkPosX, inChunkPosY, cell);

        int index = (inChunkPosY * WorldConstants.CHUNK_SIZE + inChunkPosX) * WorldConstants.NUM_MESH_VERTEX_VALUES;

        this.vertices[index + 2] = cell.getColorR();
        this.vertices[index + 3] = cell.getColorG();
        this.vertices[index + 4] = cell.getColorB();
    }

    public Chunk buildChunk() {

        Mesh mesh = new Mesh(true, WorldConstants.NUM_MESH_VERTICES, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 3, "a_vertexColor"));

        mesh.setVertices(vertices);

        return new Chunk(world, posX, posY, grid, mesh);
    }

}
