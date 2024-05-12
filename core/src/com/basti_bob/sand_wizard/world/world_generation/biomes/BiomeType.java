package com.basti_bob.sand_wizard.world.world_generation.biomes;

import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;
import com.basti_bob.sand_wizard.world.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeType {

    public static final Registry<BiomeType> REGISTRY = new Registry<>("biome_type");

    public static class SURFACE {

        public static final Registry<SurfaceBiomeType> REGISTRY = new Registry<>("surface", BiomeType.REGISTRY);

        public static final SurfaceBiomeType SPIKY_MOUNTAIN_PEAKS = REGISTRY.register("spiky_mountain_peaks", new SurfaceBiomeType.Builder(new FloatRange(-1, -0.8f), 1f)
                .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
                .terrainHeightGenerator(TerrainHeightGenerator.SPIKY_MOUNTAIN_PEAKS).build());

        public static final SurfaceBiomeType ICE_MOUNTAINS = REGISTRY.register("ice_mountains", new SurfaceBiomeType.Builder(new FloatRange(-0.8f, -0.5f), 1f)
                .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
                .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build());

        public static final SurfaceBiomeType SNOW_MOUNTAINS = REGISTRY.register("snow_mountains", new SurfaceBiomeType.Builder(new FloatRange(-0.5f, 0), 1f)
                .surfaceGenerator(SurfaceGenerator.SNOW_AND_ICE)
                .terrainHeightGenerator(TerrainHeightGenerator.MOUNTAINS).build());

        public static final SurfaceBiomeType HILLS = REGISTRY.register("hills", new SurfaceBiomeType.Builder(new FloatRange(0, 0.3f), 1f)
                .surfaceGenerator(SurfaceGenerator.GRASS_FIELD)
                .terrainHeightGenerator(TerrainHeightGenerator.HILLS)
                .surfaceDecorator(WorldDecorator.SURFACE.HILLS).build());

        public static final SurfaceBiomeType FLOWER_FIELD = REGISTRY.register("flower_field", new SurfaceBiomeType.Builder( new FloatRange(0, 0.3f), 0.1f)
                .surfaceGenerator(SurfaceGenerator.GRASS_FIELD)
                .terrainHeightGenerator(TerrainHeightGenerator.HILLS)
                .surfaceDecorator(WorldDecorator.SURFACE.FLOWER_FIELD).build());

        public static final SurfaceBiomeType DESERT = REGISTRY.register("desert", new SurfaceBiomeType.Builder(new FloatRange(0.3f, 1f), 1f)
                .surfaceGenerator(SurfaceGenerator.DESERT)
                .terrainHeightGenerator(TerrainHeightGenerator.HILLS).build());
    }

    public static class CAVE {

        public static final Registry<CaveBiomeType> REGISTRY = new Registry<>("cave", BiomeType.REGISTRY);

        public static final CaveBiomeType BASE = REGISTRY.register("base", new CaveBiomeType.Builder(new FloatRange(-1, 0.2f), 1f).build());

        public static final CaveBiomeType STALACTITES = REGISTRY.register("stalactites", new CaveBiomeType.Builder(new FloatRange(0.2f, 1), 1f)
                .caveTopDecorator(WorldDecorator.CAVE.STALACTITES)
                .caveBottomDecorator(WorldDecorator.CAVE.STALAGMITES).build());

    }

    public final FloatRange noiseRange;
    public final float weight;

    public BiomeType(FloatRange noiseRange, float weight) {
        this.noiseRange = noiseRange;
        this.weight = weight;
    }

    public boolean isInNoiseRange(float temperature) {
        return temperature >= this.noiseRange.min && temperature < this.noiseRange.max;
    }

    public static <T extends BiomeType> T calculateBiomeWithNoiseValue(List<T> biomeTypes, Random random, float noiseValue) {

        List<T> potentialBiomes = new ArrayList<>();
        float totalWeight = 0;

        for (T biomeType : biomeTypes) {
            if (biomeType.isInNoiseRange(noiseValue)) {
                potentialBiomes.add(biomeType);
                totalWeight += biomeType.weight;
            }
        }

        if (potentialBiomes.size() == 0) throw new RuntimeException("no biome in noise range");
        if (potentialBiomes.size() == 1) return potentialBiomes.get(0);

        random.setSeed((long) noiseValue * 10000000);
        float randomWeight = random.nextFloat() * totalWeight;
        float currentWeight = 0;

        for (T biomeType : potentialBiomes) {
            currentWeight += biomeType.weight;
            if (randomWeight < currentWeight) return biomeType;
        }

        throw new RuntimeException("no biome for weight");
    }
}
