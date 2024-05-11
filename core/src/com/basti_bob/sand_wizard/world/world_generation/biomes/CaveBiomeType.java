package com.basti_bob.sand_wizard.world.world_generation.biomes;

import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.surface_decoration.WorldDecorator;

import java.util.ArrayList;
import java.util.List;

public class CaveBiomeType extends BiomeType {

    public static final List<CaveBiomeType> ALL_TYPES = new ArrayList<>();

    public static final CaveBiomeType BASE = new Builder("base", new IntRange(-1, 1), 1f)
            .caveGenerator(CaveGenerator.BASE)
            .caveBottomDecorator(WorldDecorator.CAVES_BOTTOM)
            .caveTopDecorator(WorldDecorator.CAVES_TOP)
            .build();

    public final CaveGenerator caveGenerator;
    public final WorldDecorator caveBottomDecorator;
    public final WorldDecorator caveTopDecorator;

    public CaveBiomeType(Builder builder) {
        super(builder.name, builder.noiseRange, builder.weight);
        this.caveGenerator = builder.caveGenerator;
        this.caveBottomDecorator = builder.caveBottomDecorator;
        this.caveTopDecorator = builder.caveTopDecorator;

        ALL_TYPES.add(this);
    }

    public static class Builder {

        public final String name;
        public final IntRange noiseRange;
        public final float weight;
        public CaveGenerator caveGenerator = CaveGenerator.BASE;
        public WorldDecorator caveBottomDecorator = WorldDecorator.CAVES_BOTTOM;
        public WorldDecorator caveTopDecorator = WorldDecorator.CAVES_BOTTOM;

        public Builder(String name, IntRange noiseRange, float weight) {
            this.name = name;
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
