package com.basti_bob.sand_wizard.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.entities.Entity;
import com.basti_bob.sand_wizard.entities.EntityHitBox;
import com.basti_bob.sand_wizard.items.inventory.InventoryWithPlayerInventoryScreen;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.WorldLight;

import java.util.*;

public class Player extends Entity {

    List<VisibleChunkRay> visibleChunkRays = new ArrayList<>();

    public boolean onGround;
    private int stepUpHeight = 6;
    private int fastStepUpHeight = 2;

    public ChunkPos topLeftChunkPos;
    public ChunkPos bottomRightChunkPos;
    public PlayerInventory inventory;
    public OnlyInventoryWithPlayerInventoryScreen onlyPlayerInventoryScreen;
    public InventoryWithPlayerInventoryScreen inventoryScreen;

    public boolean openedInventoryScreen = false;

    private final Array2D<Chunk> renderingChunks = new Array2D<>(Chunk.class,
            WorldConstants.CHUNK_LOADING.RENDER_WIDTH, WorldConstants.CHUNK_LOADING.RENDER_HEIGHT);

    private final WorldLight light;

    public Player(World world, float x, float y) {
        super(world, x, y, new EntityHitBox(8, 16));

        loadUnloadAndSetUpdatingChunks(World.getChunkPos((int) nx), World.getChunkPos((int) ny), false);
        setRenderingChunks(World.getChunkPos((int) nx), World.getChunkPos((int) ny));

        Color c = new Color(1f, 0.5f, 0f, 1f);
        this.light = new WorldLight((int) ox, (int) oy, c.r, c.g, c.b, 1f, 1f);
        this.light.placedInWorld(world);

        this.inventory = new PlayerInventory(this);
        this.onlyPlayerInventoryScreen = new OnlyInventoryWithPlayerInventoryScreen();

        int chunkSize = WorldConstants.CHUNK_SIZE;
        int renderHeight = WorldConstants.CHUNK_LOADING.RENDER_HEIGHT;
        int renderWidth = WorldConstants.CHUNK_LOADING.RENDER_WIDTH;
        for (int i = -renderWidth / 2; i <= renderWidth / 2; i++) {
            visibleChunkRays.add(new VisibleChunkRay(i * chunkSize, -renderHeight / 2 * chunkSize));
            visibleChunkRays.add(new VisibleChunkRay(i * chunkSize, renderHeight / 2 * chunkSize));
        }
        for (int j = -(renderHeight / 2 - 1); j <= renderHeight / 2 - 1; j++) {
            visibleChunkRays.add(new VisibleChunkRay(-renderWidth / 2 * chunkSize, j * chunkSize));
            visibleChunkRays.add(new VisibleChunkRay(renderWidth / 2 * chunkSize, j * chunkSize));
        }

        System.out.println(visibleChunkRays.size());
    }

    public void openInventoryScreen(InventoryWithPlayerInventoryScreen screen) {
        inventoryScreen = screen;

        this.openedInventoryScreen = true;
    }

    public void closeInventoryScreen() {
        inventoryScreen.playerClosedScreen();
        inventoryScreen = null;
        this.openedInventoryScreen = false;
    }

    @Override
    protected void updateMoving() {
        if (WorldConstants.PLAYER_FREE_MOVE) {
            this.moveBy(xVel, yVel);
        }

        super.updateMoving();
    }

    @Override
    protected void applyGravity() {
        if (WorldConstants.PLAYER_FREE_MOVE) return;

        super.applyGravity();
    }

    public void update() {

        if (WorldConstants.PLAYER_FREE_MOVE) {
            this.moveBy(xVel, yVel);

            this.xVel *= 0.95f;
            this.yVel *= 0.95f;
        } else {
            this.yVel += WorldConstants.GRAVITY.y;
            this.xVel *= 0.95f;

            moveWithVelocity();
        }

        updatePosition();
    }

    public void jump() {
        this.onGround = false;
        this.yVel = 8;
    }

