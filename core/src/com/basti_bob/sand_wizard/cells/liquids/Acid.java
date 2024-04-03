package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class Acid extends Liquid {

    private boolean didCorrodeInUpdate;
    public Acid(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.posX, this.posY);

        didCorrodeInUpdate = false;

        for (int i = 0; i < 4; i++) {
            Cell cell = directNeighbourCells[i];

            if (cell == null || cell instanceof Acid) continue;

            didCorrodeInUpdate = cell.applyCorrosion(chunkAccessor, (float) (1f * Math.random()));
        }
    }

    @Override
    public boolean shouldActiveChunk() {
        return didCorrodeInUpdate;
    }
}
