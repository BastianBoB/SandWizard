package com.basti_bob.sand_wizard.cells.solids.movable_solids;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.cells.CellType;

public class Sand extends MovableSolid {

    public Sand(World world, int x, int y) {
        super(world, x, y);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public CellType getCellType() {
        return CellType.SAND;
    }

    @Override
    public float getMovingResistance() {
        return 0.9f;
    }
}
