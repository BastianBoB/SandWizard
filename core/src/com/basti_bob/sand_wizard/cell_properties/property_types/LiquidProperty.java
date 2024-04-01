package com.basti_bob.sand_wizard.cell_properties.property_types;

import com.basti_bob.sand_wizard.cell_properties.CellProperty;

public class LiquidProperty extends CellProperty {

    public final float dispersionRate;
    public final float density;

    public LiquidProperty(LiquidProperty.Builder builder) {
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

        public LiquidProperty build() {
            return new LiquidProperty(this);
        }
    }
}
