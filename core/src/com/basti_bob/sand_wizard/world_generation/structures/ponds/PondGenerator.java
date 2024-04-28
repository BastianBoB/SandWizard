package com.basti_bob.sand_wizard.world_generation.structures.ponds;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.AmpFreqNoise;
import com.basti_bob.sand_wizard.util.Noise;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world_generation.structures.StructureGenerator;
import jdk.jshell.execution.Util;

public class PondGenerator extends StructureGenerator {

    public final CellType cellType;
    public final int minRadiusX, maxRadiusX;
    public final int minRadiusY, maxRadiusY;
    public final Noise noise;

    private PondGenerator(Builder builder) {
        cellType = builder.cellType;
        minRadiusX = builder.minRadiusX;
        maxRadiusX = builder.maxRadiusX;
        minRadiusY = builder.minRadiusY;
        maxRadiusY = builder.maxRadiusY;
        noise = builder.noise;
    }

    public static Builder builder(CellType cellType, int minRadiusX, int maxRadiusX, int minRadiusY, int maxRadiusY) {
        return new Builder(cellType, minRadiusX, maxRadiusX, minRadiusY, maxRadiusY);
    }

    @Override
    public Structure generate(World world, int startX, int startY) {
        Structure.Builder structureBuilder = Structure.builder();

        int rx = world.random.nextInt(minRadiusX, maxRadiusX);
        int ry = world.random.nextInt(minRadiusY, maxRadiusY);

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
        private final int minRadiusX;
        private final int maxRadiusX;
        private final int minRadiusY;
        private final int maxRadiusY;
        private Noise noise = new AmpFreqNoise(0.02f, 25);

        public Builder(CellType cellType, int minRadiusX, int maxRadiusX, int minRadiusY, int maxRadiusY) {
            this.cellType = cellType;
            this.minRadiusX = minRadiusX;
            this.maxRadiusX = maxRadiusX;
            this.minRadiusY = minRadiusY;
            this.maxRadiusY = maxRadiusY;
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
