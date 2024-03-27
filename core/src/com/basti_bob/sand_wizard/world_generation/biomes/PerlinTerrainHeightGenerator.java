package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.world.World;

public class PerlinTerrainHeightGenerator implements TerrainHeightGenerator {

    private final float frequency;
    private final float amplitude;
    private final float offset;

    public PerlinTerrainHeightGenerator(float frequency, float amplitude, float offset) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.offset = offset;
    }

    @Override
    public int getTerrainHeight(World world, int cellPosX) {
        return (int) (world.openSimplexNoise.eval(cellPosX * frequency, 0, 0) * amplitude + offset);
    }
}
