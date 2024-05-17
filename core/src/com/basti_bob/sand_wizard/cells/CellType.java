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
import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;

import java.util.function.Supplier;

public class CellType {

    public static final Registry<CellType> REGISTRY = new Registry<>("cell_type");

    public static final CellType EMPTY = REGISTRY.register("empty", new CellType(PhysicalState.OTHER, CellProperty.EMPTY, cellType -> Empty.getInstance(), CellColors.EMPTY)); //(x, y, world) -> Empty.getInstance()
    public static final CellType PARTICLE = REGISTRY.register("particle", new CellType(PhysicalState.OTHER, CellProperty.EMPTY, null, CellColors.EMPTY));

    public static class SOLID {
        public static final Registry<CellType> REGISTRY = new Registry<>("solid", CellType.REGISTRY);

        public static final CellType STONE = REGISTRY.register("stone", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STONE));
        public static final CellType GRASS = REGISTRY.register("grass", new CellType(PhysicalState.SOLID, CellProperty.GRASS, ImmovableSolid::new, CellColors.GRASS));
        public static final CellType ICE = REGISTRY.register("ice", new CellType(PhysicalState.SOLID, CellProperty.ICE, Ice::new, CellColors.ICE));
        public static final CellType SUMMER_LEAF = REGISTRY.register("summer_leaf", new CellType(PhysicalState.SOLID, CellProperty.LEAF, ImmovableSolid::new, CellColors.SUMMER_LEAF));
        public static final CellType SPRING_LEAF = REGISTRY.register("spring_leaf", new CellType(PhysicalState.SOLID, CellProperty.LEAF, ImmovableSolid::new, CellColors.SPRING_LEAF));
        public static final CellType IRON_ORE = REGISTRY.register("iron_ore", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.IRON_ORE));
        public static final CellType DIORITE = REGISTRY.register("diorite", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.DIORITE));
        public static final CellType ANDESITE = REGISTRY.register("andesite", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.ANDESITE));
        public static final CellType GRANITE = REGISTRY.register("granite", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.GRANITE));
        public static final CellType BASALT = REGISTRY.register("basalt", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.BASALT));
        public static final CellType MARBLE = REGISTRY.register("marble", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.MARBLE));
        public static final CellType LIMESTONE = REGISTRY.register("limestone", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.LIMESTONE));
        public static final CellType SHALE = REGISTRY.register("shale", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.SHALE));
        public static final CellType STALACTITE_LIGHT = REGISTRY.register("stalactite_light", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STALACTITE_LIGHT));
        public static final CellType STALACTITE_DARK = REGISTRY.register("stalactite_dark", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STALACTITE_DARK));
        public static final CellType STALACTITE_CAVE_STONE = REGISTRY.register("stalactite_cave_stone", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.STALACTITE_CAVE_STONE));

        public static final CellType COMPACT_SNOW = REGISTRY.register("compact_snow", new CellType(PhysicalState.SOLID, CellProperty.COMPACT_SNOW, CompactSnow::new, CellColors.COMPACT_SNOW));
        public static final CellType WOOD = REGISTRY.register("wood", new CellType(PhysicalState.SOLID, CellProperty.WOOD, ImmovableSolid::new, CellColors.WOOD));
        public static final CellType RED_WOOD = REGISTRY.register("red_wood", new CellType(PhysicalState.SOLID, CellProperty.WOOD, ImmovableSolid::new, CellColors.RED_WOOD));
        public static final CellType SAND_STONE = REGISTRY.register("sand_stone", new CellType(PhysicalState.SOLID, CellProperty.STONE, ImmovableSolid::new, CellColors.SAND_STONE));
        public static final CellType GLOWBLOCK = REGISTRY.register("glowblock", new CellType(PhysicalState.SOLID, CellProperty.GLOWBLOCK, ImmovableSolid::new, CellColors.ICE));
    }

    public static class MOVABLE_SOLID {
        public static final Registry<CellType> REGISTRY = new Registry<>("movable_solid", CellType.REGISTRY);

        public static final CellType SAND = REGISTRY.register("sand", new CellType(PhysicalState.SOLID, CellProperty.SAND, MovableSolid::new, CellColors.SAND));
        public static final CellType FINE_SAND = REGISTRY.register("fine_sand", new CellType(PhysicalState.SOLID, CellProperty.SAND, MovableSolid::new, CellColors.FINE_SAND));
        public static final CellType DIRT = REGISTRY.register("dirt", new CellType(PhysicalState.SOLID, CellProperty.DIRT, MovableSolid::new, CellColors.DIRT));
        public static final CellType COAL = REGISTRY.register("coal", new CellType(PhysicalState.SOLID, CellProperty.COAL, MovableSolid::new, CellColors.COAL));
        public static final CellType GRAVEL = REGISTRY.register("gravel", new CellType(PhysicalState.SOLID, CellProperty.GRAVEL, MovableSolid::new, CellColors.GRAVEL));
        public static final CellType POWDER_SNOW = REGISTRY.register("powder_snow", new CellType(PhysicalState.SOLID, CellProperty.POWDER_SNOW, PowderSnow::new, CellColors.POWDER_SNOW));

    }

    public static class LIQUID {
        public static final Registry<CellType> REGISTRY = new Registry<>("liquid", CellType.REGISTRY);

        public static final CellType WATER = REGISTRY.register("water", new CellType(PhysicalState.LIQUID, CellProperty.WATER, Water::new, CellColors.WATER));
        public static final CellType OIL = REGISTRY.register("oil", new CellType(PhysicalState.LIQUID, CellProperty.OIL, Liquid::new, CellColors.OIL));
        public static final CellType ACID = REGISTRY.register("acid", new CellType(PhysicalState.LIQUID, CellProperty.ACID, Acid::new, CellColors.ACID));
        public static final CellType LAVA = REGISTRY.register("lava", new CellType(PhysicalState.LIQUID, CellProperty.LAVA, Lava::new, CellColors.LAVA));
    }


    public static class GAS {
        public static final Registry<CellType> REGISTRY = new Registry<>("gas", CellType.REGISTRY);

        public static final CellType FIRE = REGISTRY.register("fire", new CellType(PhysicalState.GAS, CellProperty.FIRE, Fire::new, CellColors.FIRE));
        public static final CellType STEAM = REGISTRY.register("steam", new CellType(PhysicalState.GAS, CellProperty.STEAM, Steam::new, CellColors.STEAM));
        public static final CellType METHANE = REGISTRY.register("methane", new CellType(PhysicalState.GAS, CellProperty.METHANE, Gas::new, CellColors.METHANE));
        public static final CellType EXPLOSION_SPARK = REGISTRY.register("explosion_spark", new CellType(PhysicalState.GAS, CellProperty.EXPLOSION_SPARK, Gas::new, CellColors.FIRE));
    }

    public static final class FIRE_BREATHING_STONES {
        public static final Registry<CellType> REGISTRY = new Registry<>("fire_breathing_stone", CellType.REGISTRY);

        public static final float aOff = MathUtil.PI / 6f;

        public static final CellType UP = REGISTRY.register("up", get(GAS.FIRE, MathUtil.PI / 2f - aOff, MathUtil.PI / 2f + aOff));
        public static final CellType DOWN = REGISTRY.register("down", get(GAS.FIRE, -MathUtil.PI / 2f - aOff, -MathUtil.PI / 2f + aOff));
        public static final CellType RIGHT = REGISTRY.register("right", get(GAS.FIRE, -aOff, +aOff));
        public static final CellType LEFT = REGISTRY.register("left", get(GAS.FIRE, MathUtil.PI - aOff, MathUtil.PI + aOff));

        public static CellType get(CellType gasType, float minAngle, float maxAngle) {

            return new CellType(PhysicalState.SOLID, CellProperty.STONE, cellType -> new GasBreathingStone(cellType, gasType,
                    new IntRange(360, 480), new IntRange(5, 60), 3, new FloatRange(minAngle, maxAngle), new FloatRange(3f, 5f), new IntRange(10, 25)), CellColors.STONE);
        }
    }

    public static final class DRIPPING_STONES {
        public static final Registry<CellType> REGISTRY = new Registry<>("dripping_stone", CellType.REGISTRY);

        public static final CellType WATER = REGISTRY.register("water", get(LIQUID.WATER));
        public static final CellType LAVA = REGISTRY.register("lava", get(LIQUID.LAVA));
        public static final CellType ACID = REGISTRY.register("acid", get(LIQUID.ACID));

        public static CellType get(CellType toCreateCellType) {
            return new CellType(PhysicalState.SOLID, CellProperty.STONE, cellType -> new DrippingStone(cellType, toCreateCellType, new IntRange(10, 600)), CellColors.STONE);
        }

    }

    public static final class FLOWER_PETAL {
        public static final Registry<CellType> REGISTRY = new Registry<>("flower_petal", CellType.REGISTRY);

        public static final CellType RED = REGISTRY.register("red", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.RED));
        public static final CellType PINK = REGISTRY.register("pink", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.PINK));
        public static final CellType YELLOW = REGISTRY.register("yellow", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW));
        public static final CellType ORANGE = REGISTRY.register("orange", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.ORANGE));
        public static final CellType PURPLE = REGISTRY.register("purple", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.PURPLE));
        public static final CellType WHITE = REGISTRY.register("white", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.WHITE));
        public static final CellType BLUE = REGISTRY.register("blue", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.BLUE));
        public static final CellType LAVENDER = REGISTRY.register("lavender", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.LAVENDER));
        public static final CellType PEACH = REGISTRY.register("peach", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.PEACH));
        public static final CellType LILAC = REGISTRY.register("lilac", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.LILAC));
        public static final CellType MAGENTA = REGISTRY.register("magenta", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.MAGENTA));
        public static final CellType CORAL = REGISTRY.register("coral", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.CORAL));
        public static final CellType CYAN = REGISTRY.register("cyan", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.CYAN));
        public static final CellType GREEN = REGISTRY.register("green", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.GREEN));
        public static final CellType BROWN = REGISTRY.register("brown", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL, ImmovableSolid::new, CellColors.FLOWER_PETAL.BROWN));

        public static final class GLOWING {
            public static final Registry<CellType> REGISTRY = new Registry<>("glowing", FLOWER_PETAL.REGISTRY);

            public static final CellType RED = REGISTRY.register("red", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.RED));
            public static final CellType PINK = REGISTRY.register("pink", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.PINK));
            public static final CellType YELLOW = REGISTRY.register("yellow", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW));
            public static final CellType ORANGE = REGISTRY.register("orange", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.ORANGE));
            public static final CellType PURPLE = REGISTRY.register("purple", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.PURPLE));
            public static final CellType WHITE = REGISTRY.register("white", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.WHITE));
            public static final CellType BLUE = REGISTRY.register("blue", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.BLUE));
            public static final CellType LAVENDER = REGISTRY.register("lavender", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.LAVENDER));
            public static final CellType PEACH = REGISTRY.register("peach", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.PEACH));
            public static final CellType LILAC = REGISTRY.register("lilac", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.LILAC));
            public static final CellType MAGENTA = REGISTRY.register("magenta", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.MAGENTA));
            public static final CellType CORAL = REGISTRY.register("coral", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.CORAL));
            public static final CellType CYAN = REGISTRY.register("cyan", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.CYAN));
            public static final CellType GREEN = REGISTRY.register("green", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.GREEN));
            public static final CellType BROWN = REGISTRY.register("brown", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.BROWN));

            public static final CellType YELLOW_RED = REGISTRY.register("yellow_red", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_RED, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW));
            public static final CellType YELLOW_YELLOW = REGISTRY.register("yellow_yellow", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_YELLOW, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW));
            public static final CellType YELLOW_BLUE = REGISTRY.register("yellow_blue", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_BLUE, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW));
            public static final CellType YELLOW_PURPLE = REGISTRY.register("yellow_purple", new CellType(PhysicalState.SOLID, CellProperty.FLOWER_PETAL_GLOW_PURPLE, ImmovableSolid::new, CellColors.FLOWER_PETAL.YELLOW));
        }
    }

    private static Color hC(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);

        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    private static Color c(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    private final PhysicalState physicalState;

    private final CellSupplier cellSupplier;
    private final CellColors cellColors;
    private final CellProperty cellProperty;

    private CellType(PhysicalState physicalState, CellProperty cellProperty, CellSupplier cellSupplier, CellColors cellColors) {
        this.physicalState = physicalState;
        this.cellProperty = cellProperty;
        this.cellSupplier = cellSupplier;
        this.cellColors = cellColors;
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

    private interface CellSupplier {
        Cell create(CellType cellType);
    }

    public Cell createCell() {
        return cellSupplier.create(this);
    }
}

