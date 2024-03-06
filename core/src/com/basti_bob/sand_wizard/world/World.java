package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;

import java.util.*;
import java.util.concurrent.*;


public class World {


    private boolean updateDirection;

    public final ArrayList<Chunk> chunks = new ArrayList<>();
    private final SortedMap<Integer, WorldUpdatingChunkRow> chunkUpdatingGrid = new TreeMap<>();
    private final HashMap<Long, Chunk> chunkLUT = new HashMap<>();

    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise();

    public World() {
        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                this.loadOrCreateChunk(i, j);
            }
        }
//
//
//        for (int i = -50; i <= 0; i++) {
//            setCell(CellType.STONE, -5, i);
//            setCell(CellType.STONE, 6, i);
//        }
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

    public void update() {
        int height = 100;
        ++updateTimes;

//        if (updateTimes >= 100) {
//            setCell(CellType.SAND, -1, height + 1);
//            setCell(CellType.STONE, -1, height + 3);
//            setCell(CellType.WATER, -1, height + 5);
//            setCell(CellType.OIL, 0, height);
//            setCell(CellType.OIL, -2, height);
//        }
//
//        if (updateTimes <= 40) {
//            setCell(CellType.WATER, -3, height + 5);
//            setCell(CellType.WATER, -2, height + 5);
//            setCell(CellType.WATER, -1, height + 5);
//        }

        setCell(CellType.SAND, -100, height);
        setCell(CellType.DIRT, 0, height);
        setCell(CellType.COAL, 100, height);

        setCell(CellType.WATER, 5, height + 25);


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
        this.removeChunk(chunk);
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

    public void removeChunk(Chunk chunk) {
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
//    public Cell getCell(int cellPosX, int cellPosY) {
//        return getCell(cellPosX, cellPosY, getChunkPos(cellPosX), getChunkPos(cellPosY));
//    }
//
//    public Cell getCell(int cellPosX, int cellPosY, int chunkPosX, int chunkPosY) {
//        return getChunkFromChunkPos(chunkPosX, chunkPosY).getCellFromInChunkPos(getInChunkPos(cellPosX), getInChunkPos(cellPosY));
//    }
//
    public void setCell(CellType cellType, int cellPosX, int cellPosY) {
        getChunkFromCellPos(cellPosX, cellPosY).setCell(cellType, cellPosX, cellPosY);
    }

    public void setCell(Cell cell, int cellPosX, int cellPosY) {
        getChunkFromCellPos(cellPosX, cellPosY).setCell(cell, cellPosX, cellPosY);
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
