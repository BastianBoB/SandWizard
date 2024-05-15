package com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation;

import com.basti_bob.sand_wizard.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class WeightedMultiTerrainHeightGenerator extends TerrainHeightGenerator {

    private final List<Pair<TerrainHeightGenerator, Float>> terrainHeightGeneratorsAndWeights;
    private final float totalWeight;

    public WeightedMultiTerrainHeightGenerator(WeightedMultiTerrainHeightGeneratorBuilder builder) {
        super(getMaxHeight(builder.terrainHeightGeneratorsAndWeights));

        this.terrainHeightGeneratorsAndWeights = builder.terrainHeightGeneratorsAndWeights;

        this.totalWeight = (float) terrainHeightGeneratorsAndWeights.stream().mapToDouble(Pair::getValue).sum();
    }

    private static float getMaxHeight(List<Pair<TerrainHeightGenerator, Float>> terrainHeightGeneratorsAndWeights) {
        int max = 0;
        for (Pair<TerrainHeightGenerator, Float> pair : terrainHeightGeneratorsAndWeights) {
            max += pair.getLeft().getMaxTerrainHeight() * pair.getRight();
        }
        return max;
    }

    public static WeightedMultiTerrainHeightGeneratorBuilder builder() {
        return new WeightedMultiTerrainHeightGeneratorBuilder();
    }

    @Override
    public float getTerrainHeight(World world, int cellPosX) {
        float terrainHeight = 0;

        for (Pair<TerrainHeightGenerator, Float> pair : terrainHeightGeneratorsAndWeights) {
            TerrainHeightGenerator generator = pair.getKey();
            float weight = pair.getValue();

            terrainHeight += generator.getTerrainHeight(world, cellPosX) * weight;
        }

        return terrainHeight / totalWeight;
    }

    public static class WeightedMultiTerrainHeightGeneratorBuilder {

        protected List<Pair<TerrainHeightGenerator, Float>> terrainHeightGeneratorsAndWeights;

        public WeightedMultiTerrainHeightGeneratorBuilder() {
            this.terrainHeightGeneratorsAndWeights = new ArrayList<>();
        }

        public WeightedMultiTerrainHeightGeneratorBuilder addGeneratorAndWeight(TerrainHeightGenerator terrainHeightGenerator, float weight) {
            this.terrainHeightGeneratorsAndWeights.add(Pair.of(terrainHeightGenerator, weight));
            return this;
        }

        public WeightedMultiTerrainHeightGenerator build() {
            return new WeightedMultiTerrainHeightGenerator(this);
        }
    }
}
