package com.basti_bob.sand_wizard.world.chunk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

import java.awt.*;

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
        this.vertices = new float[cs * cs * 5];

        float chunkRenderX = posX * cs * WorldConstants.CELL_SIZE;
        float chunkRenderY = posY * cs * WorldConstants.CELL_SIZE;

        for (int j = 0; j < cs; j++) {
            for (int i = 0; i < cs; i++) {

                int vertexI = (j * cs + i) * 5;

                vertices[vertexI] = chunkRenderX + i * WorldConstants.CELL_SIZE;
                vertices[vertexI + 1] = chunkRenderY + j * WorldConstants.CELL_SIZE;
                vertices[vertexI + 2] = 0;
                vertices[vertexI + 3] = 0;
                vertices[vertexI + 4] = 0;
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

        int index = (inChunkPosY * WorldConstants.CHUNK_SIZE + inChunkPosX) * 5;

        Color color = cell.getColor();
        this.vertices[index + 2] = color.r;
        this.vertices[index + 3] = color.g;
        this.vertices[index + 4] = color.b;
    }

    public Chunk buildChunk() {
        int cs = WorldConstants.CHUNK_SIZE;


        Mesh mesh = new Mesh(true, cs * cs * 5, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 3, "a_color"));

        mesh.setVertices(vertices);

        return new Chunk(world, posX, posY, grid, mesh);
    }

}
