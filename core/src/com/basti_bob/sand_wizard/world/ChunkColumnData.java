package com.basti_bob.sand_wizard.world;

import com.basti_bob.sand_wizard.world.world_generation.biomes.SurfaceBiomeType;

public class ChunkColumnData {

    public final World world;
    public final SurfaceBiomeType surfaceBiomeType;
    public final float[] terrainHeights;

    public ChunkColumnData(World world, int chunkX) {
        this.world = world;
        this.surfaceBiomeType = world.worldGeneration.calculateSurfaceBiomeType(chunkX);

        terrainHeights = new float[WorldConstants.CHUNK_SIZE];
        for(int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            terrainHeights[i] = world.worldGeneration.calculateTerrainHeight(chunkX * WorldConstants.CHUNK_SIZE + i);
        }
    }


}
