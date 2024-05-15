package com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing;


import com.basti_bob.sand_wizard.cells.Cell;

public class ToPlaceStructureCell {

    private final Cell cell;
    private final PlacePriority placePriority;

    public ToPlaceStructureCell(Cell cell, PlacePriority placePriority) {
        this.cell = cell;
        this.placePriority = placePriority;
    }

    public Cell getCell() {
        return cell;
    }

    public PlacePriority getPlacePriority() {
        return placePriority;
    }
}
