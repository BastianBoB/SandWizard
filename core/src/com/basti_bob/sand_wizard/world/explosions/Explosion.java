package com.basti_bob.sand_wizard.world.explosions;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.util.MoveAlongState;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;

import java.util.HashMap;
import java.util.List;

public class Explosion {

    public final World world;

    public final int x, y, radius;
    public final float strength;


    public Explosion(World world, int x, int y, int radius, float strength) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.strength = strength;
    }

    public void explode() {

        HashMap<CellPos, Boolean> visitedPositions = new HashMap<>();

        List<CellPos> circleOutlinePositions = CircleOutlineGenerator.getCircleOutLine(radius);

        for (CellPos offsetPos : circleOutlinePositions) {
            //world.setCell(CellType.STONE, x + offsetPos.x, y + offsetPos.y);

            explosionStripe(x, y, x + offsetPos.x, y + offsetPos.y, visitedPositions);
        }
    }

    public void explosionStripe(int x1, int y1, int x2, int y2, HashMap<CellPos, Boolean> visitedPositions) {
        boolean stopped = false;

        float stain = 1f;

        float stainRadius = radius * world.random.nextFloat(0.2f, 0.5f);

        float dx = x2 - x1;
        float dy = y2 - y1;

        float xDistance = Math.abs(dx);
        float yDistance = Math.abs(dy);

        boolean positiveX = dx > 0;
        boolean positiveY = dy > 0;

        float xSlope = Math.abs(dy / dx);
        float ySlope = Math.abs(dx / dy);

        float totalRadius = radius + stainRadius;

        for (int i = 1; i <= totalRadius; i++) {
            float x, y;

            if (xDistance > yDistance) {
                x = positiveX ? i : -i;
                y = positiveY ? i * xSlope : -i * xSlope;
            } else {
                x = positiveX ? i * ySlope : -i * ySlope;
                y = positiveY ? i : -i;
            }

            float dstSqr = x * x + y * y;
            if (dstSqr > totalRadius * totalRadius) return;
            if (dstSqr > radius * radius) stopped = true;


            int explodeX = (int) (x1 + x);
            int explodeY = (int) (y1 + y);


            CellPos pos = new CellPos(explodeX, explodeY);

            Boolean stoppedAtPos = visitedPositions.get(pos);
            if (stoppedAtPos != null) {

                if (stoppedAtPos) {
                    stopped = true;
                } else {
                    continue;
                }
            }

            Cell cell = world.getCell(explodeX, explodeY);

            if (cell.getExplosionResistance() > 1) {
                visitedPositions.put(pos, true);
                return;
            }

            if (stopped) {
                stain -= Math.random() * 3 + 2;
                if (stain <= 0) {
                    return;
                }
                //cell.darken(stain);
                continue;
            }

            if (cell instanceof Empty) {
                if (world.random.nextFloat() < 0.01f) {
                    //world.setCell(CellType.FIRE, explodeX, explodeY);
                }

                visitedPositions.put(pos, false);
                continue;
            }

            boolean didExplode = cell.explode(world, x1, y1, strength);
            cell.applyHeating(world, 100);

            if (didExplode) {
                visitedPositions.put(pos, false);
            } else {
                visitedPositions.put(pos, true);
                stopped = true;
                stain -= Math.random() * 3 + 2;
                if (stain <= 0) {
                    return;
                }
                //cell.darken(stain);
            }
        }
    }
}
