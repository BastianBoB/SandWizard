package com.basti_bob.sand_wizard.world.world_generation.biomes;

import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;
import com.basti_bob.sand_wizard.world.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

public class SurfaceBiomeType extends BiomeType {

    public final SurfaceGenerator surfaceGenerator;
    public final TerrainHeightGenerator terrainHeightGenerator;
    public final WorldDecorator worldDecorator;

    public SurfaceBiomeType(Builder builder) {
        super(builder.noiseRange, builder.weight);
        this.surfaceGenerator = builder.surfaceGenerator;
        this.terrainHeightGenerator = builder.terrainHeightGenerator;
        this.worldDecorator = builder.worldDecorator;
    }

    public static class Builder {

        public final FloatRange noiseRange;
        public final float weight;
        public SurfaceGenerator surfaceGenerator = SurfaceGenerator.STONE_ONLY;
        public TerrainHeightGenerator terrainHeightGenerator = TerrainHeightGenerator.FLAT;
        public WorldDecorator worldDecorator = WorldDecorator.NOTHING;

        public Builder(FloatRange noiseRange, float weight) {
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
