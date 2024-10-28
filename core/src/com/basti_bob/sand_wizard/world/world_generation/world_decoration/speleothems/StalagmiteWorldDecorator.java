package com.basti_bob.sand_wizard.world.world_generation.world_decoration.speleothems;

import com.basti_bob.sand_wizard.util.Direction;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;

public class StalagmiteWorldDecorator extends SpeleothemWorldDecorator {

    public StalagmiteWorldDecorator() {
        super(SpeleothemWorldDecorator.builder()
                .direction(Direction.UP)
                .addStructureList(StructureGenerator.STALAGMITES.LARGE.REGISTRY.getAllEntries(), 0.003f)
                .addStructureList(StructureGenerator.STALAGMITES.MEDIUM.REGISTRY.getAllEntries(), 0.005f)
                .addStructureList(StructureGenerator.STALAGMITES.SMALL.REGISTRY.getAllEntries(), 0.009f)
                .addStructureList(StructureGenerator.STALAGMITES.TINY.REGISTRY.getAllEntries(), 0.027f)
        );
    }
}