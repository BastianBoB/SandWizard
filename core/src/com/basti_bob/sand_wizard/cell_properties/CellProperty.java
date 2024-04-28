package com.basti_bob.sand_wizard.cell_properties;

import com.badlogic.gdx.graphics.Color;
import com.basti_bob.sand_wizard.cell_properties.property_types.GasProperty;
import com.basti_bob.sand_wizard.cell_properties.property_types.LiquidProperty;
import com.basti_bob.sand_wizard.cell_properties.property_types.MovableSolidProperty;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.world.lighting.Light;

import java.util.function.Consumer;

public class CellProperty {

    public static final CellProperty EMPTY = CellProperty.builder().build();
    public static final CellProperty STONE = CellProperty.builder().build();
    public static final CellProperty GRASS = CellProperty.builder().build();
    public static final CellProperty ICE = CellProperty.builder().friction(0.98f).burningTemperature(100).maxBurningTime(0).build();
    public static final CellProperty COMPACT_SNOW = CellProperty.builder().burningTemperature(100).maxBurningTime(0).build();
    public static final CellProperty WOOD = CellProperty.builder().allBurn(500, 400, 0.03f).build();
    public static final CellProperty LEAF = CellProperty.builder().allBurn(100, 10, 0.3f).build();

    public static final MovableSolidProperty SAND = MovableSolidProperty.builder().allMovableSolid(0.1f, 0.6f).build();
    public static final MovableSolidProperty DIRT = MovableSolidProperty.builder().allMovableSolid(0.3f, 0.3f).build();
    public static final MovableSolidProperty COAL = MovableSolidProperty.builder().allMovableSolid(0.8f, 0.2f).allBurn(1500, 6000, 0.1f).build();
    public static final MovableSolidProperty GRAVEL = MovableSolidProperty.builder().allMovableSolid(0.2f, 0.5f).build();
    public static final MovableSolidProperty POWDER_SNOW = MovableSolidProperty.builder().allMovableSolid(0.05f, 0.8f).burningTemperature(100).maxBurningTime(0).build();

    public static final LiquidProperty WATER = LiquidProperty.builder().allLiquid(6f, 1f).burningTemperature(100).maxBurningTime(0).build();
    public static final LiquidProperty ACID = LiquidProperty.builder().allLiquid(5f, 0.75f).allLight(8f, 0.5f, Color.LIME).allBurn(100, 120, 0.1f).build();
    public static final LiquidProperty OIL = LiquidProperty.builder().allLiquid(4f, 0.5f).allBurn(100, 5, 0.7f).build();
    public static final LiquidProperty LAVA = LiquidProperty.builder().allLiquid(2f, 5f).allLight(8f, 0.5f, CellColors.c(255, 128, 0)).build();

    public static final GasProperty METHANE = GasProperty.builder().allGas(2f, 1f, 300).build();
    public static final GasProperty STEAM = GasProperty.builder().allGas(3f, 0.5f, 300).build();
    public static final GasProperty FIRE = GasProperty.builder().allGas(2f, 1f, 40).allLight(14f, 0.8f, CellColors.c(255, 128, 0)).build();

    public static final CellProperty FLOWER_PETAL = CellProperty.builder().allBurn(100, 10, 0.5f).build();
    public static final CellProperty GLOWBLOCK = CellProperty.builder().allLight(100f, 2f, Color.YELLOW).build();

    public static final CellProperty FLOWER_PETAL_GLOW = CellProperty.builder().allBurn(100, 10, 0.5f).glowsWithCellColor(16f, 0.5f).build();

    public static final float r = 31f;
    public static final float i = 0.4f;
    public static final CellProperty FLOWER_PETAL_GLOW_RED = CellProperty.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(1f, 0.4f, 0.4f, 0)).build();
    public static final CellProperty FLOWER_PETAL_GLOW_YELLOW = CellProperty.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(1f, 1f, 0.4f, 0)).build();
    public static final CellProperty FLOWER_PETAL_GLOW_BLUE = CellProperty.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(0.4f, 0.4f, 1f, 0)).build();
    public static final CellProperty FLOWER_PETAL_GLOW_PURPLE = CellProperty.builder().allBurn(100, 10, 0.5f).allLight(r, i, new Color(1f, 0.4f, 1f, 0)).build();





    public final float friction;
    public final float speedFactor;
    public final float jumpFactor;

    public final boolean canBeHeated;
    public final boolean canBeCooled;
    public final boolean canBurn;
    public final float burningTemperature;
    public final int maxBurningTime;
    public final float fireSpreadChance;

    public final float maxCorrosionHealth;
    public final boolean canCorrode;

    public final boolean glowsWithCellColor;
    public final boolean isLightSource;
    public final float lightRadius;
    public final float lightIntensity;
    public final Color lightColor;

    public final Consumer<Cell> onCreate;

    public CellProperty(CellProperty.Builder builder) {
        this.friction = builder.friction;
        this.speedFactor = builder.speedFactor;
        this.jumpFactor = builder.jumpFactor;
        this.canBeHeated = builder.canBeHeated;
        this.canBeCooled = builder.canBeCooled;
        this.canBurn = builder.canBurn;
        this.burningTemperature = builder.burningTemperature;
        this.maxBurningTime = builder.maxBurningTime;
        this.fireSpreadChance = builder.fireSpreadChance;
        this.maxCorrosionHealth = builder.maxCorrosionHealth;
        this.canCorrode = builder.canCorrode;
        this.isLightSource = builder.isLightSource;
        this.lightRadius = builder.lightRadius;
        this.lightIntensity = builder.lightIntensity;

        this.lightColor = builder.lightColor;
        this.glowsWithCellColor = builder.glowsWithCellColor;
        this.onCreate = builder.onCreate;
    }

    public void createdCell(Cell cell) {
        if(onCreate == null) return;

        onCreate.accept(cell);
    }

    public static CellProperty.Builder builder() {
        return new CellProperty.Builder();
    }

    public static class Builder<T extends CellProperty.Builder<T>> {

        protected float friction = 0.9f;
        protected float speedFactor = 1f;
        protected float jumpFactor = 1f;

        protected boolean canBeHeated = true;
        protected boolean canBeCooled = true;
        protected boolean canBurn = false;
        protected float burningTemperature = 1000;
        protected int maxBurningTime = 100;
        protected float fireSpreadChance = 0.2f;

        protected float maxCorrosionHealth = 100;
        protected boolean canCorrode = true;

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
            this.maxCorrosionHealth = maxCorrosionHealth;
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

        public CellProperty build() {
            return new CellProperty(this);
        }
    }
}
