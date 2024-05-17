package com.basti_bob.sand_wizard.world.world_generation.biomes;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.ore_generation.OreGenerator;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class CaveBiomeType extends BiomeType {

    public final CaveGenerator caveGenerator;
    public final List<WorldDecorator> caveBottomDecorators;
    public final List<WorldDecorator> caveTopDecorators;
    public final CellType caveCellType;
    public final OreGenerator oreGenerator;

    public CaveBiomeType(Builder builder) {
        super(builder.noiseRange, builder.weight);
        this.caveGenerator = builder.caveGenerator;
        this.caveBottomDecorators = builder.caveBottomDecorators;
        this.caveTopDecorators = builder.caveTopDecorators;
        this.caveCellType = builder.caveCellType;
        this.oreGenerator = builder.oreGenerator;
    }

    public static class Builder {

        public final FloatRange noiseRange;
        public final float weight;
        public CaveGenerator caveGenerator = CaveGenerator.BASE;
        public List<WorldDecorator> caveBottomDecorators = new ArrayList<>();
        public List<WorldDecorator> caveTopDecorators = new ArrayList<>();
        public CellType caveCellType = CellType.SOLID.STONE;
        public OreGenerator oreGenerator = OreGenerator.BASE;

        public Builder(FloatRange noiseRange, float weight) {
            this.noiseRange = noiseRange;
            this.weight = weight;
        }

        public Builder caveGenerator(CaveGenerator caveGenerator) {
            this.caveGenerator = caveGenerator;
            return this;
        }

        public Builder addCaveBottomDecorator(WorldDecorator caveBottomDecorator) {
            this.caveBottomDecorators.add(caveBottomDecorator);
            return this;
        }

        public Builder addCaveTopDecorator(WorldDecorator caveTopDecorator) {
            this.caveTopDecorators.add(caveTopDecorator);
            return this;
        }

        public Builder caveCellType(CellType caveCellType) {
            this.caveCellType = caveCellType;
            return this;
        }

        public Builder oreGenerator(OreGenerator oreGenerator) {
            this.oreGenerator = oreGenerator;
            return this;
        }

        public CaveBiomeType build() {
            return new CaveBiomeType(this);
        }
    }
}
