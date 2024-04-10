package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world.chunk.CellPlaceFlag;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world_generation.trees.TreeGenerator;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class World {

    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(0L);
    private boolean updateDirection;
    public int activeChunks;
    public int updateTimes = 0;

    public final ArrayList<Chunk> chunks = new ArrayList<>();
    public final HashMap<Long, Chunk> chunkLUT = new HashMap<>();
    private final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingRows = new TreeMap<>();

    public final ConcurrentHashMap<Long, Pair<Chunk, ChunkBuilder>> chunksToRemoveOrAdd = new ConcurrentHashMap<>();

    public World() {

        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                this.loadOrCreateChunk(i, j);
            }
        }

        TreeGenerator.TREE_5.placeTree(this, -300, ChunkGenerator.getTerrainHeight(this, -300));
        TreeGenerator.TREE_1.placeTree(this, -180, ChunkGenerator.getTerrainHeight(this, -180));
        TreeGenerator.TREE_5.placeTree(this, -100, ChunkGenerator.getTerrainHeight(this, -100));
        TreeGenerator.TREE_3.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0));
        TreeGenerator.TREE_4.placeTree(this, 100, ChunkGenerator.getTerrainHeight(this, 100));
        TreeGenerator.TREE_2.placeTree(this, 180, ChunkGenerator.getTerrainHeight(this, 180));

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

    public void update() {
        updateTimes++;

        addAndRemoveChunks();

        setCell(CellType.WATER, -50, (int) (ChunkGenerator.getTerrainHeight(this, -50) + 100));
        setCell(CellType.ACID, -100, (int) (ChunkGenerator.getTerrainHeight(this, -100) + 100));

        setCell(CellType.FIRE, -300, (int) ChunkGenerator.getTerrainHeight(this, -300));
        setCell(CellType.FIRE, -180, (int) ChunkGenerator.getTerrainHeight(this, -180));
        setCell(CellType.FIRE, -100, (int) ChunkGenerator.getTerrainHeight(this, -100));
        setCell(CellType.FIRE, 0, (int) ChunkGenerator.getTerrainHeight(this, 0));
        setCell(CellType.FIRE, 100, (int) ChunkGenerator.getTerrainHeight(this, 100));
        setCell(CellType.FIRE, 180, (int) ChunkGenerator.getTerrainHeight(this, 180));

        if(updateTimes % 3600 == 0) {
            TreeGenerator.TREE_5.placeTree(this, -300, ChunkGenerator.getTerrainHeight(this, -300));
            TreeGenerator.TREE_1.placeTree(this, -180, ChunkGenerator.getTerrainHeight(this, -180));
            TreeGenerator.TREE_5.placeTree(this, -100, ChunkGenerator.getTerrainHeight(this, -100));
            TreeGenerator.TREE_3.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0));
            TreeGenerator.TREE_4.placeTree(this, 100, ChunkGenerator.getTerrainHeight(this, 100));
            TreeGenerator.TREE_2.placeTree(this, 180, ChunkGenerator.getTerrainHeight(this, 180));
        }


        int height = (int) ChunkGenerator.getTerrainHeight(this, 25) + 100;
        setCell(CellType.DIRT, 25, height);
        setCell(CellType.COAL, -25, height);
        setCell(CellType.SAND, 50, height);

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        updateChunkActiveAndSetCellsNotUpdated(executor);
        updateAllCells(executor);
        updateLighting(executor);

        executor.shutdown();

//        try {
//            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//        } catch (InterruptedException e) {
//            System.err.println("Executor interrupted");
//        }

        for (Chunk chunk : chunks) {
            //if(chunk.affectedLights.size() == 0) continue;

            //System.out.println(chunk.posX + "," + chunk.posY + ": " + chunk.affectedLights.size());
        }
    }

    private void chunkToAdd(ChunkBuilder chunkBuilder) {
        long key = getChunkKey(chunkBuilder.posX, chunkBuilder.posY);

        chunksToRemoveOrAdd.put(key, Pair.of(null, chunkBuilder));
    }

    private void chunkToRemove(Chunk chunk) {
        long key = getChunkKey(chunk.posX, chunk.posY);

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

    private void updateChunkActiveAndSetCellsNotUpdated(ExecutorService executor) {
        activeChunks = 0;

        List<Callable<Object>> tasks = new ArrayList<>();

        for (Chunk chunk : chunks) {
            chunk.updateActive();

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

    private void updateLighting(ExecutorService executor) {
//
//        List<Callable<Object>> tasks = new ArrayList<>();
//
//        for (Chunk chunk : chunks) {
//            if (!chunk.isActive()) continue;
//
//            tasks.add(Executors.callable(chunk::updateLighting));
//        }
//
//        try {
//            executor.invokeAll(tasks);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    public float getTemperatureForChunkX(int chunkX) {
        return (float) (openSimplexNoise.eval(chunkX * 0.1f, 0, 0) * 50);
    }

    public static int getChunkPos(int cellPos) {
        return (int) Math.floor(cellPos / (float) WorldConstants.CHUNK_SIZE);
    }

    public static int getInChunkPos(int cellPos) {
        return Math.floorMod(cellPos, WorldConstants.CHUNK_SIZE);
    }

    public long getChunkKey(int chunkPosX, int chunkPosY) {
        return (((long) chunkPosX) << 32) | (chunkPosY & 0xffffffffL);
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
        return chunkLUT.get(getChunkKey(chunkPosX, chunkPosY));
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

    public void loadOrCreateChunk(int chunkPosX, int chunkPosY) {
        if (this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        ChunkBuilder chunkBuilder = ChunkSaver.readChunk(this, chunkPosX, chunkPosY);

        if (chunkBuilder == null) {
            chunkBuilder = ChunkGenerator.generateNew(this, chunkPosX, chunkPosY);
        }

        addChunk(chunkBuilder.buildChunk());
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
        chunkLUT.put(getChunkKey(chunk.posX, chunk.posY), chunk);

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
        chunks.remove(chunk);
        chunkLUT.remove(getChunkKey(chunk.posX, chunk.posY), chunk);

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

        chunk.gotRemoved();
    }

    //
//
    public Cell getCell(int cellPosX, int cellPosY) {
        return getCell(cellPosX, cellPosY, getChunkPos(cellPosX), getChunkPos(cellPosY));
    }

    //
    public Cell getCell(int cellPosX, int cellPosY, int chunkPosX, int chunkPosY) {
        Chunk chunk = getChunkFromChunkPos(chunkPosX, chunkPosY);
        if (chunk == null) return null;

        return chunk.getCellFromInChunkPos(getInChunkPos(cellPosX), getInChunkPos(cellPosY));
    }

    //
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

    public void setCell(Cell cell, CellPlaceFlag flag) {
        setCell(cell, cell.posX, cell.posY, flag);
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
