package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class Water extends Liquid {

    public Water(CellType cellType) {
        super(cellType);
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.getPosX(), this.getPosY());

        for (Cell cell : directNeighbourCells) {

            if (cell instanceof Empty || cell == null) continue;

            cell.transferTemperature(chunkAccessor, 20f, 0.01f);
            cell.cleanColor(chunkAccessor, 0.01f);
        }
    }

    @Override
    public void startedBurning(ChunkAccessor chunkAccessor) {
        super.startedBurning(chunkAccessor);

        if (world.random.nextFloat() < 0.8)
            replace(CellType.GAS.STEAM, chunkAccessor);
        else
            die(chunkAccessor);
    }

    @Override
    public void changeTemperature(ChunkAccessor chunkAccessor, float temperatureChange) {
        super.changeTemperature(chunkAccessor, temperatureChange);

        if (this.getTemperature() < 0) {
            replace(CellType.SOLID.ICE, chunkAccessor);
        }
    }
}
