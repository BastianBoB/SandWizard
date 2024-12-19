package com.basti_bob.sand_wizard.cells.liquids;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;

public class HyperAcid extends Acid {

    public HyperAcid(CellType cellType) {
        super(cellType);
    }

    @Override
    public float getCorrosionAmount(World world) {
        return super.getCorrosionAmount(world) * 5;
    }
}
