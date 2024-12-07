package com.basti_bob.sand_wizard.entities.item_entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.entities.Entity;
import com.basti_bob.sand_wizard.entities.EntityHitBox;
import com.basti_bob.sand_wizard.world.World;

public class ItemEntity extends Entity {
    public ItemEntity(World world, float x, float y) {
        super(world, x, y, new EntityHitBox(2, 2));
    }

    @Override
    public void render(Camera camera, ShapeRenderer shapeRenderer) {
        super.render(camera, shapeRenderer);
    }
}
