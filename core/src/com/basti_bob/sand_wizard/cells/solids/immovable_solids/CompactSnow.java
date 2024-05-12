package com.basti_bob.sand_wizard.cells.solids.immovable_solids;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class CompactSnow extends ImmovableSolid {
    public CompactSnow(CellType cellType) {
        super(cellType);
    }

    @Override
    public void startedBurning(ChunkAccessor chunkAccessor) {
        super.startedBurning(chunkAccessor);

        replace(CellType.LIQUID.WATER, chunkAccessor);
    }

}
