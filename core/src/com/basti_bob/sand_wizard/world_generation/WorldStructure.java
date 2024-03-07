package com.basti_bob.sand_wizard.world_generation;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;

import java.util.List;

public class WorldStructure {

    private void addCellsToWorld(World world, List<Cell> cells) {
        addCellsToWorld(world, cells, getRegionsFromCells(cells));
    }

    private void addCellsToWorld(World world, List<Cell> cells, Region region) {
        Array2D<Chunk> chunks = getChunksInRegion(world, region);


    }

    private Array2D<Chunk> getChunksInRegion(World world, Region region) {
        int startChunkX = World.getChunkPos(region.startX);
        int startChunkY = World.getChunkPos(region.startY);
        int endChunkX = World.getChunkPos(region.endX);
        int endChunkY = World.getChunkPos(region.endY);

        return null;
    }

    private Region getRegionsFromCells(List<Cell> cells) {
        Cell firstCell = cells.get(0);

        int minX = firstCell.posX, minY = firstCell.posY, maxX = firstCell.posX, maxY = firstCell.posY;

        for(int i = 1; i < cells.size(); i++) {
            Cell cell = cells.get(i);

            int x = cell.posX;
            int y = cell.posY;

            if(x < minX) minX = x;
            if(x > maxX) maxX = x;
            if(y < minY) minY = y;
            if(y > maxY) maxY = y;
        }

        return new Region(minX, minY, maxX, maxY);
    }



}
