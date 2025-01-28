package com.basti_bob.sand_wizard.cells.cell_properties;

import com.badlogic.gdx.graphics.Color;
import com.basti_bob.sand_wizard.cells.cell_properties.property_types.GasProperties;
import com.basti_bob.sand_wizard.cells.cell_properties.property_types.LiquidProperties;
import com.basti_bob.sand_wizard.cells.cell_properties.property_types.MovableSolidProperties;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.util.range.IntRange;

import java.util.function.Consumer;

public class CellProperties {

    public static final CellProperties EMPTY = CellProperties.builder().build();
    public static final CellProperties STONE = CellProperties.builder().build();
    public static final CellProperties GRASS = CellProperties.builder().build();
    public static final CellProperties ICE = CellProperties.builder().friction(0.98f).burningTemperature(100).maxBurningTime(0).build();
    public static final CellProperties COMPACT_SNOW = CellProperties.builder().burningTemperature(100).maxBurningTime(0).build();
    public static final CellProperties WOOD = CellProperties.builder().allBurn(500, 400, 0.03f).build();
    public static final CellProperties LEAF = CellProperties.builder().allBurn(100, 10, 0.3f).build();

    public static final MovableSolidProperties SAND = MovableSolidProperties.builder().allMovableSolid(0.1f, 0.6f).build();
    public static final MovableSolidProperties DIRT = MovableSolidProperties.builder().allMovableSolid(0.3f, 0.3f).build();
    public static final MovableSolidProperties COAL = MovableSolidProperties.builder().allMovableSolid(0.8f, 0.2f).allBurn(1500, 6000, 0.1f).build();
    public static final MovableSolidProperties GRAVEL = MovableSolidProperties.builder().allMovableSolid(0.2f, 0.5f).build();
    public static final MovableSolidProperties POWDER_SNOW = MovableSolidProperties.builder().allMovableSolid(0.05f, 0.8f).burningTemperature(100).maxBurningTime(0).build();

    public static final LiquidProperties WATER = LiquidProperties.builder().allLiquid(6f, 1f).burningTemperature(100).maxBurningTime(0).build();
    public static final LiquidProperties ACID = LiquidProperties.builder().allLiquid(5f, 0.75f).allLight(25f, 0.1f, Color.LIME).allBurn(100, 120, 0.1f).build();
    public static final LiquidProperties HYPER_ACID = LiquidProperties.builder().allLiquid(5f, 0.75f).allLight(25f, 0.1f, Color.LIME.cpy().mul(1.2f)).allBurn(100, 120, 0.1f).build();
    public static final LiquidProperties OIL = LiquidProperties.builder().allLiquid(4f, 0.5f).allBurn(100, 5, 0.7f).build();
    public static final LiquidProperties LAVA = LiquidProperties.builder().allLiquid(2f, 5f).allLight(31f, 0.05f, CellColors.c(255, 128, 0)).build();

    public static final GasProperties METHANE = GasProperties.builder().allGas(1f, 1f, new IntRange(180, 300)).build();
    public static final GasProperties STEAM = GasProperties.builder().allGas(1.5f, 0.5f, new IntRange(180, 300)).build();
    public static final GasProperties FIRE = GasProperties.builder().allGas(1f, 1f, new IntRange(20, 50)).allLight(14f, 0.8f, CellColors.c(255, 128, 0)).build();
    public static final GasProperties EXPLOSION_SPARK = GasProperties.builder().allGas(1f, 1f, new IntRange(5, 20)).build();

    public static final CellProperties FLOWER_PETAL = CellProperties.builder().allBurn(100, 10, 0.5f).build();
    public static final CellProperties GLOWBLOCK = CellProperties.builder().allLight(31f, 2f, Color.YELLOW).build();

    public static final CellProperties FLOWER_PETAL_GLOW = CellProperties.builder().allBurn(100, 10, 0.5f).glowsWithCellColor(16f, 0.5f).build();

