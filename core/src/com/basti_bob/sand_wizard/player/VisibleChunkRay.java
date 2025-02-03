package com.basti_bob.sand_wizard.player;

import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class VisibleChunkRay {

    private final CellPos endCellPos;
    public final List<CellPos> cellPositionOffsets;

    public VisibleChunkRay(int endX, int endY) {
        cellPositionOffsets = pathToPoint(endX, endY);
        this.endCellPos = new CellPos(endX, endY);
    }

    public CellPos getEndCellPos() {
        return endCellPos;
    }

    private List<CellPos> pathToPoint(int targetX, int targetY) {
        List<CellPos> points = new ArrayList<>();

        int xDistance = Math.abs(targetX);
        int yDistance = Math.abs(targetY);

        if(targetX == 0 && targetY == 0) return points;

        int steps = Math.max(xDistance, yDistance);

        if (xDistance > yDistance) {
            float slope = targetY / (float) targetX;

            for (int i = 0; i <= steps; i++) {
                int x = targetX > 0 ? i : -i;
                float y = x * slope;

                points.add(new CellPos(x, (int) y));
            }
        } else {
            float slope = targetX / (float) targetY;

            for (int i = 0; i <= steps; i++) {
                int y = targetY > 0 ? i : -i;
                float x = y * slope;

                points.add(new CellPos((int) x, y));
            }
        }

        return points;
    }
}
