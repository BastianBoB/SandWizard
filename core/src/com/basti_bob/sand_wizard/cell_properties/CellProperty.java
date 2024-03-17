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

    public CellProperty(Builder builder) {
        this.friction = builder.friction;
        this.speedFactor = builder.speedFactor;
        this.jumpFactor = builder.jumpFactor;
        this.canBeHeated = builder.canBeHeated;
        this.canBeCooled = builder.canBeCooled;
        this.canBurn = builder.canBurn;
        this.burningTemperature = builder.burningTemperature;
        this.maxBurningTime = builder.maxBurningTime;
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
            return (T) this;
        }

        public T maxBurningTime(int maxBurningTime) {
            this.maxBurningTime = maxBurningTime;
            return (T) this;
        }

        public T friction(float friction) {
            this.friction = friction;
            return (T) this;
        }

        public CellProperty build() {
            return new CellProperty(this);
        }
    }
}