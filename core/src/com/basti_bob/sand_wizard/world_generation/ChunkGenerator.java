package com.basti_bob.sand_wizard.world_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world_generation.biomes.SurfaceGenerator;
import com.basti_bob.sand_wizard.world_generation.biomes.TerrainHeightGenerator;

public class ChunkGenerator {

    public static final int TERRAIN_HEIGHT_BLENDING_RADIUS = 3;

    public static Chunk generateNew(World world, int chunkPosX, int chunkPosY) {
        Chunk chunk = new Chunk(world, chunkPosX, chunkPosY);

        BiomeType biomeType = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX);
        TerrainHeightGenerator terrainHeightGenerator = biomeType.terrainHeightGenerator;
        SurfaceGenerator surfaceGenerator = biomeType.surfaceGenerator;
        SurfaceGenerator rightSurfaceGenerator = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX + 1).surfaceGenerator;

        TerrainHeightGenerator[] terrainHeightGeneratorsToRight = new TerrainHeightGenerator[TERRAIN_HEIGHT_BLENDING_RADIUS];

        for (int i = 0; i < TERRAIN_HEIGHT_BLENDING_RADIUS; i++) {
            terrainHeightGeneratorsToRight[i] = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX + i + 1).terrainHeightGenerator;
        }

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunk.getCellPosX(i);
            double terrainHeight = getTerrainHeight(world, chunkPosX, cellPosX, terrainHeightGenerator, terrainHeightGeneratorsToRight);

            float surfaceInterpolationFactor = i / (float) WorldConstants.CHUNK_SIZE;

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                chunk.setCellWithInChunkPos(CellType.EMPTY, i, j);

                int cellPosY = chunk.getCellPosY(j);


                CellType cellType = Math.random() < surfaceInterpolationFactor ? rightSurfaceGenerator.getCellType(cellPosY, terrainHeight) : surfaceGenerator.getCellType(cellPosY, terrainHeight);

                chunk.setCellWithInChunkPos(cellType, i, j);
            }
        }

        return chunk;

    }

    public static int getTerrainHeight(World world, int cellPosX) {
        int chunkPosX = World.getChunkPos(cellPosX);

        TerrainHeightGenerator terrainHeightGenerator = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX).terrainHeightGenerator;

        TerrainHeightGenerator[] terrainHeightGeneratorsToRight = new TerrainHeightGenerator[TERRAIN_HEIGHT_BLENDING_RADIUS];
        for (int i = 0; i < TERRAIN_HEIGHT_BLENDING_RADIUS; i++) {
            terrainHeightGeneratorsToRight[i] = BiomeType.getBiomeTypeWithChunkPos(world, chunkPosX + i + 1).terrainHeightGenerator;
        }

        return getTerrainHeight(world, chunkPosX, cellPosX, terrainHeightGenerator, terrainHeightGeneratorsToRight);
    }

    public static int nextDifferentHeightGeneratorIndex(TerrainHeightGenerator heightGenerator, TerrainHeightGenerator[]  heightGeneratorsToRight) {
        for (int i = 0; i < heightGeneratorsToRight.length; i++) {
            if (heightGenerator != heightGeneratorsToRight[i]) return i;
        }
        return -1;
    }

    public static int getTerrainHeight(World world, int chunkPosX, int cellPosX, TerrainHeightGenerator heightGenerator, TerrainHeightGenerator[] heightGeneratorsToRight) {
        int currentHeight = heightGenerator.getTerrainHeight(world, cellPosX);
        int nextDifferentHeightGeneratorIndex = nextDifferentHeightGeneratorIndex(heightGenerator, heightGeneratorsToRight);

        if (nextDifferentHeightGeneratorIndex == -1) {
            return currentHeight;
        }

        TerrainHeightGenerator HeightGeneratorRight = heightGeneratorsToRight[nextDifferentHeightGeneratorIndex];

        // Get  heights from current and adjacent biomes
        int targetX = (chunkPosX + nextDifferentHeightGeneratorIndex + 1) * WorldConstants.CHUNK_SIZE;
        int HeightRight = HeightGeneratorRight.getTerrainHeight(world, targetX);

        int heightDifference = HeightRight - currentHeight;

        float interpolationFactor = 1 - ((targetX - cellPosX) / ((float) WorldConstants.CHUNK_SIZE * TERRAIN_HEIGHT_BLENDING_RADIUS));

        return (int) (currentHeight + heightDifference * interpolationFactor);
    }

}
