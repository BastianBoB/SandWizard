package com.basti_bob.sand_wizard.world.world_saving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkSaver {

    public final String CHUNK_SAVING_LOCATION = "world/chunks/";

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private String getChunkFileLocation(int chunkX, int chunkY) {
        return CHUNK_SAVING_LOCATION + chunkX + "," + chunkY;
    }

    public void writeChunkAsync(Chunk chunk) {
        executorService.submit(() -> writeChunk(chunk));
    }

    public void writeChunk(Chunk chunk) {

        FileHandle file = Gdx.files.local(getChunkFileLocation(chunk.getPosX(), chunk.getPosY()));

        int sameCellCount = 0;
        String lastCellName = null;

        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
            for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
                CellType cellType = chunk.getCellFromInChunkPos(i, j).getCellType();

                String nameID = CellType.REGISTRY.getEntryNameID(cellType);

                if (lastCellName == null || lastCellName.equals(nameID)) {
                    sameCellCount++;
                } else {
                    stringBuilder.append(lastCellName).append(",").append(sameCellCount).append(",");
                    sameCellCount = 1;
                }

                lastCellName = nameID;
            }
        }

        stringBuilder.append(lastCellName).append(",").append(sameCellCount).append(",");

        file.writeString(stringBuilder.toString(), false);
    }


    public ChunkBuilder readChunk(World world, Chunk oldChunk, int chunkX, int chunkY) {
        //try {
            FileHandle file = Gdx.files.local(getChunkFileLocation(chunkX, chunkY));
            if (!file.exists()) return null;

            ChunkBuilder chunkBuilder = new ChunkBuilder(world, oldChunk, chunkX, chunkY);

            String[] cellsAndNumbers = file.readString().split(",");

            int arrayIndex = 0;
            int repeatedCellIteration = 0;

            CellType currentCellType = CellType.REGISTRY.getEntryWithId(cellsAndNumbers[arrayIndex]);
            int numRepeatingCells = Integer.parseInt(cellsAndNumbers[arrayIndex + 1]);

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

                    if (repeatedCellIteration >= numRepeatingCells) {
                        repeatedCellIteration = 0;
                        arrayIndex += 2;
                        currentCellType = CellType.REGISTRY.getEntryWithId(cellsAndNumbers[arrayIndex]);
                        numRepeatingCells = Integer.parseInt(cellsAndNumbers[arrayIndex + 1]);
                    }

                    chunkBuilder.setCell(currentCellType, i, j);
                    repeatedCellIteration++;
                }
            }

            return chunkBuilder;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
////            System.out.println("FAILED READING CHUNK at: " + chunkX + ", " + chunkY);
////            return null;
//        }
    }


}
