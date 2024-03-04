package com.basti_bob.sand_wizard;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class Player {

    private final World world;
    private float ox, oy, nx, ny;

    private final Array2D<Chunk> renderingChunks = new Array2D<>(Chunk.class,
            WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_X * 2 + 1,
            WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_Y * 2 + 1);

    public Player(World world, float x, float y) {
        this.world = world;
        this.ox = x;
        this.oy = y;
        this.nx = x;
        this.ny = y;

        this.loadChunksAround(World.getChunkPos((int) nx), World.getChunkPos((int) ny));
        this.setRenderingChunks(World.getChunkPos((int) nx), World.getChunkPos((int) ny));
    }

    public void update() {
        updatePosition();
    }

    public Vector2 getPosition() {
        return new Vector2(ox, oy);
    }

    public void moveTo(float x, float y) {
        this.nx = x;
        this.ny = y;
    }

    public void moveBy(float x, float y) {
        this.nx = ox + x;
        this.ny = oy + y;
    }

    private void updatePosition() {
        int oldChunkX = World.getChunkPos((int) ox);
        int oldChunkY = World.getChunkPos((int) oy);

        int newChunkX = World.getChunkPos((int) nx);
        int newChunkY = World.getChunkPos((int) ny);

        if ((newChunkX - oldChunkX != 0) || (newChunkY - oldChunkY != 0)) {
            enteredNewChunk(oldChunkX, oldChunkY, newChunkX, newChunkY);
        }

        this.ox = nx;
        this.oy = ny;
    }

    private void enteredNewChunk(int oldChunkX, int oldChunkY, int newChunkX, int newChunkY) {

        int chunkXDiff = newChunkX - oldChunkX;
        int chunkYDiff = newChunkY - oldChunkY;

        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;


        if (Math.abs(chunkXDiff) == 1) {
            int xOff = loadX * chunkXDiff;

            for (int i = -loadY; i <= loadY; i++) {
                world.unloadChunk(oldChunkX - xOff, oldChunkY + i);
                world.loadOrCreateChunk(newChunkX + xOff, newChunkY  + i);
            }
        }

        if (Math.abs(chunkYDiff) == 1) {
            int yOff = loadY * chunkYDiff;

            for (int i = -loadX; i <= loadX; i++) {
                world.unloadChunk(oldChunkX + i, oldChunkY - yOff);
                world.loadOrCreateChunk(newChunkX + i, newChunkY + yOff);
            }
        }

        setRenderingChunks(newChunkX, newChunkY);


        //TO DO: MOVE MORE THEN 1 CHUNK (RECTANGLE INTERSECTION APPROACH)

//        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
//        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;
//
//        for (int i = -loadX; i <= loadX; i++) {
//            for (int j = -loadY; j <= loadY; j++) {
//                if(isWithinRectangle())
//            }
//        }
    }

    public void setRenderingChunks(int chunkX, int chunkY) {
        int renderX = WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_X;
        int renderY = WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_Y;

        for (int i = -renderX; i <= renderX; i++) {
            for (int j = -renderY; j <= renderY; j++) {
                renderingChunks.set(i + renderX, j + renderY, world.getChunkFromChunkPos(chunkX + i, chunkY + j));
            }
        }
    }

    public static boolean isWithinRectangle(int x, int y, int centerX, int centerY, int width, int height) {
        int leftBoundary = centerX - width;
        int rightBoundary = centerX + width;
        int topBoundary = centerY - height;
        int bottomBoundary = centerY + height;

        return x >= leftBoundary && x <= rightBoundary && y >= topBoundary && y <= bottomBoundary;
    }

    public Array2D<Chunk> getRenderingChunks() {
        return renderingChunks;
    }

    private void loadChunksAround(int chunkPosX, int chunkPosY) {

        int loadX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        for (int i = -loadX; i <= loadX; i++) {
            for (int j = -loadY; j <= loadY; j++) {
                world.loadOrCreateChunk(chunkPosX + i, chunkPosY + j);
            }
        }
    }


}
