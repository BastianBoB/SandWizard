package com.basti_bob.sand_wizard.world_generation;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.WorldGeneration;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world_generation.ore_generation.OreGenerator;
import com.basti_bob.sand_wizard.world_generation.surface_decoration.WorldDecorator;
import com.basti_bob.sand_wizard.world_generation.surface_generation.SurfaceGenerator;

import java.util.HashMap;

public class ChunkGenerator {

    private final World world;
    private final WorldGeneration worldGeneration;

    public ChunkGenerator(World world) {
        this.world = world;
        this.worldGeneration = world.worldGeneration;
    }

    public ChunkBuilder generateNew(Chunk oldChunk, int chunkPosX, int chunkPosY) {
        return generateWithCells(oldChunk, chunkPosX, chunkPosY, null);
    }

    public ChunkBuilder generateWithCells(Chunk oldChunk, int chunkPosX, int chunkPosY, HashMap<InChunkPos, Cell> queuedCells) {
        ChunkBuilder chunkBuilder = new ChunkBuilder(world, oldChunk, chunkPosX, chunkPosY);

        BiomeType biomeType = worldGeneration.getBiomeTypeWithChunkPos(chunkPosX);

        WorldDecorator worldDecorator = biomeType.worldDecorator;
        SurfaceGenerator surfaceGenerator = biomeType.surfaceGenerator;
        SurfaceGenerator rightSurfaceGenerator = worldGeneration.getBiomeTypeWithChunkPos(chunkPosX + 1).surfaceGenerator;
        CaveGenerator caveGenerator = CaveGenerator.BASE;
        WorldDecorator caveBottomDecorator = WorldDecorator.CAVES;
        WorldDecorator caveTopDecorator = null;

        boolean generatedNewCell = false;

        boolean isCave = false, isCaveAbove = false, isCaveBelow = false;

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunkBuilder.getCellPosX(i);
            float terrainHeight = worldGeneration.getTerrainHeight(cellPosX);
            float surfaceInterpolationFactor = i / (float) WorldConstants.CHUNK_SIZE;

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                int cellPosY = chunkBuilder.getCellPosY(j);

                Cell cell = getCellFromMap(i, j, queuedCells);

                if (cell != null) {
                    chunkBuilder.setCell(cell, cellPosX, cellPosY, i, j);
                    generatedNewCell = false;
                    continue;
                }

                if (j == 0 || !generatedNewCell) {
                    isCave = caveGenerator.isCave(world, cellPosX, cellPosY, terrainHeight);
                    isCaveAbove = caveGenerator.isCave(world, cellPosX, cellPosY + 1, terrainHeight);
                    isCaveBelow = caveGenerator.isCave(world, cellPosX, cellPosY - 1, terrainHeight);
                } else {
                    isCaveBelow = isCave;
                    isCave = isCaveAbove;
                    isCaveAbove = caveGenerator.isCave(world, cellPosX, cellPosY + 1, terrainHeight);
                }

                CellType cellType = getNewCellType(cellPosX, cellPosY, terrainHeight, isCave, surfaceGenerator, rightSurfaceGenerator, surfaceInterpolationFactor);
                chunkBuilder.setCell(cellType, cellPosX, cellPosY, i, j);

                if (cellPosY == (int) terrainHeight + 1) {
                    worldDecorator.decorateSurface(world, cellPosX, cellPosY);
                }

                if (cellPosY < terrainHeight && isCave) {
                    if (!isCaveAbove && caveTopDecorator != null)
                        caveTopDecorator.decorateSurface(world, cellPosX, cellPosY);

                    if (!isCaveBelow && caveBottomDecorator != null)
                        caveBottomDecorator.decorateSurface(world, cellPosX, cellPosY);
                }

                generatedNewCell = true;
            }
        }

        return chunkBuilder;
    }

    private CellType getNewCellType(int cellPosX, int cellPosY, float terrainHeight, boolean isCave, SurfaceGenerator surfaceGenerator, SurfaceGenerator rightSurfaceGenerator, float surfaceInterpolationFactor) {
        if (isCave)
            return CellType.EMPTY;

        CellType ore = OreGenerator.BASE.getCellType(world, cellPosX, cellPosY, terrainHeight);
        if (ore != null) {
            return ore;
        }


        if (world.random.nextFloat() < surfaceInterpolationFactor) {
            return rightSurfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);
        } else {
            return surfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);
        }
    }

    public Cell getCellFromMap(int inChunkX, int inChunkY, HashMap<InChunkPos, Cell> queuedCells) {
        if (queuedCells == null) return null;

        return queuedCells.get(InChunkPos.get(inChunkX, inChunkY));
    }

}
