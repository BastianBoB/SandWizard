package com.basti_bob.sand_wizard.world_generation;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.WorldGeneration;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world_generation.surface_decoration.SurfaceDecorator;
import com.basti_bob.sand_wizard.world_generation.surface_generation.SurfaceGenerator;

import java.util.HashMap;

public class ChunkGenerator {


    private final World world;
    private final WorldGeneration worldGeneration;

    public ChunkGenerator(World world) {
        this.world = world;
        this.worldGeneration = world.worldGeneration;
    }

    public ChunkBuilder generateNew(Chunk oldChunk, int chunkPosX, int chunkPosY) {
        return generateWithCells(oldChunk, chunkPosX, chunkPosY, null);
    }

    public ChunkBuilder generateWithCells(Chunk oldChunk, int chunkPosX, int chunkPosY, HashMap<InChunkPos, Cell> queuedCells) {
        ChunkBuilder chunkBuilder = new ChunkBuilder(world, oldChunk, chunkPosX, chunkPosY);

        BiomeType biomeType = worldGeneration.getBiomeTypeWithChunkPos(chunkPosX);

        SurfaceDecorator surfaceDecorator = biomeType.surfaceDecorator;
        SurfaceGenerator surfaceGenerator = biomeType.surfaceGenerator;
        SurfaceGenerator rightSurfaceGenerator = worldGeneration.getBiomeTypeWithChunkPos(chunkPosX + 1).surfaceGenerator;

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunkBuilder.getCellPosX(i);
            float terrainHeight = worldGeneration.getTerrainHeight(cellPosX);

            float surfaceInterpolationFactor = i / (float) WorldConstants.CHUNK_SIZE;

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {

                int cellPosY = chunkBuilder.getCellPosY(j);

                Cell cell = getCellFromMap(i, j, queuedCells);

                if(cell != null) {
                    chunkBuilder.setCell(cell, cellPosX, cellPosY, i, j);
                } else {

                    CellType cellType = Math.random() < surfaceInterpolationFactor ?
                            rightSurfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight) : surfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);

                    chunkBuilder.setCell(cellType, cellPosX, cellPosY, i, j);
                }

                if (cellPosY == (int) terrainHeight + 1) {
                    surfaceDecorator.decorateSurface(world, cellPosX, cellPosY);
                }
            }
        }

        return chunkBuilder;
    }

    public Cell getCellFromMap(int inChunkX, int inChunkY, HashMap<InChunkPos, Cell> queuedCells) {
        if (queuedCells == null) return null;

        return queuedCells.get(InChunkPos.get(inChunkX, inChunkY));
    }

}
