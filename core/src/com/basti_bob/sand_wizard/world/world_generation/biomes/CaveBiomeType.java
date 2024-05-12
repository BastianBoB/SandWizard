package com.basti_bob.sand_wizard.world.world_generation.biomes;

import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;

public class CaveBiomeType extends BiomeType {

    public final CaveGenerator caveGenerator;
    public final WorldDecorator caveBottomDecorator;
    public final WorldDecorator caveTopDecorator;

    public CaveBiomeType(Builder builder) {
        super(builder.noiseRange, builder.weight);
        this.caveGenerator = builder.caveGenerator;
        this.caveBottomDecorator = builder.caveBottomDecorator;
        this.caveTopDecorator = builder.caveTopDecorator;
    }

    public static class Builder {

        public final FloatRange noiseRange;
        public final float weight;
        public CaveGenerator caveGenerator = CaveGenerator.BASE;
        public WorldDecorator caveBottomDecorator = WorldDecorator.CAVE.BASE_BOTTOM;
        public WorldDecorator caveTopDecorator = WorldDecorator.CAVE.BASE_TOP;

        public Builder(FloatRange noiseRange, float weight) {
            this.noiseRange = noiseRange;
            this.weight = weight;
        }

        public Builder caveGenerator(CaveGenerator caveGenerator) {
            this.caveGenerator = caveGenerator;
            return this;
        }

        public Builder caveBottomDecorator(WorldDecorator caveBottomDecorator) {
            this.caveBottomDecorator = caveBottomDecorator;
            return this;
        }

        public Builder caveTopDecorator(WorldDecorator caveTopDecorator) {
            this.caveTopDecorator = caveTopDecorator;
            return this;
        }

        public CaveBiomeType build() {
            return new CaveBiomeType(this);
        }
    }
}
