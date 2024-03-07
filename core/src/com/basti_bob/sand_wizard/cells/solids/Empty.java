package com.basti_bob.sand_wizard.cells.solids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;

public class Empty extends Cell {

    public Empty(CellType cellType, World world, int x, int y) {
        super(cellType, world, x, y);
    }
}