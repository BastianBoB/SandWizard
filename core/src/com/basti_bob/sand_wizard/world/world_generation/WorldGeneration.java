package com.basti_bob.sand_wizard.world.world_generation;

import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.noise.AmpFreqNoise;
import com.basti_bob.sand_wizard.util.noise.Noise;
import com.basti_bob.sand_wizard.util.noise.LayeredNoise;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.chunk_data.ChunkCaveData;
import com.basti_bob.sand_wizard.world.world_generation.chunk_data.ChunkColumnData;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_generation.biomes.SurfaceBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world.world_generation.biomes.CaveBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class WorldGeneration {

    private final World world;

    private final Random biomeRandom = new Random();

    public final Noise surfaceBiomeNoise = new AmpFreqNoise(0.01f, 1f);
    private final List<SurfaceBiomeType> surfaceBiomeTypes = new ArrayList<>();
    public final int surfaceBiomeBlendingRadius = 5;

    public final Noise caveBiomeNoise = new LayeredNoise(5, 1f, 0.005f, 0.6f, 2f);
    private final List<CaveBiomeType> caveBiomeTypes = new ArrayList<>();

    public final ConcurrentHashMap<Integer, ChunkColumnData> chunkColumnDataMap = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<ChunkPos, ChunkCaveData> chunkCaveDataMap = new ConcurrentHashMap<>();

    public WorldGeneration(World world) {
        this.world = world;

        surfaceBiomeTypes.addAll(BiomeType.SURFACE.REGISTRY.getAllEntries());
        caveBiomeTypes.addAll(BiomeType.CAVE.REGISTRY.getAllEntries());
    }

    public ChunkColumnData getOrCreateChunkColumnData(int chunkX) {
        return chunkColumnDataMap.computeIfAbsent(chunkX, key -> new ChunkColumnData(world, key));
    }

    public ChunkCaveData getOrCreateChunkCaveData(int chunkX, int chunkY) {
        return chunkCaveDataMap.computeIfAbsent(new ChunkPos(chunkX, chunkY), key -> new ChunkCaveData(world, key));
    }


    public float getTerrainHeight(int cellPosX) {
        int chunkPosX = World.getChunkPos(cellPosX);
        int inChunkX = World.getInChunkPos(cellPosX);

        ChunkColumnData chunkColumnData = getOrCreateChunkColumnData(chunkPosX);
        return chunkColumnData.terrainHeights[inChunkX];
    }

    public SurfaceBiomeType getSurfaceBiomeType(int chunkPosX) {
        ChunkColumnData chunkColumnData = getOrCreateChunkColumnData(chunkPosX);
        return chunkColumnData.surfaceBiomeType;
    }

    public CaveBiomeType getCaveBiomeType(int chunkPosX, int chunkPosY) {
        ChunkCaveData caveData = getOrCreateChunkCaveData(chunkPosX, chunkPosY);

        return caveData.caveBiomeType;
    }

    public CaveBiomeType getInterpolatedCaveBiomeType(float interpolatedChunkPosX, float interpolatedChunkPosY) {
        return BiomeType.calculateBiomeWithNoiseValue(caveBiomeTypes, biomeRandom, getInterpolatedCaveBiomeNoise(interpolatedChunkPosX, interpolatedChunkPosY));
    }

    public boolean isCave(int cellPosX, int cellPosY) {
        ChunkCaveData caveData = getOrCreateChunkCaveData(World.getChunkPos(cellPosX), World.getChunkPos(cellPosY));

        return caveData.isCaveWithCellPos(cellPosX, cellPosY);
    }

    public boolean calculateIsCave(int cellPosX, int cellPosY) {
        CaveGenerator caveGenerator = calculateCaveBiomeType(World.getChunkPos(cellPosX), World.getChunkPos(cellPosY)).caveGenerator;

        return caveGenerator.isCave(world, cellPosX, cellPosY, getTerrainHeight(cellPosX));
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

    public CaveBiomeType calculateCaveBiomeType(int chunkPosX, int chunkPosY) {
        return BiomeType.calculateBiomeWithNoiseValue(caveBiomeTypes, biomeRandom, getCaveBiomeNoise(chunkPosX, chunkPosY));
    }

    public float getSurfaceBiomeNoise(int chunkX) {
        return surfaceBiomeNoise.eval(chunkX);
    }

    public float getCaveBiomeNoise(int chunkX, int chunkY) {
        return caveBiomeNoise.eval(chunkX, chunkY);
    }

    public float getInterpolatedCaveBiomeNoise(float interpolatedChunkX, float interpolatedChunkY) {
        return caveBiomeNoise.eval(interpolatedChunkX, interpolatedChunkY);
    }

}
