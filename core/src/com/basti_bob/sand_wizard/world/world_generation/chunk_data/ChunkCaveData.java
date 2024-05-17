package com.basti_bob.sand_wizard.world.world_generation.chunk_data;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.biomes.CaveBiomeType;

public class ChunkCaveData {

    public final World world;
    public final ChunkPos chunkPos;
    public final CaveBiomeType caveBiomeType;
    private final boolean[][] isCave;

    public ChunkCaveData(World world, ChunkPos chunkPos) {
        this.world = world;
        this.chunkPos = chunkPos;
        this.caveBiomeType = world.worldGeneration.calculateCaveBiomeType(chunkPos.x, chunkPos.y);

        int cs = WorldConstants.CHUNK_SIZE;
        this.isCave = new boolean[cs][cs];

        for (int i = 0; i < cs; i++) {
            for (int j = 0; j < cs; j++) {
                isCave[i][j] = world.worldGeneration.calculateIsCave(chunkPos.x * cs + i, chunkPos.y * cs + j);
            }
        }
    }

    public boolean isCaveWithCellPos(int cellPosX, int cellPosY) {
        return isCaveWithInChunkPos(World.getInChunkPos(cellPosX), World.getInChunkPos(cellPosY));
    }

    public boolean isCaveWithInChunkPos(int inChunkX, int inChunkY) {
        return isCave[inChunkX][inChunkY];
    }

}
