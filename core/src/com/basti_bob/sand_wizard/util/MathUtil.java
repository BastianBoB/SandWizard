package com.basti_bob.sand_wizard.util;

public class MathUtil {

    public static float lerp(float v0, float v1, float t) {
        return v0 + t * (v1 - v0);
    }

    public static float map(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
        return outputMin + (value - inputMin) * (outputMax - outputMin) / (inputMax - inputMin);
    }

    public static float clampedMap(float value, float inputMin, float inputMax, float outputMin, float outputMax){
        return clamp(map(value, inputMin, inputMax, outputMin, outputMax), outputMin, outputMax);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
