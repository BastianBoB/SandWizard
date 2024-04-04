package com.basti_bob.sand_wizard.util;

public class MathUtil {

    public static float lerp(float v0, float v1, float t) {
        return v0 + t * (v1 - v0);
    }
}
