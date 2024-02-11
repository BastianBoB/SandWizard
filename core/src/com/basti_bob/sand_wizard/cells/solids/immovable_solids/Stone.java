package com.basti_bob.sand_wizard.cells.solids.immovable_solids;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;

public class Stone extends ImmovableSolid {

    public Stone(World world, int x, int y) {
        super(world, x, y);
    }

    @Override
    public CellType getCellType() {
        return CellType.STONE;
    }
}
