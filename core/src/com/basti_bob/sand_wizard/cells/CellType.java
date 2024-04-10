package com.basti_bob.sand_wizard.cells;

import com.badlogic.gdx.graphics.Color;
import com.basti_bob.sand_wizard.cell_properties.CellColors;
import com.basti_bob.sand_wizard.cell_properties.CellProperty;
import com.basti_bob.sand_wizard.cell_properties.PhysicalState;
import com.basti_bob.sand_wizard.cells.gases.Fire;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.cells.liquids.Acid;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.cells.liquids.Water;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.CompactSnow;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.Ice;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.ImmovableSolid;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.PowderSnow;
import com.basti_bob.sand_wizard.world.World;

import java.util.HashMap;
import java.util.Map;

public class CellType {

    private static final Map<String, CellType> ID_NAME_MAP = new HashMap<>();

    public static final CellType EMPTY = new CellType("empty", PhysicalState.OTHER, CellProperty.EMPTY, Empty::new, CellColors.EMPTY); //(x, y, world) -> Empty.getInstance()

    public static final CellType STONE = new CellType("stone", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STONE);
    public static final CellType GRASS = new CellType("grass", PhysicalState.SOLID, CellProperty.GRASS, ImmovableSolid::new, CellColors.GRASS);
    public static final CellType ICE = new CellType("ice", PhysicalState.SOLID, CellProperty.ICE, Ice::new, CellColors.ICE);
    public static final CellType SUMMER_LEAF = new CellType("summer_leaf", PhysicalState.SOLID, CellProperty.LEAF, ImmovableSolid::new, CellColors.SUMMER_LEAF);
    public static final CellType SPRING_LEAF = new CellType("spring_leaf", PhysicalState.SOLID, CellProperty.LEAF, ImmovableSolid::new, CellColors.SPRING_LEAF);
    public static final CellType COMPACT_SNOW = new CellType("compact_snow", PhysicalState.SOLID, CellProperty.COMPACT_SNOW, CompactSnow::new, CellColors.COMPACT_SNOW);
    public static final CellType WOOD = new CellType("wood", PhysicalState.SOLID, CellProperty.WOOD, ImmovableSolid::new, CellColors.WOOD);
    public static final CellType RED_WOOD = new CellType("red_wood", PhysicalState.SOLID, CellProperty.WOOD, ImmovableSolid::new, CellColors.RED_WOOD);

    public static final CellType SAND = new CellType("sand", PhysicalState.SOLID, CellProperty.SAND, MovableSolid::new, CellColors.SAND);
    public static final CellType DIRT = new CellType("dirt", PhysicalState.SOLID, CellProperty.DIRT, MovableSolid::new, CellColors.DIRT);
    public static final CellType COAL = new CellType("coal", PhysicalState.SOLID, CellProperty.COAL, MovableSolid::new, CellColors.COAL);
    public static final CellType GRAVEL = new CellType("gravel", PhysicalState.SOLID, CellProperty.GRAVEL, MovableSolid::new, CellColors.GRAVEL);
    public static final CellType POWDER_SNOW = new CellType("powder_snow", PhysicalState.SOLID, CellProperty.POWDER_SNOW, PowderSnow::new, CellColors.POWDER_SNOW);

    public static final CellType WATER = new CellType("water", PhysicalState.LIQUID, CellProperty.WATER, Water::new, CellColors.WATER);
    public static final CellType OIL = new CellType("oil", PhysicalState.LIQUID, CellProperty.OIL, Liquid::new, CellColors.OIL);
    public static final CellType ACID = new CellType("acid", PhysicalState.LIQUID, CellProperty.ACID, Acid::new, CellColors.ACID);

    public static final CellType FIRE = new CellType("fire", PhysicalState.GAS, CellProperty.FIRE, Fire::new, CellColors.FIRE);
    public static final CellType STEAM = new CellType("Steam", PhysicalState.GAS, CellProperty.STEAM, Gas::new, CellColors.STEAM);
    public static final CellType METHANE = new CellType("methane", PhysicalState.GAS, CellProperty.METHANE, Gas::new, CellColors.METHANE);

    public static final CellType GLOWBLOCK = new CellType("glowblock", PhysicalState.SOLID, CellProperty.GLOWBLOCK, ImmovableSolid::new, CellColors.ICE);

    private static Color hC(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);

        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    private static Color c(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    public final String idName;
    private final PhysicalState physicalState;

    private final CellSupplier cellSupplier;
    private final CellColors cellColors;
    private final CellProperty cellProperty;

    private CellType(String idName, PhysicalState physicalState, CellProperty cellProperty, CellSupplier cellSupplier, CellColors cellColors) {
        this.idName = idName;
        this.physicalState = physicalState;
        this.cellProperty = cellProperty;
        this.cellSupplier = cellSupplier;
        this.cellColors = cellColors;

        ID_NAME_MAP.put(idName, this);
    }

    public static CellType fromName(String name) {
        return ID_NAME_MAP.get(name);
    }

    public CellProperty getCellProperty() {
        return this.cellProperty;
    }

    public PhysicalState getPhysicalState() {
        return physicalState;
    }

    public CellColors getCellColors() {
        return cellColors;
    }

    public interface CellSupplier {
        Cell create(CellType cellType, World world, int x, int y);
    }

    public Cell createCell(World world, int x, int y) {
        return cellSupplier.create(this, world, x, y);
    }
}

