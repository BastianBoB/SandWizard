package com.basti_bob.sand_wizard.cells.gases;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class Fire extends Gas {
    public Fire(CellType cellType) {
        super(cellType);
    }

    @Override
    public void updateMoving(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.updateMoving(chunkAccessor, updateDirection);
    }

    @Override
    public void updateBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {

        if (world.random.nextFloat() > 0.2) return;

        Cell[][] neighbourCells = getNeighbourCells(chunkAccessor, this.getPosX(), this.getPosY());

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Cell cell = neighbourCells[i + 1][j + 1];

                if (cell == null || cell instanceof Empty) continue;

                cell.transferTemperature(chunkAccessor, 1500f, 0.05f);
            }
        }
    }

    @Override
    public boolean isBurning() {
        return true;
    }

}
