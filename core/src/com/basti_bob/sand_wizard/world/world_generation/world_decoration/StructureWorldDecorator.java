package com.basti_bob.sand_wizard.world.world_generation.world_decoration;

import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class StructureWorldDecorator extends WorldDecorator {

    private final List<Pair<StructureGenerator, Float>> structuresAndWeights;

    private final float totalWeight;
    public StructureWorldDecorator(List<Pair<StructureGenerator, Float>> structuresAndWeights) {
        this.structuresAndWeights = structuresAndWeights;

        float totalWeight = 0;
        for (Pair<StructureGenerator, Float> pair : structuresAndWeights) {
            totalWeight += pair.getRight();
        }
        this.totalWeight = totalWeight;
    }

    @Override
    public void decorate(World world, int cellX, int cellY) {
        float randomWeight = world.random.nextFloat();

        if(randomWeight > totalWeight) return;

        StructureGenerator generator = null;
        float currentWeight = 0;
        for (Pair<StructureGenerator, Float> pair : structuresAndWeights) {

            currentWeight += pair.getRight();

            if (randomWeight < currentWeight) {
                generator = pair.getLeft();
                break;
            }
        }

        if (generator != null) {
            StructureGenerator finalGenerator = generator;

            world.addStructureToPlaceAsync(() -> finalGenerator.generate(world, cellX, cellY));
        }
    }


    public static StructureWorldDecoratorBuilder builder() {
        return new StructureWorldDecoratorBuilder();
    }

    public static class StructureWorldDecoratorBuilder {

        private final List<Pair<StructureGenerator, Float>> structuresAndWeights;


        private StructureWorldDecoratorBuilder() {
            this.structuresAndWeights = new ArrayList<>();
        }

        public StructureWorldDecoratorBuilder addStructure(StructureGenerator generator, float weight) {
            structuresAndWeights.add(Pair.of(generator, weight));
            return this;
        }

        public <T extends StructureGenerator> StructureWorldDecoratorBuilder addStructureList(List<T> generatorList, float totalWeight) {
            for (StructureGenerator generator : generatorList) {
                structuresAndWeights.add(Pair.of(generator, totalWeight / generatorList.size()));
            }
            return this;
        }

        public StructureWorldDecorator build() {
            return new StructureWorldDecorator(structuresAndWeights);
        }
    }
}
