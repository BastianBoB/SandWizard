package com.basti_bob.sand_wizard.world_generation.surface_decoration;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.structures.StructureGenerator;

public abstract class SurfaceDecorator {

    public static final SurfaceDecorator FLOWER_FIELD = StructureSurfaceDecorator.builder()
            .addStructureList(StructureGenerator.TREES.ALL, 0.001f)
            .addStructureList(StructureGenerator.FLOWERS.ALL, 0.15f).build();

    public static final SurfaceDecorator HILLS = StructureSurfaceDecorator.builder()
            .addStructureList(StructureGenerator.TREES.ALL, 0.005f)
            .addStructureList(StructureGenerator.FLOWERS.ALL, 0.01f).build();

    public static final SurfaceDecorator NOTHING = new SurfaceDecorator() {
        @Override
        public void decorateSurface(World world, int cellX, int cellY) {

        }
    };

    public abstract void decorateSurface(World world, int cellX, int cellY);
}
