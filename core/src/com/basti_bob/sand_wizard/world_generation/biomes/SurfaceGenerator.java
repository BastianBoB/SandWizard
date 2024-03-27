package com.basti_bob.sand_wizard.world_generation.biomes;

import com.basti_bob.sand_wizard.cells.CellType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SurfaceGenerator {

    public static final SurfaceGenerator STONE_ONLY = SurfaceGenerator.builder().add(0, CellType.STONE).build();
    public static final SurfaceGenerator GRASS_FIELD = SurfaceGenerator.builder()
            .add(0, CellType.GRASS).add(-8, CellType.DIRT).add(-25, CellType.STONE).build();

    public static final SurfaceGenerator ICE_MOUNTAINS = SurfaceGenerator.builder().add(0, CellType.ICE).add(-8, CellType.COAL).add(-10, CellType.STONE).build();

    public static final SurfaceGenerator WOOD_ONLY = SurfaceGenerator.builder().add(0, CellType.WOOD).build();


    private final List<Pair<Integer, CellType>> verticalOffsetToCellType;
    public SurfaceGenerator(List<Pair<Integer, CellType>> verticalOffsetToCellType) {
        this.verticalOffsetToCellType = verticalOffsetToCellType;

        verticalOffsetToCellType.sort(Map.Entry.comparingByKey());
    }

    public CellType getCellType(int cellY, double terrainHeight) {

        for(Pair<Integer, CellType> pair : verticalOffsetToCellType) {
            if(cellY - terrainHeight <= pair.getKey()) return pair.getValue();
        }

        return CellType.EMPTY;
    }

    public static SurfaceGeneratorBuilder builder() {
        return new SurfaceGeneratorBuilder();
    }

    public static class SurfaceGeneratorBuilder {

        private final List<Pair<Integer, CellType>> verticalOffsetToCellType = new ArrayList<>();

        public SurfaceGeneratorBuilder add(int offset, CellType cellType) {
            this.verticalOffsetToCellType.add(Pair.of(offset, cellType));
            return this;
        }

        public SurfaceGenerator build() {
            return new SurfaceGenerator(verticalOffsetToCellType);
        }
    }
}
