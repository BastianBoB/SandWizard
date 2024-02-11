package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.coordinateSystems.CellPos;
import com.basti_bob.sand_wizard.coordinateSystems.ChunkPos;
import com.basti_bob.sand_wizard.coordinateSystems.InChunkPos;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.UseWithCare;

public class Chunk {


    private final Array2D<Cell> grid;
    private final World world;
    private final ChunkPos chunkPos;

    private boolean active = true;

    private Chunk(World world, ChunkPos chunkPos) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.grid = new Array2D<>(Cell.class, WorldConstants.CHUNK_SIZE, WorldConstants.CHUNK_SIZE);
    }

    @UseWithCare("Only for access in WorldRenderer")
    public Array2D<Cell> getGrid() {
        return grid;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public void setCell(CellType cellType, CellPos cellPos, InChunkPos inChunkPos) {
        grid.set(inChunkPos.getX(), inChunkPos.getY(), cellType.createCell(world, cellPos.getX(), cellPos.getY()));
    }

    public void setCell(Cell cell, CellPos cellPos, InChunkPos inChunkPos) {
        cell.x = cellPos.getX();
        cell.y = cellPos.getY();

        grid.set(inChunkPos.getX(), inChunkPos.getY(), cell);
    }

    public void setCell(Cell cell, CellPos cellPos) {
        setCell(cell, cellPos, cellPos.getInChunkPos());
    }

    public void setCell(CellType cellType, CellPos cellPos) {
        setCell(cellType, cellPos, cellPos.getInChunkPos());
    }

    private void setCell(CellType cellType, InChunkPos inChunkPos) {
        setCell(cellType, getCellPos(inChunkPos), inChunkPos);
    }


    public CellPos getCellPos(InChunkPos inChunkPos) {
        int cellX = inChunkPos.getX() + WorldConstants.CHUNK_SIZE * this.chunkPos.getX();
        int cellY = inChunkPos.getY() + WorldConstants.CHUNK_SIZE * this.chunkPos.getY();

        return new CellPos(cellX, cellY);
    }

    public Cell getCell(InChunkPos inChunkPos) {
        return grid.get(inChunkPos.getX(), inChunkPos.getY());
    }

    public void update(boolean updateDirection) {

        for (int y = 0; y < WorldConstants.CHUNK_SIZE; y++) {
            for (int x = 0; x < WorldConstants.CHUNK_SIZE; x++) {

                int xIndex = updateDirection ? x : WorldConstants.CHUNK_SIZE - x - 1;

                Cell cell = grid.get(xIndex, y);

                if (cell.hasMoved || cell instanceof Empty) continue;

                cell.update();
            }
        }
    }

    public static Chunk loadOrCreate(World world, ChunkPos chunkPos) {
        return generateNew(world, chunkPos);
    }

    public static Chunk generateNew(World world, ChunkPos chunkPos) {
        Chunk chunk = new Chunk(world, chunkPos);

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                chunk.setCell(CellType.SAND, new InChunkPos(i, j));
            }
        }

        for(int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
            chunk.setCell(CellType.STONE, new InChunkPos(i, 0));
            chunk.setCell(CellType.STONE, new InChunkPos(i, 31));
            chunk.setCell(CellType.STONE, new InChunkPos(0, i));
            chunk.setCell(CellType.STONE, new InChunkPos(31, i));
        }
        //chunk.setCell(CellType.SAND, new InChunkPos(WorldConstants.CHUNK_SIZE / 2, WorldConstants.CHUNK_SIZE / 2));

        return chunk;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
