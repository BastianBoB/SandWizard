package com.basti_bob.sand_wizard.cells.gases;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class Fire extends Gas {
    public Fire(CellType cellType, World world, int posX, int posY) {
        super(cellType, world, posX, posY);
    }

    @Override
    public void updateBurning(ChunkAccessor chunkAccessor, boolean updateDirection) {

        if (Math.random() > 0.2) return;

        Cell[][] neighbourCells = getNeighbourCells(chunkAccessor, this.posX, this.posY);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Cell cell = neighbourCells[i + 1][j + 1];

                if (cell == null || cell instanceof Empty) continue;

                cell.transferTemperature(chunkAccessor, 1500f, 0.1f);
            }
        }
    }

    @Override
    public boolean isBurning() {
        return true;
    }

    @Override
    public int getMaxBurningTime() {
        return super.getMaxBurningTime();
    }
}
