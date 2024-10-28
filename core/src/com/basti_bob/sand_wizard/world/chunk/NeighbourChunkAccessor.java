package com.basti_bob.sand_wizard.world.chunk;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.cells.other.Empty;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;

public class NeighbourChunkAccessor implements ChunkAccessor {

    private final Array2D<Chunk> surroundingChunks;
    public final Chunk centerChunk;

    public NeighbourChunkAccessor(Chunk centerChunk) {
        this.surroundingChunks = new Array2D<>(Chunk.class, 3, 3);
        this.surroundingChunks.set(1, 1, centerChunk);
        this.centerChunk = centerChunk;
    }

    public Chunk[] getSurroundingChunks() {
        return surroundingChunks.getArray();
    }

    public Chunk getNeighbourChunkWithOffset(int offsetX, int offsetY) {
        return surroundingChunks.get(offsetX + 1, offsetY + 1);
    }

    public void setSurroundingChunk(Chunk chunk) {
        int gridX = chunk.getPosX() - centerChunk.getPosX() + 1;
        int gridY = chunk.getPosY() - centerChunk.getPosY() + 1;

        surroundingChunks.set(gridX, gridY, chunk);
    }

    public void removeSurroundingChunk(Chunk chunk) {
        int gridX = chunk.getPosX() - centerChunk.getPosX() + 1;
        int gridY = chunk.getPosY() - centerChunk.getPosY() + 1;

        surroundingChunks.set(gridX, gridY, null);
    }

    @Override
    public Chunk getChunkFromCellPos(int targetX, int targetY) {
        int targetChunkX = World.getChunkPos(targetX);
        int targetChunkY = World.getChunkPos(targetY);

        return getChunkFromChunkPos(targetChunkX, targetChunkY);
    }

    @Override
    public Chunk getChunkFromChunkPos(int targetChunkX, int targetChunkY) {
        int gridX = targetChunkX - centerChunk.getPosX() + 1;
        int gridY = targetChunkY - centerChunk.getPosY() + 1;

        if(gridX < 0 || gridX > 2 || gridY < 0 || gridY > 2) return null;

        return surroundingChunks.get(gridX, gridY);
    }
}
