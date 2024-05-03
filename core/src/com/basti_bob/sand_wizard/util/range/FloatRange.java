package com.basti_bob.sand_wizard.util.range;

import java.util.Random;

public class FloatRange {

    public final float min, max;

    public FloatRange(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float getRandom(Random random) {
        return random.nextFloat(min, max);
    }
}
