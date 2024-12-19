package com.basti_bob.sand_wizard.entities;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;

public class ChunkUpdatingEntity extends Entity {

    private Chunk currentChunk;

    public ChunkUpdatingEntity(World world, float x, float y, EntityHitBox hitBox) {
        super(world, x, y, hitBox);
    }

    public void addedToWorld(World world) {
        super.addedToWorld(world);
        gotSpawnedInChunk(world.getChunkFromCellPos((int) nx, (int) ny));
    }

    @Override
    public void removedFromWorld(World world) {
        super.removedFromWorld(world);
        gotRemovedInChunk(currentChunk);
    }

    public void gotSpawnedInChunk(Chunk chunk) {
        this.currentChunk = chunk;
    }

    public void gotRemovedInChunk(Chunk chunk) {
        this.currentChunk = null;
    }

    @Override
    public void update() {
        super.update();

        int chunkX = World.getChunkPos((int) nx);
        int chunkY = World.getChunkPos((int) ny);

        if(chunkX != currentChunk.getPosX() || chunkY != currentChunk.getPosX()) {
            Chunk newChunk = world.getChunkFromChunkPos(chunkX, chunkY);
            movedIntoNewChunk(currentChunk, newChunk);
            currentChunk = newChunk;
        }
    }

    public void movedIntoNewChunk(Chunk oldChunk, Chunk newChunk) {

    }
}
