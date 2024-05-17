package com.basti_bob.sand_wizard.world.world_generation.world_decoration.speleothems;

import com.basti_bob.sand_wizard.util.Direction;
import com.basti_bob.sand_wizard.util.RandomObjectWithWeights;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.static_structure.StaticStructureGenerator;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.StructureWorldDecorator;
import com.basti_bob.sand_wizard.world.world_generation.world_decoration.WorldDecorator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class SpeleothemWorldDecorator extends WorldDecorator {

    private final List<Pair<StaticStructureGenerator, Float>> generatorProbabiltyList;
    private final int maxSpeleothemHeight;
    private final float totalProbability;
    private final Direction direction;

    public SpeleothemWorldDecorator(SpeleothemWorldDecoratorBuilder builder) {
        this.generatorProbabiltyList = builder.structuresAndWeights;
        this.direction = builder.direction;

        int maxSpeleothemHeight = 0;
        float totalProbability = 0;

        for (Pair<StaticStructureGenerator, Float> pair : generatorProbabiltyList) {
            StaticStructureGenerator generator = pair.getLeft();
            int height = generator.getHeight();
            if (height > maxSpeleothemHeight) maxSpeleothemHeight = height;

            totalProbability += pair.getRight();
        }

        this.maxSpeleothemHeight = maxSpeleothemHeight;
        this.totalProbability = totalProbability;
    }

    @Override
    public void decorate(World world, int cellX, int cellY) {
        if(world.random.nextFloat() > totalProbability) return;

        int directionCaveSpace = getCaveSpace(world, cellX, cellY, direction, maxSpeleothemHeight);

        List<Pair<StaticStructureGenerator, Float>> possibleGenerators = new ArrayList<>();

        for (Pair<StaticStructureGenerator, Float> pair : generatorProbabiltyList) {
            StaticStructureGenerator generator = pair.getLeft();
            if (generator.getHeight() <= directionCaveSpace) possibleGenerators.add(pair);
        }

        if (possibleGenerators.size() == 0) return;

        StaticStructureGenerator generator = RandomObjectWithWeights.getObject(possibleGenerators, world.random);

        if(generator == null) return;

        int testHeight = generator.getHeight();
        int widthOffset = (int) (generator.getWidth() / 2f);

        int leftTestCellPosX = cellX - direction.getYOff() * widthOffset;
        int leftTestCellPosY = cellY - direction.getXOff() * widthOffset;

        int rightTestCellPosX = cellX + direction.getYOff() * widthOffset;
        int rightTestCellPosY = cellY + direction.getXOff() * widthOffset;


        Direction oppositeDirection = direction.getOpposite();

        int spaceLeft = getCaveSpace(world, leftTestCellPosX, leftTestCellPosY, oppositeDirection, testHeight);
        int spaceRight = getCaveSpace(world, rightTestCellPosX, rightTestCellPosY, oppositeDirection, testHeight);

        int maxSpace = Math.max(spaceLeft, spaceRight);
        if (maxSpace >= testHeight) return;

        world.addStructureToPlaceAsync(() -> generator.generate(world, cellX - direction.getXOff() * maxSpace, cellY - direction.getYOff() * maxSpace));
    }

    private int getCaveSpace(World world, int cellX, int cellY, Direction direction, int distance) {
        int xOff = direction.getXOff();
        int yOff = direction.getYOff();

        for (int i = 0; i < distance; i++) {
            if (!world.worldGeneration.isCave(cellX + xOff * i, cellY + yOff * i)) return i;
        }
        return distance;
    }

    public static SpeleothemWorldDecoratorBuilder builder() {
        return new SpeleothemWorldDecoratorBuilder();
    }

    public static class SpeleothemWorldDecoratorBuilder {

        private final List<Pair<StaticStructureGenerator, Float>> structuresAndWeights;
        private Direction direction;

        private SpeleothemWorldDecoratorBuilder() {
            this.structuresAndWeights = new ArrayList<>();
        }

        public SpeleothemWorldDecoratorBuilder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public SpeleothemWorldDecoratorBuilder addStructure(StaticStructureGenerator generator, float weight) {
            structuresAndWeights.add(Pair.of(generator, weight));
            return this;
        }

        public <T extends StaticStructureGenerator> SpeleothemWorldDecoratorBuilder addStructureList(List<T> generatorList, float totalWeight) {
            for (StaticStructureGenerator generator : generatorList) {
                structuresAndWeights.add(Pair.of(generator, totalWeight / generatorList.size()));
            }
            return this;
        }

        public SpeleothemWorldDecorator build() {
            return new SpeleothemWorldDecorator(this);
        }
    }
}