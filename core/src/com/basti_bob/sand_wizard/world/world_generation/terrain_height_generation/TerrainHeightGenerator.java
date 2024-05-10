package com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation;

import com.basti_bob.sand_wizard.util.noise.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.generators.SpikyTerrainHeightGenerator;

import java.util.Random;

public abstract class TerrainHeightGenerator {

    public static final Random random = new Random(0);

    public static final TerrainHeightGenerator FLAT = FLAT(0);

    public static final TerrainHeightGenerator SPIKY_MOUNTAIN_PEAKS = ScaledShiftedTerrainHeightGenerator.normalToRange(
            WeightedMultiTerrainHeightGenerator.builder()
                    .addGeneratorAndWeight(NORMAL.SPIKY(0.01f, 32), 5f)
                    .addGeneratorAndWeight(NORMAL.EROSION(0.002f), 0.1f).build(),
            1500, 2000);

    public static final TerrainHeightGenerator MOUNTAINS = ScaledShiftedTerrainHeightGenerator.normalToRange(
            WeightedMultiTerrainHeightGenerator.builder()
                    .addGeneratorAndWeight(NORMAL.SPIKY(0.001f, 32), 1f)
                    .addGeneratorAndWeight(NORMAL.PLATEAU(0.001f), 1f)
                    .addGeneratorAndWeight(NORMAL.EROSION(0.002f), 3f)
                    .addGeneratorAndWeight(NORMAL.PEAKS_AND_VALLEYS(0.01f), 0.1f).build(),
            700, 1500);

    public static final TerrainHeightGenerator HILLS = ScaledShiftedTerrainHeightGenerator.normalToRange(
            WeightedMultiTerrainHeightGenerator.builder()
                    .addGeneratorAndWeight(NORMAL.PLATEAU(0.001f), 1f)
                    .addGeneratorAndWeight(NORMAL.EROSION(0.0005f), 1f)
                    .addGeneratorAndWeight(NORMAL.PEAKS_AND_VALLEYS(0.01f), 0.5f)
                    .addGeneratorAndWeight(NORMAL.PEAKS_AND_VALLEYS(0.002f), 5f).build(),
            100, 700);

    public static final TerrainHeightGenerator DESERT = ScaledShiftedTerrainHeightGenerator.normalToRange(
            WeightedMultiTerrainHeightGenerator.builder()
                    .addGeneratorAndWeight(NORMAL.PLATEAU(0.001f), 1f)
                    .addGeneratorAndWeight(NORMAL.EROSION(0.0001f), 1f)
                    .addGeneratorAndWeight(NORMAL.PEAKS_AND_VALLEYS(0.01f), 0.1f).build(),
            100, 300);


    public static TerrainHeightGenerator FLAT(int y) {
        return new TerrainHeightGenerator() {
            @Override
            public float getTerrainHeight(World world, int cellPosX) {
                return y;
            }
        };
    }

    public static class NORMAL {

        public static TerrainHeightGenerator NORMAL(float frequency) {
            return new TerrainHeightGenerator() {
                @Override
                public float getTerrainHeight(World world, int cellPosX) {
                    return (float) openSimplexNoise.eval(cellPosX * frequency, 0f, 0f);
                }
            };
        }

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

        public static TerrainHeightGenerator SPIKY(float frequency, int stepSize) {
            return new SpikyTerrainHeightGenerator(NORMAL(frequency), stepSize);
        }
    }

    public final OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(random.nextLong());

    public abstract float getTerrainHeight(World world, int cellPosX);
}