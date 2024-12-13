package com.basti_bob.sand_wizard.cells.cell_properties.property_types;

import com.basti_bob.sand_wizard.cells.cell_properties.CellProperties;

public class LiquidProperties extends CellProperties {

    public final float dispersionRate;
    public final float density;

    public LiquidProperties(LiquidProperties.Builder builder) {
        super(builder);

        this.dispersionRate = builder.dispersionRate;
        this.density = builder.density;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends CellProperties.Builder<Builder> {

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

        public Builder allLiquid(float dispersionRate, float density) {
            this.dispersionRate = dispersionRate;
            this.density = density;
            return this;
        }

        public LiquidProperties build() {
            return new LiquidProperties(this);
        }
    }
}
