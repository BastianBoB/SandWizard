package com.basti_bob.sand_wizard.cell_properties;

import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;

public class CellProperties {

    public static final CellProperty EMPTY = CellProperty.builder().build();
    public static final CellProperty STONE = CellProperty.builder().build();
    public static final CellProperty GRASS = CellProperty.builder().build();
    public static final CellProperty ICE = CellProperty.builder().friction(0.98f).build();
    public static final CellProperty WOOD = CellProperty.builder().burningTemperature(200).maxBurningTime(600).build();
    public static final CellProperty LEAF = CellProperty.builder().burningTemperature(50).maxBurningTime(10).build();

    public static final MovableSolidProperty SAND = MovableSolidProperty.builder().movingResistance(0.1f).sprayFactor(0.6f).build();
    public static final MovableSolidProperty DIRT = MovableSolidProperty.builder().movingResistance(0.3f).sprayFactor(0.3f).build();
    public static final MovableSolidProperty COAL = MovableSolidProperty.builder().movingResistance(0.8f).sprayFactor(0.2f).build();

    public static final LiquidProperty WATER = LiquidProperty.builder().dispersionRate(7f).density(1f).build();
    public static final LiquidProperty OIL = LiquidProperty.builder().dispersionRate(7f).density(0.5f).maxBurningTime(1).burningTemperature(1).build();

    public static final GasProperty METHANE = GasProperty.builder().dispersionRate(1f).density(1f).build();
    public static final GasProperty FIRE = GasProperty.builder().dispersionRate(1f).density(1f).maxBurningTime(40).build();
}
