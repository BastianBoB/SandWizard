package com.basti_bob.sand_wizard.world.world_generation.structures;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Structure {

    private final HashMap<ChunkPos, HashMap<InChunkPos, ToPlaceStructureCell>> chunksWithCells;

    private Structure(HashMap<ChunkPos, HashMap<InChunkPos, ToPlaceStructureCell>> chunksWithCells) {
        this.chunksWithCells = chunksWithCells;
    }

    public void placeInWorld(World world) {

        Set<ChunkPos> toLoadChunks = new HashSet<>();

        for (Map.Entry<ChunkPos, HashMap<InChunkPos, ToPlaceStructureCell>> entry : chunksWithCells.entrySet()) {

            ChunkPos chunkPos = entry.getKey();
            Chunk chunk = world.chunkManager.getChunk(chunkPos);

            HashMap<InChunkPos, ToPlaceStructureCell> toPlaceCells = entry.getValue();

            if (chunk == null) {
                toLoadChunks.add(chunkPos);
                world.structurePlacingManager.addUnloadedStructureCells(chunkPos, toPlaceCells);
            } else {
                world.structurePlacingManager.placeStructureCellsInChunk(toPlaceCells, chunk);
            }
        }


        for (ChunkPos chunkPos : toLoadChunks) {
            world.loadChunkAsync(chunkPos.x, chunkPos.y);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final HashMap<ChunkPos, HashMap<InChunkPos, ToPlaceStructureCell>> chunksWithCells = new HashMap<>();

        public Builder() {

        }

        public void addCell(ToPlaceStructureCell toPlaceStructureCell, int cellX, int cellY) {
            int chunkX = World.getChunkPos(cellX);
            int chunkY = World.getChunkPos(cellY);

            int inChunkX = World.getInChunkPos(cellX);
            int inChunkY = World.getInChunkPos(cellY);

            HashMap<InChunkPos, ToPlaceStructureCell> chunkCells = chunksWithCells.computeIfAbsent(new ChunkPos(chunkX, chunkY), k -> new HashMap<>());

            chunkCells.put(InChunkPos.get(inChunkX, inChunkY), toPlaceStructureCell);
        }


        public Structure build() {
            return new Structure(chunksWithCells);
        }
    }
}
