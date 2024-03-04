package com.basti_bob.sand_wizard.cells;

import com.basti_bob.sand_wizard.world.ChunkAccessor;

public interface MovingCell {

    boolean moveWithVelocity(ChunkAccessor chunkAccessor, boolean updateDirection);

    boolean moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int lastValidX, int lastValidY, boolean updateDirection);


}
