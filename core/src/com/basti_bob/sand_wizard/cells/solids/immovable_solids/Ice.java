package com.basti_bob.sand_wizard.cells.solids.immovable_solids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class Ice extends ImmovableSolid {
    public Ice(CellType cellType, World world, int x, int y) {
        super(cellType, world, x, y);
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

//        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.posX, this.posY);
//
//        for (int i = 0; i < 4; i++) {
//            Cell cell = directNeighbourCells[i];
//
//            if (cell instanceof Empty || cell == null) continue;
//
//            cell.transferTemperature(chunkAccessor, -5f, 0.01f);
//        }
    }

    @Override
    public void startedBurning(ChunkAccessor chunkAccessor) {
        super.startedBurning(chunkAccessor);

        replace(CellType.WATER, chunkAccessor);
    }


}
