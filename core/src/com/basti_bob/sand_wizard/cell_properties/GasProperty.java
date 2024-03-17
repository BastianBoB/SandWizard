package com.basti_bob.sand_wizard.cell_properties;

public class GasProperty extends CellProperty {

    public final float dispersionRate;
    public final float density;

    public GasProperty(Builder builder) {
        super(builder);

        this.dispersionRate = builder.dispersionRate;
        this.density = builder.density;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends CellProperty.Builder<Builder> {

        protected float dispersionRate = 5f;
        protected float density = 1f;


        public Builder dispersionRate(float dispersionRate) {
            this.dispersionRate = dispersionRate;
            return this;
        }

        public Builder density(float density) {
            this.density = density;
            return this;
        }

        public GasProperty build() {
            return new GasProperty(this);
        }
    }
}