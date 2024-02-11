package com.basti_bob.sand_wizard.cells;

import java.util.ArrayList;

public enum PhysicalState {
    OTHER("other"), SOLID("solids"), LIQUID("liquids"), GAS("gases");

    final ArrayList<CellType> cellTypes = new ArrayList<CellType>();
    final String displayName;

    private PhysicalState(String displayName) {
        this.displayName = displayName;
    }
}
