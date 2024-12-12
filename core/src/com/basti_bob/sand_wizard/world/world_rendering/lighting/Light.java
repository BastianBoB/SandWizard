package com.basti_bob.sand_wizard.world.world_rendering.lighting;

import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

import java.util.concurrent.CompletableFuture;

public abstract class Light {

    public static final int NUM_FLOAT_DATA = 7;

    private float posX, posY;
    private float radius;
    private int chunkRadius;
    private float intensity;
    private float r, g, b;
    private final float[] data;

    public Light(int posX, int posY, float r, float g, float b, float radius, float intensity) {
        this.posX = posX;
        this.posY = posY;
        this.r = r;
        this.g = g;
        this.b = b;
        this.radius = radius;
        this.intensity = intensity;

        this.data = new float[]{posX, posY, radius, intensity, r, g, b};

        this.chunkRadius = (int) (radius / WorldConstants.CHUNK_SIZE) + 1;
    }

    public abstract void placedInChunk(Chunk chunk);
    public abstract void removedFromChunk(Chunk chunk);
    public abstract void moveIntoNewChunk(Chunk previousChunk, Chunk newChunk);

    public void setNewPosition(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;

        data[0] = posX;
        data[1] = posY;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        data[2] = radius;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        data[3] = intensity;
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        data[4] = r;
        data[5] = g;
        data[6] = b;
    }

    public float[] getData() {
        return data;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getRadius() {
        return radius;
    }

    public int getChunkRadius() {
        return chunkRadius;
    }

    public float getIntensity() {
        return intensity;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public boolean isEmittingLight() {
        return true;
    }
}
