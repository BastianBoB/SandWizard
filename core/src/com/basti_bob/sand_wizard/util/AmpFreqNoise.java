package com.basti_bob.sand_wizard.util;

import java.util.Random;

public class AmpFreqNoise implements Noise {

    private static final Random random = new Random();

    private final OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
    private final float frequency, amplitude;

    public AmpFreqNoise(float frequency, float amplitude) {
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    @Override
    public float eval(float x) {
        return noise.eval(x * frequency) * amplitude;
    }

    @Override
    public float eval(float x, float y) {
        return noise.eval(x * frequency, y * frequency) * amplitude;
    }

    @Override
    public float eval(float x, float y, float z) {
        return noise.eval(x * frequency, y * frequency, z * frequency) * amplitude;
    }
}
