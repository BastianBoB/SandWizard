package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;
import com.basti_bob.sand_wizard.world.World;

public class Water extends Liquid {

    public Water(CellType cellType) {
        super(cellType);
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.posX, this.posY);

        for (Cell cell : directNeighbourCells) {

            if (cell instanceof Empty || cell == null) continue;

            cell.transferTemperature(chunkAccessor, 20f, 0.01f);
            cell.cleanColor(chunkAccessor, 0.01f);
        }
    }

    @Override
    public void startedBurning(ChunkAccessor chunkAccessor) {
        super.startedBurning(chunkAccessor);

        replace(CellType.STEAM, chunkAccessor);
    }

    @Override
    public void changeTemperature(ChunkAccessor chunkAccessor, float temperatureChange) {
        super.changeTemperature(chunkAccessor, temperatureChange);

        if (this.getTemperature() < 0) {
            replace(CellType.ICE, chunkAccessor);
        }
    }
}
