package com.basti_bob.sand_wizard.world_generation.surface_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.OpenSimplexNoise;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.ScaledShiftedTerrainHeightGenerator;
import com.basti_bob.sand_wizard.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.Random;

public abstract class SurfaceGenerator {

    public static final Random random = new Random(0);

    public static final LayerSurfaceGenerator STONE_ONLY = LayerSurfaceGenerator.builder().add(0, CellType.STONE).build();

    public static final LayerSurfaceGenerator ICE_MOUNTAINS = LayerSurfaceGenerator.builder().add(0, CellType.ICE).add(-8, CellType.COAL).add(-10, CellType.STONE).build();
    public static final LayerSurfaceGenerator WOOD_ONLY = LayerSurfaceGenerator.builder().add(0, CellType.WOOD).build();

    public static final SurfaceGenerator GRASS_FIELD = BottomTerrainSurfaceGenerator.builder()
            .add(CellType.EMPTY)
            .add(CellType.GRASS, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.SPIKY(0.02f, 32), 5, 10))
            .add(CellType.DIRT, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.SPIKY(0.1f, 32), 20, 25))
            .add(CellType.GRAVEL, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.SPIKY(0.5f, 32), 0, 5))
            .add(CellType.STONE)
            .build();

    public static final SurfaceGenerator SNOW_AND_ICE = BottomTerrainSurfaceGenerator.builder()
            .add(CellType.EMPTY)
            .add(CellType.POWDER_SNOW, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.SPIKY(0.02f, 32), 4, 8))
            .add(CellType.COMPACT_SNOW, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.SPIKY(0.05f, 32), 10, 30))
            .add(CellType.ICE, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.SPIKY(0.1f, 16), 100, 200))
            .add(CellType.STONE)
            .build();

    public static final SurfaceGenerator DESERT = BottomTerrainSurfaceGenerator.builder()
            .add(CellType.EMPTY)
            .add(CellType.FINE_SAND, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.PEAKS_AND_VALLEYS(0.01f), 30, 60))
            .add(CellType.SAND, ScaledShiftedTerrainHeightGenerator.normalToRange(TerrainHeightGenerator.NORMAL.PEAKS_AND_VALLEYS(0.01f), 100, 200))
            .add(CellType.SAND_STONE)
            .build();

    public abstract CellType getCellType(World world, int cellX, int cellY, double terrainHeight);

}
