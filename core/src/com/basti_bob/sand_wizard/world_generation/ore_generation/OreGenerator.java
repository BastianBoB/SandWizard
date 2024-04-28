package com.basti_bob.sand_wizard.world_generation.ore_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.LayeredNoise;
import com.basti_bob.sand_wizard.util.Noise;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.util.Triplet;
import com.basti_bob.sand_wizard.world.World;


import java.util.ArrayList;
import java.util.List;

public class OreGenerator {

    public static final OreGenerator BASE = OreGenerator.builder()
            .addOre(CellType.COAL, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.IRON_ORE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.005f)
            .addOre(CellType.ANDESITE, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.1f)
            .addOre(CellType.DIORITE, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.1f)
            .addOre(CellType.GRANITE, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.1f)
            .addOre(CellType.GRAVEL, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.2f)
            .addOre(CellType.BASALT, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.MARBLE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.LIMESTONE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.SHALE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)

            .build();

    private final List<Triplet<CellType, Noise, Float>> generatorList;

    private final float maxTerrainHeightOffset = 500f;


    public OreGenerator(List<Triplet<CellType, Noise, Float>> generatorList) {
        this.generatorList = generatorList;
    }

    public CellType getCellType(World world, int cellX, int cellY, float terrainHeight) {

        if (terrainHeight - cellY < maxTerrainHeightOffset) return null;


        for (Triplet<CellType, Noise, Float> triplet : generatorList) {
            float val = triplet.getSecond().eval(cellX, cellY) * 0.5f + 0.5f;

            if (val < triplet.getThird()) return triplet.getFirst();
        }

        return null;
    }

    public static OreGeneratorBuilder builder() {
        return new OreGeneratorBuilder();
    }

    public static class OreGeneratorBuilder {

        private final List<Triplet<CellType, Noise, Float>> generatorList = new ArrayList<>();

        public OreGeneratorBuilder addOre(CellType cellType, Noise noise, float value) {
            generatorList.add(new Triplet<>(cellType, noise, value));
            return this;
        }

        public OreGenerator build() {
            return new OreGenerator(generatorList);
        }
    }

}
