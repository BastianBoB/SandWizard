package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class Acid extends Liquid {
    public Acid(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
    }

    @Override
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.updateMoving(chunkAccessor, updateDirection);

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.posX, this.posY);

        boolean corrodedCell = false;
        for (int i = 0; i < 4; i++) {
            Cell cell = directNeighbourCells[i];

            if (cell == null || cell instanceof Acid) continue;

            corrodedCell = cell.applyCorrosion(1f);
        }

        if(corrodedCell) chunkAccessor.cellActivatesChunk(this.posX, this.posY);
    }
}
