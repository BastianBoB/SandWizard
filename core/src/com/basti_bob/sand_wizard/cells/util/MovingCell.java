package com.basti_bob.sand_wizard.cells.util;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.world.chunk.ChunkAccessor;

public interface MovingCell {

    boolean moveWithVelocity(ChunkAccessor chunkAccessor, boolean updateDirection);

    boolean moveAlong(ChunkAccessor chunkAccessor, Cell targetCell, int lastValidX, int lastValidY, boolean updateDirection);


}
