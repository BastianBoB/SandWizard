package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.chunk.CellPlaceFlag;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkProvider;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;


public class World {

    private boolean updateDirection;
    public int activeChunks;
    public int updateTimes;

    public final ChunkProvider chunkProvider;
    public final ChunkGenerator chunkGenerator;
    public final WorldGeneration worldGeneration;

    public final ConcurrentHashMap<ChunkPos, Pair<Boolean, Supplier<Chunk>>> chunksToRemoveOrAdd = new ConcurrentHashMap<>();

    public final Deque<Structure> unplacedStructures = new ArrayDeque<>();
    public final HashMap<ChunkPos, HashMap<InChunkPos, Cell>> unloadedStructureCells = new HashMap<>();

    public World() {
        this.chunkProvider = new ChunkProvider(this);
        this.worldGeneration = new WorldGeneration(this);
        this.chunkGenerator = new ChunkGenerator(this);
    }

    public void test() {
        addStructureToPlaceAsync(() -> StructureGenerator.TREES.TREE_2.generate(this, 50, (int) worldGeneration.getTerrainHeight(50)));
        addStructureToPlaceAsync(() -> StructureGenerator.TREES.TREE_3.generate(this, 100, (int) worldGeneration.getTerrainHeight(100)));
        addStructureToPlaceAsync(() -> StructureGenerator.TREES.TREE_4.generate(this, 150, (int) worldGeneration.getTerrainHeight(150)));
        addStructureToPlaceAsync(() -> StructureGenerator.TREES.TREE_5.generate(this, 200, (int) worldGeneration.getTerrainHeight(200)));
        addStructureToPlaceAsync(() -> StructureGenerator.TREES.TREE_2.generate(this, 250, (int) worldGeneration.getTerrainHeight(250)));


        int y = 400;
        int x = 100;
        for (CellType cellType : CellType.FLOWER_PETAL.ALL) {


            for (int i = -10; i <= 10; i++) {
                for (int j = -10; j <= 10; j++) {
                    setCell(cellType, x + i, y + j);
                }
            }


            x += 25;
        }
    }

    public void addStructureToPlace(Structure structure) {
        this.unplacedStructures.add(structure);
    }

    public void addStructureToPlaceAsync(Supplier<Structure> structureSupplier) {
        CompletableFuture.runAsync(() -> unplacedStructures.add(structureSupplier.get()));
    }

