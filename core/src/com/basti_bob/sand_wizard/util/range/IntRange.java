package com.basti_bob.sand_wizard.util.range;

import java.util.Random;

public class IntRange {

    public final int min, max;

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getRandom(Random random) {
        return random.nextInt(min, max);
    }
}
