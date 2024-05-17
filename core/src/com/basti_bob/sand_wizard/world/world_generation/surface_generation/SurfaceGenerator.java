package com.basti_bob.sand_wizard.world.world_generation.surface_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.ScaledShiftedTerrainHeightGenerator;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.Random;

public abstract class SurfaceGenerator {

    public static final Random random = new Random(0);

    public static final Registry<SurfaceGenerator> REGISTRY = new Registry<>("surface_generator");

    public static final LayerSurfaceGenerator STONE_ONLY = REGISTRY.register("stone_only", LayerSurfaceGenerator.builder().add(0, CellType.SOLID.STONE).build());

    public static final SurfaceGenerator GRASS_FIELD = REGISTRY.register("grass_field", BottomTerrainSurfaceGenerator.builder()
            .add(CellType.EMPTY)
            .add(CellType.SOLID.GRASS, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.02f, 32), 5, 10))
            .add(CellType.MOVABLE_SOLID.DIRT, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.1f, 32), 20, 25))
            .add(CellType.MOVABLE_SOLID.GRAVEL, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.5f, 32), 2, 8))
            .add(CellType.SOLID.STONE, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.5f, 32), 700, 800))
            .build());

    public static final SurfaceGenerator SNOW_AND_ICE = REGISTRY.register("snow_and_ice", BottomTerrainSurfaceGenerator.builder()
            .add(CellType.EMPTY)
            .add(CellType.MOVABLE_SOLID.POWDER_SNOW, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.02f, 32), 2, 5))
            .add(CellType.SOLID.COMPACT_SNOW, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.05f, 32), 5, 20))
            .add(CellType.SOLID.ICE, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.1f, 16), 100, 200))
            .add(CellType.SOLID.STONE, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.05f, 32), 500, 600))

            .build());

    public static final SurfaceGenerator SNOW = REGISTRY.register("snow", BottomTerrainSurfaceGenerator.builder()
            .add(CellType.EMPTY)
            .add(CellType.MOVABLE_SOLID.POWDER_SNOW, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.02f, 32), 5, 10))
            .add(CellType.SOLID.COMPACT_SNOW, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.05f, 32), 20, 40))
            .add(CellType.SOLID.STONE, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.SPIKY(0.05f, 32), 700, 800))
            .build());

    public static final SurfaceGenerator DESERT = REGISTRY.register("desert", BottomTerrainSurfaceGenerator.builder()
            .add(CellType.EMPTY)
            .add(CellType.MOVABLE_SOLID.FINE_SAND, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.PEAKS_AND_VALLEYS(0.01f), 30, 60))
            .add(CellType.MOVABLE_SOLID.SAND, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.PEAKS_AND_VALLEYS(0.01f), 100, 200))
            .add(CellType.SOLID.SAND_STONE, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMALS.PEAKS_AND_VALLEYS(0.01f), 500, 600))
            .build());


    private final int maxSurfaceHeight;
    public SurfaceGenerator(int maxSurfaceHeight) {
        this.maxSurfaceHeight = maxSurfaceHeight;
    }

    public int getMaxSurfaceHeight() {
        return maxSurfaceHeight;
    }

    public abstract CellType getCellType(World world, int cellX, int cellY, double terrainHeight);

}
