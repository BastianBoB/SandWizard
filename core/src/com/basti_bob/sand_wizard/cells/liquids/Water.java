package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class Water extends Liquid {

    public Water(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
    }
    @Override
    public void updateBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        replace(CellType.STEAM, chunkAccessor);
    }

    @Override
    public boolean finishedBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {
        replace(CellType.STEAM, chunkAccessor);
        return true;
    }
}
