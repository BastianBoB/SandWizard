package com.basti_bob.sand_wizard.cell_properties;

import com.basti_bob.sand_wizard.cell_properties.property_types.GasProperty;
import com.basti_bob.sand_wizard.cell_properties.property_types.LiquidProperty;
import com.basti_bob.sand_wizard.cell_properties.property_types.MovableSolidProperty;

public class CellProperty {

    public static final CellProperty EMPTY = CellProperty.builder().build();
    public static final CellProperty STONE = CellProperty.builder().build();
    public static final CellProperty GRASS = CellProperty.builder().build();
    public static final CellProperty ICE = CellProperty.builder().friction(0.98f).build();
    public static final CellProperty WOOD = CellProperty.builder().burningTemperature(80).maxBurningTime(600).fireSpreadChance(0.08f).build();
    public static final CellProperty LEAF = CellProperty.builder().burningTemperature(30).maxBurningTime(10).fireSpreadChance(0.5f).build();
    public static final CellProperty COMPACT_SNOW = CellProperty.builder().burningTemperature(30).maxBurningTime(0).build();

    public static final MovableSolidProperty SAND = MovableSolidProperty.builder().movingResistance(0.1f).sprayFactor(0.6f).build();
    public static final MovableSolidProperty DIRT = MovableSolidProperty.builder().movingResistance(0.3f).sprayFactor(0.3f).build();
    public static final MovableSolidProperty COAL = MovableSolidProperty.builder().movingResistance(0.8f).sprayFactor(0.2f).burningTemperature(200).maxBurningTime(6000).fireSpreadChance(0.1f).build();
    public static final MovableSolidProperty GRAVEL = MovableSolidProperty.builder().movingResistance(0.2f).sprayFactor(0.5f).build();
    public static final MovableSolidProperty POWDER_SNOW = MovableSolidProperty.builder().burningTemperature(30).maxBurningTime(0).movingResistance(0.05f).sprayFactor(0.8f).build();


    public static final LiquidProperty WATER = LiquidProperty.builder().dispersionRate(7f).density(1f).burningTemperature(100).maxBurningTime(0).build();
    public static final LiquidProperty ACID = LiquidProperty.builder().dispersionRate(6f).density(0.75f).burningTemperature(30).maxBurningTime(200).fireSpreadChance(0.3f).build();
    public static final LiquidProperty OIL = LiquidProperty.builder().dispersionRate(5f).density(0.5f).burningTemperature(1).maxBurningTime(5).fireSpreadChance(0.7f).build();

    public static final GasProperty METHANE = GasProperty.builder().dispersionRate(2f).density(1f).build();
    public static final GasProperty STEAM = GasProperty.builder().dispersionRate(3f).density(0.5f).build();
    public static final GasProperty FIRE = GasProperty.builder().dispersionRate(2f).density(1f).maxBurningTime(40).build();

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

        public T friction(float friction) {
            this.friction = friction;
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

        public CellProperty build() {
            return new CellProperty(this);
        }
    }
}
