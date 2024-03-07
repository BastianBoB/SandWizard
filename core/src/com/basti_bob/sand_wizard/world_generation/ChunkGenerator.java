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
            double terrainHeight = getTerrainHeight(world, cellPosX);

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

//        int worldX = chunk.getCellPosX(0);
//        int terrainHeight = getTerrainHeight(world, worldX);
//        if(chunk.getCellPosY(0) < terrainHeight){
//            if(Math.random() < 0.1) {
//                TreeGenerator.BASE_TREE.placeTree(world, worldX, terrainHeight + 20);
//            }
//        }


        //chunk.setCell(CellType.GRASS, 0, 0);

        return chunk;
    }

    public static int getTerrainHeight(World world, int cellPosX) {
        return (int) (world.openSimplexNoise.eval(cellPosX / 100f, 0, 0) * 60 - 30);
    }
}
