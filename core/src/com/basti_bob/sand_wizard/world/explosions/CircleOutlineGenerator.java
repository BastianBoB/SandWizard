package com.basti_bob.sand_wizard.world.explosions;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;

import java.util.*;

public class CircleOutlineGenerator {


    public static final HashMap<Integer, Set<CellPos>> radiusPointsMap = new HashMap<>();

    public static Set<CellPos> getCircleOutLine(int radius) {

        return radiusPointsMap.computeIfAbsent(radius, CircleOutlineGenerator::midPointCircle);
    }


    private static Set<CellPos> midPointCircle(int radius) {
        Set<CellPos> outline = new HashSet<>();

        int x = 0;
        int y = radius;
        int d = 1 - radius;

        drawCirclePoints(outline, x, y);

        while (y > x) {
            if (d < 0) {
                d += 2 * x + 3;
            } else {
                int xOff = x > 0 ? 1 : - 1;
                //drawCirclePoints(outline, x + xOff, y);

                d += 2 * (x - y) + 5;
                y--;
            }
            x++;
            drawCirclePoints(outline, x, y);
        }

        return outline;
    }

    private static void drawCirclePoints(Set<CellPos> outline, int x, int y) {
        // Add points for the current circle quadrant
        outline.add(new CellPos(x, y));
        outline.add(new CellPos(-x, y));
        outline.add(new CellPos(x, -y));
        outline.add(new CellPos(-x, -y));
        outline.add(new CellPos(y, x));
        outline.add(new CellPos(y, -x));
        outline.add(new CellPos(-y, x));
        outline.add(new CellPos(-y, -x));
    }

}
