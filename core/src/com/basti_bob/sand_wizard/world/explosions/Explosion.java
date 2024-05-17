package com.basti_bob.sand_wizard.world.explosions;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Explosion {

    public final World world;

    public final int x, y, radius;
    public final float strength;

    public static int a = 0;


    public Explosion(World world, int x, int y, int radius, float strength) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.strength = strength;
    }

    private enum VisitedState {
        NOT_STOPPED, STOPPED
    }

    public void explode() {

        int iterateRadius = (int) (radius * 1.5f);

        VisitedState[][] visitedPositions = new VisitedState[iterateRadius * 2][iterateRadius * 2];

        Set<CellPos> circleOutlinePositions = CircleOutlineGenerator.getCircleOutLine(iterateRadius);

        List<Pair<Cell, Float>> toExplodeCells = new ArrayList<>();

        float invRadius = 1f / radius;

        a = 0;

        for (CellPos offsetPos : circleOutlinePositions) {

            float dx = Math.abs(offsetPos.x);
            float dy = Math.abs(offsetPos.y);

            float factor = Math.max(dx, dy) * invRadius;

            toExplodeCells.addAll(explosionStripe(offsetPos.x, offsetPos.y, iterateRadius, strength * factor, visitedPositions));
        }

        for (Pair<Cell, Float> pair : toExplodeCells) {
            pair.getLeft().explode(world, x, y, pair.getRight());
        }

        System.out.println(a);
    }

    public List<Pair<Cell, Float>> explosionStripe(int xOff, int yOff, int iterateRadius, float strength, VisitedState[][] visitedPositions) {
        List<Pair<Cell, Float>> toExplodeCells = new ArrayList<>();

        float currentStrength = strength;
        boolean stopped = false;

        float stain = 1f;

        float stainRadius = radius * world.random.nextFloat(0.2f, 0.5f);

        int x1 = 0;
        int y1 = 0;

        int dx = Math.abs(xOff);
        int dy = Math.abs(yOff);
        int sx = 0 < xOff ? 1 : -1;
        int sy = 0 < yOff ? 1 : -1;
        int err = dx - dy;

        while (x1 != xOff || y1 != yOff) {
            a++;

            int err2 = 2 * err;
            if (err2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (err2 < dx) {
                err += dx;
                y1 += sy;
            }

            float dstSqr = x1 * x1 + y1 * y1;
            if (dstSqr > radius * radius) stopped = true;

            int explodeX = x + x1;
            int explodeY = y + y1;

            int arrayIndexX = x1 + iterateRadius;
            int arrayIndexY = y1 + iterateRadius;

            VisitedState visitedState = visitedPositions[arrayIndexX][arrayIndexY];
            if (visitedState == null) {
                visitedPositions[arrayIndexX][arrayIndexY] = stopped ? VisitedState.STOPPED : VisitedState.NOT_STOPPED;
            } else {
                switch (visitedState) {
                    case NOT_STOPPED -> {
                        continue;
                    }
                    case STOPPED -> {
                        stopped = true;
                    }
                }
            }


            Cell cell = world.getCell(explodeX, explodeY);
            if (cell == null) continue;

            if (cell.getExplosionResistance() > 1) {
                return toExplodeCells;
            }

            if (stopped) {
                stain -= Math.random() * 3 + 2;
                if (stain <= 0) {
                    return toExplodeCells;
                }
                //cell.darken(stain);
                continue;
            }

            if (cell.getCellType() == CellType.GAS.EXPLOSION_SPARK) {
                continue;
            }

            if (cell instanceof Empty) {
                if (world.random.nextFloat() < 0.05f) {
                    world.setCell(CellType.GAS.EXPLOSION_SPARK.createCell(), explodeX, explodeY);
                }
                continue;
            }

            float cellHealth = cell.getExplosionHealth();

            boolean wouldExplode = cell.wouldExplode(world, explodeX, explodeY, currentStrength);
            toExplodeCells.add(Pair.of(cell, currentStrength));

            if (wouldExplode) {
                currentStrength -= cellHealth;
            } else {
                //cell.applyHeating(world, 100);

                stopped = true;
                stain -= Math.random() * 3 + 2;
                if (stain <= 0) {
                    return toExplodeCells;
                }
            }
            //cell.darken(stain);
        }

        return toExplodeCells;
    }


}

