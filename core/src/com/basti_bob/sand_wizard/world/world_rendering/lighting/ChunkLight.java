package com.basti_bob.sand_wizard.world.world_rendering.lighting;

import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

/**
 * Used for Cells that are very common and have a small light radius
 */
public class ChunkLight extends Light {

    private final int chunkRadius;

    public ChunkLight(int cellX, int cellY, float r, float g, float b, float radius, float intensity) {
      super(cellX, cellY, r, g, b, radius, intensity);

        this.chunkRadius = (int) (radius / WorldConstants.CHUNK_SIZE) + 1;
    }

    @Override
    public void placedInChunk(Chunk chunk) {
        chunk.lightsInChunk.add(this);

        if (chunkRadius == 1) {
            for (Chunk targetChunk : chunk.chunkAccessor.getSurroundingChunks()) {
                if (targetChunk == null) continue;

                targetChunk.affectedLights.add(this);
            }
            return;
        }

        for (int i = -chunkRadius; i <= chunkRadius; i++) {
            for (int j = -chunkRadius; j <= chunkRadius; j++) {
                Chunk targetChunk = chunk.world.getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if (targetChunk != null) {

                    targetChunk.affectedLights.add(this);
                }
            }
        }
    }

    @Override
    public void removedFromChunk(Chunk chunk) {
        chunk.lightsInChunk.remove(this);

        if (chunkRadius == 1) {
            for (Chunk targetChunk : chunk.chunkAccessor.getSurroundingChunks()) {
                if (targetChunk == null) continue;

                targetChunk.affectedLights.remove(this);
            }
            return;
        }

        for (int i = -chunkRadius; i <= chunkRadius; i++) {
            for (int j = -chunkRadius; j <= chunkRadius; j++) {
                Chunk targetChunk = chunk.world.getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if (targetChunk == null) continue;

                targetChunk.affectedLights.remove(this);
            }
        }
    }

    @Override
    public void moveIntoNewChunk(Chunk previousChunk, Chunk newChunk) {
        removedFromChunk(previousChunk);
        placedInChunk(newChunk);
    }
}
