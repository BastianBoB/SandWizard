package com.basti_bob.sand_wizard.world.world_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.ChunkBuilder;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;
import com.basti_bob.sand_wizard.world.world_generation.ore_generation.OreGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.PlacePriority;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;

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

        ChunkCreationData chunkData = new ChunkCreationData(worldGeneration, chunkPosX, chunkPosY);


        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunkBuilder.getCellPosX(i);
            float terrainHeight = worldGeneration.getTerrainHeight(cellPosX);
            float surfaceInterpolationFactor = i / (float) WorldConstants.CHUNK_SIZE;
            boolean generatedNewCell = false;
            boolean isCave = false, isCaveAbove = false, isCaveBelow = false;


            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {

                int cellPosY = chunkBuilder.getCellPosY(j);

                if (j == 0 || !generatedNewCell) {
                    isCave = chunkData.caveGenerator.isCave(world, cellPosX, cellPosY, terrainHeight);
                    isCaveAbove = chunkData.caveGenerator.isCave(world, cellPosX, cellPosY + 1, terrainHeight);
                    isCaveBelow = chunkData.caveGenerator.isCave(world, cellPosX, cellPosY - 1, terrainHeight);
                } else {
                    isCaveBelow = isCave;
                    isCave = isCaveAbove;
                    isCaveAbove = chunkData.caveGenerator.isCave(world, cellPosX, cellPosY + 1, terrainHeight);
                }

                CellType newCellType = getNewCellType(cellPosX, cellPosY, terrainHeight, isCave, surfaceInterpolationFactor, chunkData);

                ToPlaceStructureCell toPlaceStructureCell = getCellFromMap(i, j, queuedCells);
                if (toPlaceStructureCell != null && !(toPlaceStructureCell.getPlacePriority() == PlacePriority.REPLACE_EMPTY && newCellType != CellType.EMPTY)) {
                    chunkBuilder.setCell(toPlaceStructureCell.getCell(), cellPosX, cellPosY, i, j);
                    generatedNewCell = false;
                } else {
                    chunkBuilder.setCell(newCellType, cellPosX, cellPosY, i, j);
                    generatedNewCell = true;
                }

                if (cellPosY == (int) terrainHeight + 1) {
                    chunkData.surfaceDecorator.decorate(world, cellPosX, cellPosY);
                }

                if (cellPosY < terrainHeight && isCave) {
                    if (!isCaveAbove) chunkData.caveTopDecorator.decorate(world, cellPosX, cellPosY);

                    if (!isCaveBelow) chunkData.caveBottomDecorator.decorate(world, cellPosX, cellPosY);
                }
            }
        }

        return chunkBuilder;
    }

    private CellType getNewCellType(int cellPosX, int cellPosY, float terrainHeight, boolean isCave, float surfaceInterpolationFactor, ChunkCreationData chunkData) {

        if (isCave) return CellType.EMPTY;

//        CellType caveCell = CaveGenerator.BASE.getCaveCellType(world, cellPosX, cellPosY, terrainHeight);
//        if (caveCell != null) return caveCell;
//
//        if (true)
//            return CellType.EMPTY;

        CellType ore = chunkData.oreGenerator.getCellType(world, cellPosX, cellPosY, terrainHeight);
        if (ore != null) return ore;


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

        CellType caveBiomeCellType = chunkData.caveBiomeType.caveCellType;
        return caveBiomeCellType;
    }

    public ToPlaceStructureCell getCellFromMap(int inChunkX, int inChunkY, HashMap<InChunkPos, ToPlaceStructureCell> queuedCells) {
        //return Empty.getInstance();

        if (queuedCells == null) return null;

        return queuedCells.get(InChunkPos.get(inChunkX, inChunkY));
    }

}
