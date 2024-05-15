package com.basti_bob.sand_wizard.world.world_generation.surface_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class LayerSurfaceGenerator extends SurfaceGenerator {


    private final List<Pair<Integer, CellType>> verticalOffsetToCellType;

    public LayerSurfaceGenerator(List<Pair<Integer, CellType>> verticalOffsetToCellType) {
        super(getMaxSurfaceHeight(verticalOffsetToCellType));

        this.verticalOffsetToCellType = verticalOffsetToCellType;

        verticalOffsetToCellType.sort(Map.Entry.comparingByKey());
        Collections.reverse(verticalOffsetToCellType);
    }

    private static int getMaxSurfaceHeight(List<Pair<Integer, CellType>> verticalOffsetToCellType) {
        int maxSurfaceHeight = 0;
        for (Pair<Integer, CellType> pair : verticalOffsetToCellType) {
            maxSurfaceHeight += pair.getLeft();
        }
        return maxSurfaceHeight;
    }

    @Override
    public CellType getCellType(World world, int cellX, int cellY, double terrainHeight) {

        for (Pair<Integer, CellType> pair : verticalOffsetToCellType) {
            if (cellY - terrainHeight <= pair.getKey()) return pair.getValue();
        }

        return CellType.EMPTY;
    }

    public List<Pair<Integer, CellType>> getVerticalOffsetToCellType() {
        return verticalOffsetToCellType;
    }

    public static SurfaceGeneratorBuilder builder() {
        return new SurfaceGeneratorBuilder();
    }

    public static class SurfaceGeneratorBuilder {

        protected final List<Pair<Integer, CellType>> verticalOffsetToCellType = new ArrayList<>();

        public SurfaceGeneratorBuilder add(int offset, CellType cellType) {
            this.verticalOffsetToCellType.add(Pair.of(offset, cellType));
            return this;
        }

        public LayerSurfaceGenerator build() {
            return new LayerSurfaceGenerator(verticalOffsetToCellType);
        }
    }
}
