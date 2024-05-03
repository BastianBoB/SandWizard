package com.basti_bob.sand_wizard.world_generation.cave_generation;

import com.basti_bob.sand_wizard.util.noise.LayeredNoise;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.noise.Noise;
import com.basti_bob.sand_wizard.world.World;

public class CaveGenerator {

    public static final CaveGenerator BASE = new CaveGenerator();

    private final Noise cheeseNoise = new LayeredNoise(8, 1f, 0.001f, 0.6f, 1.8f);
    private final Noise spaghettiNoise = new LayeredNoise(10, 1f, 0.004f, 0.5f, 2f);
    private final float cheeseNoiseThreshold = 0.35f;
    private final float spaghettiNoiseThreshold = 0.3f;

    private final float verticalSquishFactor = 1.8f;
    private final float maxTerrainHeightOffset = 500;

    public CaveGenerator() {
    }

    public boolean isCave(World world, int cellX, int cellY, float terrainHeight) {
        float terrainHeightOffset = terrainHeight - cellY;

        if (terrainHeightOffset < 0) return false;

        float evalY = cellY * verticalSquishFactor;


        float factor = terrainHeightOffset > maxTerrainHeightOffset ? spaghettiNoiseThreshold
                : MathUtil.clampedMap(terrainHeightOffset, 0, maxTerrainHeightOffset, 0.05f, spaghettiNoiseThreshold);

        if (Math.abs(spaghettiNoise.eval(cellX, evalY)) < factor)
            return true;

        if (terrainHeight - cellY < maxTerrainHeightOffset) return false;

        return cheeseNoise.eval(cellX, evalY) > cheeseNoiseThreshold;
    }
}
