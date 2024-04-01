package com.basti_bob.sand_wizard.world_generation.terrain_height_generation;

import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world.World;

import java.util.ArrayList;
import java.util.Random;

public abstract class TerrainHeightGenerator {

    public static final Random random = new Random(0);

    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(random.nextLong());

    public abstract float getTerrainHeight(World world, int cellPosX);

    public static final TerrainHeightGenerator FANCY = WeightedMultiTerrainHeightGenerator.builder()
            .addGeneratorAndWeight(new ScaledShiftedTerrainHeightGenerator(NORMAL.PLATEAU(0.001f), 300f, 0), 5f)
            .addGeneratorAndWeight(new ScaledShiftedTerrainHeightGenerator(NORMAL.EROSION(0.002f), 500f, 0), 1f)
            .addGeneratorAndWeight(new ScaledShiftedTerrainHeightGenerator(NORMAL.PEAKS_AND_VALLEYS(0.01f), 50f, 0), 1f).build();

    public static TerrainHeightGenerator FLAT(int x) {
        return new TerrainHeightGenerator() {
            @Override
            public float getTerrainHeight(World world, int cellPosX) {
                return x;
            }
        };
    }

    public static class NORMAL {

        public static TerrainHeightGenerator PLATEAU(float frequency) {
            return SplinePointsTerrainHeightGenerator.builder(frequency)
                    .addSpline(-1f, -1f)
                    .addSpline(-0.5f, -1f)
                    .addSpline(-0.4f, 0f)
                    .addSpline(-0.1f, 0f)
                    .addSpline(0f, 0.8f)
                    .addSpline(1f, 1f)
                    .build();
        }

        public static TerrainHeightGenerator EROSION(float frequency) {
            return SplinePointsTerrainHeightGenerator.builder(frequency)
                    .addSpline(-1f, 1f)
                    .addSpline(-0.3f, 0f)
                    .addSpline(0f, -1f)
                    .addSpline(0.7f, -1f)
                    .addSpline(0.75f, -0.5f)
                    .addSpline(0.85f, -0.5f)
                    .addSpline(0.9f, -1f)
                    .addSpline(1f, -1f)
                    .build();
        }

        public static TerrainHeightGenerator PEAKS_AND_VALLEYS(float frequency) {
            return SplinePointsTerrainHeightGenerator.builder(frequency)
                    .addSpline(-1f, -1f)
                    .addSpline(0f, -0.3f)
                    .addSpline(0.5f, 1f)
                    .addSpline(1f, 1f)
                    .build();
        }

        public static TerrainHeightGenerator SPIKY(float frequency) {
            return SplinePointsTerrainHeightGenerator.builder(frequency)
                    .addSpline(-1f, -1f)
                    .addSpline(-0.5f, -1f)
                    .addSpline(-0.4f, -0.6f) // Adjusted y-value to introduce spikes
                    .addSpline(-0.1f, 0.2f)  // Adjusted y-value to introduce spikes
                    .addSpline(0f, 0.8f)
                    .addSpline(1f, 1f)
                    .build();
        }
    }

}
