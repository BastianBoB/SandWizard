package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.ImmovableSolid;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class PowderSnow extends MovableSolid {
    public PowderSnow(CellType cellType, World world, int x, int y) {
        super(cellType, world, x, y);
    }

    @Override
    public void startedBurning(ChunkAccessor chunkAccessor) {
        super.startedBurning(chunkAccessor);

        if(Math.random() < 0.5) {
            replace(CellType.WATER, chunkAccessor);
        } else {
            die(chunkAccessor);
        }
    }

    @Override
    public Vector2 getGravity() {
        return new Vector2(0, -1);
    }
}
