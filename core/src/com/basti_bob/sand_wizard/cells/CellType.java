package com.basti_bob.sand_wizard.cells;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.cell_properties.CellProperties;
import com.basti_bob.sand_wizard.cell_properties.CellProperty;
import com.basti_bob.sand_wizard.cells.gases.DebugCellSingleTick;
import com.basti_bob.sand_wizard.cells.gases.Fire;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.cells.liquids.Acid;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.cells.liquids.Water;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.ImmovableSolid;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;

import java.util.ArrayList;
import java.util.Arrays;

public enum CellType {

    EMPTY("empty", PhysicalState.OTHER, CellProperties.EMPTY, Empty::new, hC(0x000000)), //(x, y, world) -> Empty.getInstance()

    SINGLE_TICK("single_tick", PhysicalState.OTHER, CellProperties.EMPTY, DebugCellSingleTick::new, hC(0xFF0000)),

    STONE("stone", PhysicalState.SOLID, CellProperties.STONE, ImmovableSolid::new, hC(0x4c4c4c), hC(0x666666), hC(0x7f7f7f)),
    GRASS("grass", PhysicalState.SOLID, CellProperties.GRASS, ImmovableSolid::new, c(19, 109, 21), c(38, 139, 7), c(65, 152, 10)),
    ICE("ice", PhysicalState.SOLID, CellProperties.ICE, ImmovableSolid::new, c(255, 255, 255), c(185, 232, 234), c(134, 214, 216), c(63, 208, 212), c(32, 195, 208)),
    SUMMER_LEAF("summer_leaf", PhysicalState.SOLID, CellProperties.LEAF, ImmovableSolid::new, c(119, 163, 122), c(95, 146, 106), c(88, 126, 96)),
    SPRING_LEAF("spring_leaf", PhysicalState.SOLID, CellProperties.LEAF, ImmovableSolid::new, c(197, 227, 175), c(154, 195, 123), c(114, 162, 78), c(84, 134, 46)),

    WOOD("wood", PhysicalState.SOLID, CellProperties.WOOD, ImmovableSolid::new, hC(0x6E470B), hC(0x785115), hC(0x643D10)),
    RED_WOOD("red_wood", PhysicalState.SOLID, CellProperties.WOOD, ImmovableSolid::new, c(70, 31, 0), c(80, 41, 0), c(90, 51, 0)),

    SAND("sand", PhysicalState.SOLID, CellProperties.SAND, MovableSolid::new, hC(0xB8860C), hC(0xE7B744), hC(0xF1D581)),
    DIRT("dirt", PhysicalState.SOLID, CellProperties.DIRT, MovableSolid::new, hC(0xA0522D), hC(0x8B4513), hC(0xD2691E)),
    COAL("coal", PhysicalState.SOLID, CellProperties.COAL, MovableSolid::new, hC(0x363232), hC(0x292929), hC(0x393939)),
    GRAVEL("gravel", PhysicalState.SOLID, CellProperties.GRAVEL, MovableSolid::new, c(114, 114, 114), c(85, 85, 85), c(75, 71, 68), c(61, 55, 51), c(51, 44, 40)),
//
//    GUNPOWDER("Gunpowder", Gunpowder::new, PhysicalState.SOLID, true, hC(0x18181a), hC(0x1e1e21), hC(0x131315)),

    WATER("water", PhysicalState.LIQUID, CellProperties.WATER, Water::new, c(30, 125, 200), c(35, 137, 218), c(25, 115, 185)),
    OIL("oil", PhysicalState.LIQUID, CellProperties.OIL, Liquid::new, hC(0x0E0F0E), hC(0x0B0C0B), hC(0x121312)),
    ACID("acid", PhysicalState.LIQUID, CellProperties.ACID, Acid::new, hC(0x9BE60F), hC(0xBFFF28), hC(0xAffA19)),

    FIRE("fire", PhysicalState.GAS, CellProperties.FIRE, Fire::new, hC(0xFF8800), hC(0xFF2200), hC(0xFFFF00), hC(0xFFFF00), hC(0xFF2200), hC(0xFFFF00), hC(0xFF8800), hC(0xFFFF00), hC(0xFFFFFF)),
//
    STEAM("Steam",PhysicalState.GAS, CellProperties.STEAM, Gas::new, hC(0xC7D5E0), hC(0xC7D5E0), hC(0xFFFFFF)),
    METHANE("methane", PhysicalState.GAS, CellProperties.METHANE, Gas::new, hC(0xFFFFFF), hC(0xDDDDFF), hC(0xEEEEFF)),

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

    public final String idName;
    private final PhysicalState physicalState;

    private final CellSupplier cellSupplier;
    private final ArrayList<Color> colors = new ArrayList<>();
    private final CellProperty cellProperty;

    CellType(String idName, PhysicalState physicalState, CellProperty cellProperty, CellSupplier cellSupplier, Color... colors) {
        this.idName = idName;
        this.physicalState = physicalState;
        this.cellProperty = cellProperty;
        this.cellSupplier = cellSupplier;
        this.colors.addAll(Arrays.asList(colors));

        physicalState.cellTypes.add(this);
    }

    public static CellType fromName(String name) {
        int[] a = {0, 0, 0};

        int[] b = new int[]{0, 0, 0};

        for (CellType cellType : CellType.values()) {
            if (cellType.idName.equals(name)) return cellType;
        }
        return null;
    }

    public CellProperty getCellProperty() {
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

