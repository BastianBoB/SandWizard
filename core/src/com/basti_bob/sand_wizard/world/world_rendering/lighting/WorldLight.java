package com.basti_bob.sand_wizard.world.world_rendering.lighting;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

/**
 * Used for Cells that are rare and have a big light radius
 */
public class WorldLight extends Light {
    public WorldLight(int posX, int posY, float r, float g, float b, float radius, float intensity) {
        super(posX, posY, r, g, b, radius, intensity);
    }

    @Override
    public void placedInChunk(Chunk chunk) {
        placedInWorld(chunk.world);
    }

    public void placedInWorld(World world) {
        world.globalLights.add(this);
    }

    @Override
    public void removedFromChunk(Chunk chunk) {
        removedFromWorld(chunk.world);
    }

    public void removedFromWorld(World world) {
        world.globalLights.remove(this);
    }

    @Override
    public void moveIntoNewChunk(Chunk previousChunk, Chunk newChunk) {

    }
}
