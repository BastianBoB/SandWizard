package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.coordinateSystems.CellPos;
import com.basti_bob.sand_wizard.coordinateSystems.ChunkPos;

import java.util.ArrayList;
import java.util.HashMap;

public class World {

    public static final Vector2 GRAVITY = new Vector2(0, 0.1f);

    private boolean updateDirection;

    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final ArrayList<ChunkPos> chunksToLoad = new ArrayList<>();
    private final HashMap<ChunkPos, Chunk> chunkLUT = new HashMap<>();

    public World() {
        loadOrCreateChunk(new ChunkPos(0, 0));
    }

    public void update() {

        long start = System.nanoTime();

        updateDirection = !updateDirection;

        for (Chunk chunk : chunks) {
            //if(!chunk.isActive()) continue;

            chunk.update(updateDirection);
        }

        System.out.println("updating " + chunks.size() + " chunks took:" + (System.nanoTime() - start) / 1e6 + " ms");

    }

    public static int getChunkPos(int cellPos) {
        return  (int) Math.floor(cellPos / (float) WorldConstants.CHUNK_SIZE);
    }

    public static int getInChunkPos(int cellPos) {
        return Math.floorMod(cellPos, WorldConstants.CHUNK_SIZE);
    }

    public void addChunkToLoad() {

    }

    public boolean hasChunk(CellPos cellPos) {
        return getChunk(cellPos) != null;
    }

    public boolean hasChunk(ChunkPos chunkPos) {
        return getChunk(chunkPos) != null;
    }

    public Chunk getChunk(CellPos cellPos) {
        return getChunk(cellPos.getChunkPos());
    }

    public Chunk getChunk(ChunkPos chunkPos) {
        return chunkLUT.get(chunkPos);
    }

    public void loadOrCreateChunk(ChunkPos chunkPos) {
        if(this.hasChunk(chunkPos)) return;

        Chunk chunk = Chunk.loadOrCreate(this, chunkPos);

        chunks.add(chunk);
        chunkLUT.put(chunkPos, chunk);
    }

    public void unloadChunk(ChunkPos chunkPos) {

    }

    public Cell getCell(CellPos cellPos) {
        return getChunk(cellPos).getCell(cellPos.getInChunkPos());
    }

    public void setCell(CellType cellType, CellPos cellPos) {
        getChunk(cellPos).setCell(cellType, cellPos);
    }

    public void setCell(Cell cell, CellPos cellPos) {
        getChunk(cellPos).setCell(cell, cellPos);
    }

    public boolean isEmpty(CellPos cellPos) {
        if (!hasChunk(cellPos)) return true;

        return getCell(cellPos) instanceof Empty;
    }

}
