package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Supplier;

public class ChunkProvider {

    public final World world;
    public final ArrayList<Chunk> chunks = new ArrayList<>();
    public final HashMap<Long, Chunk> chunkLUT = new HashMap<>();

    private final Stack<Chunk> unusedChunkPool = new Stack<>();

    public ChunkProvider(World world) {
        this.world = world;

        for (int i = 0; i < 4000; i++) {
            unusedChunkPool.push(new Chunk());
        }
    }

    public boolean hasChunk(int chunkX, int chunkY) {
        return chunkLUT.containsKey(World.getPositionLong(chunkX, chunkY));
    }

    public Chunk getChunk(long chunkPositionKey) {
        return chunkLUT.get(chunkPositionKey);
    }

    public Chunk getChunk(int chunkX, int chunkY) {
        return getChunk(World.getPositionLong(chunkX, chunkY));
    }

    public Supplier<Chunk> createChunk(long chunkPositionKey, int chunkX, int chunkY) {

        Chunk poolChunk = getChunkFromPool();

        if (WorldConstants.SAVE_CHUNK_DATA) {
            ChunkBuilder savedChunk = ChunkSaver.readChunk(world, poolChunk, chunkX, chunkY);

            if (savedChunk != null) return savedChunk::buildChunk;
        }

        HashMap<Long, Cell> queuedCells = world.unloadedStructureCells.get(chunkPositionKey);
        if (queuedCells != null) {
            return ChunkGenerator.generateWithCells(world, poolChunk, chunkX, chunkY, queuedCells)::buildChunk;
        }

        return ChunkGenerator.generateNew(world, poolChunk, chunkX, chunkY)::buildChunk;
    }

    public Supplier<Chunk> getOrCreateChunk(long chunkPositionKey, int chunkX, int chunkY) {
        Chunk chunk = chunkLUT.get(chunkPositionKey);
        if (chunk != null) return () -> chunk;

        return createChunk(chunkPositionKey, chunkX, chunkY);
    }

    public Supplier<Chunk> createChunk(int chunkX, int chunkY) {
        return createChunk(World.getPositionLong(chunkX, chunkY), chunkX, chunkY);
    }

    public Supplier<Chunk> createChunk(long chunkPositionKey) {
        return createChunk(chunkPositionKey, World.getXFromPositionKey(chunkPositionKey), World.getYFromPositionKey(chunkPositionKey));
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
        chunkLUT.put(World.getPositionLong(chunk.posX, chunk.posY), chunk);
    }

    public void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
        chunkLUT.remove(World.getPositionLong(chunk.posX, chunk.posY), chunk);

        unusedChunkPool.push(chunk);
    }

    private Chunk getChunkFromPool() {
        return unusedChunkPool.pop();
    }
}
