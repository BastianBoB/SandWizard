package com.basti_bob.sand_wizard.world_generation;

public class Region {

    public int startX, startY, endX, endY;

    public Region(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public Point getCenter() {
        return new Point((int) ((startX + endX) / 2f), (int) ((startY + endY) / 2f));
    }

    public float getDistanceToCenter(float x, float y) {
        Point center = getCenter();
        float dx = x - center.x;
        float dy = y - center.y;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float getMaximumDistanceToCenter() {
        return getDistanceToCenter(endX, endY);
    }
}
