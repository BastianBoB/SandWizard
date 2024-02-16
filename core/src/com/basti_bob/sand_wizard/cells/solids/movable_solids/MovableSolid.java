package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.cells.solids.Solid;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public abstract class MovableSolid extends Solid {

    private boolean moving;

    public MovableSolid(World world, int x, int y) {
        super(world, x, y);
    }

    public float getMovingResistance() {
        return 0;
    }

    public void setMoving() {
        this.moving = this.moving || MathUtils.random() > this.getMovingResistance();
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, int inChunkX, int inChunkY, boolean updateDirection) {
        super.update(chunkAccessor, inChunkX, inChunkY, updateDirection);
//
//
        if (chunkAccessor.moveToIfEmpty(this, posX, posY - 1)) return;
        if (updateDirection) {
            if (chunkAccessor.moveToIfEmpty(this, posX + 1, posY - 1)) return;
            chunkAccessor.moveToIfEmpty(this, posX - 1, posY - 1);
        } else {
            if (chunkAccessor.moveToIfEmpty(this, posX - 1, posY - 1)) return;
            chunkAccessor.moveToIfEmpty(this, posX + 1, posY - 1);

        }
    }

}
