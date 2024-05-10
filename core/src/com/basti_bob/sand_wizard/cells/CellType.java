package com.basti_bob.sand_wizard.cells;

import com.badlogic.gdx.graphics.Color;
import com.basti_bob.sand_wizard.cells.cell_properties.CellColors;
import com.basti_bob.sand_wizard.cells.cell_properties.CellProperty;
import com.basti_bob.sand_wizard.cells.cell_properties.PhysicalState;
import com.basti_bob.sand_wizard.cells.gases.Fire;
import com.basti_bob.sand_wizard.cells.gases.Gas;
import com.basti_bob.sand_wizard.cells.gases.Steam;
import com.basti_bob.sand_wizard.cells.liquids.Acid;
import com.basti_bob.sand_wizard.cells.liquids.Lava;
import com.basti_bob.sand_wizard.cells.liquids.Liquid;
import com.basti_bob.sand_wizard.cells.liquids.Water;
import com.basti_bob.sand_wizard.cells.solids.immovable_solids.*;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.MovableSolid;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.cells.solids.movable_solids.PowderSnow;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CellType implements Supplier<Cell> {

    private static final Map<String, CellType> ID_NAME_MAP = new HashMap<>();

    public static final CellType EMPTY = new CellType("empty", PhysicalState.OTHER, CellProperty.EMPTY, (cellType -> Empty.getInstance()), CellColors.EMPTY); //(x, y, world) -> Empty.getInstance()

    public static final CellType PARTICLE = new CellType("particle", PhysicalState.OTHER, CellProperty.EMPTY, null, CellColors.EMPTY);

    public static final CellType STONE = new CellType("stone", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STONE);
    public static final CellType GRASS = new CellType("grass", PhysicalState.SOLID, CellProperty.GRASS, ImmovableSolid::new, CellColors.GRASS);
    public static final CellType ICE = new CellType("ice", PhysicalState.SOLID, CellProperty.ICE, Ice::new, CellColors.ICE);
    public static final CellType SUMMER_LEAF = new CellType("summer_leaf", PhysicalState.SOLID, CellProperty.LEAF, ImmovableSolid::new, CellColors.SUMMER_LEAF);
    public static final CellType SPRING_LEAF = new CellType("spring_leaf", PhysicalState.SOLID, CellProperty.LEAF, ImmovableSolid::new, CellColors.SPRING_LEAF);
    public static final CellType IRON_ORE = new CellType("iron_ore", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.IRON_ORE);
    public static final CellType DIORITE = new CellType("diorite", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.DIORITE);
    public static final CellType ANDESITE = new CellType("andesite", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.ANDESITE);
    public static final CellType GRANITE = new CellType("granite", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.GRANITE);
    public static final CellType BASALT = new CellType("basalt", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.BASALT);
    public static final CellType MARBLE = new CellType("marble", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.MARBLE);
    public static final CellType LIMESTONE = new CellType("limestone", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.LIMESTONE);
    public static final CellType SHALE = new CellType("shale", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.SHALE);
    public static final CellType STALACTITE_LIGHT = new CellType("stalactite_light", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STALACTITE_LIGHT);
    public static final CellType STALACTITE_DARK = new CellType("stalactite_dark", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STALACTITE_DARK);


    public static final CellType COMPACT_SNOW = new CellType("compact_snow", PhysicalState.SOLID, CellProperty.COMPACT_SNOW, CompactSnow::new, CellColors.COMPACT_SNOW);
    public static final CellType WOOD = new CellType("wood", PhysicalState.SOLID, CellProperty.WOOD, ImmovableSolid::new, CellColors.WOOD);
    public static final CellType RED_WOOD = new CellType("red_wood", PhysicalState.SOLID, CellProperty.WOOD, ImmovableSolid::new, CellColors.RED_WOOD);
    public static final CellType SAND_STONE = new CellType("sand_stone", PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.SAND_STONE);

    public static final CellType SAND = new CellType("sand", PhysicalState.SOLID, CellProperty.SAND, MovableSolid::new, CellColors.SAND);
    public static final CellType FINE_SAND = new CellType("sand", PhysicalState.SOLID, CellProperty.SAND, MovableSolid::new, CellColors.FINE_SAND);
    public static final CellType DIRT = new CellType("dirt", PhysicalState.SOLID, CellProperty.DIRT, MovableSolid::new, CellColors.DIRT);
    public static final CellType COAL = new CellType("coal", PhysicalState.SOLID, CellProperty.COAL, MovableSolid::new, CellColors.COAL);
    public static final CellType GRAVEL = new CellType("gravel", PhysicalState.SOLID, CellProperty.GRAVEL, MovableSolid::new, CellColors.GRAVEL);
    public static final CellType POWDER_SNOW = new CellType("powder_snow", PhysicalState.SOLID, CellProperty.POWDER_SNOW, PowderSnow::new, CellColors.POWDER_SNOW);

    public static final CellType WATER = new CellType("water", PhysicalState.LIQUID, CellProperty.WATER, Water::new, CellColors.WATER);
    public static final CellType OIL = new CellType("oil", PhysicalState.LIQUID, CellProperty.OIL, Liquid::new, CellColors.OIL);
    public static final CellType ACID = new CellType("acid", PhysicalState.LIQUID, CellProperty.ACID, Acid::new, CellColors.ACID);
    public static final CellType LAVA = new CellType("lava", PhysicalState.LIQUID, CellProperty.LAVA, Lava::new, CellColors.LAVA);


    public static final CellType FIRE = new CellType("fire", PhysicalState.GAS, CellProperty.FIRE, Fire::new, CellColors.FIRE);
    public static final CellType STEAM = new CellType("Steam", PhysicalState.GAS, CellProperty.STEAM, Steam::new, CellColors.STEAM);
    public static final CellType METHANE = new CellType("methane", PhysicalState.GAS, CellProperty.METHANE, Gas::new, CellColors.METHANE);
    public static final CellType EXPLOSION_SPARK = new CellType("explosion_spark", PhysicalState.GAS, CellProperty.EXPLOSION_SPARK, Gas::new, CellColors.FIRE);

    public static final class FIRE_BREATHING_STONES {
        public static final float aOff = MathUtil.PI / 6f;

        public static final CellType UP = get("fire_breathing_stone_up", CellType.FIRE, MathUtil.PI / 2f - aOff, MathUtil.PI / 2f + aOff);
        public static final CellType DOWN = get("fire_breathing_stone_up", CellType.FIRE, -MathUtil.PI / 2f - aOff, -MathUtil.PI / 2f + aOff);
        public static final CellType RIGHT = get("fire_breathing_stone_up", CellType.FIRE, -aOff, +aOff);
        public static final CellType LEFT = get("fire_breathing_stone_up", CellType.FIRE, MathUtil.PI - aOff, MathUtil.PI + aOff);


        public static CellType get(String id, CellType gasType, float minAngle, float maxAngle) {

            return new CellType(id, PhysicalState.SOLID, CellProperty.STONE, cellType -> new GasBreathingStone(cellType, gasType,
                    new IntRange(180, 240), new IntRange(30, 60), 3, new FloatRange(minAngle, maxAngle), new FloatRange(3f, 5f), new IntRange(10, 25)), CellColors.STONE);
        }
    }

    public static final class DRIPPING_STONES {

        public static final CellType WATER = get("water_dripping_stone", CellType.WATER);
        public static final CellType LAVA = get("water_dripping_stone", CellType.LAVA);
        public static final CellType ACID = get("water_dripping_stone", CellType.ACID);

        public static CellType get(String id, CellType toCreateCellType) {
            return new CellType(id, PhysicalState.SOLID, CellProperty.STONE, cellType -> new DrippingStone(cellType, toCreateCellType, new IntRange(10, 600)), CellColors.STONE);
        }

    }

    public static final class FLOWER_PETAL {
        public static final CellType RED = new CellType("flower_petal_red", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.RED);
        public static final CellType PINK = new CellType("flower_petal_pink", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.PINK);
        public static final CellType YELLOW = new CellType("flower_petal_yellow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW);
        public static final CellType ORANGE = new CellType("flower_petal_orange", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.ORANGE);
        public static final CellType PURPLE = new CellType("flower_petal_purple", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.PURPLE);
        public static final CellType WHITE = new CellType("flower_petal_white", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.WHITE);
        public static final CellType BLUE = new CellType("flower_petal_blue", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.BLUE);
        public static final CellType LAVENDER = new CellType("flower_petal_lavender", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.LAVENDER);
        public static final CellType PEACH = new CellType("flower_petal_peach", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.PEACH);
        public static final CellType LILAC = new CellType("flower_petal_lilac", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.LILAC);
        public static final CellType MAGENTA = new CellType("flower_petal_magenta", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.MAGENTA);
        public static final CellType CORAL = new CellType("flower_petal_coral", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.CORAL);
        public static final CellType CYAN = new CellType("flower_petal_cyan", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.CYAN);
        public static final CellType GREEN = new CellType("flower_petal_green", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.GREEN);
        public static final CellType BROWN = new CellType("flower_petal_brown", PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.BROWN);

        public static final CellType RED_GLOW = new CellType("flower_petal_red_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.RED);
        public static final CellType PINK_GLOW = new CellType("flower_petal_pink_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.PINK);
        public static final CellType YELLOW_GLOW = new CellType("flower_petal_yellow_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW);
        public static final CellType ORANGE_GLOW = new CellType("flower_petal_orange_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.ORANGE);
        public static final CellType PURPLE_GLOW = new CellType("flower_petal_purple_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.PURPLE);
        public static final CellType WHITE_GLOW = new CellType("flower_petal_white_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.WHITE);
        public static final CellType BLUE_GLOW = new CellType("flower_petal_blue_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.BLUE);
        public static final CellType LAVENDER_GLOW = new CellType("flower_petal_lavender_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.LAVENDER);
        public static final CellType PEACH_GLOW = new CellType("flower_petal_peach_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.PEACH);
        public static final CellType LILAC_GLOW = new CellType("flower_petal_lilac_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.LILAC);
        public static final CellType MAGENTA_GLOW = new CellType("flower_petal_magenta_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.MAGENTA);
        public static final CellType CORAL_GLOW = new CellType("flower_petal_coral_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.CORAL);
        public static final CellType CYAN_GLOW = new CellType("flower_petal_cyan_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.CYAN);
        public static final CellType GREEN_GLOW = new CellType("flower_petal_green_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.GREEN);
        public static final CellType BROWN_GLOW = new CellType("flower_petal_brown_glow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.BROWN);

        public static final CellType YELLOW_GLOW_RED = new CellType("flower_petal_yellow_glow_red", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_RED, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW);
        public static final CellType YELLOW_GLOW_YELLOW = new CellType("flower_petal_yellow_glow_yellow", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_YELLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW);
        public static final CellType YELLOW_GLOW_BLUE = new CellType("flower_petal_yellow_glow_blue", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_BLUE, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW);
        public static final CellType YELLOW_GLOW_PURPLE = new CellType("flower_petal_yellow_glow_purple", PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_PURPLE, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW);

    }

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
        Cell create(CellType cellType);
    }

    @Override
    public Cell get() {
        return cellSupplier.create(this);
    }

    public Cell createCell() {
        return cellSupplier.create(this);
    }
}

