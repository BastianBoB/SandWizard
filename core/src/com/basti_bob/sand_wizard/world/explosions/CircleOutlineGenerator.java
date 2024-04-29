package com.basti_bob.sand_wizard.world.explosions;

import com.basti_bob.sand_wizard.world.coordinates.CellPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CircleOutlineGenerator {


    public static final HashMap<Integer, List<CellPos>> radiusPointsMap = new HashMap<>();

    public static List<CellPos> getCircleOutLine(int radius) {

        return radiusPointsMap.computeIfAbsent(radius, CircleOutlineGenerator::createCircleOutline);
    }


    private static List<CellPos> createCircleOutline(int radius) {
        List<CellPos> outline = new ArrayList<>();

        int t1 = radius / 16;
        int x = radius;
        int y = 0;

        while (x >= y) {
            outline.add(new CellPos(x, y));
            outline.add(new CellPos(y, x));
            outline.add(new CellPos(-x, y));
            outline.add(new CellPos(-y, x));
            outline.add(new CellPos(-x, -y));
            outline.add(new CellPos(-y, -x));
            outline.add(new CellPos(x, -y));
            outline.add(new CellPos(y, -x));

            y++;
            t1 += y;

            int t2 = t1 - x;

            if (t2 >= 0) {
                t1 = t2;
                x--;
            }
        }

        return outline;
    }
}
