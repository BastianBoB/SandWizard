package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.ScaledShiftedTerrainHeightGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeType {

    public static final List<BiomeType> allTypes = new ArrayList<>();
    public static final BiomeType ERROR = new BiomeTypeBuilder(0, 0, 0f).build();

//    public static final BiomeType GRASS_FIELD = new BiomeTypeBuilder(10, 20, 0.8f).surfaceGenerator(SurfaceGenerator.GRASS_FIELD).build();
//    public static final BiomeType FLOWER_FIELD = new BiomeTypeBuilder(20, 30, 0.2f).surfaceGenerator(SurfaceGenerator.GRASS_FIELD).build();
//
    public static final BiomeType ICE_MOUNTAINS = new BiomeTypeBuilder(-100, -50, 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build();

    public static final BiomeType SNOW_MOUNTAINS = new BiomeTypeBuilder(-50, 0, 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build();
    
    public static final BiomeType HILLS = new BiomeTypeBuilder(0, 30, 1f)
            .surfaceGenerator(SurfaceGenerator.GRASS_FIELD)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS).build();

    public static final BiomeType DESERT = new BiomeTypeBuilder(30, 100, 1f)
            .surfaceGenerator(SurfaceGenerator.DESERT)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS).build();


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

        allTypes.add(this);
    }

    public float getAverageTemperature() {
        return (minTemperature + maxTemperature)/2f;
    }

    public boolean isInTemperatureRange(float temperature) {
        return temperature >= this.minTemperature && temperature <= this.maxTemperature;
    }

    public static class BiomeTypeBuilder {

        public final int minTemperature;
        public final int maxTemperature;
        public final float weight;
        public SurfaceGenerator surfaceGenerator = SurfaceGenerator.STONE_ONLY;
        public TerrainHeightGenerator terrainHeightGenerator = TerrainHeightGenerator.HILLS;

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
