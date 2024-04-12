package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world.chunk.CellPlaceFlag;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world_generation.structures.trees.TreeGenerator;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;


public class World {

    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(0L);
    private boolean updateDirection;
    public int activeChunks;
    public int updateTimes = 0;

    public final ArrayList<Chunk> chunks = new ArrayList<>();
    public final HashMap<Long, Chunk> chunkLUT = new HashMap<>();
    private final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingRows = new TreeMap<>();

    public final ConcurrentHashMap<Long, Pair<Chunk, ChunkBuilder>> chunksToRemoveOrAdd = new ConcurrentHashMap<>();
    public final Deque<Pair<Long, Structure>> unplacedStructures = new ArrayDeque<>();
    public final HashMap<Long, List<Cell>> queuedStructureCells = new HashMap<>();

    public World() {

        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                this.loadOrCreateChunk(i, j);
            }
        }

//        TreeGenerator.TREE_5.placeTree(this, -300, ChunkGenerator.getTerrainHeight(this, -300));
//        TreeGenerator.TREE_1.placeTree(this, -180, ChunkGenerator.getTerrainHeight(this, -180));
//        TreeGenerator.TREE_5.placeTree(this, -100, ChunkGenerator.getTerrainHeight(this, -100));
//        TreeGenerator.TREE_3.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0));
//        TreeGenerator.TREE_4.placeTree(this, 100, ChunkGenerator.getTerrainHeight(this, 100));
//        TreeGenerator.TREE_2.placeTree(this, 180, ChunkGenerator.getTerrainHeight(this, 180));

        //setCell(CellType.GLOWBLOCK, 50, 100);


        //FunctionRunTime.timeFunction("generating Tree", () -> TreeGenerator.TREE_2.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0)));
