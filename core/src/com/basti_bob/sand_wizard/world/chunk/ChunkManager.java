package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class ChunkManager {

    public final World world;
    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final HashMap<ChunkPos, Chunk> chunkLUT = new HashMap<>();

    public final Queue<Chunk> chunksToAdd = new ConcurrentLinkedDeque<>();
    public final Queue<Chunk> chunksToRemove = new ConcurrentLinkedDeque<>();

    public final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingRows = new TreeMap<>();

    private final Stack<Chunk> unusedChunkPool = new Stack<>();

    public ChunkManager(World world) {
        this.world = world;

        for (int i = 0; i < WorldConstants.CHUNK_LOADING.CHUNK_POOL_SIZE; i++) {
            unusedChunkPool.push(new Chunk());
        }
    }

    public void addAndRemoveChunks() {
        if (!chunksToAdd.isEmpty())
            System.out.println("add: " + chunksToAdd.size());

        if (!chunksToRemove.isEmpty())
            System.out.println("remove: " + chunksToRemove.size());

        while (!chunksToAdd.isEmpty()) {
            addChunk(chunksToAdd.poll());
        }

        while (!chunksToRemove.isEmpty()) {
            removeChunk(chunksToRemove.poll());
        }
    }

    public Chunk getChunk(ChunkPos chunkPos) {
        return chunkLUT.get(chunkPos);
    }

    public Chunk getChunk(int chunkX, int chunkY) {
        return getChunk(new ChunkPos(chunkX, chunkY));
    }


    public Chunk createChunk(ChunkPos chunkPos, int chunkX, int chunkY) {
        Chunk poolChunk = getChunkFromPool();

        if (WorldConstants.SAVE_CHUNK_DATA) {
            ChunkBuilder savedChunk = world.chunkSaver.readChunk(world, poolChunk, chunkX, chunkY);
            if (savedChunk != null) return savedChunk.buildChunk();
        }

        HashMap<InChunkPos, ToPlaceStructureCell> queuedCells = world.structurePlacingManager.getUnloadedStructureCells(chunkPos);
        if (queuedCells != null) {
            return world.chunkGenerator.generateWithCells(poolChunk, chunkX, chunkY, queuedCells).buildChunk();
        }

        return world.chunkGenerator.generateNew(poolChunk, chunkX, chunkY).buildChunk();
    }

    public Chunk createChunk(int chunkX, int chunkY) {
        return createChunk(new ChunkPos(chunkX, chunkY), chunkX, chunkY);
    }

    public void addChunk(Chunk chunk) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = world.getChunkFromChunkPos(chunk.getPosX() + i, chunk.getPosY() + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.activateChunk();
                neighbourChunk.chunkAccessor.setSurroundingChunk(chunk);
                chunk.chunkAccessor.setSurroundingChunk(neighbourChunk);

                if (!neighbourChunk.lightsInChunk.isEmpty())
                    chunk.affectedLights.addAll(neighbourChunk.lightsInChunk);
            }
        }
        chunk.gotAddedToWorld();

        chunks.add(chunk);
        chunkLUT.put(new ChunkPos(chunk.getPosX(), chunk.getPosY()), chunk);

        WorldUpdatingChunkRow chunkRow = chunkUpdatingRows.get(chunk.getPosY());
        if (chunkRow == null) {
            chunkRow = new WorldUpdatingChunkRow(chunk.getPosY());
            chunkUpdatingRows.put(chunk.getPosY(), chunkRow);
        }
        chunkRow.addChunk(chunk);
    }

    public void removeChunk(Chunk chunk) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = world.getChunkFromChunkPos(chunk.getPosX() + i, chunk.getPosY() + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.chunkAccessor.removeSurroundingChunk(chunk);
                chunk.chunkAccessor.removeSurroundingChunk(neighbourChunk);
            }
        }
        chunk.gotRemovedFromWorld();

        chunks.remove(chunk);
        chunkLUT.remove(new ChunkPos(chunk.getPosX(), chunk.getPosY()), chunk);

        WorldUpdatingChunkRow chunkRow = chunkUpdatingRows.get(chunk.getPosY());
        chunkRow.removeChunk(chunk);
        if (chunkRow.isEmpty()) {
            chunkUpdatingRows.remove(chunk.getPosY());
        }

        unusedChunkPool.push(chunk);
    }

    private Chunk getChunkFromPool() {
        return unusedChunkPool.pop();
    }

    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    public HashMap<ChunkPos, Chunk> getChunkLUT() {
        return chunkLUT;
    }
}
