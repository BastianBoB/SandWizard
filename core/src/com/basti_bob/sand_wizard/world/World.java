package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.entities.Entity;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.chunk.ChunkManager;
import com.basti_bob.sand_wizard.world.chunk.WorldUpdatingChunkRow;
import com.basti_bob.sand_wizard.world.explosions.Explosion;
import com.basti_bob.sand_wizard.world.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world.world_generation.WorldGeneration;
import com.basti_bob.sand_wizard.world.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.StructurePlacingManager;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.WorldLight;
import com.basti_bob.sand_wizard.world.world_saving.ChunkSaver;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;


public class World implements ChunkAccessor {

    public final Random random = new Random(0);

    private final ExecutorService executor;

    private boolean updateDirection;
    public int activeChunks, loadedChunks;
    public int updateTimes;

    public final ChunkSaver chunkSaver;
    public final ChunkManager chunkManager;
    public final ChunkGenerator chunkGenerator;
    public final WorldGeneration worldGeneration;
    public final StructurePlacingManager structurePlacingManager;

    public final Queue<Structure> unplacedStructures = new ConcurrentLinkedDeque<>();

    private final Stack<Explosion> explosions = new Stack<>();

    public final List<WorldLight> globalLights = new ArrayList<>();

    private final List<Entity> entities = new ArrayList<>();

    public World() {
        this.chunkSaver = new ChunkSaver();
        this.chunkManager = new ChunkManager(this);
        this.worldGeneration = new WorldGeneration(this);
        this.chunkGenerator = new ChunkGenerator(this);
        this.structurePlacingManager = new StructurePlacingManager(this);

        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println(numThreads);
        executor = Executors.newFixedThreadPool(numThreads);
    }


    public void addStructureToPlace(Structure structure) {
        this.unplacedStructures.add(structure);
    }

    public void addStructureToPlaceAsync(Supplier<Structure> structureSupplier) {
        CompletableFuture.runAsync(() -> unplacedStructures.add(structureSupplier.get()));
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
        entity.addedToWorld(this);
    }

    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
        entity.removedFromWorld(this);
    }

    public void update() {
        updateTimes++;

        chunkManager.addAndRemoveChunks();
        placeStructures();

        updateChunkActiveAndSetCellsNotUpdated();

        if (SandWizard.isUpdating) {
            updateAllCells();
            updateExplosions();
        }

        for (Entity entity : entities) {
            entity.update();
        }
    }


    private void placeStructures() {

        while (!unplacedStructures.isEmpty()) {
            Structure structure = unplacedStructures.poll();

            structure.placeInWorld(this);
        }
    }

    private void updateChunkActiveAndSetCellsNotUpdated() {
        activeChunks = 0;
        loadedChunks = 0;

        List<Callable<Object>> tasks = new ArrayList<>();

        for (Chunk chunk : chunkManager.getChunks()) {
            chunk.updateActive();

            if (!chunk.isUpdating()) continue;
            loadedChunks++;

            if (!chunk.isActive()) continue;
            activeChunks++;

            final Array2D<Cell> grid = chunk.getGrid();

            tasks.add(Executors.callable(() -> {
                for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
                    for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {
                        Cell cell = grid.get(inChunkX, inChunkY);

                        cell.gotUpdated = false;
                    }
                }
            }));
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateAllCells() {
        updateDirection = !updateDirection;

        for (WorldUpdatingChunkRow worldUpdatingChunkRow : chunkManager.chunkUpdatingRows.values()) {

            for (int i = 0; i < 3; i++) {
                int index = updateDirection ? i : 2 - i;

                ArrayList<Chunk> separatedChunks = new ArrayList<>(worldUpdatingChunkRow.separateChunksList[index]);

                List<Callable<Object>> tasks = new ArrayList<>();

                for (Chunk chunk : separatedChunks) {
                    if (!chunk.isActive() || !chunk.isUpdating()) continue;

                    tasks.add(Executors.callable(() -> chunk.update(updateDirection)));
                }

                try {
                    if (tasks.size() == 1) {
                        tasks.get(0).call();
                    } else {
                        executor.invokeAll(tasks);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateExplosions() {

        while (!explosions.isEmpty()) {
            Explosion explosion = explosions.pop();

            explosion.explode();
        }

    }

    public void addExplosion(Explosion explosion) {
        this.explosions.push(explosion);
    }

    public static int getChunkPos(int cellPos) {
        return (int) Math.floor(cellPos / (float) WorldConstants.CHUNK_SIZE);
    }

    public static int getInChunkPos(int cellPos) {
        return Math.floorMod(cellPos, WorldConstants.CHUNK_SIZE);
    }

    public boolean hasChunkFromChunkPos(int chunkPosX, int chunkPosY) {
        return getChunkFromChunkPos(chunkPosX, chunkPosY) != null;
    }

    @Override
    public Chunk getChunkFromCellPos(int cellPosX, int cellPosY) {
        return getChunkFromChunkPos(getChunkPos(cellPosX), getChunkPos(cellPosY));
    }

    @Override
    public Chunk getChunkFromChunkPos(int chunkPosX, int chunkPosY) {
        return chunkManager.getChunk(chunkPosX, chunkPosY);
    }

    public void unloadChunk(int chunkPosX, int chunkPosY) {
        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);

        unloadChunk(chunk);
    }

    public void unloadChunk(Chunk chunk) {
        if (chunk == null) return;

        if (WorldConstants.SAVE_CHUNK_DATA && chunk.hasBeenModified)
            chunkSaver.writeChunk(chunk);

        chunkManager.chunksToRemove.add(chunk);
    }

    public void unloadChunkAsync(Chunk chunk) {
        if (chunk == null) return;

        CompletableFuture.runAsync(() -> {
            if (WorldConstants.SAVE_CHUNK_DATA && chunk.hasBeenModified)
                chunkSaver.writeChunk(chunk);

            chunkManager.chunksToRemove.add(chunk);
        });
    }

    public void loadChunk(int chunkPosX, int chunkPosY) {
        if(hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        chunkManager.addChunk(chunkManager.createChunk(chunkPosX, chunkPosY));
    }

    public void loadChunkAsync(int chunkPosX, int chunkPosY) {
        if(hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        CompletableFuture.runAsync(() -> chunkManager.chunksToAdd.add(chunkManager.createChunk(chunkPosX, chunkPosY)));
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
