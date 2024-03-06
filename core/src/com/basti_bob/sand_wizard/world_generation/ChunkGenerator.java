package com.basti_bob.sand_wizard.world_generation;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class ChunkGenerator {

    public static Chunk generateNew(World world, int chunkPosX, int chunkPosY) {
        Chunk chunk = new Chunk(world, chunkPosX, chunkPosY);

        for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

            int cellPosX = chunk.getCellPosX(i);
            double terrainHeight = world.openSimplexNoise.eval(cellPosX / 100f, 0, 0) * 60 - 30;

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                chunk.setCellWithInChunkPos(CellType.EMPTY, i, j);

                int cellPosY = chunk.getCellPosY(j);

                CellType cellType = CellType.EMPTY;

                if (cellPosY < terrainHeight) cellType = CellType.GRASS;
                if (cellPosY < terrainHeight - 10) cellType = CellType.DIRT;
                if (cellPosY < terrainHeight - 30) cellType = CellType.STONE;

                chunk.setCellWithInChunkPos(cellType, i, j);
            }
        }

        //chunk.setCell(CellType.GRASS, 0, 0);

        return chunk;
    }
}
