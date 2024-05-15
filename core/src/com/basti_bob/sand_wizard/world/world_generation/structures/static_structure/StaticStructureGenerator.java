package com.basti_bob.sand_wizard.world.world_generation.structures.static_structure;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.Region;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.PlacePriority;
import com.basti_bob.sand_wizard.world.world_generation.structures.structure_placing.ToPlaceStructureCell;

import java.util.HashMap;
import java.util.function.Supplier;

public class StaticStructureGenerator extends StructureGenerator {

    private final HashMap<CellPos, Supplier<ToPlaceStructureCell>> positionCellSupplierMap;
    private final int width, height;

    public StaticStructureGenerator(HashMap<CellPos, Supplier<ToPlaceStructureCell>> positionCellSupplierMap) {
        this.positionCellSupplierMap = positionCellSupplierMap;

        Region cellRegion = getCellRegion(positionCellSupplierMap);

        this.width = cellRegion.getWidth();
        this.height = cellRegion.getHeight();
    }
    public Region getCellRegion(HashMap<CellPos, Supplier<ToPlaceStructureCell>> positionCellSupplierMap) {

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (CellPos cellPos : positionCellSupplierMap.keySet()) {
            if (cellPos.x < minX) minX = cellPos.x;
            if (cellPos.x > maxX) maxX = cellPos.x;
            if (cellPos.y < minY) minY = cellPos.y;
            if (cellPos.y > maxY) maxY = cellPos.y;
        }

        return new Region(minX, minY, maxX, maxY);
    }

    @Override
    public Structure generate(World world, int startX, int startY) {
        Structure.Builder structureBuilder = Structure.builder();

        positionCellSupplierMap.forEach((pos, cellSupplier) -> structureBuilder.addCell(cellSupplier.get(), startX + pos.x, startY + pos.y));

        return structureBuilder.build();
    }

    public static StaticStructureGeneratorBuilder builder() {
        return new StaticStructureGeneratorBuilder();
    }

    public static class StaticStructureGeneratorBuilder {

        private final HashMap<CellPos, Supplier<ToPlaceStructureCell>> positionTypeMap;

        public StaticStructureGeneratorBuilder() {
            this.positionTypeMap = new HashMap<>();
        }

        public StaticStructureGeneratorBuilder addCell(Supplier<ToPlaceStructureCell> toPlaceStructureCellSupplier, int x, int y) {
            positionTypeMap.put(new CellPos(x, y), toPlaceStructureCellSupplier);
            return this;
        }

        public StaticStructureGeneratorBuilder fromStringGrid(String[][] cells, int startX, int startY, HashMap<String, Supplier<ToPlaceStructureCell>> charToCellSupplierMap) {

            for (int i = 0; i < cells[0].length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    Supplier<ToPlaceStructureCell> toPlaceStructureCellSupplier = charToCellSupplierMap.get(cells[cells.length - j - 1][i]);

                    if (toPlaceStructureCellSupplier == null) continue;

                    addCell(toPlaceStructureCellSupplier, i - startX, j - startY);
                }
            }

            return this;
        }

        public StaticStructureGeneratorBuilder fromIndexString(String str, int width, int height, int startX, int startY, Supplier<ToPlaceStructureCell>[] cellSuppliers, boolean flipHorizontal, boolean flipVertical) {
            for (int index = 0; index < str.length(); index++) {

                int colorIndex = Character.getNumericValue(str.charAt(index));
                Supplier<ToPlaceStructureCell> toPlaceStructureCellSupplier = cellSuppliers[colorIndex];
                if (toPlaceStructureCellSupplier == null) continue;

                int i = index % width;
                int j = height - index / width;

                if (flipHorizontal) i = width - i;
                if (flipVertical) j = height - j;

                addCell(toPlaceStructureCellSupplier, i - startX, j - startY);

            }

            return this;
        }

        public StaticStructureGeneratorBuilder fromIndexString(String str, int width, int height, int startX, int startY, Supplier<ToPlaceStructureCell>[] cellSuppliers) {
            return fromIndexString(str, width, height, startX, startY, cellSuppliers, false, false);
        }

        public StaticStructureGeneratorBuilder fromRLEIndexString(String str, int width, int height, int startX, int startY, Supplier<ToPlaceStructureCell>[] cellSuppliers) {
            return fromIndexString(rleDecode(str), width, height, startX, startY, cellSuppliers, false, false);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