//
//
//        for (int i = -50; i <= 100; i++) {
//            setCell(CellType.STONE, -50, i);
//
//            setCell(CellType.STONE, i, i > 0 ? 50 : 51);
////            if(i < 45)
////                setCell(CellType.STONE, 0, i);
//        }
    }

    public void addStructureToPlace(Structure structure, int x, int y) {
        this.unplacedStructures.add(Pair.of(getPositionLong(x, y), structure));
    }

    public void addStructureToPlaceAsync(Supplier<Structure> structureSupplier, int x, int y) {
        CompletableFuture.runAsync(() -> unplacedStructures.add(Pair.of(getPositionLong(x, y), structureSupplier.get())));
    }

    public void update() {
        updateTimes++;


        setCell(CellType.FIRE, -300, (int) ChunkGenerator.getTerrainHeight(this, -300));
        setCell(CellType.FIRE, -180, (int) ChunkGenerator.getTerrainHeight(this, -180));
        setCell(CellType.FIRE, -100, (int) ChunkGenerator.getTerrainHeight(this, -100));
        setCell(CellType.FIRE, 0, (int) ChunkGenerator.getTerrainHeight(this, 0));
        setCell(CellType.FIRE, 100, (int) ChunkGenerator.getTerrainHeight(this, 100));
        setCell(CellType.FIRE, 180, (int) ChunkGenerator.getTerrainHeight(this, 180));

//        if (updateTimes % 3600 == 0) {
//            TreeGenerator.TREE_5.placeTree(this, -300, ChunkGenerator.getTerrainHeight(this, -300));
//            TreeGenerator.TREE_1.placeTree(this, -180, ChunkGenerator.getTerrainHeight(this, -180));
//            TreeGenerator.TREE_5.placeTree(this, -100, ChunkGenerator.getTerrainHeight(this, -100));
//            TreeGenerator.TREE_3.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0));
//            TreeGenerator.TREE_4.placeTree(this, 100, ChunkGenerator.getTerrainHeight(this, 100));
//            TreeGenerator.TREE_2.placeTree(this, 180, ChunkGenerator.getTerrainHeight(this, 180));
//        }


        int height = (int) ChunkGenerator.getTerrainHeight(this, 25) + 200;

        setCell(CellType.ACID, -100, height);
        setCell(CellType.WATER, -50, height);
        setCell(CellType.DIRT, 25, height);
        setCell(CellType.COAL, -25, height);
        setCell(CellType.SAND, 50, 300);
        setCell(CellType.FINE_SAND, 100, 300);

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        addAndRemoveChunks();
        placeStructures();
        //placeCells();
        updateChunkActiveAndSetCellsNotUpdated(executor);
        updateAllCells(executor);

        executor.shutdown();
    }

    private void chunkToAdd(ChunkBuilder chunkBuilder) {
        long key = getPositionLong(chunkBuilder.posX, chunkBuilder.posY);

        chunksToRemoveOrAdd.put(key, Pair.of(null, chunkBuilder));
    }

    private void chunkToRemove(Chunk chunk) {
        long key = getPositionLong(chunk.posX, chunk.posY);

        chunksToRemoveOrAdd.put(key, Pair.of(chunk, null));
    }

    private void addAndRemoveChunks() {
        Iterator<Map.Entry<Long, Pair<Chunk, ChunkBuilder>>> iterator = chunksToRemoveOrAdd.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, Pair<Chunk, ChunkBuilder>> entry = iterator.next();

            Pair<Chunk, ChunkBuilder> pair = entry.getValue();

            Chunk chunk = pair.getLeft();
            if (chunk != null) {
                removeChunk(pair.getLeft());
            } else {
                addChunk(pair.getRight().buildChunk());
            }

            iterator.remove();
        }
    }

    private void placeStructures() {
        if (!unplacedStructures.isEmpty()) {
            Pair<Long, Structure> structurePair = unplacedStructures.pop();

            long positionKey = structurePair.getKey();
            int xOff = getXFromPositionKey(positionKey);
            int yOff = getYFromPositionKey(positionKey);
            Structure structure = structurePair.getValue();

           // structure.placeInWorldAsync(this, xOff, yOff);
        }
    }

    private void updateChunkActiveAndSetCellsNotUpdated(ExecutorService executor) {
        activeChunks = 0;

        List<Callable<Object>> tasks = new ArrayList<>();

        for (Chunk chunk : chunks) {
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


        for (WorldUpdatingChunkRow worldUpdatingChunkRow : chunkUpdatingRows.values()) {

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


    public float getTemperatureForChunkX(int chunkX) {
        return (float) (openSimplexNoise.eval(chunkX * 0.01f, 0, 0) * 50);
    }

    public static int getChunkPos(int cellPos) {
        return (int) Math.floor(cellPos / (float) WorldConstants.CHUNK_SIZE);
    }

    public static int getInChunkPos(int cellPos) {
        return Math.floorMod(cellPos, WorldConstants.CHUNK_SIZE);
    }

    public static long getPositionLong(int x, int y) {
        return (((long) x) << 32) | (y & 0xFFFFFFFFL);
    }

    public static int getXFromPositionKey(long key) {
        return (int) (key >> 32);
    }

    public static int getYFromPositionKey(long key) {
        return (int) key;
    }


    public boolean hasChunkFromCellPos(int cellPosX, int cellPosY) {
        return getChunkFromCellPos(cellPosX, cellPosY) != null;
    }

    public boolean hasChunkFromChunkPos(int chunkPosX, int chunkPosY) {
        return getChunkFromChunkPos(chunkPosX, chunkPosY) != null;
    }

    public Chunk getChunkFromCellPos(int cellPosX, int cellPosY) {
        return getChunkFromChunkPos(getChunkPos(cellPosX), getChunkPos(cellPosY));
    }

    public Chunk getChunkFromChunkPos(int chunkPosX, int chunkPosY) {
        return chunkLUT.get(getPositionLong(chunkPosX, chunkPosY));
    }

    public void unloadChunk(int chunkPosX, int chunkPosY) {
        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);
        if (chunk == null) return;


        if (WorldConstants.SAVE_CHUNK_DATA)
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

    public Chunk loadOrCreateChunk(int chunkPosX, int chunkPosY) {
        Chunk chunk = getChunkFromChunkPos(chunkPosX, chunkPosY);

        if (chunk != null) return chunk;

        ChunkBuilder chunkBuilder = ChunkSaver.readChunk(this, chunkPosX, chunkPosY);

        if (chunkBuilder == null) {
            chunkBuilder = ChunkGenerator.generateNew(this, chunkPosX, chunkPosY);
        }

        chunk = chunkBuilder.buildChunk();

        addChunk(chunk);

        return chunk;
    }

    public void loadOrCreateChunkAsync(int chunkPosX, int chunkPosY) {
        if (this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        ChunkBuilder chunkBuilder = ChunkSaver.readChunk(this, chunkPosX, chunkPosY);

        if (chunkBuilder == null) {
            chunkBuilder = ChunkGenerator.generateNew(this, chunkPosX, chunkPosY);
        }

        chunkToAdd(chunkBuilder);
    }

    private void addChunk(Chunk chunk) {
        chunks.add(chunk);
        chunkLUT.put(getPositionLong(chunk.posX, chunk.posY), chunk);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.chunkAccessor.setSurroundingChunk(chunk);
                chunk.chunkAccessor.setSurroundingChunk(neighbourChunk);
            }
        }

        WorldUpdatingChunkRow chunkRow = chunkUpdatingRows.get(chunk.posY);

        if (chunkRow == null) {
            chunkRow = new WorldUpdatingChunkRow(chunk.posY);
            chunkUpdatingRows.put(chunk.posY, chunkRow);
        }
        chunkRow.addChunk(chunk);
    }

    private void removeChunk(Chunk chunk) {
        chunk.gotRemoved();

        chunks.remove(chunk);
        chunkLUT.remove(getPositionLong(chunk.posX, chunk.posY), chunk);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.chunkAccessor.removeSurroundingChunk(chunk);
                chunk.chunkAccessor.removeSurroundingChunk(neighbourChunk);
            }
        }

        WorldUpdatingChunkRow chunkRow = chunkUpdatingRows.get(chunk.posY);
        chunkRow.removeChunk(chunk);
        if (chunkRow.isEmpty()) {
            chunkUpdatingRows.remove(chunk.posY);
        }
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

//
//    public boolean isEmpty(int cellPosX, int cellPosY) {
//        int chunkPosX = getChunkPos(cellPosX);
//        int chunkPosY = getChunkPos(cellPosY);
//
//        if (!hasChunkFromChunkPos(chunkPosX, chunkPosY)) return false;
//
//        return false;
//
//        //return getCell(cellPosX, cellPosY, chunkPosX, chunkPosY) instanceof Empty;
//    }

}
