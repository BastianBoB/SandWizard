package com.basti_bob.sand_wizard.util;

public class LayeredNoise implements Noise {

    private final Noise[] noises;

    public LayeredNoise(int numOctaves, float startAmp, float startFreq, float ampMultiplier, float freqMultiplier) {
        super();

        float amplitude = startAmp;
        float frequency = startFreq;

        this.noises = new Noise[numOctaves];

        for (int i = 0; i < numOctaves; i++) {
            noises[i] = new AmpFreqNoise(frequency, amplitude);

            amplitude *= ampMultiplier;
            frequency *= freqMultiplier;
        }
    }


    @Override
    public float eval(float x) {
        return eval(x, 0, 0);
    }

    public float eval(float x, float y) {
        return eval(x, y, 0);
    }

    @Override
    public float eval(float x, float y, float z) {
        float noiseValue = 0;

        for (Noise noise : noises) {
            noiseValue += noise.eval(x, y, z);
        }

        return noiseValue;
    }
}
