package com.basti_bob.sand_wizard.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
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
    private float ox;
    private float oy;
    private float nx;
    public float ny;
    public float xVel, yVel;
    public boolean onGround;
    private int stepUpHeight = 16;

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

    public void update(float deltaTime) {
        this.yVel += WorldConstants.GRAVITY.y;
        this.xVel *= 0.95;

        moveWithVelocity(deltaTime);
        updatePosition();
    }

    public void jump() {
        this.onGround = false;
        this.yVel = 6;
    }


    public void render(Camera camera) {
        int s = WorldConstants.CELL_SIZE;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);

        float rw = hitBox.getWidth() * s;
        float rh = hitBox.getHeight() * s;

        shapeRenderer.rect(this.nx * s - rw / 2f, this.ny * s, rw, rh);
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

    public int getDownY(int i) {
        return (int) this.oy - i;
    }

    public int getUpY(int i) {
        return (int) (this.oy + this.hitBox.getHeight()) + i;
    }

    public int getRightX(int i) {
        return (int) (this.ox + this.hitBox.getWidth() / 2f) + i;
    }

    public int getLeftX(int i) {
        return (int) (this.ox - this.hitBox.getWidth() / 2f) - i;
    }

    private void moveWithVelocity(float deltaTime) {
        if (this.xVel > 0) {
            int a = 2;
        }

        float targetX = ox + xVel;
        float targetY = oy + yVel;

        if ((int) Math.floor(targetY) != (int) Math.floor(oy)) {

            int steps = (int) Math.abs(yVel) + 1;
            int step;
            for (step = 0; step < steps; step++) {
                int checkY = yVel > 0 ? getUpY(step + 1) : getDownY(step + 1);

                if (wouldCollideVertically((int) ox, checkY)) {
                    break;
                }
            }

            if (step == steps) {
                this.ny = targetY;
            } else {
                this.ny = (int) (oy + step * (yVel > 0 ? 1 : -1));
                if (this.yVel < 0) onGround = true;
                this.yVel = 0;
            }
        } else {
            this.ny = targetY;
        }

        if ((int) (targetX) != (int) ox) {

            int steps = (int) Math.abs(xVel) + 1;
            int step;
            for (step = 0; step < steps; step++) {
                int checkX = xVel > 0 ? getRightX(step + 1) : getLeftX(step + 1);

                int stepHeight = stepHeightAtTarget(checkX, (int) this.ny);

                if (stepHeight < this.stepUpHeight) {
                    this.ny = this.ny + stepHeight;
                } else {
                    this.xVel = 0;
                    break;
                }
            }

            if (step == steps) {
                this.nx = targetX;
            } else {
                this.nx = ox + step * (xVel > 0 ? 1 : -1);
            }
        } else {
            this.nx = targetX;
        }

    }

    public int stepHeightAtTarget(int x, int y) {

        int stepHeight = 0;
        for (int j = 0; j <= hitBox.getHeight(); j++) {
            if (world.getCell(x, y + j) instanceof Solid)
                stepHeight = j+1;
        }

        return stepHeight;
    }

    public boolean wouldCollideVertically(int x, int y) {
        int offsetX = (int) Math.ceil(hitBox.getWidth() / 2f);

        for (int i = -offsetX + 1; i <= offsetX; i++) {
            if (world.getCell(x + i, y) instanceof Solid)
                return true;
        }
        return false;
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
