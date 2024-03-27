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

        Cell[][] neighbourCells = this.getNeighbourCells(chunkAccessor, this.posX, posY);

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(i == 1 && j == 1) continue;

                Cell cell = neighbourCells[i][j];

                if(cell == null || cell instanceof Acid) continue;

                cell.applyCorrosion(1f);
            }
        }
    }
}
