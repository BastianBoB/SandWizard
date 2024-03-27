package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.world.World;

public interface TerrainHeightGenerator {

    TerrainHeightGenerator BASE_PERLIN_NOISE = new PerlinTerrainHeightGenerator(0.01f, 50, -20);
    TerrainHeightGenerator MOUNTAINS = new PerlinTerrainHeightGenerator(0.04f, 100, 100);
    TerrainHeightGenerator FLAT = (world, cellPosX) -> 0;

    int getTerrainHeight(World world, int cellPosX);
}
