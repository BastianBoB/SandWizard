package com.basti_bob.sand_wizard.world.world_generation.surface_decoration;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;

public abstract class WorldDecorator {

    public static final WorldDecorator FLOWER_FIELD = StructureWorldDecorator.builder()
            .addStructureList(StructureGenerator.TREES.REGISTRY.allEntries, 0.001f)
            .addStructureList(StructureGenerator.FLOWERS.REGISTRY.allEntries, 0.15f).build();

    public static final WorldDecorator HILLS = StructureWorldDecorator.builder()
            .addStructureList(StructureGenerator.TREES.REGISTRY.allEntries, 0.005f)
            .addStructureList(StructureGenerator.FLOWERS.REGISTRY.allEntries, 0.01f)
            .addStructure(StructureGenerator.PONDS.MEDIUM_WATER, 0.0005f).build();

    public static final WorldDecorator BASE_CAVES_BOTTOM = StructureWorldDecorator.builder()
            .addStructureList(StructureGenerator.FLOWERS.BERRY_BUSHES.GLOWING.REGISTRY.allEntries, 0.002f)
            .addStructure(StructureGenerator.PONDS.SMALL_ACID, 0.001f)
            .addStructure(StructureGenerator.PONDS.SMALL_LAVA, 0.002f)
            .addStructure(StructureGenerator.PONDS.SMALL_WATER, 0.004f)
            .addStructure(StructureGenerator.PONDS.MEDIUM_ACID, 0.00002f)
            .addStructure(StructureGenerator.PONDS.MEDIUM_LAVA, 0.00004f)
            .addStructure(StructureGenerator.PONDS.MEDIUM_WATER, 0.0001f)
            .build();
    public static final WorldDecorator BASE_CAVES_TOP = StructureWorldDecorator.builder()
            .build();

    public static final WorldDecorator STALACTITES = StructureWorldDecorator.builder()
            .addStructureList(StructureGenerator.STALACTITES.REGISTRY.allEntries, 1f).build();


    public static final WorldDecorator NOTHING = new WorldDecorator() {
        @Override
        public void decorateSurface(World world, int cellX, int cellY) {

        }
    };

    public abstract void decorateSurface(World world, int cellX, int cellY);
}
