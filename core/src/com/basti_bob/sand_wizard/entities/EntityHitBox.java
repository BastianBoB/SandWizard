package com.basti_bob.sand_wizard.entities;

import com.basti_bob.sand_wizard.cells.PhysicalState;

import java.util.List;

public class EntityHitBox {

    private float width, height;

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
