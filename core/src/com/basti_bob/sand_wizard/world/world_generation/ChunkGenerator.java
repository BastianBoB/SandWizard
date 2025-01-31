package com.basti_bob.sand_wizard.world.world_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.biomes.CaveBiomeType;
import com.basti_bob.sand_wizard.world.world_generation.chunk_data.ChunkCaveData;
import com.basti_bob.sand_wizard.world.world_generation.chunk_data.TempChunkCreationData;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.PlacePriority;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;

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

    public ChunkBuilder generateWithCells(Chunk oldChunk, int chunkPosX, int chunkPosY, HashMap<InChunkPos, ToPlaceStructureCell> queuedCells) {

        ChunkBuilder chunkBuilder = new ChunkBuilder(world, oldChunk, chunkPosX, chunkPosY);

        TempChunkCreationData chunkCreationData = new TempChunkCreationData(worldGeneration, chunkPosX, chunkPosY);
        ChunkCaveData chunkCaveData = worldGeneration.getOrCreateChunkCaveData(chunkPosX, chunkPosY);

        if(queuedCells != null) chunkBuilder.isModifiedChunk = true;

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunkBuilder.getCellPosX(i);
            float terrainHeight = worldGeneration.getTerrainHeight(cellPosX);
            float surfaceInterpolationFactor = i / (float) WorldConstants.CHUNK_SIZE;

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {

                int cellPosY = chunkBuilder.getCellPosY(j);

                boolean isCave = chunkCaveData.isCaveWithInChunkPos(i, j);
                boolean isCaveAbove = (j == WorldConstants.CHUNK_SIZE - 1) ? worldGeneration.isCave(cellPosX, cellPosY + 1) : chunkCaveData.isCaveWithInChunkPos(i, j + 1);
                boolean isCaveBelow = (j == 0) ? worldGeneration.isCave(cellPosX, cellPosY - 1) : chunkCaveData.isCaveWithInChunkPos(i, j - 1);

                CellType newCellType = getNewCellType(cellPosX, cellPosY, terrainHeight, isCave, surfaceInterpolationFactor, chunkCreationData);

                ToPlaceStructureCell toPlaceStructureCell = getCellFromMap(i, j, queuedCells);

                if (toPlaceStructureCell != null && !(toPlaceStructureCell.getPlacePriority() == PlacePriority.REPLACE_EMPTY)) {
                    chunkBuilder.setCell(toPlaceStructureCell.getCell(), cellPosX, cellPosY, i, j);
                } else {
                    chunkBuilder.setCell(newCellType, cellPosX, cellPosY, i, j);
                }

                decorateChunk(cellPosX, cellPosY, terrainHeight, isCaveBelow, isCave, isCaveAbove, chunkCreationData);
            }
        }

        return chunkBuilder;
    }

    private void decorateChunk(int cellPosX, int cellPosY, float terrainHeight, boolean isCaveBelow, boolean isCave, boolean isCaveAbove, TempChunkCreationData chunkData) {
        if (cellPosY == (int) terrainHeight + 1) {

            for (WorldDecorator surfaceDecorator : chunkData.surfaceDecorators) {
                surfaceDecorator.decorate(world, cellPosX, cellPosY);
            }
        }

        if (cellPosY < terrainHeight && isCave) {
            if (!isCaveAbove) {
                for (WorldDecorator caveTopDecorator : chunkData.caveTopDecorators) {
                    caveTopDecorator.decorate(world, cellPosX, cellPosY);
                }
            }

            if (!isCaveBelow) {
                for (WorldDecorator caveBottomDecorator : chunkData.caveBottomDecorators) {
                    caveBottomDecorator.decorate(world, cellPosX, cellPosY);
                }
            }
        }
    }

    private CellType getNewCellType(int cellPosX, int cellPosY, float terrainHeight, boolean isCave, float surfaceInterpolationFactor, TempChunkCreationData chunkData) {

        if (isCave) return CellType.EMPTY;

//        CellType caveCell = CaveGenerator.BASE.getCaveCellType(world, cellPosX, cellPosY, terrainHeight);
//        if (caveCell != null) return caveCell;
//
//        if (true)
//            return CellType.EMPTY;

        CellType ore = chunkData.oreGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);
        if (ore != null) {
            return ore;
        }


        CellType surfaceCellType;
        if (terrainHeight - cellPosY > chunkData.surfaceGenerator.getMaxSurfaceHeight()) {
            surfaceCellType = null;
        } else {
            if (world.random.nextFloat() < surfaceInterpolationFactor) {
                surfaceCellType = chunkData.rightSurfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);
            } else {
                surfaceCellType = chunkData.surfaceGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);
            }
        }

        if (surfaceCellType != null) {
            return surfaceCellType;
        }

        if (!isCaveBiomeEdgeChunk(chunkData.chunkPos, chunkData.caveBiomeType)) {
            return chunkData.caveBiomeType.caveCellType;
        }

        float interpolatedChunkPosX = cellPosX / (float) WorldConstants.CHUNK_SIZE;
        float interpolatedChunkPosY = cellPosY / (float) WorldConstants.CHUNK_SIZE;

        return worldGeneration.getInterpolatedCaveBiomeType(interpolatedChunkPosX, interpolatedChunkPosY).caveCellType;
    }

    private boolean isCaveBiomeEdgeChunk(ChunkPos chunkPos, CaveBiomeType caveBiomeType) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                if (worldGeneration.getCaveBiomeType(chunkPos.x + i, chunkPos.y + j) != caveBiomeType) return true;
            }
        }
        return false;
    }

    public ToPlaceStructureCell getCellFromMap(int inChunkX, int inChunkY, HashMap<InChunkPos, ToPlaceStructureCell> queuedCells) {
        //return Empty.getInstance();

        if (queuedCells == null) return null;

        return queuedCells.get(InChunkPos.get(inChunkX, inChunkY));
    }

}
