package com.basti_bob.sand_wizard.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.entities.EntityHitBox;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.Light;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.WorldLight;

import java.util.concurrent.CompletableFuture;

public class Player {

    private final EntityHitBox hitBox;
    private final World world;
    private float ox;
    private float oy;
    public float nx;
    public float ny;
    public float xVel, yVel;
    public boolean onGround;
    private int stepUpHeight = 6;
    private int fastStepUpHeight = 2;

    public ChunkPos topLeftChunkPos;
    public ChunkPos bottomRightChunkPos;

    private final Array2D<Chunk> renderingChunks = new Array2D<>(Chunk.class,
            WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_X * 2 + 1,
            WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_Y * 2 + 1);

    private final ShapeRenderer shapeRenderer;
    private final WorldLight light;

    public Player(World world, float x, float y) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.ox = x;
        this.oy = y;
        this.nx = x;
        this.ny = y;
        this.hitBox = new EntityHitBox(8, 16);

        this.loadChunksAround(World.getChunkPos((int) nx), World.getChunkPos((int) ny));
        this.setRenderingChunks(World.getChunkPos((int) nx), World.getChunkPos((int) ny));

        Color c = Color.WHITE;
        this.light = new WorldLight((int) ox, (int) oy, c.r, c.g, c.b, 127f, 1f);
        this.light.placedInWorld(world);
    }

    public void update(float deltaTime) {

        if (WorldConstants.PLAYER_FREE_MOVE) {
            this.moveBy(xVel, yVel);

            this.xVel *= 0.95;
            this.yVel *= 0.95;
        } else {
            this.yVel += WorldConstants.GRAVITY.y;
            this.xVel *= 0.95;

            moveWithVelocity(deltaTime);
        }

        updatePosition();

        int chunkX = World.getChunkPos((int) this.nx);
        int chunkY = World.getChunkPos((int) this.ny);
        setRenderingChunks(chunkX, chunkY);
    }

    public void jump() {
        this.onGround = false;
        this.yVel = 8;
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
        return new Vector2(nx, ny);
    }

    public Vector2 getHeadPosition() {
        return new Vector2(nx, ny + hitBox.getHeight());
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
        clampVelocity();

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

                if (stepHeight < this.fastStepUpHeight) {
                    this.ny = this.ny + stepHeight;
                } else if (stepHeight < this.stepUpHeight) {
                    this.ny = this.ny + stepHeight;
                    this.xVel *= 0.5;
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

    public void clampVelocity() {
        if (xVel > WorldConstants.CHUNK_SIZE) xVel = WorldConstants.CHUNK_SIZE;
        else if (xVel < -WorldConstants.CHUNK_SIZE) xVel = -WorldConstants.CHUNK_SIZE;

        if (yVel > WorldConstants.CHUNK_SIZE) yVel = WorldConstants.CHUNK_SIZE;
        else if (yVel < -WorldConstants.CHUNK_SIZE) yVel = -WorldConstants.CHUNK_SIZE;
    }

    public int stepHeightAtTarget(int x, int y) {

        int stepHeight = 0;
        for (int j = 0; j <= hitBox.getHeight(); j++) {
            Cell cell = world.getCell(x, y + j);

            if (cell != null && cell.isSolid())
                stepHeight = j + 1;
        }

        return stepHeight;
    }

    public boolean wouldCollideVertically(int x, int y) {
        int offsetX = (int) Math.ceil(hitBox.getWidth() / 2f);

        for (int i = -offsetX + 1; i <= offsetX; i++) {
            Cell cell = world.getCell(x + i, y);

            if (cell == null) return false;

            if (cell.isSolid())
                return true;
        }
        return false;
    }


    private void updatePosition() {

        Chunk previousChunk = world.getChunkFromCellPos((int) ox, (int) oy);
        Chunk newChunk = world.getChunkFromCellPos((int) nx, (int) ny);

        if (previousChunk != newChunk) {
            enteredNewChunk(previousChunk, newChunk);
        }

        this.ox = nx;
        this.oy = ny;

        this.light.setNewPosition((int) nx, (int) ny);
    }

    private void enteredNewChunk(Chunk previousChunk, Chunk newChunk) {
        if (newChunk == null || previousChunk == null) return;

        if (!(Math.abs(previousChunk.posX - newChunk.posX) == 1 || Math.abs(previousChunk.posY - newChunk.posY) == 1)) {
            System.out.println("Player moved more than 1 chunk");
        }

        int oldChunkX = previousChunk.posX;
        int oldChunkY = previousChunk.posY;

        int newChunkX = newChunk.posX;
        int newChunkY = newChunk.posY;

        generateAndRemoveChunks(oldChunkX, oldChunkY, newChunkX, newChunkY);
        loadAndUnloadChunks(oldChunkX, oldChunkY, newChunkX, newChunkY);

        setRenderingChunks(newChunkX, newChunkY);
    }

    private void generateAndRemoveChunks(int oldChunkX, int oldChunkY, int newChunkX, int newChunkY) {
        int chunkXDiff = newChunkX - oldChunkX;
        int chunkYDiff = newChunkY - oldChunkY;

        int generateX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int generateY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;


        CompletableFuture.runAsync(() -> {
            if (Math.abs(chunkXDiff) == 1) {
                for (int i = -generateY; i <= generateY; i++) {
                    world.loadChunkAsync(newChunkX + generateX * chunkXDiff, newChunkY + i);
                }
            }

            if (Math.abs(chunkYDiff) == 1) {
                for (int i = -generateX; i <= generateX; i++) {
                    world.loadChunkAsync(newChunkX + i, newChunkY + generateY * chunkYDiff);
                }
            }

            for (Chunk chunk : world.chunkProvider.chunks) {
                if (Math.abs(chunk.posX - newChunkX) > WorldConstants.PLAYER_CHUNK_UNLOAD_RADIUS_X || Math.abs(chunk.posY - newChunkY) > WorldConstants.PLAYER_CHUNK_UNLOAD_RADIUS_Y) {
                    world.unloadChunkAsync(chunk.posX, chunk.posY);
                }
            }
        });
    }

    private void loadAndUnloadChunks(int oldChunkX, int oldChunkY, int newChunkX, int newChunkY) {
        int chunkXDiff = newChunkX - oldChunkX;
        int chunkYDiff = newChunkY - oldChunkY;

        int loadX = WorldConstants.PLAYER_CHUNK_UPDATE_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_UPDATE_RADIUS_Y;

        if (Math.abs(chunkXDiff) == 1) {
            for (int i = -loadY; i <= loadY; i++) {
                setChunkLoaded(newChunkX + loadX * chunkXDiff, newChunkY + i, true);
                setChunkLoaded(oldChunkX - loadX * chunkXDiff, oldChunkY + i, false);
            }
        }

        if (Math.abs(chunkYDiff) == 1) {
            for (int i = -loadX; i <= loadX; i++) {
                setChunkLoaded(newChunkX + i, newChunkY + loadY * chunkYDiff, true);
                setChunkLoaded(oldChunkX + i, oldChunkY - loadY * chunkYDiff, false);
            }
        }
    }

    private void setChunkLoaded(int chunkX, int chunkY, boolean loaded) {
        Chunk chunk = world.getChunkFromChunkPos(chunkX, chunkY);
        if(chunk == null) return;

        chunk.setLoaded(loaded);
    }

    public void setRenderingChunks(int chunkX, int chunkY) {
        int renderX = WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_X;
        int renderY = WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_Y;

        topLeftChunkPos = new ChunkPos(chunkX - renderX, chunkY + renderY);
        bottomRightChunkPos = new ChunkPos(chunkX + renderX, chunkY - renderY);

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


        int createX = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_X;
        int createY = WorldConstants.PLAYER_CHUNK_LOAD_RADIUS_Y;

        int loadX = WorldConstants.PLAYER_CHUNK_UPDATE_RADIUS_X;
        int loadY = WorldConstants.PLAYER_CHUNK_UPDATE_RADIUS_Y;

        for (int i = -createX; i <= createX; i++) {
            for (int j = -createY; j <= createY; j++) {
                Chunk chunk = world.loadChunk(chunkPosX + i, chunkPosY + j);

                if(i >= -loadX && i <= loadX && j >= -loadY && j <= loadY)
                    chunk.setLoaded(true);
            }
        }
    }

    public World getWorld() {
        return world;
    }

}
