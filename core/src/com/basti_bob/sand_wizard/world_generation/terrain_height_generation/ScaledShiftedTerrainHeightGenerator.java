package com.basti_bob.sand_wizard.world_generation.terrain_height_generation;

import com.basti_bob.sand_wizard.world.World;

public class ScaledShiftedTerrainHeightGenerator extends TerrainHeightGenerator {

    private final TerrainHeightGenerator baseTerrainHeightGenerator;
    private final float scale;
    private final float shift;

    public ScaledShiftedTerrainHeightGenerator(TerrainHeightGenerator baseTerrainHeightGenerator, float scale, float shift) {
        this.baseTerrainHeightGenerator = baseTerrainHeightGenerator;
        this.scale = scale;
        this.shift = shift;
    }

    public static ScaledShiftedTerrainHeightGenerator normalToRange(TerrainHeightGenerator baseTerrainHeightGenerator, float minY, float maxY) {
        float scale = (maxY - minY)/2f;
        float shift = minY + scale;

        return new ScaledShiftedTerrainHeightGenerator(baseTerrainHeightGenerator, scale, shift);
    }

    @Override
    public float getTerrainHeight(World world, int cellPosX) {
        return baseTerrainHeightGenerator.getTerrainHeight(world, cellPosX) * scale + shift;
    }
}
