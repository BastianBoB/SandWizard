package com.basti_bob.sand_wizard.world_generation.structures;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Structure {

    private final HashMap<Long, HashMap<Long, Cell>> chunksWithCells;

    private Structure(HashMap<Long, HashMap<Long, Cell>> chunksWithCells) {
        this.chunksWithCells = chunksWithCells;
    }

    public void placeInWorld(World world) {

        List<Long> toLoadChunks = new ArrayList<>();

        for (Map.Entry<Long, HashMap<Long, Cell>> entry : chunksWithCells.entrySet()) {

            long chunkKey = entry.getKey();
            Chunk chunk = world.chunkProvider.getChunk(chunkKey);
            HashMap<Long, Cell> toPlaceCells = entry.getValue();

            if (chunk == null) {
                toLoadChunks.add(chunkKey);
                world.unloadedStructureCells.put(chunkKey, toPlaceCells);
            } else {
                world.placeStructureCellsInChunk(toPlaceCells, chunk);
            }
        }

        CompletableFuture.runAsync(() -> {
            for (long chunkKey : toLoadChunks) {

                int chunkX = World.getXFromPositionKey(chunkKey);
                int chunkY = World.getYFromPositionKey(chunkKey);

                world.loadChunkAsync(chunkX, chunkY);
            }
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final HashMap<Long, HashMap<Long, Cell>> chunksWithCells = new HashMap<>();

        public Builder() {

        }

        public void addCell(CellType cellType, int cellX, int cellY) {
            int chunkX = World.getChunkPos(cellX);
            int chunkY = World.getChunkPos(cellY);
            long chunkKey = World.getPositionLong(chunkX, chunkY);

            HashMap<Long, Cell> chunkCells = chunksWithCells.computeIfAbsent(chunkKey, k -> new HashMap<>());

            long positionKey = World.getPositionLong(cellX, cellY);
            chunkCells.put(positionKey, cellType.createCell());
        }

        public Structure build() {
            return new Structure(chunksWithCells);
        }
    }
}
