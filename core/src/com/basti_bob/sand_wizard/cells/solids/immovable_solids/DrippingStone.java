package com.basti_bob.sand_wizard.cells.solids.immovable_solids;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class DrippingStone extends ImmovableSolid {

    private final CellType toCreateCell;
    private final IntRange intervalRange;

    private int timeUntilActive;

    public DrippingStone(CellType cellType, CellType toCreateCell, IntRange intervalRange) {
        super(cellType);
        this.toCreateCell = toCreateCell;
        this.intervalRange = intervalRange;
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        timeUntilActive--;
        if (timeUntilActive < 0) {

            dripCell();

            if (timeUntilActive < 0) {
                timeUntilActive = intervalRange.getRandom(world.random);
            }
        }
    }

    public void dripCell() {
        world.setCellIfEmpty(toCreateCell, this.getPosX(), this.getPosY() - 1);
    }

    @Override
    public boolean shouldActiveChunk() {
        return true;
    }
}
