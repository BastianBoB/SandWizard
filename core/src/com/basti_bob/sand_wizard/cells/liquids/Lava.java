package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.Light;

public class Lava extends Liquid {

    private boolean fullySurrounded = false;

    public Lava(CellType cellType) {
        super(cellType);
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.getPosX(), this.getPosY());

        fullySurrounded = true;

        for (int i = 0; i < directNeighbourCells.length; i++) {
            Cell cell = directNeighbourCells[i];

            if (cell == null) continue;

            if (cell instanceof Empty) {
                fullySurrounded = false;

                if (i != 0 || moving) continue;

                chunkAccessor.cellActivatesChunk(this.getPosX(), this.getPosY());

                if (world.random.nextFloat() < 0.002) {
                    chunkAccessor.setCell(CellType.GAS.FIRE.createCell(), this.getPosX(), this.getPosY() + 1);
                }

            } else {
                cell.transferTemperature(chunkAccessor, 2000f, 0.05f);
            }
        }
    }

    @Override
    public boolean isEmittingLight() {
        return true;
        //return !fullySurrounded;
    }
}
