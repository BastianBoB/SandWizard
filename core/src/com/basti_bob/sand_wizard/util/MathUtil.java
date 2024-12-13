package com.basti_bob.sand_wizard.util;

public class MathUtil {

    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = 2 * PI;

    public static boolean isPointInRect(float pointX, float pointY, float rectX, float rectY, float rectWidth, float rectHeight) {
        return pointX >= rectX && pointX <= rectX + rectWidth && pointY >= rectY && pointY <= rectY + rectHeight;
    }

    public static float lerp(float v0, float v1, float t) {
        return v0 + t * (v1 - v0);
    }

    public static float map(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
        return outputMin + (value - inputMin) * (outputMax - outputMin) / (inputMax - inputMin);
    }

    public static float clampedMap(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
        return clamp(map(value, inputMin, inputMax, outputMin, outputMax), outputMin, outputMax);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(distSqr(x1, y1, x2, y2));
    }

    public static float distSqr(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        return dx * dx + dy * dy;
    }
}
