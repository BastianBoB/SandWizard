package com.basti_bob.sand_wizard.world.world_generation;

import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.noise.AmpFreqNoise;
import com.basti_bob.sand_wizard.world.ChunkColumnData;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_generation.biomes.SurfaceBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world.world_generation.biomes.CaveBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.List;
import java.util.Random;

public class WorldGeneration {

    private final World world;

    private final Random biomeRandom = new Random();

    public final AmpFreqNoise surfaceBiomeNoise = new AmpFreqNoise(0.01f, 1f);
    private final List<SurfaceBiomeType> surfaceBiomeTypes = SurfaceBiomeType.ALL_TYPES;
    public final int surfaceBiomeBlendingRadius = 5;

    public final AmpFreqNoise caveBiomeNoise = new AmpFreqNoise(0.01f, 1f);
    private final List<CaveBiomeType> caveBiomeTypes = CaveBiomeType.ALL_TYPES;

    public WorldGeneration(World world) {
        this.world = world;
    }

    public float getTerrainHeight(int cellPosX) {
        int chunkPosX = World.getChunkPos(cellPosX);
        int inChunkX = World.getInChunkPos(cellPosX);

        ChunkColumnData chunkColumnData = world.chunkProvider.getOrCreateChunkColumn(chunkPosX);
        return chunkColumnData.terrainHeights[inChunkX];
    }

    public SurfaceBiomeType getSurfaceBiomeType(int chunkPosX) {
        ChunkColumnData chunkColumnData = world.chunkProvider.getOrCreateChunkColumn(chunkPosX);
        return chunkColumnData.surfaceBiomeType;
    }

    public CaveBiomeType getCaveBiomeType(int chunkPosX, int chunkPosY) {
        return BiomeType.calculateBiomeWithNoiseValue(caveBiomeTypes, biomeRandom, getCaveBiomeNoise(chunkPosX, chunkPosY));
    }


    public float calculateTerrainHeight(int cellPosX) {
        int chunkPosX = World.getChunkPos(cellPosX);

        TerrainHeightGenerator terrainHeightGenerator = calculateSurfaceBiomeType(chunkPosX).terrainHeightGenerator;

        TerrainHeightGenerator[] terrainHeightGeneratorsToRight = new TerrainHeightGenerator[surfaceBiomeBlendingRadius];
        for (int i = 0; i < surfaceBiomeBlendingRadius; i++) {
            terrainHeightGeneratorsToRight[i] = calculateSurfaceBiomeType(chunkPosX + i + 1).terrainHeightGenerator;
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
            float interpolationFactor = 1 - ((targetX - cellPosX) / ((float) WorldConstants.CHUNK_SIZE * surfaceBiomeBlendingRadius));

            baseHeight = MathUtil.lerp(baseHeight, neighborHeight, interpolationFactor);

        }

        return baseHeight;
    }


    public SurfaceBiomeType calculateSurfaceBiomeType(int chunkPosX) {
        return BiomeType.calculateBiomeWithNoiseValue(surfaceBiomeTypes, biomeRandom, getSurfaceBiomeNoise(chunkPosX));
    }

    public float getSurfaceBiomeNoise(int chunkX) {
        return surfaceBiomeNoise.eval(chunkX);
    }

    public float getCaveBiomeNoise(int chunkX, int chunkY) {
        return caveBiomeNoise.eval(chunkX, chunkY);
    }

}
