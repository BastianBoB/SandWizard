package com.basti_bob.sand_wizard.world_generation.structures;

import com.basti_bob.sand_wizard.world.World;

public interface StructureGenerator {

    Structure generate(World world, int startX, int startY);
}
