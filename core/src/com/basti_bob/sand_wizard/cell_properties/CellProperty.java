package com.basti_bob.sand_wizard.cell_properties;

public class CellProperty {

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

    public CellProperty(Builder builder) {
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<T extends Builder<T>> {

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