    public void render(Camera camera, ShapeRenderer shapeRenderer) {
        super.render(camera, shapeRenderer);

        if (openedInventoryScreen) inventoryScreen.render();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.ORANGE);
//        for (VisibleChunkRay ray : visibleChunkRays) {
//            Vector2 rayTarget = this.getPosition().cpy().add(ray.getEndCellPos().x, ray.getEndCellPos().y);
//            shapeRenderer.line(this.getPosition().cpy().scl(WorldConstants.CELL_SIZE), rayTarget.scl(WorldConstants.CELL_SIZE));
//        }
//        shapeRenderer.end();
    }

    public Vector2 getPosition() {
        return new Vector2(nx, ny);
    }

    public Vector2 getFeetPosition() {
        return new Vector2(nx, ny - hitBox.getHeight() / 2f);
    }

    public Vector2 getHeadPosition() {
        return new Vector2(nx, ny + hitBox.getHeight() / 2f);
    }

    public int getDownY(int i) {
        return (int) this.oy - i;
    }

    public int getUpY(int i) {
        return (int) (this.oy + hitBox.getHeight()) + i;
    }

    public int getRightX(int i) {
        return (int) (this.ox + hitBox.getWidth() / 2f) + i;
    }

    public int getLeftX(int i) {
        return (int) (this.ox - hitBox.getWidth() / 2f) - i;
    }

    private void moveWithVelocity() {


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
                    this.xVel *= 0.5f;
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
        if (ox == nx && oy == ny) return;

        Chunk previousChunk = world.getChunkFromCellPos((int) ox, (int) oy);
        Chunk newChunk = world.getChunkFromCellPos((int) nx, (int) ny);

        if (previousChunk != newChunk) {
            enteredNewChunk(previousChunk, newChunk);
        }

        this.ox = nx;
        this.oy = ny;

        this.light.setNewPosition((int) nx, (int) ny);

        CellPos playerCellPos = this.getCellPos();
        int count = 0;
        for (VisibleChunkRay visibleChunkRay : visibleChunkRays) {
            for (CellPos cellPositionOffset : visibleChunkRay.cellPositionOffsets) {
                int targetX = playerCellPos.x + cellPositionOffset.x;
                int targetY = playerCellPos.y + cellPositionOffset.y;

                //  world.setCell(CellType.SOLID.STONE, cellPositionOffset.x, cellPositionOffset.y);

                if (world.getCell(targetX, targetY).isSolid()) break;
                world.getChunkFromCellPos(targetX, targetY).setVisibleByPlayer(true);
                count++;
            }
        }

        System.out.println("PLAYER RAY WORLD GET CHUNK CALLS: " + count);
    }

    private void enteredNewChunk(Chunk previousChunk, Chunk newChunk) {
        if (newChunk == null) return;

        int newChunkX = newChunk.getPosX();
        int newChunkY = newChunk.getPosY();

        loadUnloadAndSetUpdatingChunks(newChunkX, newChunkY, true);
        setRenderingChunks(newChunkX, newChunkY);
    }

    private void loadUnloadAndSetUpdatingChunks(int centerChunkX, int centerChunkY, boolean async) {

        Set<ChunkPos> newUpdatedChunks = new HashSet<>();
        Set<ChunkPos> newLoadedChunks = new HashSet<>();

        int updateWidth = WorldConstants.CHUNK_LOADING.UPDATE_WIDTH;
        int updateHeight = WorldConstants.CHUNK_LOADING.UPDATE_HEIGHT;
        int loadWidth = WorldConstants.CHUNK_LOADING.LOAD_WIDTH;
        int loadHeight = WorldConstants.CHUNK_LOADING.LOAD_HEIGHT;
        int unloadWidth = WorldConstants.CHUNK_LOADING.UNLOAD_WIDTH;
        int unloadHeight = WorldConstants.CHUNK_LOADING.UNLOAD_HEIGHT;

        for (int i = -loadWidth / 2; i <= loadWidth / 2; i++) {
            for (int j = -loadHeight / 2; j <= loadHeight / 2; j++) {
                ChunkPos chunkPos = new ChunkPos(centerChunkX + i, centerChunkY + j);

                if (MathUtil.isPointInEllipse(i, j, 0, 0, updateWidth, updateHeight))
                    newUpdatedChunks.add(chunkPos);

                if (MathUtil.isPointInEllipse(i, j, 0, 0, loadWidth, loadHeight))
                    newLoadedChunks.add(chunkPos);
            }
        }

        for (Map.Entry<ChunkPos, Chunk> entry : world.chunkManager.getChunkLUT().entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            Chunk chunk = entry.getValue();

            if (!MathUtil.isPointInEllipse(chunkPos.x, chunkPos.y, centerChunkX, centerChunkY, unloadWidth, unloadHeight)) {
                if (async)
                    world.unloadChunkAsync(chunk);
                else
                    world.unloadChunk(chunk);
                continue;
            }

            if (newUpdatedChunks.contains(chunkPos)) {
                if (!chunk.isUpdating()) chunk.setUpdating(true);
            } else {
                chunk.setUpdating(false);
            }
        }

        for (ChunkPos newLoadedChunkPos : newLoadedChunks) {
            if (async)
                world.loadChunkAsync(newLoadedChunkPos.x, newLoadedChunkPos.y);
            else
                world.loadChunk(newLoadedChunkPos.x, newLoadedChunkPos.y);
        }

    }

    public void setRenderingChunks(int chunkX, int chunkY) {
        int renderWidth = WorldConstants.CHUNK_LOADING.RENDER_WIDTH;
        int renderHeight = WorldConstants.CHUNK_LOADING.RENDER_HEIGHT;

        topLeftChunkPos = new ChunkPos(chunkX - renderWidth / 2, chunkY + renderHeight / 2);
        bottomRightChunkPos = new ChunkPos(chunkX + renderWidth / 2, chunkY - renderHeight / 2);

        for (int i = -renderWidth / 2; i <= renderWidth / 2; i++) {
            for (int j = -renderHeight / 2; j <= renderHeight / 2; j++) {
                renderingChunks.set(i + renderWidth / 2, j + renderHeight / 2, world.getChunkFromChunkPos(chunkX + i, chunkY + j));
            }
        }
    }

    public Array2D<Chunk> getRenderingChunks() {
        return renderingChunks;
    }
}
