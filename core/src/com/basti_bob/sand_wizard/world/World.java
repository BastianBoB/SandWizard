package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;
import com.basti_bob.sand_wizard.world_generation.trees.TreeGenerator;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;

import java.util.*;
import java.util.concurrent.*;


public class World {



    private boolean updateDirection;

    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingGrid = new TreeMap<>();
    private final HashMap<Long, Chunk> chunkLUT = new HashMap<>();

    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(0L);

    private final ConcurrentHashMap<Chunk, Boolean> removeOrAddChunk = new ConcurrentHashMap<>();

    public World() {
        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                this.loadOrCreateChunk(i, j);
            }
        }

//        TreeGenerator.TREE_1.placeTree(this, -180, ChunkGenerator.getTerrainHeight(this, -180));
//        TreeGenerator.TREE_2.placeTree(this, -100, ChunkGenerator.getTerrainHeight(this, -100));
//        TreeGenerator.TREE_3.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0));
//        TreeGenerator.TREE_4.placeTree(this, 100, ChunkGenerator.getTerrainHeight(this, 100));
//        TreeGenerator.TREE_5.placeTree(this, 180, ChunkGenerator.getTerrainHeight(this, 180));


        //FunctionRunTime.timeFunction("generating Tree", () -> TreeGenerator.TREE_2.placeTree(this, 0, ChunkGenerator.getTerrainHeight(this, 0)));
//
//
        for (int i = -50; i <= 100; i++) {
            setCell(CellType.STONE, 50, i);
            setCell(CellType.STONE, -50, i);

//            if(i < 45)
//                setCell(CellType.STONE, 0, i);
        }
//
//        for (int i = -50; i <= 25; i++) {
//            setCell(CellType.STONE, -20, i);
//            setCell(CellType.STONE, 20, i);
//        }
//
//        setCell(CellType.SAND, 1, 100 + 1);
    }

    private int updateTimes = 0;

    public int numActiveChunks;

    public void asyncRemoveOrAddChunk(Chunk chunk, boolean remove) {
        removeOrAddChunk.put(chunk, remove);
    }

    public void update() {
        Iterator<Map.Entry<Chunk, Boolean>> iterator = removeOrAddChunk.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Chunk, Boolean> entry = iterator.next();
            Chunk chunk = entry.getKey();
            if (entry.getValue()) {
                removeChunk(chunk);
            } else {
                addChunk(chunk);
            }
            iterator.remove();
        }

        int height = 200;
        ++updateTimes;

//        try {
//
//            setCell(CellType.FIRE, -30, 0);
//
//            setCell(CellType.SAND, -100, height);
//            setCell(CellType.DIRT, 0, height);
//            setCell(CellType.COAL, 100, height);
//
//
//            for (int i = -5; i <= 5; i++) {
//                setCell(CellType.OIL, i * 5 + 1, height + 5);
//            }
//        } catch (Exception e) {
//
//        }


        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (Chunk chunk : chunks) {
            chunk.updateActive();

            final Array2D<Cell> grid = chunk.getGrid();

            executor.submit(() -> {
                for (int inChunkY = 0; inChunkY < WorldConstants.CHUNK_SIZE; inChunkY++) {
                    for (int inChunkX = 0; inChunkX < WorldConstants.CHUNK_SIZE; inChunkX++) {
                        grid.get(inChunkX, inChunkY).gotUpdated = false;
                    }
                }
            });
        }

        updateDirection = !updateDirection;

        numActiveChunks = 0;

        for (WorldUpdatingChunkRow worldUpdatingChunkRow : chunkUpdatingGrid.values()) {

            for (int i = 0; i < 3; i++) {
                ArrayList<Chunk> separatedChunks = worldUpdatingChunkRow.separateChunksList[i];

                List<Future<?>> futures = new ArrayList<>();

                for (Chunk chunk : separatedChunks) {

                    if (!chunk.isActive()) continue;

                    numActiveChunks++;

                    futures.add(executor.submit(() -> chunk.update(updateDirection)));
                }

                //Wait for all tasks submitted in this iteration to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        executor.shutdown();
//        try {
//            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//        } catch (InterruptedException e) {
//            System.err.println("Executor interrupted");
//        }

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
        if (!this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);
        ChunkSaver.writeChunk(chunk);

        removeChunk(chunk);
    }

    public void unloadChunkAsync(int chunkPosX, int chunkPosY) {
        if (!this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        Chunk chunk = this.getChunkFromChunkPos(chunkPosX, chunkPosY);
        ChunkSaver.writeChunk(chunk);

        asyncRemoveOrAddChunk(chunk, true);

    }

    public void loadOrCreateChunk(int chunkPosX, int chunkPosY) {
        if (this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        Chunk chunk = ChunkSaver.readChunk(this, chunkPosX, chunkPosY);

        if (chunk == null) {
            chunk = ChunkGenerator.generateNew(this, chunkPosX, chunkPosY);
        }

        addChunk(chunk);
    }

    public void loadOrCreateChunkAsync(int chunkPosX, int chunkPosY) {
        if (this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        Chunk chunk = ChunkSaver.readChunk(this, chunkPosX, chunkPosY);

        if (chunk == null) {
            chunk = ChunkGenerator.generateNew(this, chunkPosX, chunkPosY);
        }

        asyncRemoveOrAddChunk(chunk, false);
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
        return getChunkFromChunkPos(chunkPosX, chunkPosY).getCellFromInChunkPos(getInChunkPos(cellPosX), getInChunkPos(cellPosY));
    }
//
    public void setCell(CellType cellType, int cellPosX, int cellPosY) {
        getChunkFromCellPos(cellPosX, cellPosY).setCell(cellType, cellPosX, cellPosY);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY) {
        getChunkFromCellPos(cellPosX, cellPosY).setCell(cell, cellPosX, cellPosY);
    }

    public void setCell(Cell cell) {
        getChunkFromCellPos(cell.posX, cell.posY).setCell(cell, cell.posX, cell.posY);
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
