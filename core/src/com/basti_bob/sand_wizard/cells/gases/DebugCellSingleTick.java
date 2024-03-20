package com.basti_bob.sand_wizard.cells.gases;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class DebugCellSingleTick extends Cell {

    public DebugCellSingleTick(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
    }

    @Override
    public int getMaxBurningTime() {
        return 1;
    }

    @Override
    public float getFireSpreadChance() {
        return 0;
    }

    @Override
    public boolean isBurning() {
        return true;
    }
}
