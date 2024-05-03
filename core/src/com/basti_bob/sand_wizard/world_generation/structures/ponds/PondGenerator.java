package com.basti_bob.sand_wizard.world_generation.structures.ponds;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.noise.AmpFreqNoise;
import com.basti_bob.sand_wizard.util.noise.Noise;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world_generation.structures.StructureGenerator;

public class PondGenerator extends StructureGenerator {

    public final CellType cellType;
    public final IntRange xRadiusRange;
    public final IntRange yRadiusRange;
    public final Noise noise;

    private PondGenerator(Builder builder) {
        cellType = builder.cellType;
        xRadiusRange = builder.xRadiusRange;
        yRadiusRange = builder.yRadiusRange;
        noise = builder.noise;
    }

    public static Builder builder(CellType cellType, IntRange xRadiusRange, IntRange yRadiusRange) {
        return new Builder(cellType, xRadiusRange, yRadiusRange);
    }

    @Override
    public Structure generate(World world, int startX, int startY) {
        Structure.Builder structureBuilder = Structure.builder();

        int rx = xRadiusRange.getRandom(world.random);
        int ry = yRadiusRange.getRandom(world.random);

        for (int i = -rx; i <= rx; i++) {

            float baseVal = ry * (float) Math.sqrt(1 - Math.pow(i / (float) rx, 2));
            float noiseVal = noise.eval(startX + i);

            int height = (int) (baseVal + noiseVal);

            int waterYStart = (int) Math.min(world.worldGeneration.getTerrainHeight(startX + i), startY);

            for (int y = waterYStart; y >= startY - height; y--)
                structureBuilder.addCell(cellType, startX + i, y);
        }

        return structureBuilder.build();
    }

    public static final class Builder {

        private final CellType cellType;
        private final IntRange xRadiusRange, yRadiusRange;
        private Noise noise = new AmpFreqNoise(0.02f, 25);

        public Builder(CellType cellType, IntRange xRadiusRange, IntRange yRadiusRange) {
            this.cellType = cellType;
            this.xRadiusRange = xRadiusRange;
            this.yRadiusRange = yRadiusRange;
        }

        public Builder noise(Noise val) {
            noise = val;
            return this;
        }

        public PondGenerator build() {
            return new PondGenerator(this);
        }
    }
}
