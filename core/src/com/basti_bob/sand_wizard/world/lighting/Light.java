package com.basti_bob.sand_wizard.world.lighting;

import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

import java.util.concurrent.CompletableFuture;

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

    //
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

    public void setNewPosition(int posX, int posY) {
        this.cellX = posX;
        this.cellY = posY;

        data[0] = cellX;
        data[1] = cellY;
    }

    public void moveIntoNewChunk(Chunk previousChunk, Chunk newChunk) {
        removedFromChunk(previousChunk);
        placedInChunk(newChunk);

//        int oldChunkX = previousChunk.posX;
//        int oldChunkY = previousChunk.posY;
//
//        int newChunkX = newChunk.posX;
//        int newChunkY = newChunk.posY;
//
//        int chunkXDiff = newChunkX - oldChunkX;
//        int chunkYDiff = newChunkY - oldChunkY;
//
//
//        if (Math.abs(chunkXDiff) == 1) {
//            int xOff = chunkRadius * chunkXDiff;
//
//            for (int i = -chunkRadius; i <= chunkRadius; i++) {
//                Chunk targetChunk = previousChunk.world.getChunkFromChunkPos(oldChunkX - xOff, oldChunkY + i);
//                if (targetChunk == null) continue;
//
//                targetChunk.affectedLights.remove(this);
//            }
//        }
//
//        if (Math.abs(chunkYDiff) == 1) {
//            int yOff = loadY * chunkYDiff;
//
//            for (int i = -loadX; i <= loadX; i++) {
//                world.loadChunkAsync(newChunkX + i, newChunkY + yOff);
//                //world.unloadChunkAsync(oldChunkX + i, oldChunkY - yOff);
//            }
//        }

    }

    public float[] getData() {
        return data;
    }
}
