package com.basti_bob.sand_wizard.world.world_generation.structures.static_structure;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.Structure;

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

        public StaticStructureGeneratorBuilder fromStringGrid(String[][] cells, int startX, int startY, HashMap<String, CellType> charToTypeMap) {

            for (int i = 0; i < cells[0].length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    CellType cellType = charToTypeMap.get(cells[cells.length - j - 1][i]);

                    if (cellType == null) continue;

                    addCell(cellType, i - startX, j - startY);
                }
            }

            return this;
        }

        public StaticStructureGeneratorBuilder fromIndexString(String str, int width, int height, int startX, int startY, CellType[] types, boolean flipHorizontal, boolean flipVertical) {
            for (int index = 0; index < str.length(); index++) {

                int colorIndex = Character.getNumericValue(str.charAt(index));
                CellType cellType = types[colorIndex];
                if (cellType == null) continue;

                int i = index % width;
                int j = height - index / width;

                if (flipHorizontal) i = width - i;
                if (flipVertical) j = height - j;

                addCell(cellType, i - startX, j - startY);

            }

            return this;
        }

        public StaticStructureGeneratorBuilder fromIndexString(String str, int width, int height, int startX, int startY, CellType[] types) {
            return fromIndexString(str, width, height, startX, startY, types, false, false);
        }

        public StaticStructureGeneratorBuilder fromRLEIndexString(String str, int width, int height, int startX, int startY, CellType[] types) {
            return fromIndexString(rleDecode(str), width, height, startX, startY, types, false, false);
        }

        private static String rleDecode(String input) {
            StringBuilder decodedString = new StringBuilder();
            String[] parts = input.split(",");

            for (int i = 0; i < parts.length; i += 2) {
                char character = parts[i].charAt(0);
                int count = Integer.parseInt(parts[i + 1]);

                decodedString.append(String.valueOf(character).repeat(Math.max(0, count)));
            }

            return decodedString.toString();
        }

        public StaticStructureGenerator build() {
            return new StaticStructureGenerator(positionTypeMap);
        }
    }

    interface CharToCellType {
        CellType get(char c);
    }
}
