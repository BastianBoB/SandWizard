package com.basti_bob.sand_wizard.cells.cell_properties.property_types;

import com.basti_bob.sand_wizard.cells.cell_properties.CellProperty;
import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;

public class GasProperty extends CellProperty {

    public final float dispersionRate;
    public final float density;
    public final IntRange lifeTime;

    public GasProperty(Builder builder) {
        super(builder);

        this.dispersionRate = builder.dispersionRate;
        this.density = builder.density;
        this.lifeTime = builder.lifeTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends CellProperty.Builder<Builder> {

        protected float dispersionRate = 5f;
        protected float density = 1f;
        protected IntRange lifeTime = new IntRange(100, 200);

        public Builder dispersionRate(float dispersionRate) {
            this.dispersionRate = dispersionRate;
            return this;
        }

        public Builder density(float density) {
            this.density = density;
            return this;
        }

        public Builder lifeTime(IntRange lifeTime) {
            this.lifeTime = lifeTime;
            return this;
        }

        public Builder allGas(float dispersionRate, float density, IntRange lifeTime) {
            this.dispersionRate = dispersionRate;
            this.density = density;
            this.lifeTime = lifeTime;
            return this;
        }

        public GasProperty build() {
            return new GasProperty(this);
        }
    }
}