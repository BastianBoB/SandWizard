package com.basti_bob.sand_wizard.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class RandomObjectWithWeights {

    public static <T> T getObject(List<Pair<T, Float>> objectsWeightList, Random random) {
        return getObjectWithTotalProbability(objectsWeightList, random, 1);
    }
    public static <T> T getObjectWithTotalProbability(List<Pair<T, Float>> objectsWeightList, Random random, float totalProbability) {
        if(random.nextFloat() > totalProbability) return null;

        float totalWeight = 0;
        for (Pair<T, Float> pair : objectsWeightList) {
            totalWeight += pair.getRight();
        }

        float randomWeight = random.nextFloat() * totalWeight;

        float cumulativeWeight  = 0;
        for (Pair<T, Float> pair : objectsWeightList) {

            cumulativeWeight  += pair.getRight();

            if (randomWeight < cumulativeWeight ) {
               return pair.getLeft();
            }
        }

        throw new IllegalStateException("Random value exceeded cumulative weight");
    }

}
