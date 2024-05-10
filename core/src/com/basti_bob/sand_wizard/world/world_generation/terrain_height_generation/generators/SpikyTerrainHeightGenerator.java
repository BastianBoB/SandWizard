package com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.generators;

import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

public class SpikyTerrainHeightGenerator extends TerrainHeightGenerator {

    private final TerrainHeightGenerator baseGenerator;
    private final int stepSize;

    public SpikyTerrainHeightGenerator(TerrainHeightGenerator baseGenerator, int stepSize) {
        this.baseGenerator = baseGenerator;
        this.stepSize = stepSize;
    }

    @Override
    public float getTerrainHeight(World world, int cellPosX) {
        int x1 = (int) (Math.floor(cellPosX / (float) stepSize) * stepSize);
        int x2 = x1 + stepSize;

        float height1 = baseGenerator.getTerrainHeight(world, x1);
        float height2 = baseGenerator.getTerrainHeight(world, x2);

        float t = (cellPosX - x1) / (float) stepSize;

        return MathUtil.lerp(height1, height2, t);
    }
}
