package com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing;

import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.CellPlaceFlag;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.coordinates.InChunkPos;

import java.util.HashMap;
import java.util.Map;

public class StructurePlacingManager {

    private final World world;

    private final HashMap<ChunkPos, HashMap<InChunkPos, ToPlaceStructureCell>> unloadedStructureCells = new HashMap<>();

    public StructurePlacingManager(World world) {
        this.world = world;
    }

    public HashMap<InChunkPos, ToPlaceStructureCell> getUnloadedStructureCells(ChunkPos chunkPos) {
        return unloadedStructureCells.get(chunkPos);
    }

    public void addUnloadedStructureCells(ChunkPos chunkPos, HashMap<InChunkPos, ToPlaceStructureCell> toPlaceCells) {
        HashMap<InChunkPos, ToPlaceStructureCell> currentCells = unloadedStructureCells.get(chunkPos);

        if(currentCells == null) {
            unloadedStructureCells.put(chunkPos, toPlaceCells);
        } else {

            for (Map.Entry<InChunkPos, ToPlaceStructureCell> entry : toPlaceCells.entrySet()) {
                InChunkPos inChunkPos = entry.getKey();
                ToPlaceStructureCell toPlaceStructureCell = entry.getValue();

                if (canOverrideUnloadedStructureCell(inChunkPos, toPlaceStructureCell, currentCells)) {
                    currentCells.put(inChunkPos, toPlaceStructureCell);
                }
            }
        }
    }

    private boolean canOverrideUnloadedStructureCell(InChunkPos inChunkPos, ToPlaceStructureCell toPlaceStructureCell, HashMap<InChunkPos, ToPlaceStructureCell> currentCells) {
        if(currentCells == null) return true;

        ToPlaceStructureCell currentCell = currentCells.get(inChunkPos);
        if (currentCell == null) return true;

        return toPlaceStructureCell.getPlacePriority().compareTo(currentCell.getPlacePriority()) > 0;
    }

    public void placeStructureCellsInChunk(HashMap<InChunkPos, ToPlaceStructureCell> toPlaceCells, Chunk chunk) {

        for (Map.Entry<InChunkPos, ToPlaceStructureCell> cellEntry : toPlaceCells.entrySet()) {
            InChunkPos inChunkPos = cellEntry.getKey();
            ToPlaceStructureCell toPlaceStructureCell = cellEntry.getValue();

            if (toPlaceStructureCell.getPlacePriority().compareTo(PlacePriority.REPLACE_EMPTY) > 0 || chunk.getCellFromInChunkPos(inChunkPos.x, inChunkPos.y) instanceof Empty) {
                chunk.setCellWithInChunkPos(toPlaceStructureCell.getCell(), inChunkPos.x, inChunkPos.y, CellPlaceFlag.NEW);
            }
        }
    }

    public void loadedChunk(Chunk chunk, ChunkPos chunkPos) {

        HashMap<InChunkPos, ToPlaceStructureCell> toPlaceCells = unloadedStructureCells.get(chunkPos);
        if (toPlaceCells != null) {
            placeStructureCellsInChunk(toPlaceCells, chunk);
            unloadedStructureCells.remove(chunkPos);
        }
    }
}
