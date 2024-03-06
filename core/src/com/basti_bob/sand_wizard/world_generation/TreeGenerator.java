package com.basti_bob.sand_wizard.world_generation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.world.Chunk;

import java.util.ArrayList;
import java.util.List;

public class TreeGenerator {

    public static final TreeGenerator baseTree = new TreeGeneratorBuilder("FF+[+F-F-F]-[-F+F+F]").build();

    private final String lSystem;
    private final int iterations;
    private final float startLength;
    private final float lengthMultiplier;

    public TreeGenerator(String lSystem, int iterations, float startLength, float lengthMultiplier) {
        this.lSystem = lSystem;
        this.iterations = iterations;
        this.lengthMultiplier = lengthMultiplier;
        this.startLength = startLength;
    }

    public void placeTree(Chunk chunk, float inChunkX, float inChunkY) {

    }

    private static String generateLSystem(String rule, int iterations) {
        String lSystem = rule;

        for (int i = 0; i < iterations; i++) {

            StringBuilder next = new StringBuilder();

            for (char c : lSystem.toCharArray()) {
                if (c == 'F')
                    next.append(rule);
                else
                    next.append(c);
            }

            lSystem = next.toString();
        }

        return lSystem;
    }

    private static class TreeGeneratorBuilder {

        private final String lSystem;
        private int iterations = 3;
        private float lengthMultiplier = 0.7f;
        private float startLength = 5f;

        private TreeGeneratorBuilder(String rule) {
            this.lSystem = generateLSystem(rule, iterations);
        }

        private TreeGeneratorBuilder startLength(float startLength) {
            this.startLength = startLength;
            return this;
        }

        private TreeGeneratorBuilder lengthMultiplier(float lengthMultiplier) {
            this.lengthMultiplier = lengthMultiplier;
            return this;
        }

        private TreeGeneratorBuilder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        private TreeGenerator build() {
            return new TreeGenerator(lSystem, iterations, startLength, lengthMultiplier);
        }
    }

}
