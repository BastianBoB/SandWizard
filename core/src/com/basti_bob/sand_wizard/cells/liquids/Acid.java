package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public class Acid extends Liquid {

    private boolean didCorrodeInUpdate;

    public Acid(CellType cellType) {
        super(cellType);
    }

    @Override
    public void update(ChunkAccessor chunkAccessor, boolean updateDirection) {
        super.update(chunkAccessor, updateDirection);

        Cell[] directNeighbourCells = this.getDirectNeighbourCells(chunkAccessor, this.getPosX(), this.getPosY());

        didCorrodeInUpdate = false;

        for (Cell cell : directNeighbourCells) {

            if (cell == null || cell instanceof Empty || cell instanceof Acid) continue;

            if(cell.applyCorrosion(chunkAccessor, getCorrosionAmount(world)))
                didCorrodeInUpdate = true;

//            if(didCorrodeInUpdate) {
//                cell.taintWithColor(chunkAccessor, this.getColorR(), this.getColorG(), this.getColorB(), 0.01f);
//            }
        }
    }

    public float getCorrosionAmount(World world) {
        return world.random.nextFloat() * 2;
    }

    @Override
    public boolean shouldActiveChunk() {
        return didCorrodeInUpdate;
    }
}
