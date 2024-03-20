package com.basti_bob.sand_wizard.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.Solid;
import com.basti_bob.sand_wizard.entities.EntityHitBox;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

import java.util.concurrent.CompletableFuture;

public class Player {

    private final EntityHitBox hitBox;
    private final World world;
    private float ox, oy, nx, ny;
    public float xVel, yVel;

    private final Array2D<Chunk> renderingChunks = new Array2D<>(Chunk.class,
            WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_X * 2 + 1,
            WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_Y * 2 + 1);

    private final ShapeRenderer shapeRenderer;

    public Player(World world, float x, float y) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.ox = x;
        this.oy = y;
        this.nx = x;
        this.ny = y;
        this.hitBox = new EntityHitBox(8, 16);
        //this.xVel = 0.1f;

        this.loadChunksAround(World.getChunkPos((int) nx), World.getChunkPos((int) ny));
        this.setRenderingChunks(World.getChunkPos((int) nx), World.getChunkPos((int) ny));
    }

    public void update() {
        this.yVel += WorldConstants.GRAVITY.y;

        moveWithVelocity();
        updatePosition();
    }


    public void render(Camera camera) {
        int s = WorldConstants.CELL_SIZE;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);

        float rw = hitBox.getWidth() * s;
        float rh = hitBox.getHeight() * s;

        shapeRenderer.rect(this.nx * s - rw / 2f, this.ny * s - rh / 2f, rw, rh);
        shapeRenderer.end();
    }

    public Vector2 getPosition() {
        return new Vector2(ox, oy);
    }

    public void moveTo(float x, float y) {
        this.nx = x;
        this.ny = y;
    }

    public void moveBy(float x, float y) {
        moveTo(ox + x, oy + y);
    }

    private void moveWithVelocity() {
        float targetX = ox + xVel;

        if ((int) (targetX) != (int) ox) {
            if (canMoveFromTo(ox, oy, targetX, oy))
                this.nx = targetX;
        } else {
            this.nx = targetX;
        }

        float targetY = oy + yVel;

        if ((int) (targetY) != (int) oy) {
            if (canMoveFromTo(nx, oy, nx, targetY))
                this.ny = targetY;
        } else {
            this.ny = targetY;
        }
    }

    public boolean canMoveFromTo(float x1, float y1, float x2, float y2) {
        int hitBoxOffsetX = (int) Math.ceil(hitBox.getWidth() / 2f);
        int hitBoxOffsetY = (int) Math.ceil(hitBox.getHeight() / 2f);

        for (int j = -hitBoxOffsetY + 1; j <= hitBoxOffsetY; j++) {
            int targetX = (int) (x2 + hitBoxOffsetX * (x2 > x1 ? 1 : -1));
            int targetY = (int) (y2 + j);

            Cell cell = world.getCell(targetX, targetY);

            if (cell instanceof Solid)
                return false;
        }

        for (int i = -hitBoxOffsetX + 1; i <= hitBoxOffsetX; i++) {
            int targetX = (int) (x2 + i);
            int targetY = (int) (y2 + hitBoxOffsetY * (y2 > y1 ? 1 : -1));

            Cell cell = world.getCell(targetX, targetY);

            if (cell instanceof Solid)
                return false;
        }

        return true;
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


        CompletableFuture.runAsync(() -> {
            if (Math.abs(chunkXDiff) == 1) {
                int xOff = loadX * chunkXDiff;

                for (int i = -loadY; i <= loadY; i++) {
                    world.unloadChunkAsync(oldChunkX - xOff, oldChunkY + i);
                    world.loadOrCreateChunkAsync(newChunkX + xOff, newChunkY + i);
                }
            }

            if (Math.abs(chunkYDiff) == 1) {
                int yOff = loadY * chunkYDiff;

                for (int i = -loadX; i <= loadX; i++) {
                    world.unloadChunkAsync(oldChunkX + i, oldChunkY - yOff);
                    world.loadOrCreateChunkAsync(newChunkX + i, newChunkY + yOff);
                }
            }
        }).thenRun(() -> setRenderingChunks(newChunkX, newChunkY));
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
