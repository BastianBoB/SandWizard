package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class Dirt extends MovableSolid {

    public Dirt(World world, int x, int y) {
        super(world, x, y);
    }

    @Override
    public CellType getCellType() {
        return CellType.DIRT;
    }

    @Override
    public float getMovingResistance() {
        return 0.9f;
    }
}
