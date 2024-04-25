package com.basti_bob.sand_wizard.util;

import java.util.Random;

public class LayeredNoise {


    private final Random random = new Random();
    private final int numOctaves;
    public float startAmp, startFreq;
    private final float ampMultiplier, freqMultiplier;
    private final OpenSimplexNoise[] noises;

    public LayeredNoise(int numOctaves, float startAmp, float startFreq, float ampMultiplier, float freqMultiplier) {
        this.numOctaves = numOctaves;
        this.startAmp = startAmp;
        this.startFreq = startFreq;
        this.ampMultiplier = ampMultiplier;
        this.freqMultiplier = freqMultiplier;

        this.noises = new OpenSimplexNoise[numOctaves];
        for (int i = 0; i < numOctaves; i++) {
            noises[i] = new OpenSimplexNoise(random.nextLong());
        }
    }

    public float eval(float x, float y) {
        float noiseValue = 0;
        float amplitude = startAmp;
        float frequency = startFreq;
        for (OpenSimplexNoise noise : noises) {
            noiseValue += amplitude * noise.eval(x * frequency, y * frequency, 0);

            amplitude *= ampMultiplier;
            frequency *= freqMultiplier;
        }

        return noiseValue;
    }
}
