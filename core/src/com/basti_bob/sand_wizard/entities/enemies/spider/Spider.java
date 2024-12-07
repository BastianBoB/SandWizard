package com.basti_bob.sand_wizard.entities.enemies.spider;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.entities.Entity;
import com.basti_bob.sand_wizard.entities.EntityHitBox;
import com.basti_bob.sand_wizard.util.Direction;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;

import java.util.LinkedList;
import java.util.Queue;

public class Spider extends Entity {


    private final FabrikChain[] legs;

    private final int bodySize = 4;
    private final int legSearchDistance = 10;
    private final int legSearchRadius = 30;
    private final int numLegs = 8;

    public Spider(World world, float x, float y) {
        super(world, x, y, new EntityHitBox(5, 5));

        legs = new FabrikChain[numLegs];
        for (int i = 0; i < numLegs; i++) {

            FabrikChain leg = new FabrikChain(x, y);
            leg.addSegment(6);
            leg.addSegment(9);
            legs[i] = leg;
        }
    }

    @Override
    protected void updateMoving() {
//        xVel += (SandWizard.player.getHeadPosition().x - nx) * 0.01f;
//        yVel += (SandWizard.player.getHeadPosition().y - ny) * 0.01f;

        super.updateMoving();



        //moveTo(MathUtil.lerp(nx, SandWizard.player.getHeadPosition().x, 0.05f), MathUtil.lerp(ny, SandWizard.player.getHeadPosition().y, 0.05f));
    }

    public void update() {
        super.update();

        for (int i = 0; i < numLegs; i++) {
            FabrikChain leg = legs[i];
            float angle = MathUtil.TWO_PI / numLegs * (i + 0.5f);

            float newLegBaseX = (float) (nx + bodySize * Math.cos(angle));
            float newLegBaseY = (float) (ny + bodySize * Math.sin(angle));
            leg.setBasePosition(newLegBaseX, newLegBaseY);

            float baseToTargetDist = MathUtil.dist(newLegBaseX, newLegBaseY, leg.getTargetX(), leg.getTargetY());

            if (baseToTargetDist < legSearchDistance * 0.75 || baseToTargetDist > legSearchDistance * 1.25) {
                setLegsTargetPosition(leg, angle);
                leg.resetSegmentsWithAngle(angle);
            }

            leg.update();
        }
    }

    private void setLegsTargetPosition(FabrikChain leg, float angle) {

        int searchPositionDistance = bodySize + legSearchDistance;

        int searchX = (int) (nx + searchPositionDistance * Math.cos(angle));
        int searchY = (int) (ny + searchPositionDistance * Math.sin(angle));

        CellPos closestSolidCellAlongPath = getClosestSolidCellAlongPath(leg.getBaseX(), leg.getBaseY(), searchX, searchY);
        if (closestSolidCellAlongPath != null) {
            leg.setTargetPosition(closestSolidCellAlongPath.x, closestSolidCellAlongPath.y);
            return;
        }

        CellPos closestSolidCellInRadius = getClosestSolidCellInRadius(searchX, searchY, legSearchRadius);
        if (closestSolidCellInRadius != null) {
            leg.setTargetPosition(closestSolidCellInRadius.x, closestSolidCellInRadius.y);
            return;
        }

        leg.setTargetPosition(searchX, searchY);
    }

    public void render(Camera camera, ShapeRenderer shapeRenderer) {
        int s = WorldConstants.CELL_SIZE;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(nx * s, ny * s, bodySize * s);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle((nx + bodySize / 2f) * s, (ny + bodySize / 3f) * s, (bodySize / 5f) * s);
        shapeRenderer.circle((nx - bodySize / 2f) * s, (ny + bodySize / 3f) * s, (bodySize / 5f) * s);
        shapeRenderer.setColor(Color.BLACK);


        for (FabrikChain leg : legs) {
            int size = 4;
            for (FabrikSegment segment : leg.getSegments()) {
                shapeRenderer.rectLine(segment.startX * s, segment.startY * s, segment.endX * s, segment.endY * s, size);
                size --;
            }
        }
        shapeRenderer.end();

        super.render(camera, shapeRenderer);
    }

    private CellPos getClosestSolidCellAlongPath(float x1, float y1, float x2, float y2) {

        int xDistance = (int) Math.abs(x2 - x1);
        int yDistance = (int) Math.abs(y2 - y1);

        boolean positiveX = (x2 - x1) > 0;
        boolean positiveY = (y2 - y1) > 0;

        int steps = Math.max(xDistance, yDistance);

        if (xDistance > yDistance) {
            float slope = Math.abs((y2 - y1) / (x2 - x1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i : -i;
                float y = positiveY ? i * slope : -i * slope;

                if (world.getCell((int) (x1 + x), (int) (y1 + y)).isSolid())
                    return new CellPos((int) (x1 + x), (int) (y1 + y));
            }
        } else {
            float slope = Math.abs((x2 - x1) / (y2 - y1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i * slope : -i * slope;
                float y = positiveY ? i : -i;

                if (world.getCell((int) (x1 + x), (int) (y1 + y)).isSolid())
                    return new CellPos((int) (x1 + x), (int) (y1 + y));
            }
        }

        return null;
    }

    private CellPos getClosestSolidCellInRadius(int startX, int startY, int maxRadius) {
        Queue<int[]> queue = new LinkedList<>();
        boolean visited[][] = new boolean[maxRadius * 2 + 1][maxRadius * 2 + 1];

        queue.offer(new int[]{startX, startY, 0});
        visited[maxRadius][maxRadius] = true;

        while (!queue.isEmpty()) {
            int[] position = queue.poll();
            int x = position[0];
            int y = position[1];
            int distance = position[2];

            Cell cell = world.getCell(x, y);
            if (cell == null || cell.isSolid()) {
                return new CellPos(x, y);
            }

            if (distance >= maxRadius) return null;

            for (Direction direction : Direction.values()) {
                int newX = x + direction.getXOff();
                int newY = y + direction.getYOff();

                int arrayX = newX - startX + maxRadius;
                int arrayY = newY - startY + maxRadius;

                if (visited[arrayX][arrayY]) continue;

                visited[arrayX][arrayY] = true;
                queue.offer(new int[]{newX, newY, distance + 1});
            }
        }

        return null;
    }

}
