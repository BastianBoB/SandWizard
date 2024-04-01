package com.basti_bob.sand_wizard.entities;

public class EntityHitBox {

    private final float width, height;

    public EntityHitBox(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
