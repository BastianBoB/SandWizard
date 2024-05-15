package com.basti_bob.sand_wizard.world.world_generation;

import com.basti_bob.sand_wizard.world.world_generation.biomes.CaveBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.biomes.SurfaceBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.ore_generation.OreGenerator;
import com.basti_bob.sand_wizard.world.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;

public class ChunkCreationData {

    public final SurfaceBiomeType surfaceBiomeType;
    public final WorldDecorator surfaceDecorator;
    public final SurfaceGenerator surfaceGenerator;
    public final SurfaceGenerator rightSurfaceGenerator;

    public final CaveBiomeType caveBiomeType;
    public final CaveGenerator caveGenerator;
    public final WorldDecorator caveBottomDecorator;
    public final WorldDecorator caveTopDecorator;
    public final OreGenerator oreGenerator;

    public ChunkCreationData(WorldGeneration worldGeneration, int chunkPosX, int chunkPosY){

        this.surfaceBiomeType = worldGeneration.getSurfaceBiomeType(chunkPosX);
        this.surfaceDecorator = surfaceBiomeType.worldDecorator;
        this.surfaceGenerator = surfaceBiomeType.surfaceGenerator;
        this.rightSurfaceGenerator = worldGeneration.getSurfaceBiomeType(chunkPosX + 1).surfaceGenerator;

        this.caveBiomeType = worldGeneration.getCaveBiomeType(chunkPosX, chunkPosY);
        this.caveGenerator = caveBiomeType.caveGenerator;
        this.caveBottomDecorator = caveBiomeType.caveBottomDecorator;
        this.caveTopDecorator = caveBiomeType.caveTopDecorator;
        this.oreGenerator = caveBiomeType.oreGenerator;
    }
}
