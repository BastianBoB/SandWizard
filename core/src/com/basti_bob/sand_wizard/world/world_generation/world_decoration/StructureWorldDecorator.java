package com.basti_bob.sand_wizard.world.world_generation.world_decoration;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureWorldDecorator extends WorldDecorator {

    private final HashMap<StructureGenerator, Float> structuresAndWeights;

    private final float totalWeight;
    public StructureWorldDecorator(HashMap<StructureGenerator, Float> structuresAndWeights) {
        this.structuresAndWeights = structuresAndWeights;

        float totalWeight = 0;
        for (Float value : structuresAndWeights.values()) {
            totalWeight += value;
        }
        this.totalWeight = totalWeight;
    }

    @Override
    public void decorate(World world, int cellX, int cellY) {
        float randomWeight = world.random.nextFloat();

        if(randomWeight > totalWeight) return;

        StructureGenerator generator = null;
        float currentWeight = 0;
        for (Map.Entry<StructureGenerator, Float> entry : structuresAndWeights.entrySet()) {

            currentWeight += entry.getValue();

            if (randomWeight < currentWeight) {
                generator = entry.getKey();
                break;
            }
        }

        if (generator != null) {
            StructureGenerator finalGenerator = generator;

            world.addStructureToPlaceAsync(() -> finalGenerator.generate(world, cellX, cellY));
        }
    }

    public static StructureSurfaceDecoratorBuilder builderFrom(StructureWorldDecorator decorator) {
        return new StructureSurfaceDecoratorBuilder(decorator);
    }

    public static StructureSurfaceDecoratorBuilder builder() {
        return new StructureSurfaceDecoratorBuilder();
    }

    public static class StructureSurfaceDecoratorBuilder {

        private final HashMap<StructureGenerator, Float> structuresAndWeights;

        private StructureSurfaceDecoratorBuilder(StructureWorldDecorator decorator) {
            this.structuresAndWeights = decorator.structuresAndWeights;
        }

        private StructureSurfaceDecoratorBuilder() {
            this.structuresAndWeights = new HashMap<>();
        }

        public StructureSurfaceDecoratorBuilder addStructure(StructureGenerator generator, float weight) {
            structuresAndWeights.put(generator, weight);
            return this;
        }

        public <T extends StructureGenerator> StructureSurfaceDecoratorBuilder addStructureList(List<T> generatorList, float totalWeight) {
            for (StructureGenerator generator : generatorList) {
                structuresAndWeights.put(generator, totalWeight / generatorList.size());
            }
            return this;
        }

        public StructureWorldDecorator build() {
            return new StructureWorldDecorator(structuresAndWeights);
        }
    }
}
