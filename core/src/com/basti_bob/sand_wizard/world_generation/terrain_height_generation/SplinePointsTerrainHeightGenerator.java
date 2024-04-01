package com.basti_bob.sand_wizard.world_generation.terrain_height_generation;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.Point;

import java.awt.geom.Arc2D;
import java.util.*;

public class SplinePointsTerrainHeightGenerator extends TerrainHeightGenerator {

    private final float frequency;
    private final List<Vector2> splinePoints;

    public SplinePointsTerrainHeightGenerator(SplinePointsTerrainHeightGeneratorBuilder builder) {
        this.frequency = builder.frequency;
        this.splinePoints = builder.splinePoints;
        splinePoints.sort(Comparator.comparingDouble(v -> v.x));
    }

    public static SplinePointsTerrainHeightGeneratorBuilder builder(float frequency) {
        return new SplinePointsTerrainHeightGeneratorBuilder(frequency);
    }

    @Override
    public float getTerrainHeight(World world, int cellPosX) {
        float noise = (float) this.openSimplexNoise.eval(cellPosX * frequency, 0, 0);

        Vector2 point1 = splinePoints.get(0);
        Vector2 point2 = splinePoints.get(1);

        for (int i = 1; i < splinePoints.size() - 1; i++) {
            Vector2 currentPoint = splinePoints.get(i);
            Vector2 nextPoint = splinePoints.get(i + 1);
            if (currentPoint.x <= noise && noise <= nextPoint.x) {
                point1 = currentPoint;
                point2 = nextPoint;
                break;
            }
        }

        float t = (noise - point1.x) / (point2.x - point1.x);
        float interpolatedHeight = point1.y + t * (point2.y - point1.y);

        return interpolatedHeight;
    }

    public static class SplinePointsTerrainHeightGeneratorBuilder {

        protected final float frequency;
        protected List<Vector2> splinePoints;

        public SplinePointsTerrainHeightGeneratorBuilder(float frequency) {
            this.splinePoints = new ArrayList<>();
            this.frequency = frequency;
        }

        public SplinePointsTerrainHeightGeneratorBuilder addSpline(float x, float y) {
            this.splinePoints.add(new Vector2(x, y));
            return this;
        }

        public SplinePointsTerrainHeightGenerator build() {
            return new SplinePointsTerrainHeightGenerator(this);
        }
    }
}