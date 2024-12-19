package com.basti_bob.sand_wizard.entities.fireworks;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.entities.ChunkUpdatingEntity;
import com.basti_bob.sand_wizard.entities.EntityHitBox;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.ChunkLight;

import java.util.ArrayList;

public class FireworkEntity extends ChunkUpdatingEntity {

    private final ArrayList<ChunkLight> chunkLights = new ArrayList<>();

    public FireworkEntity(World world, float x, float y, EntityHitBox hitBox) {
        super(world, x, y, hitBox);
    }

    public void addLight(ChunkLight chunkLight) {
        chunkLights.add(chunkLight);
    }

    @Override
    public void render(Camera camera, ShapeRenderer shapeRenderer) {
        //super.render(camera, shapeRenderer);
    }

    @Override
    public void movedIntoNewChunk(Chunk oldChunk, Chunk newChunk) {
        super.movedIntoNewChunk(oldChunk, newChunk);
        for(ChunkLight chunkLight : chunkLights) {
            chunkLight.moveIntoNewChunk(oldChunk, newChunk);
        }
    }

    @Override
    public void gotSpawnedInChunk(Chunk chunk) {
        super.gotSpawnedInChunk(chunk);
        for(ChunkLight chunkLight : chunkLights) {
            chunkLight.placedInChunk(chunk);
        }
    }

    @Override
    public void gotRemovedInChunk(Chunk chunk) {
        super.gotRemovedInChunk(chunk);
        for(ChunkLight chunkLight : chunkLights) {
            chunkLight.removedFromChunk(chunk);
        }
    }

    @Override
    public float getFriction() {
        return 0.99f;
    }

    @Override
    public void update() {
        super.update();

        for(ChunkLight chunkLight : chunkLights) {
            chunkLight.setNewPosition(nx, ny);
        }
    }

    public ArrayList<ChunkLight> getChunkLights() {
        return chunkLights;
    }
}
