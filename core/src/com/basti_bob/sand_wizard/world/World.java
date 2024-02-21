package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;


public class World {

    public static final Vector2 GRAVITY = new Vector2(0, 0.1f);

    private boolean updateDirection;

    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingGrid = new TreeMap<>();

    private final ArrayList<Long> chunksToLoad = new ArrayList<>();
    private final HashMap<Long, Chunk> chunkLUT = new HashMap<>();

    public World() {

//        for(int i = 0; i <= 0; i++) {
//            loadOrCreateChunk(0, i);
//        }
//        loadOrCreateChunk(0, 0);
//        loadOrCreateChunk(0, -1);
//        setCell(CellType.SAND, 1, 20);
//        setCell(CellType.SAND, 1, 21);
//        setCell(CellType.SAND, 1, 22);
//        setCell(CellType.SAND, 1, 23);


//        int r = 5;
//
//        for (int i = -r; i < r; i++) {
//            for (int j = -r; j < r; j++) {
//                loadOrCreateChunk(i, j);
//            }
//        }
//
//        System.out.println(getCell(0, 0).getCellType());

    }

    public void update() {

        for (Chunk chunk : chunks) {
            Array2D<Cell> grid = chunk.getGrid();

            for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
                for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                    grid.get(i, j).hasMoved = false;
                }
            }
        }


        long start = System.nanoTime();

        updateDirection = !updateDirection;

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (WorldUpdatingChunkRow worldUpdatingChunkRow : chunkUpdatingGrid.values()) {
            System.out.println(worldUpdatingChunkRow.chunkPosY);

            for (int i = 0; i < 3; i++) {
                ArrayList<Chunk> seperatedChunks = worldUpdatingChunkRow.separateChunksList[i];

                List<Future<?>> futures = new ArrayList<>();

                for (Chunk chunk : seperatedChunks) {

                    futures.add(executor.submit(() -> {
                        chunk.update(updateDirection);
                    }));
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
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Executor interrupted");
        }

        System.out.println("updating " + chunks.size() + " chunks took:" + (System.nanoTime() - start) / 1e6 + " ms");
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

    public void addChunkToLoad() {

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

    public void loadOrCreateChunk(int chunkPosX, int chunkPosY) {
        if (this.hasChunkFromChunkPos(chunkPosX, chunkPosY)) return;

        Chunk chunk = Chunk.loadOrCreate(this, chunkPosX, chunkPosY);

        addChunk(chunk);
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
        chunkLUT.put(getChunkKey(chunk.posX, chunk.posY), chunk);

        WorldUpdatingChunkRow chunkRow = chunkUpdatingGrid.get(chunk.posY);

        if (chunkRow == null) {
            chunkRow = new WorldUpdatingChunkRow(chunk.posY);
            chunkUpdatingGrid.put(chunk.posY, chunkRow);
        }
        chunkRow.addChunk(chunk);
    }

    public void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
        chunkLUT.remove(getChunkKey(chunk.posX, chunk.posY));

        WorldUpdatingChunkRow chunkRow = chunkUpdatingGrid.get(chunk.posY);

        chunkRow.removeChunk(chunk);

        if (chunkRow.isEmpty()) {
            chunkUpdatingGrid.remove(chunk.posY);
        }
    }


    public Cell getCell(int cellPosX, int cellPosY) {
        return getCell(cellPosX, cellPosY, getChunkPos(cellPosX), getChunkPos(cellPosY));
    }

    public Cell getCell(int cellPosX, int cellPosY, int chunkPosX, int chunkPosY) {
        return getChunkFromChunkPos(chunkPosX, chunkPosY).getCellFromInChunkPos(getInChunkPos(cellPosX), getInChunkPos(cellPosY));
    }

    public void setCell(CellType cellType, int cellPosX, int cellPosY) {
        getChunkFromCellPos(cellPosX, cellPosY).setCell(cellType, cellPosX, cellPosY);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY) {
        getChunkFromCellPos(cellPosX, cellPosY).setCell(cell, cellPosX, cellPosY);
    }

    public boolean isEmpty(int cellPosX, int cellPosY) {
        int chunkPosX = getChunkPos(cellPosX);
        int chunkPosY = getChunkPos(cellPosY);

        if (!hasChunkFromChunkPos(chunkPosX, chunkPosY)) return false;

        return false;

        //return getCell(cellPosX, cellPosY, chunkPosX, chunkPosY) instanceof Empty;
    }

}
