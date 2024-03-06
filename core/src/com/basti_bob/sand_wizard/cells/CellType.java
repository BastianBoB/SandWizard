package com.basti_bob.sand_wizard.cells;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.ImmovableSolid;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.cells.solids.Empty;

import java.util.ArrayList;
import java.util.Arrays;

public enum CellType {

    EMPTY("Empty", PhysicalState.OTHER, Cell.CellProperty.EMPTY, Empty::new, true, hC(0x000000)), //(x, y, world) -> Empty.getInstance()
    STONE("Stone", PhysicalState.SOLID, Cell.CellProperty.STONE, ImmovableSolid::new, true, hC(0x4c4c4c), hC(0x666666), hC(0x7f7f7f)),
    GRASS("Grass", PhysicalState.SOLID, Cell.CellProperty.GRASS, ImmovableSolid::new, true, c(19, 109, 21), c(38, 139, 7), c(65, 152, 10)),
    ICE("Ice", PhysicalState.SOLID, Cell.CellProperty.ICE, ImmovableSolid::new, true, c(255, 255, 255), c(185, 232, 234), c(134, 214, 216), c(63, 208, 212), c(32, 195, 208)),
//
//    WOOD("Wood",PhysicalState.SOLID, Wood::new, true, hC(0x6E470B), hC(0x785115), hC(0x643D10)),
//
//    BEDROCK("Bedrock",PhysicalState.SOLID, Bedrock::new, true, hC(0xF0F0F0), hC(0x505050), hC(0x808080)),

    SAND("Sand", PhysicalState.SOLID, MovableSolid.MovableSolidProperty.SAND, MovableSolid::new, true, hC(0xB8860C), hC(0xE7B744), hC(0xF1D581)),
    DIRT("Dirt", PhysicalState.SOLID, MovableSolid.MovableSolidProperty.DIRT, MovableSolid::new, true, hC(0xA0522D), hC(0x8B4513), hC(0xD2691E)),
    //
    COAL("Coal", PhysicalState.SOLID, MovableSolid.MovableSolidProperty.COAL, MovableSolid::new, true, hC(0x363232), hC(0x292929), hC(0x393939)),
//
//    GUNPOWDER("Gunpowder", Gunpowder::new, PhysicalState.SOLID, true, hC(0x18181a), hC(0x1e1e21), hC(0x131315)),

    WATER("Water", PhysicalState.LIQUID, Liquid.LiquidProperty.WATER, Liquid::new, true, c(30, 125, 200), c(35, 137, 218), c(25, 115, 185)),

    OIL("Oil", PhysicalState.LIQUID, Liquid.LiquidProperty.OIL, Liquid::new, true, hC(0x0E0F0E), hC(0x0B0C0B), hC(0x121312)),
//
//    ACID("Acid",PhysicalState.LIQUID, Acid::new, true, hC(0x9BE60F), hC(0xBFFF28), hC(0xAffA19)),
//
//    FIRE("Fire",PhysicalState.GAS, Fire::new, true, hC(0xFF8800), hC(0xFF2200), hC(0xFFFF00),
//            hC(0xFFFF00), hC(0xFF2200), hC(0xFFFF00), hC(0xFF8800), hC(0xFFFF00), hC(0xFFFFFF)),
//
//    STEAM("Steam",PhysicalState.GAS, Steam::new, true, hC(0xC7D5E0), hC(0xC7D5E0), hC(0xFFFFFF));
    ;

    private static Color hC(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);

        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    private static Color c(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    private final String displayName;
    private final PhysicalState physicalState;
    private final boolean addToEditor;

    private final CellSupplier cellSupplier;
    private final ArrayList<Color> colors = new ArrayList<>();
    private final Cell.CellProperty cellProperty;

    CellType(String displayName, PhysicalState physicalState, Cell.CellProperty cellProperty, CellSupplier cellSupplier, boolean addToEditor, Color... colors) {
        this.displayName = displayName;
        this.physicalState = physicalState;
        this.cellProperty = cellProperty;
        this.cellSupplier = cellSupplier;
        this.addToEditor = addToEditor;
        this.colors.addAll(Arrays.asList(colors));

        physicalState.cellTypes.add(this);
    }

    public Cell.CellProperty getCellProperty() {
        return this.cellProperty;
    }

    public Color randomColor() {
        int i = (int) (MathUtils.random() * colors.size());
        return colors.get(i);
    }

    public interface CellSupplier {
        Cell create(CellType cellType, World world, int x, int y);
    }

    public Cell createCell(World world, int x, int y) {
        return cellSupplier.create(this, world, x, y);
    }
}