    private static final float r = 31f;
    private static final float i = 0.4f;
    public static final CellProperties FLOWER_PETAL_GLOW_RED = CellProperties.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(1f, 0.4f, 0.4f, 0)).build();
    public static final CellProperties FLOWER_PETAL_GLOW_YELLOW = CellProperties.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(1f, 1f, 0.4f, 0)).build();
    public static final CellProperties FLOWER_PETAL_GLOW_BLUE = CellProperties.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(0.4f, 0.4f, 1f, 0)).build();
    public static final CellProperties FLOWER_PETAL_GLOW_PURPLE = CellProperties.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(1f, 0.4f, 1f, 0)).build();

    public final float friction;
    public final float speedFactor;
    public final float jumpFactor;

    public final boolean canBeHeated;
    public final boolean canBeCooled;
    public final boolean canBurn;
    public final float burningTemperature;
    public final int maxBurningTime;
    public final float fireSpreadChance;

    public final float corrosionHealth;
    public final boolean canCorrode;
    public final float corrosionResistance;

    public final float explosionHealth;
    public final float explosionResistance;
    public final boolean canExplode;
    
    public final boolean glowsWithCellColor;
    public final boolean isLightSource;
    public final float lightRadius;
    public final float lightIntensity;
    public final Color lightColor;

    public final Consumer<Cell> onCreate;

    public CellProperties(CellProperties.Builder builder) {
        this.friction = builder.friction;
        this.speedFactor = builder.speedFactor;
        this.jumpFactor = builder.jumpFactor;
        this.canBeHeated = builder.canBeHeated;
        this.canBeCooled = builder.canBeCooled;
        this.canBurn = builder.canBurn;
        this.burningTemperature = builder.burningTemperature;
        this.maxBurningTime = builder.maxBurningTime;
        this.fireSpreadChance = builder.fireSpreadChance;


        this.corrosionHealth = builder.corrosionHealth;
        this.corrosionResistance = builder.corrosionResistance;
        this.canCorrode = builder.canCorrode;

        this.isLightSource = builder.isLightSource;
        this.lightRadius = builder.lightRadius;
        this.lightIntensity = builder.lightIntensity;

        this.lightColor = builder.lightColor;
        this.glowsWithCellColor = builder.glowsWithCellColor;

        this.explosionHealth = builder.explosionHealth;
        this.explosionResistance = builder.explosionResistance;
        this.canExplode = builder.canExplode;

        this.onCreate = builder.onCreate;
    }

    public void createdCell(Cell cell) {
        if(onCreate == null) return;

        onCreate.accept(cell);
    }

    public static CellProperties.Builder builder() {
        return new CellProperties.Builder();
    }

    public static class Builder<T extends CellProperties.Builder<T>> {

        protected float friction = 0.9f;
        protected float speedFactor = 1f;
        protected float jumpFactor = 1f;

        protected boolean canBeHeated = true;
        protected boolean canBeCooled = true;
        protected boolean canBurn = false;
        protected float burningTemperature = 1000;
        protected int maxBurningTime = 100;
        protected float fireSpreadChance = 0.2f;

        protected float corrosionResistance = 0f;
        protected float corrosionHealth = 100;
        protected boolean canCorrode = true;

        protected float explosionHealth = 100;
        protected float explosionResistance = 0f;
        protected boolean canExplode = true;

        protected boolean glowsWithCellColor = false;
        protected boolean isLightSource = false;
        protected float lightRadius = 32f;
        protected float lightIntensity = 1f;
        protected Color lightColor = Color.WHITE;
        protected Consumer<Cell> onCreate = null;

        public T friction(float friction) {
            this.friction = friction;
            return (T) this;
        }

        public T speedFactor(float speedFactor) {
            this.speedFactor = speedFactor;
            return (T) this;
        }

        public T jumpFactor(float jumpFactor) {
            this.jumpFactor = jumpFactor;
            return (T) this;
        }

        public T canBeHeated(boolean canBeHeated) {
            this.canBeHeated = canBeHeated;
            return (T) this;
        }

        public T canBeCooled(boolean canBeCooled) {
            this.canBeCooled = canBeCooled;
            return (T) this;
        }

        public T canBurn(boolean canBurn) {
            this.canBurn = canBurn;
            return (T) this;
        }

        public T allBurn(float burningTemperature, int maxBurningTime, float fireSpreadChance) {
            this.canBurn = true;
            this.burningTemperature = burningTemperature;
            this.maxBurningTime = maxBurningTime;
            this.fireSpreadChance = fireSpreadChance;
            return (T) this;
        }

        public T burningTemperature(float burningTemperature) {
            this.burningTemperature = burningTemperature;
            this.canBurn = true;
            return (T) this;
        }

        public T maxBurningTime(int maxBurningTime) {
            this.maxBurningTime = maxBurningTime;
            this.canBurn = true;
            return (T) this;
        }

        public T fireSpreadChance(float fireSpreadChance) {
            this.fireSpreadChance = fireSpreadChance;
            this.canBurn = true;
            return (T) this;
        }

        public T maxCorrosionHealth(float maxCorrosionHealth) {
            this.corrosionHealth = maxCorrosionHealth;
            return (T) this;
        }

        public T canCorrode(boolean canCorrode) {
            this.canCorrode = canCorrode;
            return (T) this;
        }

        public T allLight(float lightRadius, float lightIntensity, Color lightColor) {
            this.isLightSource = true;
            this.lightRadius = lightRadius;
            this.lightIntensity = lightIntensity;
            this.lightColor = lightColor;
            return (T) this;
        }

        public T glowsWithCellColor(float lightRadius, float lightIntensity) {
            this.isLightSource = true;
            this.glowsWithCellColor = true;
            this.lightRadius = lightRadius;
            this.lightIntensity = lightIntensity;
            return (T) this;
        }

        public T lightRadius(float lightRadius) {
            this.isLightSource = true;
            this.lightRadius = lightRadius;
            return (T) this;
        }

        public T lightIntensity(float lightIntensity) {
            this.isLightSource = true;
            this.lightIntensity = lightIntensity;
            return (T) this;
        }

        public T lightColor(Color lightColor) {
            this.isLightSource = true;
            this.lightColor = lightColor;
            return (T) this;
        }

        public T onCreate(Consumer<Cell> onCreate) {
            this.onCreate = onCreate;
            return (T) this;
        }

        public T canExplode(boolean canExplode) {
            this.canExplode = canExplode;
            return (T) this;
        }

        public T explosionHealt(float explosionHealth) {
            this.explosionHealth = explosionHealth;
            return (T) this;
        }

        public T explosionResistance(float explosionResistance) {
            this.explosionResistance = explosionResistance;
            return (T) this;
        }

        public CellProperties build() {
            return new CellProperties(this);
        }
    }
}
