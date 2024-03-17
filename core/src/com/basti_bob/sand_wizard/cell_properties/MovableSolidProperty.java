package com.basti_bob.sand_wizard.cell_properties;

public class MovableSolidProperty extends CellProperty {

    public final float movingResistance;
    public final float sprayFactor;

    public MovableSolidProperty(Builder builder) {
        super(builder);

        this.movingResistance = builder.movingResistance;
        this.sprayFactor = builder.sprayFactor;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder extends CellProperty.Builder<Builder> {

        protected float movingResistance = 0f;
        protected float sprayFactor = 0f;

        public Builder movingResistance(float movingResistance) {
            this.movingResistance = movingResistance;
            return this;
        }

        public Builder sprayFactor(float sprayFactor) {
            this.sprayFactor = sprayFactor;
            return this;
        }

        public MovableSolidProperty build() {
            return new MovableSolidProperty(this);
        }
    }

}
