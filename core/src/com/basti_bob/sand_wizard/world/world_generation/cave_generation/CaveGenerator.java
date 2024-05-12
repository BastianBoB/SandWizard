package com.basti_bob.sand_wizard.world.world_generation.cave_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.util.noise.LayeredNoise;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.noise.Noise;
import com.basti_bob.sand_wizard.world.World;

public class CaveGenerator {

    public static final Registry<CaveGenerator> REGISTRY = new Registry<>("cave_generator");

    public static final CaveGenerator BASE = REGISTRY.register("base", new CaveGenerator());

    private final Noise cheeseNoise = new LayeredNoise(7, 1f, 0.001f, 0.7f, 1.8f);
    private final Noise spaghettiNoise = new LayeredNoise(5, 1f, 0.001f, 0.6f, 1.8f);
    private final float cheeseNoiseThreshold = -0.3f;
    private final float spaghettiNoiseThreshold = 0.1f;

    private final float verticalSquishFactor = 2f;
    private final float surfaceOffset = 500;

    public CaveGenerator() {
    }

    public CellType getCaveCellType(World world, int cellX, int cellY, float terrainHeight) {
        float terrainHeightOffset = terrainHeight - cellY;
        if (terrainHeightOffset < 0) return null;
        float evalY = cellY * verticalSquishFactor;

        return isSpaghettiCave(world, cellX, cellY, evalY, terrainHeightOffset) ? CellType.FLOWER_PETAL.RED :
                isCheeseCave(world, cellX, cellY, evalY, terrainHeightOffset) ? CellType.FLOWER_PETAL.BLUE :
                        null;
    }

    private boolean isSpaghettiCave(World world, int cellX, int cellY, float evalY, float terrainHeightOffset) {

        float factor = terrainHeightOffset > surfaceOffset ? spaghettiNoiseThreshold
                : MathUtil.clampedMap(terrainHeightOffset, 0, surfaceOffset, 0.02f, spaghettiNoiseThreshold);

        return Math.abs(spaghettiNoise.eval(cellX, evalY)) < factor;
    }

    private boolean isCheeseCave(World world, int cellX, int cellY, float evalY, float terrainHeight) {
        if (terrainHeight - cellY < surfaceOffset) return false;

        return cheeseNoise.eval(cellX, evalY) < cheeseNoiseThreshold;
    }

    public boolean isCave(World world, int cellX, int cellY, float terrainHeight) {
        float terrainHeightOffset = terrainHeight - cellY;
        if (terrainHeightOffset < 0) return false;
        float evalY = cellY * verticalSquishFactor;

        if (isSpaghettiCave(world, cellX, cellY, evalY, terrainHeightOffset)) return true;

        if (isCheeseCave(world, cellX, cellY, evalY, terrainHeightOffset)) return true;

        return false;
    }
}
