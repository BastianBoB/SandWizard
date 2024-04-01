package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.ScaledShiftedTerrainHeightGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;

public class BiomeType {

    public static final List<BiomeType> biomeTypes = new ArrayList<>();
    public static final BiomeType ERROR = new BiomeTypeBuilder(0, 0, 0f).build();

//    public static final BiomeType GRASS_FIELD = new BiomeTypeBuilder(10, 20, 0.8f).surfaceGenerator(SurfaceGenerator.GRASS_FIELD).build();
//    public static final BiomeType FLOWER_FIELD = new BiomeTypeBuilder(20, 30, 0.2f).surfaceGenerator(SurfaceGenerator.GRASS_FIELD).build();
//
//    public static final BiomeType ICE_MOUNTAINS = new BiomeTypeBuilder(-50, -20, 1f)
//            .surfaceGenerator(SurfaceGenerator.ICE_MOUNTAINS)
//            .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build();
    
    public static final BiomeType FANCY = new BiomeTypeBuilder(-100, 100, 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.FANCY).build();


    public final int minTemperature;
    public final int maxTemperature;
    public final float weight;
    public final SurfaceGenerator surfaceGenerator;
    public final TerrainHeightGenerator terrainHeightGenerator;

    public BiomeType(BiomeTypeBuilder builder) {
        this.minTemperature = builder.minTemperature;
        this.maxTemperature = builder.maxTemperature;
        this.weight = builder.weight;
        this.surfaceGenerator = builder.surfaceGenerator;
        this.terrainHeightGenerator = builder.terrainHeightGenerator;

        biomeTypes.add(this);
    }

    public float getAverageTemperature() {
        return (minTemperature + maxTemperature)/2f;
    }

    private boolean isInTemperatureRange(float temperature) {
        return temperature >= this.minTemperature && temperature <= this.maxTemperature;
    }


    public static BiomeType getBiomeTypeWithChunkPos(World world, int chunkPosX) {
        return getBiomeWithTemperature(world.getTemperatureForChunkX(chunkPosX));
    }
    public static BiomeType getBiomeWithTemperature(float temperature) {

        List<BiomeType> potentialBiomes = new ArrayList<>();
        float totalWeight = 0;

        for (BiomeType biomeType : biomeTypes) {
            if (biomeType.isInTemperatureRange(temperature)) {
                potentialBiomes.add(biomeType);
                totalWeight += biomeType.weight;
            }
        }

        float randomWeight = (float) (Math.random() * totalWeight);

        float currentWeight = 0;
        for (BiomeType biomeType : potentialBiomes) {
            currentWeight += biomeType.weight;

            if (randomWeight < currentWeight) return biomeType;
        }

        //shouldn't happen
        return BiomeType.ERROR;
    }

    public static class BiomeTypeBuilder {

        public final int minTemperature;
        public final int maxTemperature;
        public final float weight;
        public SurfaceGenerator surfaceGenerator = SurfaceGenerator.STONE_ONLY;
        public TerrainHeightGenerator terrainHeightGenerator = TerrainHeightGenerator.FANCY;

        public BiomeTypeBuilder(int minTemperature, int maxTemperature, float weight) {
            this.minTemperature = minTemperature;
            this.maxTemperature = maxTemperature;
            this.weight = weight;
        }

        public BiomeTypeBuilder surfaceGenerator(SurfaceGenerator surfaceGenerator) {
            this.surfaceGenerator = surfaceGenerator;
            return this;
        }

        public BiomeTypeBuilder terrainHeightGenerator(TerrainHeightGenerator terrainHeightGenerator) {
            this.terrainHeightGenerator = terrainHeightGenerator;
            return this;
        }

        public BiomeType build() {
            return new BiomeType(this);
        }
    }
}