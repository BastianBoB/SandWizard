package com.basti_bob.sand_wizard.world.world_generation.ore_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.util.noise.LayeredNoise;
import com.basti_bob.sand_wizard.util.noise.Noise;
import com.basti_bob.sand_wizard.util.Triplet;
import com.basti_bob.sand_wizard.world.World;


import java.util.ArrayList;
import java.util.List;

public class OreGenerator {

    public static final Registry<OreGenerator> REGISTRY = new Registry<>("ore_generator");

    public static final OreGenerator BASE = REGISTRY.register("base", OreGenerator.builder()
            .addOre(CellType.MOVABLE_SOLID.COAL, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.SOLID.IRON_ORE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.SOLID.ANDESITE, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.05f)
            .addOre(CellType.SOLID.DIORITE, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.05f)
            .addOre(CellType.SOLID.GRANITE, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.05f)
            .addOre(CellType.MOVABLE_SOLID.GRAVEL, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.1f)
            .addOre(CellType.SOLID.BASALT, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.SOLID.MARBLE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.SOLID.LIMESTONE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.SOLID.SHALE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .build());

    public static final OreGenerator STALACTITE = REGISTRY.register("stalactite", OreGenerator.builder()
            .addOre(CellType.SOLID.IRON_ORE, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.01f)
            .addOre(CellType.SOLID.GRANITE, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.05f)
            .addOre(CellType.MOVABLE_SOLID.GRAVEL, new LayeredNoise(5, 1f, 0.02f, 0.6f, 1.5f), 0.1f)
            .addOre(CellType.SOLID.BASALT, new LayeredNoise(5, 1f, 0.01f, 0.6f, 1.5f), 0.05f)
            .build());

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
