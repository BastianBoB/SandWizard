package com.basti_bob.sand_wizard.cells.other;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;

public class Empty extends Cell {
    public Empty(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
    }
}
