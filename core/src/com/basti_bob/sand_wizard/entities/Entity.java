package com.basti_bob.sand_wizard.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.util.Direction;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.WorldLight;

public class Entity {

    protected final EntityHitBox hitBox;
    protected final World world;
    protected float ox;
    protected float oy;
    protected float nx;
    protected float ny;
    protected float xVel;
    protected float yVel;

    public Entity(World world, float x, float y, EntityHitBox hitBox) {
        this.world = world;
        this.ox = x;
        this.oy = y;
        this.nx = x;
        this.ny = y;
        this.hitBox = hitBox;
    }

    public void update() {
        applyGravity();
        applyVelocityFriction();
        updateMoving();
        updatePosition();
    }

    protected void applyGravity() {
        this.yVel += WorldConstants.GRAVITY.y;
    }

    protected void applyVelocityFriction() {
        this.xVel *= 0.95;
        this.yVel *= 0.95;
    }

    protected void updateMoving() {
        moveWithVelocity();
    }

    private void updatePosition() {
        this.ox = nx;
        this.oy = ny;
    }

    private void moveWithVelocity() {
        clampVelocity();

        float targetX = ox + xVel;
        float targetY = oy + yVel;

        if ((int) targetY == (int) oy && (int) targetX == (int) ox) return;

        int xDistance = (int) Math.abs(xVel);
        int yDistance = (int) Math.abs(yVel);

        boolean positiveX = xVel > 0;
        boolean positiveY = yVel > 0;

        float lastValidX = ox;
        float lastValidY = oy;

        int steps = Math.max(xDistance, yDistance);

        float slope = xDistance > yDistance ? Math.abs(yVel / xVel) : Math.abs(xVel / yVel);

        int step;
        for (step = 1; step <= steps; step++) {
            float xOff, yOff;

            if (xDistance > yDistance) {
                xOff = positiveX ? step : -step;
                yOff = positiveY ? step * slope : -step * slope;
            } else {
                xOff = positiveX ? step * slope : -step * slope;
                yOff = positiveY ? step : -step;
            }

            float checkX = ox + xOff;
            float checkY = oy + yOff;

            if (hitBox.collidesWithCell(world, checkX, checkY, xVel > 0 ? Direction.RIGHT : Direction.LEFT, Cell::isSolid)) {
                this.nx = lastValidX;
                this.xVel = 0;
                break;
            }

            if (hitBox.collidesWithCell(world, checkX, checkY, yVel > 0 ? Direction.UP : Direction.DOWN, Cell::isSolid)) {
                this.ny = lastValidY;
                this.yVel = 0;
                break;
            }

            lastValidX = checkX;
            lastValidY = checkY;
        }

        if(step == steps + 1) {
            this.nx = lastValidX;
            this.ny = lastValidY;
        }

    }

    public boolean wouldCollideVertically(int x, int y) {
        int offsetX = (int) Math.ceil(getHitBox().getWidth() / 2f);

        for (int i = -offsetX + 1; i <= offsetX; i++) {
            Cell cell = getWorld().getCell(x + i, y);

            if (cell == null) return false;

            if (cell.isSolid())
                return true;
        }
        return false;
    }

    public void clampVelocity() {
        if (xVel > WorldConstants.CHUNK_SIZE) xVel = WorldConstants.CHUNK_SIZE;
        else if (xVel < -WorldConstants.CHUNK_SIZE) xVel = -WorldConstants.CHUNK_SIZE;

        if (yVel > WorldConstants.CHUNK_SIZE) yVel = WorldConstants.CHUNK_SIZE;
        else if (yVel < -WorldConstants.CHUNK_SIZE) yVel = -WorldConstants.CHUNK_SIZE;
    }

    public void render(Camera camera, ShapeRenderer shapeRenderer) {
        if(WorldConstants.RENDER_ENTITY_HITBOX) {
            int s = WorldConstants.CELL_SIZE;

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLUE);

            float rw = hitBox.getWidth() * s;
            float rh = hitBox.getHeight() * s;

            shapeRenderer.rect(this.nx * s - rw / 2f, this.ny * s - rh / 2f, rw, rh);
            shapeRenderer.end();
        }
    }

    public void moveTo(float x, float y) {
        this.nx = x;
        this.ny = y;
    }

    public void moveBy(float x, float y) {
        moveTo(ox + x, oy + y);
    }

    public EntityHitBox getHitBox() {
        return hitBox;
    }

    public World getWorld() {
        return world;
    }

    public float getxVel() {
        return xVel;
    }

    public float getyVel() {
        return yVel;
    }

    public void setxVel(float xVel) {
        this.xVel = xVel;
    }

    public void setyVel(float yVel) {
        this.yVel = yVel;
    }
}
