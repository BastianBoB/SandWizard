package com.basti_bob.sand_wizard.world_generation.cave_generation;

import com.basti_bob.sand_wizard.util.LayeredNoise;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.World;

import java.util.Random;

public class CaveGenerator {

    public static final CaveGenerator BASE = new CaveGenerator();

    private final Random random = new Random(0);
    private final LayeredNoise cheeseNoise = new LayeredNoise(8, 1f, 0.002f, 0.6f, 1.5f);
    private final LayeredNoise spaghettiNoise = new LayeredNoise(10, 1f, 0.003f, 0.5f, 1.7f);
    private final float verticalSquishFactor = 2f;

    private final float maxTerrainHeightOffset = 500;

    public CaveGenerator() {
    }

    public boolean isCave(World world, int cellX, int cellY, float terrainHeight) {
        //return false;

        float evalY = cellY * verticalSquishFactor;

        float factor = MathUtil.clampedMap(terrainHeight - cellY, 0, maxTerrainHeightOffset, 0.05f, 0.2f);

        if(Math.abs(spaghettiNoise.eval(cellX, evalY)) < factor)
            return true;

        return false;

//        if (terrainHeight - cellY < maxTerrainHeightOffset) return false;
//
//        return cheeseNoise.eval(cellX, evalY) > 0.3;
    }
}
