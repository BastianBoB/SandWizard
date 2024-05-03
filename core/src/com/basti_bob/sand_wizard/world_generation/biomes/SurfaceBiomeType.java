package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world_generation.surface_decoration.WorldDecorator;
import com.basti_bob.sand_wizard.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;

public class SurfaceBiomeType extends BiomeType {

    public static final List<SurfaceBiomeType> ALL_TYPES = new ArrayList<>();

    public static final SurfaceBiomeType SPIKY_MOUNTAIN_PEAKS = new Builder("spiky_mountain_peaks", new IntRange(-100, -80), 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.SPIKY_MOUNTAIN_PEAKS).build();

    public static final SurfaceBiomeType ICE_MOUNTAINS = new Builder("ice_mountains", new IntRange(-80, -50), 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build();

    public static final SurfaceBiomeType SNOW_MOUNTAINS = new Builder("snow_mountains", new IntRange(-50, 0), 1f)
            .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
            .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build();

    public static final SurfaceBiomeType HILLS = new Builder("hills", new IntRange(0, 30), 1f)
            .surfaceGenerator(SurfaceGenerator.GRASS_FIELD)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS)
            .surfaceDecorator(WorldDecorator.HILLS).build();

    public static final SurfaceBiomeType FLOWER_FIELD = new Builder("flower_field", new IntRange(0, 30), 0.1f)
            .surfaceGenerator(SurfaceGenerator.GRASS_FIELD)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS)
            .surfaceDecorator(WorldDecorator.FLOWER_FIELD).build();

    public static final SurfaceBiomeType DESERT = new Builder("desert", new IntRange(30, 100), 1f)
            .surfaceGenerator(SurfaceGenerator.DESERT)
            .terrainHeightGenerator(TerrainHeightGenerator.HILLS).build();


    public final SurfaceGenerator surfaceGenerator;
    public final TerrainHeightGenerator terrainHeightGenerator;
    public final WorldDecorator worldDecorator;

    public SurfaceBiomeType(Builder builder) {
        super(builder.name, builder.noiseRange, builder.weight);
        this.surfaceGenerator = builder.surfaceGenerator;
        this.terrainHeightGenerator = builder.terrainHeightGenerator;
        this.worldDecorator = builder.worldDecorator;

        ALL_TYPES.add(this);
    }

    public static class Builder {

        public final String name;
        public final IntRange noiseRange;
        public final float weight;
        public SurfaceGenerator surfaceGenerator = SurfaceGenerator.STONE_ONLY;
        public TerrainHeightGenerator terrainHeightGenerator = TerrainHeightGenerator.FLAT;
        public WorldDecorator worldDecorator = WorldDecorator.NOTHING;

        public Builder(String name, IntRange noiseRange, float weight) {
            this.name = name;
            this.noiseRange = noiseRange;
            this.weight = weight;
        }

        public Builder surfaceGenerator(SurfaceGenerator surfaceGenerator) {
            this.surfaceGenerator = surfaceGenerator;
            return this;
        }

        public Builder terrainHeightGenerator(TerrainHeightGenerator terrainHeightGenerator) {
            this.terrainHeightGenerator = terrainHeightGenerator;
            return this;
        }

        public Builder surfaceDecorator(WorldDecorator worldDecorator) {
            this.worldDecorator = worldDecorator;
            return this;
        }

        public SurfaceBiomeType build() {
            return new SurfaceBiomeType(this);
        }
    }
}
