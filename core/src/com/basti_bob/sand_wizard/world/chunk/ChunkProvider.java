package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.world.ChunkColumnData;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;
import com.basti_bob.sand_wizard.world.world_saving.ChunkSaver;

import java.util.*;
import java.util.function.Supplier;

public class ChunkProvider {

    public final World world;
    public final ArrayList<Chunk> chunks = new ArrayList<>();
    public final HashMap<ChunkPos, Chunk> chunkLUT = new HashMap<>();

    public final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingRows = new TreeMap<>();
    public final HashMap<Integer, ChunkColumnData> chunkColumns = new HashMap<>();

    private final Stack<Chunk> unusedChunkPool = new Stack<>();

    public ChunkProvider(World world) {
        this.world = world;

        for (int i = 0; i < WorldConstants.CHUNK_POOL_SIZE; i++) {
            unusedChunkPool.push(new Chunk());
        }
    }

    public ChunkColumnData getOrCreateChunkColumn(int chunkX) {
        return chunkColumns.computeIfAbsent(chunkX, key -> new ChunkColumnData(world, key));
    }

    public Chunk getChunk(ChunkPos chunkPos) {
        return chunkLUT.get(chunkPos);
    }

    public Chunk getChunk(int chunkX, int chunkY) {
        return getChunk(new ChunkPos(chunkX, chunkY));
    }

    public Supplier<Chunk> createChunk(ChunkPos chunkPos, int chunkX, int chunkY) {

        Chunk poolChunk = getChunkFromPool();

        if (WorldConstants.SAVE_CHUNK_DATA) {
            ChunkBuilder savedChunk = ChunkSaver.readChunk(world, poolChunk, chunkX, chunkY);

            if (savedChunk != null) return savedChunk::buildChunk;
        }

        HashMap<InChunkPos, ToPlaceStructureCell> queuedCells = world.structurePlacingManager.getUnloadedStructureCells(chunkPos);
        if (queuedCells != null) {
            return world.chunkGenerator.generateWithCells(poolChunk, chunkX, chunkY, queuedCells)::buildChunk;
        }

        return world.chunkGenerator.generateNew(poolChunk, chunkX, chunkY)::buildChunk;
    }

    public Supplier<Chunk> getOrCreateChunk(ChunkPos chunkPos, int chunkX, int chunkY) {
        Chunk chunk = chunkLUT.get(chunkPos);

        if (chunk != null) return () -> chunk;

        return createChunk(chunkPos, chunkX, chunkY);
    }

    public Supplier<Chunk> createChunk(int chunkX, int chunkY) {
        return createChunk(new ChunkPos(chunkX, chunkY), chunkX, chunkY);
    }


    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
        chunkLUT.put(new ChunkPos(chunk.posX, chunk.posY), chunk);

        WorldUpdatingChunkRow chunkRow = chunkUpdatingRows.get(chunk.posY);
        if (chunkRow == null) {
            chunkRow = new WorldUpdatingChunkRow(chunk.posY);
            chunkUpdatingRows.put(chunk.posY, chunkRow);
        }
        chunkRow.addChunk(chunk);
    }

    public void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
        chunkLUT.remove(new ChunkPos(chunk.posX, chunk.posY), chunk);

        WorldUpdatingChunkRow chunkRow = chunkUpdatingRows.get(chunk.posY);
        chunkRow.removeChunk(chunk);
        if (chunkRow.isEmpty()) {
            chunkUpdatingRows.remove(chunk.posY);
        }


        unusedChunkPool.push(chunk);
    }

    private Chunk getChunkFromPool() {
        return unusedChunkPool.pop();
    }
}
