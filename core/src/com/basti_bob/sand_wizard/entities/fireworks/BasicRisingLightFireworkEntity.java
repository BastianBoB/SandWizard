package com.basti_bob.sand_wizard.entities.fireworks;

import com.badlogic.gdx.graphics.Color;
import com.basti_bob.sand_wizard.cells.gases.Fire;
import com.basti_bob.sand_wizard.entities.ChunkUpdatingEntity;
import com.basti_bob.sand_wizard.entities.EntityHitBox;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.ChunkLight;

public class BasicRisingLightFireworkEntity extends FireworkEntity {

    private int explodeTimer = 70;
    private final ChunkLight innerLight, outerLight;

    public BasicRisingLightFireworkEntity(World world, float x, float y, Color color) {
        super(world, x, y, new EntityHitBox(1, 1));
        outerLight = new ChunkLight(x, y, color.r, color.g, color.b, 6, 5);
        addLight(outerLight);

        innerLight = new ChunkLight(x, y, Color.YELLOW.r, Color.YELLOW.g, Color.YELLOW.b, 3, 10);
        addLight(innerLight);
        this.yVel = 10;
    }


    @Override
    public void update() {
        super.update();

        float add = ((float) Math.sin(this.getUpdateTimes() / 3f) + 1) * 2f;
        outerLight.setRadius(6 + add);
        outerLight.setIntensity(5 + add);

        if (explodeTimer < 10) {
            innerLight.setRadius(3 - (1 - explodeTimer / 10f) * 3);
            outerLight.setRadius(6 - (1 - explodeTimer / 10f) * 6);
        }

        if (--explodeTimer == 0) {
            explode();
        }
    }

    public void explode() {
        die();
    }
}
