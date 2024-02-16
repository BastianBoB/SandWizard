package com.basti_bob.sand_wizard;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class Player {

    private final World world;
    public float x, y;


    public Player(World world, float x, float y) {
        this.world = world;
        this.x = x;
        this.y = y;
    }

    public void update() {
        loadChunksAround();
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public Array2D<Chunk> getRenderingChunks() {

        int chunkPosX = World.getChunkPos((int) x);
        int chunkPosY = World.getChunkPos((int) y);

        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        Array2D<Chunk> chunks = new Array2D<>(Chunk.class, loadX * 2 + 1, loadY * 2 + 1);

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                chunks.set(i + loadX, j + loadY, world.getChunkFromChunkPos(chunkPosX +i, chunkPosY + j));
            }
        }

        return chunks;
    }

    private void loadChunksAround() {

        int chunkPosX = World.getChunkPos((int) x);
        int chunkPosY = World.getChunkPos((int) y);


        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                //world.loadOrCreateChunk(chunkPosX + i, chunkPosY + j);
            }
        }
    }

}
