package com.basti_bob.sand_wizard.world.lighting;

import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

public class Light {

    public static final int NUM_FLOAT_DATA = 7;

    private int cellX, cellY;
    private final float radius;
    private final int chunkRadius;
    private final float intensity;
    private final float r, g, b;
    private final float[] data;

    public Light(int cellX, int cellY, float r, float g, float b, float radius, float intensity) {
        this.cellX = cellX;
        this.cellY = cellY;
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = radius;
        this.intensity = intensity;

        this.data = new float[]{cellX, cellY, radius, intensity, r, g, b};

        this.chunkRadius = (int) (radius / WorldConstants.CHUNK_SIZE) + 1;
    }

    public void placedInChunk(Chunk chunk) {
        if(chunkRadius == 1) {
            for(Chunk targetChunk : chunk.chunkAccessor.getSurroundingChunks()) {
                if(targetChunk == null) continue;

                targetChunk.affectedLights.add(this);
            }
            return;
        }

        for(int i = -chunkRadius; i <= chunkRadius; i++) {
            for(int j = -chunkRadius; j <= chunkRadius; j++) {
                Chunk targetChunk = chunk.world.getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if(targetChunk != null) {

                    targetChunk.affectedLights.add(this);
                }
            }
        }
    }
//
    public void removedFromChunk(Chunk chunk) {
        if(chunkRadius == 1) {
            for(Chunk targetChunk : chunk.chunkAccessor.getSurroundingChunks()) {
                if(targetChunk == null) continue;

                targetChunk.affectedLights.remove(this);
            }
            return;
        }

        for(int i = -chunkRadius; i <= chunkRadius; i++) {
            for(int j = -chunkRadius; j <= chunkRadius; j++) {
                Chunk targetChunk = chunk.world.getChunkFromChunkPos(chunk.posX + i, chunk.posY + j);

                if(targetChunk == null) continue;

                targetChunk.affectedLights.remove(this);
            }
        }
    }

    public void setNewPosition(int posX, int posY) {
        this.cellX = posX;
        this.cellY = posY;

        data[0] = cellX;
        data[1] = cellY;
    }

    public void moveIntoNewChunk(Chunk previousChunk, Chunk newChunk) {
        removedFromChunk(previousChunk);
        placedInChunk(newChunk);

        int dx = newChunk.posX - previousChunk.posX;
        int dy = newChunk.posY - previousChunk.posY;


//
//        previousChunk.affectedLights.remove(this);
//        newChunk.affectedLights.add(this);
    }

    public float[] getData() {
        return data;
    }
}
