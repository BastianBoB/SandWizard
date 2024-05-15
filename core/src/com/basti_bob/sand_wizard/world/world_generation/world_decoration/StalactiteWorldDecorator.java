package com.basti_bob.sand_wizard.world.world_generation.world_decoration;

import com.basti_bob.sand_wizard.util.Direction;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.WorldGeneration;
import com.basti_bob.sand_wizard.world.world_generation.cave_generation.CaveGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.static_structure.StaticStructureGenerator;

import java.util.ArrayList;
import java.util.List;

public class StalactiteWorldDecorator extends WorldDecorator {

    private final List<StaticStructureGenerator> generatorList = new ArrayList<>();
    private final int maxStalactiteHeight;

    public StalactiteWorldDecorator() {
        generatorList.addAll(StructureGenerator.STALACTITES.REGISTRY.getAllEntries());

        int maxStalactiteHeight = 0;
        for (StaticStructureGenerator generator : generatorList) {
            if (generator.getHeight() > maxStalactiteHeight) maxStalactiteHeight = generator.getHeight();
        }

        this.maxStalactiteHeight = maxStalactiteHeight;
    }

    @Override
    public void decorate(World world, int cellX, int cellY) {
        if(world.random.nextFloat() < 0.98) return;

        int verticalCaveSpace = getCaveSpace(world, cellX, cellY, Direction.DOWN, maxStalactiteHeight);

        List<StaticStructureGenerator> possibleGenerators = new ArrayList<>();

        for (StaticStructureGenerator generator : generatorList) {
            if (generator.getHeight() <= verticalCaveSpace) possibleGenerators.add(generator);
        }

        if(possibleGenerators.size() == 0) return;

        StaticStructureGenerator generator = possibleGenerators.get(world.random.nextInt(possibleGenerators.size()));

        int testHeight = (int) (generator.getHeight() / 2f);

        int leftTestCellPosX = cellX - (int) (generator.getWidth() / 2f);
        int rightTestCellPosX = cellX + (int) (generator.getWidth() / 2f);

        int spaceLeft = getCaveSpace(world, leftTestCellPosX, cellY, Direction.UP, testHeight);
        int spaceRight = getCaveSpace(world, rightTestCellPosX, cellY, Direction.UP, testHeight);

        int maxSpace = Math.max(spaceLeft, spaceRight);
        if(maxSpace >= testHeight) return;

        world.addStructureToPlaceAsync(() -> generator.generate(world, cellX, cellY + maxSpace));
    }

    private int getCaveSpace(World world, int cellX, int cellY, Direction direction, int distance) {
        int xOff = direction.getXOff();
        int yOff = direction.getYOff();

        for (int i = 0; i < distance; i++) {
            if (!world.worldGeneration.isCave(cellX + xOff * i, cellY + yOff * i)) return i;
        }
        return distance;
    }
}
