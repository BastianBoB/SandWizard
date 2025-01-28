package com.basti_bob.sand_wizard.world.world_rendering.lighting;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.world.chunk.Chunk;

public class CellLight extends ChunkLight {

    private final Cell cell;

    public CellLight(Cell cell, int cellX, int cellY, float r, float g, float b, float radius, float intensity) {
        super(cellX, cellY, r, g, b, radius, intensity);
        this.cell = cell;
    }

    @Override
    public boolean isEmittingLight() {
        return cell.isEmittingLight();
    }
}
