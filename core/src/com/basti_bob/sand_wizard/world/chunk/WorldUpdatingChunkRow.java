package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.world.chunk.Chunk;

import java.util.ArrayList;

public class WorldUpdatingChunkRow {

    public final ArrayList<Chunk>[] separateChunksList = new ArrayList[3];
    public final int chunkPosY;

    public WorldUpdatingChunkRow(int chunkPosY) {
        this.chunkPosY = chunkPosY;

        for(int i = 0; i < 3; i++) {
            separateChunksList[i] = new ArrayList<>();
        }
    }

    public void addChunk(Chunk chunk) {
        int i = Math.floorMod(chunk.posX, 3);
        separateChunksList[i].add(chunk);
    }

    public void removeChunk(Chunk chunk) {
        int i = Math.floorMod(chunk.posX, 3);
        separateChunksList[i].remove(chunk);
    }

    public boolean isEmpty() {
        for (int i = 0; i < 3; i++)
            if (!separateChunksList[i].isEmpty()) return false;

        return true;
    }
}
