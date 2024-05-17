package com.basti_bob.sand_wizard.world.world_generation.chunk_data;

import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.WorldGeneration;
import com.basti_bob.sand_wizard.world.world_generation.biomes.CaveBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.biomes.SurfaceBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.ore_generation.OreGenerator;
import com.basti_bob.sand_wizard.world.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;

import java.util.List;

public class TempChunkCreationData {

    public final ChunkPos chunkPos;

    public final SurfaceBiomeType surfaceBiomeType;
    public final List<WorldDecorator> surfaceDecorators;
    public final SurfaceGenerator surfaceGenerator;
    public final SurfaceGenerator rightSurfaceGenerator;

    public final CaveBiomeType caveBiomeType;
    public final CaveGenerator caveGenerator;
    public final List<WorldDecorator> caveBottomDecorators;
    public final List<WorldDecorator> caveTopDecorators;
    public final OreGenerator oreGenerator;

    public TempChunkCreationData(WorldGeneration worldGeneration, int chunkPosX, int chunkPosY) {
        this.chunkPos = new ChunkPos(chunkPosX, chunkPosY);
        this.surfaceBiomeType = worldGeneration.getSurfaceBiomeType(chunkPosX);
        this.surfaceDecorators = surfaceBiomeType.worldDecorators;
        this.surfaceGenerator = surfaceBiomeType.surfaceGenerator;
        this.rightSurfaceGenerator = worldGeneration.getSurfaceBiomeType(chunkPosX + 1).surfaceGenerator;

        this.caveBiomeType = worldGeneration.getCaveBiomeType(chunkPosX, chunkPosY);
        this.caveGenerator = caveBiomeType.caveGenerator;
        this.caveBottomDecorators = caveBiomeType.caveBottomDecorators;
        this.caveTopDecorators = caveBiomeType.caveTopDecorators;
        this.oreGenerator = caveBiomeType.oreGenerator;
    }
}
