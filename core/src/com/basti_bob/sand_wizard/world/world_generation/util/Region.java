package com.basti_bob.sand_wizard.world.world_generation.util;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;

import java.awt.*;

public class Region {

    public final int startX, startY, endX, endY;
    public final CellPos center;
    public final int centerX, centerY;

    public Region(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.centerX = (int) ((startX + endX) / 2f);
        this.centerY = (int) ((startY + endY) / 2f);
        this.center = new CellPos(centerX, centerY);
    }

    public float getDistanceToCenter(float x, float y) {
        float dx = x - centerX;
        float dy = y - centerY;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float getMaximumDistanceToCenter() {
        return getDistanceToCenter(endX, endY);
    }
}
