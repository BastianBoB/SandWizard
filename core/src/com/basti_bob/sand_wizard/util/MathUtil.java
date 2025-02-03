package com.basti_bob.sand_wizard.util;

import com.basti_bob.sand_wizard.world.coordinates.CellPos;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {

    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = 2 * PI;

    public static boolean isPointInRect(float x, float y, float rectX, float rectY, float width, float height) {
        return x >= rectX && x <= rectX + width && y >= rectY && y <= rectY + height;
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

    public static boolean isPointInEllipse(float x, float y, float centerX, float centerY, float width, float height) {
        float dx = centerX - x;
        float dy = centerY - y;

        return (dx * dx) / (width / 2 * width / 2) + (dy * dy) / (height / 2 * height / 2) <= 1;
    }

    public static float distSqr(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        return dx * dx + dy * dy;
    }

    public static List<CellPos> pathBetweenPoints(float x1, float y1, float x2, float y2, int thickness) {
        List<CellPos> points = new ArrayList<>();

        int xDistance = (int) Math.abs(x2 - x1);
        int yDistance = (int) Math.abs(y2 - y1);

        boolean positiveX = (x2 - x1) > 0;
        boolean positiveY = (y2 - y1) > 0;

        int steps = Math.max(xDistance, yDistance);

        int thickOffMin = -thickness / 2;
        int thickOffMax = (int) Math.ceil(thickness / 2f);

        if (xDistance > yDistance) {
            float slope = Math.abs((y2 - y1) / (x2 - x1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i : -i;
                float y = positiveY ? i * slope : -i * slope;

                for (int k = thickOffMin; k < thickOffMax; k++)
                    points.add(new CellPos((int) (x1 + x), (int) (y1 + y) + k));

            }
        } else {
            float slope = Math.abs((x2 - x1) / (y2 - y1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i * slope : -i * slope;
                float y = positiveY ? i : -i;

                for (int k = thickOffMin; k < thickOffMax; k++)
                    points.add(new CellPos((int) (x1 + x) + k, (int) (y1 + y)));
            }
        }

        return points;
    }
}
