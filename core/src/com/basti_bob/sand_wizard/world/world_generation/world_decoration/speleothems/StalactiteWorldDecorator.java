package com.basti_bob.sand_wizard.world.world_generation.world_decoration.speleothems;

import com.basti_bob.sand_wizard.util.Direction;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;

public class StalactiteWorldDecorator extends SpeleothemWorldDecorator {

    public StalactiteWorldDecorator() {
        super(SpeleothemWorldDecorator.builder()
                .direction(Direction.DOWN)
                .addStructureList(StructureGenerator.STALACTITES.LARGE.REGISTRY.getAllEntries(), 0.003f)
                .addStructureList(StructureGenerator.STALACTITES.MEDIUM.REGISTRY.getAllEntries(), 0.005f)
                .addStructureList(StructureGenerator.STALACTITES.SMALL.REGISTRY.getAllEntries(), 0.009f)
                .addStructureList(StructureGenerator.STALACTITES.TINY.REGISTRY.getAllEntries(), 0.027f)
        );
    }
}