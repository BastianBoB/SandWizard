package com.basti_bob.sand_wizard.cells;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.Stone;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.cells.solids.Empty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.Sand;

import java.util.ArrayList;
import java.util.Arrays;

public enum CellType {

    EMPTY("Empty", PhysicalState.OTHER, (x, y, world) -> Empty.getInstance(), true, hC(0x000000)),

    STONE("Stone", PhysicalState.SOLID, Stone::new, true, hC(0x4c4c4c), hC(0x666666), hC(0x7f7f7f)),
//
//    WOOD("Wood",PhysicalState.SOLID, Wood::new, true, hC(0x6E470B), hC(0x785115), hC(0x643D10)),
//
//    BEDROCK("Bedrock",PhysicalState.SOLID, Bedrock::new, true, hC(0xF0F0F0), hC(0x505050), hC(0x808080)),

    SAND("Sand",PhysicalState.SOLID, Sand::new, true, hC(0xB8860C), hC(0xE7B744), hC(0xF1D581)),

//    DIRT("Dirt",PhysicalState.SOLID, Dirt::new, true, hC(0xA0522D), hC(0x8B4513), hC(0xD2691E)),
//
//    COAL("Coal",PhysicalState.SOLID, Coal::new, true, hC(0x363232), hC(0x292929), hC(0x393939)),
//
//    GUNPOWDER("Gunpowder", Gunpowder::new, PhysicalState.SOLID, true, hC(0x18181a), hC(0x1e1e21), hC(0x131315)),

    //WATER("Water",PhysicalState.LIQUID, Water::new, true, hC(0x2389da), hC(0x1ca3ec)),

//    OIL("Oil",PhysicalState.LIQUID, Oil::new, true, hC(0x0E0F0E), hC(0x0B0C0B), hC(0x121312)),
//
//    ACID("Acid",PhysicalState.LIQUID, Acid::new, true, hC(0x9BE60F), hC(0xBFFF28), hC(0xAffA19)),
//
//    FIRE("Fire",PhysicalState.GAS, Fire::new, true, hC(0xFF8800), hC(0xFF2200), hC(0xFFFF00),
//            hC(0xFFFF00), hC(0xFF2200), hC(0xFFFF00), hC(0xFF8800), hC(0xFFFF00), hC(0xFFFFFF)),
//
//    STEAM("Steam",PhysicalState.GAS, Steam::new, true, hC(0xC7D5E0), hC(0xC7D5E0), hC(0xFFFFFF));
    ;

    private static Color hC(int hex){
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);

        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    private final String displayName;
    private final PhysicalState physicalState;
    private final boolean addToEditor;

    private final CellSupplier<World, Integer, Integer, Cell> cellSupplier;
    private final ArrayList<Color> colors = new ArrayList<>();

    CellType(String displayName, PhysicalState physicalState, CellSupplier<World, Integer, Integer, Cell> cellSupplier, boolean addToEditor, Color... colors) {
        this.displayName = displayName;
        this.physicalState = physicalState;
        this.cellSupplier = cellSupplier;
        this.addToEditor = addToEditor;

        this.colors.addAll(Arrays.asList(colors));

        physicalState.cellTypes.add(this);
    }

    public Color randomColor() {
        int i = (int) (MathUtils.random() * colors.size());
        return colors.get(i);
    }

    public interface CellSupplier<W, X, Y, C> {
        C create(W w, X x, Y y);
    }

    public Cell createCell(World world, int x, int y) {
        return cellSupplier.create(world, x, y);
    }
}

