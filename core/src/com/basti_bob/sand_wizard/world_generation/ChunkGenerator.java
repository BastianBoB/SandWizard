package com.basti_bob.sand_wizard.world_generation;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world_generation.structures.trees.TreeGenerator;
import com.basti_bob.sand_wizard.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.HashMap;

public class ChunkGenerator {

    public static final int TERRAIN_HEIGHT_BLENDING_RADIUS = 5;

    public static ChunkBuilder generateNew(World world, Chunk oldChunk, int chunkPosX, int chunkPosY) {
        return generateWithCells(world, oldChunk, chunkPosX, chunkPosY, null);
    }

    public static ChunkBuilder generateWithCells(World world, Chunk oldChunk, int chunkPosX, int chunkPosY, HashMap<Long, Cell> queuedCells) {
        ChunkBuilder chunkBuilder = new ChunkBuilder(world, oldChunk, chunkPosX, chunkPosY);

        BiomeType biomeType = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX);
        TerrainHeightGenerator terrainHeightGenerator = biomeType.terrainHeightGenerator;
        SurfaceGenerator surfaceGenerator = biomeType.surfaceGenerator;
        SurfaceGenerator rightSurfaceGenerator = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX + 1).surfaceGenerator;

        TerrainHeightGenerator[] terrainHeightGeneratorsToRight = new TerrainHeightGenerator[TERRAIN_HEIGHT_BLENDING_RADIUS];

        for (int i = 0; i < TERRAIN_HEIGHT_BLENDING_RADIUS; i++) {
            terrainHeightGeneratorsToRight[i] = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX + i + 1).terrainHeightGenerator;
        }

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunkBuilder.getCellPosX(i);
            float terrainHeight = getTerrainHeight(world, chunkPosX, cellPosX, terrainHeightGenerator, terrainHeightGeneratorsToRight);

            float surfaceInterpolationFactor = i / (float) WorldConstants.CHUNK_SIZE;

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {

                int cellPosY = chunkBuilder.getCellPosY(j);

                Cell cell = getCellFromMap(cellPosX, cellPosY, queuedCells);

                if(cell != null) {
                    chunkBuilder.setCell(cell, cellPosX, cellPosY, i, j);
                } else {

                    CellType cellType = Math.random() < surfaceInterpolationFactor ?
                            rightSurfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight) : surfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);

                    chunkBuilder.setCell(cellType, cellPosX, cellPosY, i, j);
                }

                if (cellPosY == (int) terrainHeight && Math.random() < 0.01) {
                    //world.addStructureToPlaceAsync(() -> TreeGenerator.TREE_2.generateStructure(cellPosX, cellPosY));
                }
            }
        }

        return chunkBuilder;
    }

    public static Cell getCellFromMap(int cellPosX, int cellPosY, HashMap<Long, Cell> queuedCells) {
        if (queuedCells == null) return null;

        long positionKey = World.getPositionLong(cellPosX, cellPosY);
        return queuedCells.get(positionKey);
    }


    public static float getTerrainHeight(World world, int cellPosX) {
        int chunkPosX = World.getChunkPos(cellPosX);

        TerrainHeightGenerator terrainHeightGenerator = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX).terrainHeightGenerator;

        TerrainHeightGenerator[] terrainHeightGeneratorsToRight = new TerrainHeightGenerator[TERRAIN_HEIGHT_BLENDING_RADIUS];
        for (int i = 0; i < TERRAIN_HEIGHT_BLENDING_RADIUS; i++) {
            terrainHeightGeneratorsToRight[i] = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX + i + 1).terrainHeightGenerator;
        }

        return getTerrainHeight(world, chunkPosX, cellPosX, terrainHeightGenerator, terrainHeightGeneratorsToRight);
    }

    public static float getTerrainHeight(World world, int chunkPosX, int cellPosX, TerrainHeightGenerator heightGenerator, TerrainHeightGenerator[] heightGeneratorsToRight) {
        float baseHeight = heightGenerator.getTerrainHeight(world, cellPosX); // Base height from current chunk

        for (int i = 0; i < heightGeneratorsToRight.length; i++) {
            TerrainHeightGenerator nextGenerator = heightGeneratorsToRight[i];

            if (nextGenerator == heightGenerator) continue;

            int targetX = (chunkPosX + i + 1) * WorldConstants.CHUNK_SIZE;
            float neighborHeight = nextGenerator.getTerrainHeight(world, cellPosX);
            float interpolationFactor = 1 - ((targetX - cellPosX) / ((float) WorldConstants.CHUNK_SIZE * TERRAIN_HEIGHT_BLENDING_RADIUS));

            baseHeight = MathUtil.lerp(baseHeight, neighborHeight, interpolationFactor);

        }

        return baseHeight;
    }

}
