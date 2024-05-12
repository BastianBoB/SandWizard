package com.basti_bob.sand_wizard.registry;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.world_generation.biomes.BiomeType;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.ore_generation.OreGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;
import com.basti_bob.sand_wizard.world.world_generation.surface_generation.SurfaceGenerator;
import com.basti_bob.sand_wizard.world.world_generation.terrain_height_generation.TerrainHeightGenerator;

import java.util.ArrayList;
import java.util.List;

public class RegistryLoader {

    public static void loadRegistries() {
        List<Class<?>> allRegistryClasses = new ArrayList<>();

        allRegistryClasses.add(CellType.class);
        allRegistryClasses.add(StructureGenerator.class);
        allRegistryClasses.add(TerrainHeightGenerator.class);
        allRegistryClasses.add(WorldDecorator.class);
        allRegistryClasses.add(SurfaceGenerator.class);
        allRegistryClasses.add(CaveGenerator.class);
        allRegistryClasses.add(OreGenerator.class);
        allRegistryClasses.add(BiomeType.class);


        for (Class<?> registryClass : allRegistryClasses) {
            loadClassAndInnerClasses(registryClass);
        }
    }

    public static void loadClassAndInnerClasses(Class<?> clazz) {
        try {
            Class.forName(clazz.getName());

            for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
                loadClassAndInnerClasses(declaredClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
