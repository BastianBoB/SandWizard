package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.util.range.IntRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeType {

    public final String name;
    public final IntRange noiseRange;
    public final float weight;

    public BiomeType(String name, IntRange noiseRange, float weight) {
        this.name = name;
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

        if (potentialBiomes.size() == 0) return null;
        if (potentialBiomes.size() == 1) return potentialBiomes.get(0);

        random.setSeed((long) noiseValue * 10000000);
        float randomWeight = random.nextFloat() * totalWeight;
        float currentWeight = 0;

        for (T biomeType : potentialBiomes) {
            currentWeight += biomeType.weight;
            if (randomWeight < currentWeight) return biomeType;
        }

        return null;
    }
}
