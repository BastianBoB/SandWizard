package com.basti_bob.sand_wizard.world.world_generation.structures.mineshafts;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.range.IntRange;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.PlacePriority;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;
import jdk.jshell.execution.Util;

public class MineShaftGenerator extends StructureGenerator {

    private static final IntRange widthRange = new IntRange(20, 320);

    @Override
    public Structure generate(World world, int startX, int startY) {
        int width = widthRange.getRandom(world.random);

        Structure.Builder structureBuilder = Structure.builder();

        for(int i = -width/2; i < width/2; i++) {
            structureBuilder.addCell(new ToPlaceStructureCell(CellType.SOLID.WOOD.createCell(), PlacePriority.BUILDINGS), startX+i, startY);

            for(int j = 1; j < 20; j++) {
                structureBuilder.addCell(new ToPlaceStructureCell(CellType.EMPTY.createCell(), PlacePriority.BUILDINGS), startX+i, startY+j);
            }
        }

        return structureBuilder.build();
    }
}
