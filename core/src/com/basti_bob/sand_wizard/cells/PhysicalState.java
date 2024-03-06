package com.basti_bob.sand_wizard.cells;

import java.util.ArrayList;
import java.util.List;

public enum PhysicalState {
    OTHER("other"), SOLID("solids"), LIQUID("liquids"), GAS("gases");

    final List<CellType> cellTypes = new ArrayList<>();
    final String displayName;

    PhysicalState(String displayName) {
        this.displayName = displayName;
    }
}
