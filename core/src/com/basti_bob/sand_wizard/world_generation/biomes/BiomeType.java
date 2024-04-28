package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.world_generation.surface_decoration.WorldDecorator;
import com.basti_bob.sand_wizard.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;

public class BiomeType {

    public static final List<BiomeType> allTypes = new ArrayList<>();
    public static final BiomeType ERROR = new BiomeTypeBuilder("error", 0, 0, 0f).build();

    public static final BiomeType SPIKY_MOUNTAIN_PEAKS = new BiomeTypeBuilder("spiky_mountain_peaks", -100, -80, 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.SPIKY_MOUNTAIN_PEAKS).build();

    public static final BiomeType ICE_MOUNTAINS = new BiomeTypeBuilder("ice_mountains", -80, -50, 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build();

    public static final BiomeType SNOW_MOUNTAINS = new BiomeTypeBuilder("snow_mountains", -50, 0, 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build();

    public static final BiomeType HILLS = new BiomeTypeBuilder("hills", 0, 30, 1f)
            .surfaceGenerator(SurfaceGenerator.GRASS_FIELD)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS)
            .surfaceDecorator(WorldDecorator.HILLS).build();

    public static final BiomeType FLOWER_FIELD = new BiomeTypeBuilder("flower_field", 0, 30, 0.1f)
            .surfaceGenerator(SurfaceGenerator.GRASS_FIELD)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS)
            .surfaceDecorator(WorldDecorator.FLOWER_FIELD).build();

    public static final BiomeType DESERT = new BiomeTypeBuilder("desert", 30, 100, 1f)
            .surfaceGenerator(SurfaceGenerator.DESERT)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS).build();


    public final String name;
    public final int minTemperature;
    public final int maxTemperature;
    public final float weight;
    public final SurfaceGenerator surfaceGenerator;
    public final TerrainHeightGenerator terrainHeightGenerator;
    public final WorldDecorator worldDecorator;

    public BiomeType(BiomeTypeBuilder builder) {
        this.name = builder.name;
        this.minTemperature = builder.minTemperature;
        this.maxTemperature = builder.maxTemperature;
        this.weight = builder.weight;
        this.surfaceGenerator = builder.surfaceGenerator;
        this.terrainHeightGenerator = builder.terrainHeightGenerator;
        this.worldDecorator = builder.worldDecorator;

        allTypes.add(this);
    }

    public float getAverageTemperature() {
        return (minTemperature + maxTemperature) / 2f;
    }

    public boolean isInTemperatureRange(float temperature) {
        return temperature >= this.minTemperature && temperature <= this.maxTemperature;
    }

    public static class BiomeTypeBuilder {

        public final String name;
        public final int minTemperature;
        public final int maxTemperature;
        public final float weight;
        public SurfaceGenerator surfaceGenerator = SurfaceGenerator.STONE_ONLY;
        public TerrainHeightGenerator terrainHeightGenerator = TerrainHeightGenerator.FLAT;
        public WorldDecorator worldDecorator = WorldDecorator.NOTHING;

        public BiomeTypeBuilder(String name, int minTemperature, int maxTemperature, float weight) {
            this.name = name;
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

        public BiomeTypeBuilder surfaceDecorator(WorldDecorator worldDecorator) {
            this.worldDecorator = worldDecorator;
            return this;
        }

        public BiomeType build() {
            return new BiomeType(this);
        }
    }
}
