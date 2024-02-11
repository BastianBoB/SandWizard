package com.basti_bob.sand_wizard.coordinateSystems;

import com.basti_bob.sand_wizard.world.WorldConstants;

public class CellPos {

    private final int x, y;

    public CellPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getChunkX() {
        return (int) Math.floor(this.x / (float) WorldConstants.CHUNK_SIZE);
    }

    public int getChunkY() {
        return (int) Math.floor(this.y / (float) WorldConstants.CHUNK_SIZE);
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos(getChunkX(), getChunkY());
    }

    public CellPos offset(CellPos other) {
        return new CellPos(this.x + other.x, this.y + other.y);
    }

    public InChunkPos getInChunkPos() {
        int inChunkX = Math.floorMod(x, WorldConstants.CHUNK_SIZE);
        int inChunkY = Math.floorMod(y, WorldConstants.CHUNK_SIZE);

        return new InChunkPos(inChunkX, inChunkY);
    }

}
