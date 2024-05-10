package com.basti_bob.sand_wizard.world.world_generation.surface_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class BottomTerrainSurfaceGenerator extends SurfaceGenerator {

    private final Pair<CellType, TerrainHeightGenerator>[] cellsAndGenerators;

    private BottomTerrainSurfaceGenerator(BottomTerrainSurfaceGeneratorBuilder builder) {
        this.cellsAndGenerators = builder.cellsAndGenerators.toArray(new Pair[0]);
    }

    @Override
    public CellType getCellType(World world, int cellX, int cellY, double terrainHeight) {

        float totalOffset = 0;
        for (Pair<CellType, TerrainHeightGenerator> pair : cellsAndGenerators) {

            TerrainHeightGenerator generator = pair.getRight();
            if (generator != null) {
                totalOffset -= generator.getTerrainHeight(world, cellX);
            }

            if (cellY - terrainHeight >= totalOffset) return pair.getLeft();
        }

        return cellsAndGenerators[cellsAndGenerators.length - 1].getLeft();
    }

    public static BottomTerrainSurfaceGeneratorBuilder builder() {
        return new BottomTerrainSurfaceGeneratorBuilder();
    }

    public static class BottomTerrainSurfaceGeneratorBuilder {

        protected final List<Pair<CellType, TerrainHeightGenerator>> cellsAndGenerators = new ArrayList<>();

        public BottomTerrainSurfaceGeneratorBuilder add(CellType cellType, TerrainHeightGenerator generator) {
            this.cellsAndGenerators.add(Pair.of(cellType, generator));
            return this;
        }

        public BottomTerrainSurfaceGeneratorBuilder add(CellType cellType) {
            this.cellsAndGenerators.add(Pair.of(cellType, null));
            return this;
        }

        public BottomTerrainSurfaceGenerator build() {
            return new BottomTerrainSurfaceGenerator(this);
        }
    }
}
