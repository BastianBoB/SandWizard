package com.basti_bob.sand_wizard.cells.solids.immovable_solids;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.cells.solids.Solid;

public abstract class ImmovableSolid extends Solid {

    public ImmovableSolid(World world, int x, int y) {
        super(world, x, y);
    }
}
