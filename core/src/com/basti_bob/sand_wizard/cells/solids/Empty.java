package com.basti_bob.sand_wizard.cells.solids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;

public class Empty extends Cell {

    private static Empty INSTANCE;

    private Empty() {
        super(CellType.EMPTY, null, -1, -1);
    }

    public Empty(CellType cellType, World world, int x, int y) {
        super(cellType, world, x, y);
    }

    @Override
    public CellType getCellType() {
        return CellType.EMPTY;
    }

    public static Empty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Empty();
        }
        return INSTANCE;
    }
}