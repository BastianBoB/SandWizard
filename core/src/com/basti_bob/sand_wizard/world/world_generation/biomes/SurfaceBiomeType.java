package com.basti_bob.sand_wizard.world.world_generation.biomes;

import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;
import com.basti_bob.sand_wizard.world.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;

public class SurfaceBiomeType extends BiomeType {

    public final SurfaceGenerator surfaceGenerator;
    public final TerrainHeightGenerator terrainHeightGenerator;
    public final List<WorldDecorator> worldDecorators;

    public SurfaceBiomeType(Builder builder) {
        super(builder.noiseRange, builder.weight);
        this.surfaceGenerator = builder.surfaceGenerator;
        this.terrainHeightGenerator = builder.terrainHeightGenerator;
        this.worldDecorators = builder.worldDecorators;
    }

    public static class Builder {

        public final FloatRange noiseRange;
        public final float weight;
        public SurfaceGenerator surfaceGenerator = SurfaceGenerator.STONE_ONLY;
        public TerrainHeightGenerator terrainHeightGenerator = TerrainHeightGenerator.FLAT;
        public List<WorldDecorator> worldDecorators = new ArrayList<>();

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

        public Builder addSurfaceDecorator(WorldDecorator worldDecorator) {
            this.worldDecorators.add(worldDecorator);
            return this;
        }

        public SurfaceBiomeType build() {
            return new SurfaceBiomeType(this);
        }
    }
}
