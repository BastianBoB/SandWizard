package com.basti_bob.sand_wizard.cells.solids;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.cells.Cell;

public abstract class Solid extends Cell {

    public Solid(World world, int x, int y) {
        super(world, x, y);
    }
}
