package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.GasBreathingStone;
import com.basti_bob.sand_wizard.entities.Entity;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.FunctionRunTime;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.chunk.ChunkProvider;
import com.basti_bob.sand_wizard.world.chunk.WorldUpdatingChunkRow;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.explosions.Explosion;
import com.basti_bob.sand_wizard.world.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world.world_generation.WorldGeneration;
import com.basti_bob.sand_wizard.world.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.StructurePlacingManager;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.WorldLight;
import com.basti_bob.sand_wizard.world.world_saving.ChunkSaver;
import org.apache.commons.lang3.tuple.Pair;

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
    public final ChunkProvider chunkProvider;
    public final ChunkGenerator chunkGenerator;
    public final WorldGeneration worldGeneration;
    public final StructurePlacingManager structurePlacingManager;

    public final ConcurrentHashMap<ChunkPos, Pair<Boolean, Supplier<Chunk>>> chunksToRemoveOrAdd = new ConcurrentHashMap<>();

    public final Deque<Structure> unplacedStructures = new ArrayDeque<>();

    private final Stack<Explosion> explosions = new Stack<>();

    public final List<WorldLight> globalLights = new ArrayList<>();

    private final List<Entity> entities = new ArrayList<>();

    public World() {
        this.chunkSaver = new ChunkSaver();
        this.chunkProvider = new ChunkProvider(this);
        this.worldGeneration = new WorldGeneration(this);
        this.chunkGenerator = new ChunkGenerator(this);
        this.structurePlacingManager = new StructurePlacingManager(this);

        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println(numThreads);
        executor = Executors.newFixedThreadPool(numThreads);
    }

    public void test() {
            for(int i = -1000; i < 1000; i++) {
                for (int j = -1000; j < 1000; j++) {
                    setCell(CellType.LIQUID.HYPER_ACID, i, j);
                }
            }
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

        addAndRemoveChunks();
        placeStructures();
        updateChunkActiveAndSetCellsNotUpdated();



        if (SandWizard.isUpdating) {
            updateAllCells();
            float time = FunctionRunTime.timeFunction(() -> updateExplosions());
            if (time > 0.1) System.out.println("UPDATE EXPLOSION: " + time);
        }

        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.update();
        }
    }

    private void chunkToAdd(Supplier<Chunk> supplier, int chunkX, int chunkY) {
        chunksToRemoveOrAdd.put(new ChunkPos(chunkX, chunkY), Pair.of(true, supplier));
    }

    private void chunkToRemove(Chunk chunk) {
        ChunkPos pos = new ChunkPos(chunk.getPosX(), chunk.getPosY());

        if (chunksToRemoveOrAdd.containsKey(pos)) return;

        chunksToRemoveOrAdd.put(pos, Pair.of(false, chunk));
    }

    private void addAndRemoveChunks() {
        while (!chunksToRemoveOrAdd.isEmpty()) {

            Iterator<Map.Entry<ChunkPos, Pair<Boolean, Supplier<Chunk>>>> iterator = chunksToRemoveOrAdd.entrySet().iterator();

            while (iterator.hasNext()) {

                Map.Entry<ChunkPos, Pair<Boolean, Supplier<Chunk>>> entry = iterator.next();
                boolean add = entry.getValue().getLeft();
                Chunk chunk = entry.getValue().getRight().get();

                if (add) {
                    addChunk(chunk);
                    structurePlacingManager.loadedChunk(chunk, entry.getKey());
                } else {
                    removeChunk(chunk);
                }

                iterator.remove();
            }
        }
    }


    private void placeStructures() {

        if (!unplacedStructures.isEmpty()) {
            Structure structure = unplacedStructures.pop();

            structure.placeInWorld(this);
        }
    }

    private void updateChunkActiveAndSetCellsNotUpdated() {
        activeChunks = 0;
        loadedChunks = 0;

        List<Callable<Object>> tasks = new ArrayList<>();

        for (Chunk chunk : chunkProvider.getChunks()) {
            chunk.updateActive();

            if (!chunk.isLoaded()) continue;
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

        for (WorldUpdatingChunkRow worldUpdatingChunkRow : chunkProvider.chunkUpdatingRows.values()) {

            for (int i = 0; i < 3; i++) {
                int index = updateDirection ? i : 2 - i;

                ArrayList<Chunk> separatedChunks = new ArrayList<>(worldUpdatingChunkRow.separateChunksList[index]);

                List<Callable<Object>> tasks = new ArrayList<>();

                for (Chunk chunk : separatedChunks) {
                    if (!chunk.isActive() || !chunk.isLoaded()) continue;

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
        return chunkProvider.getChunk(chunkPosX, chunkPosY);
    }

    public void unloadChunk(int chunkPosX, int chunkPosY) {
        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);
        if (chunk == null) return;

        if (WorldConstants.SAVE_CHUNK_DATA && chunk.hasBeenModified)
            chunkSaver.writeChunk(chunk);

        removeChunk(chunk);
    }

    public void unloadChunkAsync(int chunkPosX, int chunkPosY) {
        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);
        if (chunk == null) return;

        if (WorldConstants.SAVE_CHUNK_DATA && chunk.hasBeenModified)
            chunkSaver.writeChunkAsync(chunk);

        chunkToRemove(chunk);
    }

    public Chunk loadChunk(int chunkPosX, int chunkPosY) {
        Chunk chunk = chunkProvider.createChunk(chunkPosX, chunkPosY).get();

        addChunk(chunk);

        return chunk;
    }

    public void loadChunkAsync(int chunkPosX, int chunkPosY) {
        if (this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        Supplier<Chunk> chunkSupplier = chunkProvider.createChunk(chunkPosX, chunkPosY);

        chunkToAdd(chunkSupplier, chunkPosX, chunkPosY);
    }

    private void addChunk(Chunk chunk) {
        chunk.gotAddedToWorld();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = getChunkFromChunkPos(chunk.getPosX() + i, chunk.getPosY() + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.activateChunk();
                neighbourChunk.chunkAccessor.setSurroundingChunk(chunk);
                chunk.chunkAccessor.setSurroundingChunk(neighbourChunk);

                if (neighbourChunk.lightsInChunk.size() > 0)
                    chunk.affectedLights.addAll(neighbourChunk.lightsInChunk);
            }
        }

        chunkProvider.addChunk(chunk);
        chunk.activateChunk();
    }

    private void removeChunk(Chunk chunk) {
        chunk.gotRemovedFromWorld();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = getChunkFromChunkPos(chunk.getPosX() + i, chunk.getPosY() + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.chunkAccessor.removeSurroundingChunk(chunk);
                chunk.chunkAccessor.removeSurroundingChunk(neighbourChunk);
            }
        }

        chunkProvider.removeChunk(chunk);
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
