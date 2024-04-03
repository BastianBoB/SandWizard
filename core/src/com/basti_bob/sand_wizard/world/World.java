package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.FunctionRunTime;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world_generation.trees.TreeGenerator;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.text.Utilities;
import java.util.*;
import java.util.concurrent.*;


public class World {


    private boolean updateDirection;

    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingGrid = new TreeMap<>();
    private final HashMap<Long, Chunk> chunkLUT = new HashMap<>();

    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(0L);

    private final ConcurrentHashMap<Long, Pair<Chunk, ChunkBuilder>> chunksToRemoveOrAdd = new ConcurrentHashMap<>();

    public World() {
        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                this.loadOrCreateChunk(i, j);
            }
        }

        TreeGenerator.TREE_1.placeTree(this, -180, ChunkGenerator.getTerrainHeight(this, -180));
        TreeGenerator.TREE_2.placeTree(this, -100, ChunkGenerator.getTerrainHeight(this, -100));
        TreeGenerator.TREE_3.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0));
        TreeGenerator.TREE_4.placeTree(this, 100, ChunkGenerator.getTerrainHeight(this, 100));
        TreeGenerator.TREE_5.placeTree(this, 180, ChunkGenerator.getTerrainHeight(this, 180));


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
        addAndRemoveChunks();

        int height = (int) ChunkGenerator.getTerrainHeight(this, 0) + 100;
        setCell(CellType.FIRE, -100, (int) ChunkGenerator.getTerrainHeight(this, -100));
        setCell(CellType.ACID, -50, height);
        setCell(CellType.DIRT, 25, height);
        setCell(CellType.COAL, -25, height);
        setCell(CellType.SAND, 50, height);

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        updateChunkActiveAndSetCellsNotUpdated(executor);
        updateAllCells(executor);

        executor.shutdown();

//        try {
//            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//        } catch (InterruptedException e) {
//            System.err.println("Executor interrupted");
//        }

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
        List<Callable<Object>> tasks = new ArrayList<>();

        for (Chunk chunk : chunks) {
            chunk.updateActive();

            if (!chunk.isActive()) continue;

            final Array2D<Cell> grid = chunk.getGrid();

            tasks.add(Executors.callable(() -> {
                for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
                    for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {
                        grid.get(inChunkX, inChunkY).gotUpdated = false;
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

        for (WorldUpdatingChunkRow worldUpdatingChunkRow : chunkUpdatingGrid.values()) {

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

        WorldUpdatingChunkRow chunkRow = chunkUpdatingGrid.get(chunk.posY);

        if (chunkRow == null) {
            chunkRow = new WorldUpdatingChunkRow(chunk.posY);
            chunkUpdatingGrid.put(chunk.posY, chunkRow);
        }
        chunkRow.addChunk(chunk);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Chunk neighbourChunk = getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if (neighbourChunk == null) continue;

                neighbourChunk.chunkAccessor.setSurroundingChunk(chunk);
                chunk.chunkAccessor.setSurroundingChunk(neighbourChunk);
            }
        }
    }

    private void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
        chunkLUT.remove(getChunkKey(chunk.posX, chunk.posY));

        WorldUpdatingChunkRow chunkRow = chunkUpdatingGrid.get(chunk.posY);

        chunkRow.removeChunk(chunk);

        if (chunkRow.isEmpty()) {
            chunkUpdatingGrid.remove(chunk.posY);
        }
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

    public void setCell(Cell cell, int cellPosX, int cellPosY) {
        Chunk chunk = getChunkFromCellPos(cellPosX, cellPosY);
        if (chunk == null) return;

        chunk.setCell(cell, cellPosX, cellPosY);
    }

    public void setCell(Cell cell) {
        Chunk chunk = getChunkFromCellPos(cell.posX, cell.posY);
        if (chunk == null) return;

        chunk.setCell(cell, cell.posX, cell.posY);
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
