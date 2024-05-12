package com.basti_bob.sand_wizard.world.world_generation.world_decoration;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;

public abstract class WorldDecorator {

    public static final Registry<WorldDecorator> REGISTRY = new Registry<>("world_decorator");

    public static class SURFACE {

        public static final Registry<WorldDecorator> REGISTRY = new Registry<>("surface", WorldDecorator.REGISTRY);

        public static final WorldDecorator FLOWER_FIELD = REGISTRY.register("flower_field", StructureWorldDecorator.builder()
                .addStructureList(StructureGenerator.TREES.REGISTRY.getAllEntries(), 0.001f)
                .addStructureList(StructureGenerator.FLOWERS.REGISTRY.getAllEntries(), 0.15f).build());

        public static final WorldDecorator HILLS = REGISTRY.register("hills", StructureWorldDecorator.builder()
                .addStructureList(StructureGenerator.TREES.REGISTRY.getAllEntries(), 0.005f)
                .addStructureList(StructureGenerator.FLOWERS.REGISTRY.getAllEntries(), 0.01f)
                .addStructure(StructureGenerator.PONDS.MEDIUM_WATER, 0.0005f).build());
    }

    public static class CAVE {

        public static final Registry<WorldDecorator> REGISTRY = new Registry<>("cave", WorldDecorator.REGISTRY);

        public static final WorldDecorator BASE_BOTTOM = REGISTRY.register("base_bottom", StructureWorldDecorator.builder()
                .addStructureList(StructureGenerator.FLOWERS.BERRY_BUSHES.GLOWING.REGISTRY.getAllEntries(), 0.002f)
                .addStructure(StructureGenerator.PONDS.SMALL_ACID, 0.001f)
                .addStructure(StructureGenerator.PONDS.SMALL_LAVA, 0.002f)
                .addStructure(StructureGenerator.PONDS.SMALL_WATER, 0.004f)
                .addStructure(StructureGenerator.PONDS.MEDIUM_ACID, 0.00002f)
                .addStructure(StructureGenerator.PONDS.MEDIUM_LAVA, 0.00004f)
                .addStructure(StructureGenerator.PONDS.MEDIUM_WATER, 0.0001f)
                .build());

        public static final WorldDecorator BASE_TOP = REGISTRY.register("base_top", StructureWorldDecorator.builder()
                .build());

        public static final WorldDecorator STALACTITES = REGISTRY.register("stalactites", StructureWorldDecorator.builder()
                .addStructureList(StructureGenerator.STALACTITES.REGISTRY.getAllEntries(), 0.01f).build());

        public static final WorldDecorator STALAGMITES = REGISTRY.register("stalagmites", StructureWorldDecorator.builder()
                .addStructureList(StructureGenerator.STALAGMITES.REGISTRY.getAllEntries(), 0.01f).build());
    }

    public static final WorldDecorator NOTHING = REGISTRY.register("nothing", new WorldDecorator() {
        @Override
        public void decorate(World world, int cellX, int cellY) {

        }
    });

    public abstract void decorate(World world, int cellX, int cellY);
}
