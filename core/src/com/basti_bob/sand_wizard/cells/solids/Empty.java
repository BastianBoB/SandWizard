package com.basti_bob.sand_wizard.cells.solids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;

public class Empty extends Cell {

    private static Empty INSTANCE;

    private Empty() {
        super(null, -1, -1);
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

    @Override
    public void update() {

    }
}