    public void update() {
        updateTimes++;

        for (int i = 50; i <= 250; i += 50)
            setCell(CellType.FIRE, i, (int) worldGeneration.getTerrainHeight(i));


        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        addAndRemoveChunks();
        placeStructures();

        updateChunkActiveAndSetCellsNotUpdated(executor);
        updateAllCells(executor);

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void chunkToAdd(Supplier<Chunk> supplier, int chunkX, int chunkY) {
        chunksToRemoveOrAdd.put(new ChunkPos(chunkX, chunkY), Pair.of(true, supplier));
    }

    private void chunkToRemove(Chunk chunk) {
        ChunkPos pos = new ChunkPos(chunk.posX, chunk.posY);

        if (chunksToRemoveOrAdd.containsKey(pos)) return;

        chunksToRemoveOrAdd.put(pos, Pair.of(false, chunk));
    }

    private void addAndRemoveChunks() {
        while (!chunksToRemoveOrAdd.isEmpty()) {

            Iterator<Map.Entry<ChunkPos, Pair<Boolean, Supplier<Chunk>>>> iterator = chunksToRemoveOrAdd.entrySet().iterator();

            while (iterator.hasNext()) {

                Map.Entry<ChunkPos, Pair<Boolean, Supplier<Chunk>>> entry = iterator.next();
                ChunkPos chunkPos = entry.getKey();
                boolean add = entry.getValue().getLeft();
                Chunk chunk = entry.getValue().getRight().get();

                if (add) {
                    addChunk(chunk);

                    HashMap<InChunkPos, Cell> toPlaceCells = unloadedStructureCells.get(chunkPos);
                    if (toPlaceCells != null) {
                        placeStructureCellsInChunk(toPlaceCells, chunk);
                        unloadedStructureCells.remove(chunkPos);
                    }

                } else {
                    removeChunk(chunk);
                }
                iterator.remove();
            }
        }
    }

    public void placeStructureCellsInChunk(HashMap<InChunkPos, Cell> toPlaceCells, Chunk chunk) {

        for (Map.Entry<InChunkPos, Cell> cellEntry : toPlaceCells.entrySet()) {
            InChunkPos inChunkPos = cellEntry.getKey();
            chunk.setCellWithInChunkPos(cellEntry.getValue(), inChunkPos.x, inChunkPos.y, CellPlaceFlag.NEW);
        }
    }


    private void placeStructures() {
        if (!unplacedStructures.isEmpty()) {
            Structure structure = unplacedStructures.pop();

            structure.placeInWorld(this);
        }
    }

    private void updateChunkActiveAndSetCellsNotUpdated(ExecutorService executor) {
        activeChunks = 0;

        List<Callable<Object>> tasks = new ArrayList<>();

        for (Chunk chunk : chunkProvider.chunks) {

            chunk.updateActive();

            if (!chunk.isActive()) continue;
            activeChunks++;

            final Array2D<Cell> grid = chunk.getGrid();

            executor.submit(() -> {
                for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
                    for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {
                        Cell cell = grid.get(inChunkX, inChunkY);

                        cell.gotUpdated = false;
                    }
                }
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        updateDirection = !updateDirection;
    }

    private void updateAllCells(ExecutorService executor) {
        updateDirection = !updateDirection;


        for (WorldUpdatingChunkRow worldUpdatingChunkRow : chunkProvider.chunkUpdatingRows.values()) {

            for (int i = 0; i < 3; i++) {
                ArrayList<Chunk> separatedChunks = worldUpdatingChunkRow.separateChunksList[i];

                List<Callable<Object>> tasks = new ArrayList<>();

                for (Chunk chunk : separatedChunks) {

                    if (!chunk.isActive()) continue;
                    tasks.add(Executors.callable(() -> chunk.update(updateDirection)));
                }

                try {
                    executor.invokeAll(tasks);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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

    public Chunk getChunkFromCellPos(int cellPosX, int cellPosY) {
        return getChunkFromChunkPos(getChunkPos(cellPosX), getChunkPos(cellPosY));
    }

    public Chunk getChunkFromChunkPos(int chunkPosX, int chunkPosY) {
        return chunkProvider.getChunk(chunkPosX, chunkPosY);
    }

    public void unloadChunk(int chunkPosX, int chunkPosY) {
        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);
        if (chunk == null) return;

        if (WorldConstants.SAVE_CHUNK_DATA && chunk.hasBeenModified)
            ChunkSaver.writeChunk(chunk);

        removeChunk(chunk);
    }

    public void unloadChunkAsync(int chunkPosX, int chunkPosY) {
        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);
        if (chunk == null) return;

        if (WorldConstants.SAVE_CHUNK_DATA && chunk.hasBeenModified)
            ChunkSaver.writeChunk(chunk);

        chunkToRemove(chunk);
    }

    public void loadChunk(int chunkPosX, int chunkPosY) {
        addChunk(chunkProvider.createChunk(chunkPosX, chunkPosY).get());
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

                Chunk neighbourChunk = getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.activateChunk();
                neighbourChunk.chunkAccessor.setSurroundingChunk(chunk);
                chunk.chunkAccessor.setSurroundingChunk(neighbourChunk);
            }
        }

        chunkProvider.addChunk(chunk);
    }

    private void removeChunk(Chunk chunk) {
        chunk.gotRemovedFromWorld();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.chunkAccessor.removeSurroundingChunk(chunk);
                chunk.chunkAccessor.removeSurroundingChunk(neighbourChunk);
            }
        }

        chunkProvider.removeChunk(chunk);
    }


    public Cell getCell(int cellPosX, int cellPosY) {
        return getCell(cellPosX, cellPosY, getChunkPos(cellPosX), getChunkPos(cellPosY));
    }


    public Cell getCell(int cellPosX, int cellPosY, int chunkPosX, int chunkPosY) {
        Chunk chunk = getChunkFromChunkPos(chunkPosX, chunkPosY);
        if (chunk == null) return null;

        return chunk.getCellFromInChunkPos(getInChunkPos(cellPosX), getInChunkPos(cellPosY));
    }


    public void setCell(CellType cellType, int cellPosX, int cellPosY) {
        Chunk chunk = getChunkFromCellPos(cellPosX, cellPosY);
        if (chunk == null) return;

        chunk.setCell(cellType, cellPosX, cellPosY);
    }


    public void setCell(Cell cell, int cellPosX, int cellPosY, CellPlaceFlag flag) {
        Chunk chunk = getChunkFromCellPos(cellPosX, cellPosY);
        if (chunk == null) return;

        chunk.setCell(cell, cellPosX, cellPosY, flag);
    }

}
