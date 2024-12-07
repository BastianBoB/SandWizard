package com.basti_bob.sand_wizard.entities.enemies.spider;

public class FabrikSegment {

    float startX, startY, endX, endY, length;

    public FabrikSegment(float startX, float startY, float angle, float length) {
        this.length = length;

        reset(startX, startY, angle);
    }

    public void setEnd(float x, float y) {
        this.endX = x;
        this.endY = y;

        float angle = (float) Math.atan2(endY - startY, endX - startX);
        startX = (float) (endX - Math.cos(angle) * length);
        startY = (float) (endY - Math.sin(angle) * length);
    }

    public void reset(float startX, float startY, float angle) {
        this.startX = startX;
        this.startY = startY;

        this.endX = (float) (startX + Math.cos(angle) * length);
        this.endY = (float) (startY + Math.sin(angle) * length);
    }

    public void setStart(float x, float y) {
        this.startX = x;
        this.startY = y;

        float angle = (float) Math.atan2(endY - startY, endX - startX);
        endX = (float) (startX + Math.cos(angle) * length);
        endY = (float) (startY + Math.sin(angle) * length);
    }
}
