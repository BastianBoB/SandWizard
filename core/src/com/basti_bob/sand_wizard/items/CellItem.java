package com.basti_bob.sand_wizard.items;

import com.basti_bob.sand_wizard.cells.CellType;

public class CellItem extends ItemType {

    private final CellType cellType;

    public CellItem(ItemProperties properties, CellType cellType) {
        super(properties);
        this.cellType = cellType;
    }
}
