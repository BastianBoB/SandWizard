package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGeneration {

    private final Random biomeRandom = new Random();
    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(0L);

    private final World world;
    private final List<BiomeType> biomeTypes;
    public final int blendingRadius = 5;

    public WorldGeneration(World world) {
        this.world = world;
        this.biomeTypes = BiomeType.allTypes;
    }

    public float getTerrainHeight(int cellPosX) {
        int chunkPosX = World.getChunkPos(cellPosX);
        int inChunkX = World.getInChunkPos(cellPosX);

        ChunkColumnData chunkColumnData = world.chunkProvider.getOrCreateChunkColumn(chunkPosX);
        return chunkColumnData.terrainHeights[inChunkX];
    }

    public BiomeType getBiomeTypeWithChunkPos(int chunkPosX) {
        ChunkColumnData chunkColumnData = world.chunkProvider.getOrCreateChunkColumn(chunkPosX);
        return chunkColumnData.biomeType;
    }


    public float calculateTerrainHeight(int cellPosX) {
        int chunkPosX = World.getChunkPos(cellPosX);

        TerrainHeightGenerator terrainHeightGenerator = calculateBiomeTypeWithChunkPos(chunkPosX).terrainHeightGenerator;

        TerrainHeightGenerator[] terrainHeightGeneratorsToRight = new TerrainHeightGenerator[blendingRadius];
        for (int i = 0; i < blendingRadius; i++) {
            terrainHeightGeneratorsToRight[i] = calculateBiomeTypeWithChunkPos(chunkPosX + i + 1).terrainHeightGenerator;
        }

        return calculateTerrainHeight(chunkPosX, cellPosX, terrainHeightGenerator, terrainHeightGeneratorsToRight);
    }

    private float calculateTerrainHeight(int chunkPosX, int cellPosX, TerrainHeightGenerator heightGenerator, TerrainHeightGenerator[] heightGeneratorsToRight) {
        float baseHeight = heightGenerator.getTerrainHeight(world, cellPosX); // Base height from current chunk

        for (int i = 0; i < heightGeneratorsToRight.length; i++) {
            TerrainHeightGenerator nextGenerator = heightGeneratorsToRight[i];

            if (nextGenerator == heightGenerator) continue;

            int targetX = (chunkPosX + i + 1) * WorldConstants.CHUNK_SIZE;
            float neighborHeight = nextGenerator.getTerrainHeight(world, cellPosX);
            float interpolationFactor = 1 - ((targetX - cellPosX) / ((float) WorldConstants.CHUNK_SIZE * blendingRadius));

            baseHeight = MathUtil.lerp(baseHeight, neighborHeight, interpolationFactor);

        }

        return baseHeight;
    }


    public BiomeType calculateBiomeTypeWithChunkPos(int chunkPosX) {
        return calculateBiomeWithTemperature(getTemperatureForChunkX(chunkPosX));
    }

    public BiomeType calculateBiomeWithTemperature(float temperature) {

        List<BiomeType> potentialBiomes = new ArrayList<>();
        float totalWeight = 0;

        for (BiomeType biomeType : biomeTypes) {
            if (biomeType.isInTemperatureRange(temperature)) {
                potentialBiomes.add(biomeType);
                totalWeight += biomeType.weight;
            }
        }

        if (potentialBiomes.size() == 0) {
            return BiomeType.ERROR;
        }

        if (potentialBiomes.size() == 1) {
            return potentialBiomes.get(0);
        }

        biomeRandom.setSeed((long) temperature);

        float randomWeight = biomeRandom.nextFloat() * totalWeight;

        float currentWeight = 0;
        for (BiomeType biomeType : potentialBiomes) {
            currentWeight += biomeType.weight;

            if (randomWeight < currentWeight) return biomeType;
        }

        //shouldn't happen
        return BiomeType.ERROR;
    }


    public float getTemperatureForChunkX(int chunkX) {
        return (float) (openSimplexNoise.eval(chunkX * 0.01f, 0, 0) * 50);
    }
}
