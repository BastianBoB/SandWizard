package com.basti_bob.sand_wizard.world_generation.structures;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world_generation.util.Region;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Structure {

    private final HashMap<Long, List<Cell>> chunksWithCells;

    public Structure(HashMap<Long, List<Cell>> chunksWithCells) {
        this.chunksWithCells = chunksWithCells;
    }

    public void placeInWorld(World world, int xOffset, int yOffset) {
        for(Map.Entry<Long, CellType> entry : cells.entrySet()) {

            long positionKey = entry.getKey();
            int cellX = World.getXFromPositionKey(positionKey) + xOffset;
            int cellY = World.getYFromPositionKey(positionKey) + yOffset;
            CellType cellType = entry.getValue();

            world.setCellAndLoadChunksAsync(cellType, cellX, cellY);
        }
    }

    public static class StructureGenerator {

        private final HashMap<Long, List<Cell>> chunksWithCells = new HashMap<>();

        public StructureGenerator() {

        }

        public void addCell(CellType cellType, int cellX, int cellY) {
            int chunkX = World.getChunkPos(cellX);
            int chunkY = World.getChunkPos(cellY);
            long chunkKey = World.getPositionLong(chunkX, chunkY);

            chunksWithCells.get(chunkKey).add(cellType.cre)
        }
    }
}
