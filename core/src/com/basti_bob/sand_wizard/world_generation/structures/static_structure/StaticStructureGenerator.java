package com.basti_bob.sand_wizard.world_generation.structures.static_structure;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world_generation.structures.StructureGenerator;

import java.util.HashMap;

public class StaticStructureGenerator extends StructureGenerator {


    private final HashMap<CellPos, CellType> positionTypeMap;

    public StaticStructureGenerator(HashMap<CellPos, CellType> positionTypeMap) {
        this.positionTypeMap = positionTypeMap;
    }

    @Override
    public Structure generate(World world, int startX, int startY) {
        Structure.Builder structureBuilder = Structure.builder();

        positionTypeMap.forEach((pos, type) -> structureBuilder.addCell(type.createCell(), startX + pos.x, startY + pos.y));

        return structureBuilder.build();
    }

    public static StaticStructureGeneratorBuilder builder() {
        return new StaticStructureGeneratorBuilder();
    }

    public static class StaticStructureGeneratorBuilder {

        private final HashMap<CellPos, CellType> positionTypeMap;

        public StaticStructureGeneratorBuilder() {
            this.positionTypeMap = new HashMap<>();
        }

        public StaticStructureGeneratorBuilder addCell(CellType cellType, int x, int y) {
            positionTypeMap.put(new CellPos(x, y), cellType);

            return this;
        }

        public StaticStructureGeneratorBuilder fromStringArray(String[][] cells, int startX, int startY, HashMap<String, CellType> charToTypeMap) {

            for(int i = 0; i < cells[0].length; i++) {
                for(int j = 0; j < cells.length; j++) {
                    CellType cellType = charToTypeMap.get(cells[cells.length - j - 1][i]);

                    if(cellType == null) continue;

                    addCell(cellType, i - startX, j - startY);
                }
            }

            return this;
        }

        public StaticStructureGenerator build() {
            return new StaticStructureGenerator(positionTypeMap);
        }
    }

    interface CharToCellType {
        CellType get(char c);
    }
}
