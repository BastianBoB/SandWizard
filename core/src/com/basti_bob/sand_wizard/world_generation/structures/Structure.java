package com.basti_bob.sand_wizard.world_generation.structures;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Structure {

    private final HashMap<ChunkPos, HashMap<InChunkPos, Cell>> chunksWithCells;

    private Structure(HashMap<ChunkPos, HashMap<InChunkPos, Cell>> chunksWithCells) {
        this.chunksWithCells = chunksWithCells;
    }

    public void placeInWorld(World world) {

        Set<ChunkPos> toLoadChunks = new HashSet<>();

        for (Map.Entry<ChunkPos, HashMap<InChunkPos, Cell>> entry : chunksWithCells.entrySet()) {

            ChunkPos chunkPos = entry.getKey();
            Chunk chunk = world.chunkProvider.getChunk(chunkPos);

            HashMap<InChunkPos, Cell> toPlaceCells = entry.getValue();

            if (chunk == null) {
                toLoadChunks.add(chunkPos);
                world.unloadedStructureCells.put(chunkPos, toPlaceCells);
            } else {
                world.placeStructureCellsInChunk(toPlaceCells, chunk);
            }
        }

        CompletableFuture.runAsync(() -> {
            for (ChunkPos chunkPos : toLoadChunks) {
                world.loadChunkAsync(chunkPos.x, chunkPos.y);
            }
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final HashMap<ChunkPos, HashMap<InChunkPos, Cell>> chunksWithCells = new HashMap<>();

        public Builder() {

        }

        public void addCell(CellType cellType, int cellX, int cellY) {
            addCell(cellType.createCell(), cellX, cellY);
        }

        public void addCell(Cell cell, int cellX, int cellY) {
            int chunkX = World.getChunkPos(cellX);
            int chunkY = World.getChunkPos(cellY);

            int inChunkX = World.getInChunkPos(cellX);
            int inChunkY = World.getInChunkPos(cellY);

            HashMap<InChunkPos, Cell> chunkCells = chunksWithCells.computeIfAbsent(new ChunkPos(chunkX, chunkY), k -> new HashMap<>());

            chunkCells.put(InChunkPos.get(inChunkX, inChunkY), cell);
        }


        public Structure build() {
            return new Structure(chunksWithCells);
        }
    }
}
