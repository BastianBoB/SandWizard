package com.basti_bob.sand_wizard.world.world_generation.chunk_data;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_generation.biomes.SurfaceBiomeType;

public class ChunkColumnData {

    public final World world;
    public final int chunkX;
    public final SurfaceBiomeType surfaceBiomeType;
    public final float[] terrainHeights;

    public ChunkColumnData(World world, int chunkX) {
        this.world = world;
        this.chunkX = chunkX;
        this.surfaceBiomeType = world.worldGeneration.calculateSurfaceBiomeType(chunkX);

        terrainHeights = new float[WorldConstants.CHUNK_SIZE];
        for(int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
            terrainHeights[i] = world.worldGeneration.calculateTerrainHeight(chunkX * WorldConstants.CHUNK_SIZE + i);
        }
    }


}
