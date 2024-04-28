package com.basti_bob.sand_wizard.world_generation.structures;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.structures.ponds.PondGenerator;
import com.basti_bob.sand_wizard.world_generation.structures.static_structure.StaticStructureGenerator;
import com.basti_bob.sand_wizard.world_generation.structures.trees.TreeGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class StructureGenerator {


    public static class PONDS {

        public static final PondGenerator SMALL_ACID = PondGenerator.builder(CellType.ACID, 5, 20, 3, 10).build();
        public static final PondGenerator SMALL_LAVA = PondGenerator.builder(CellType.LAVA, 5, 20, 3, 10).build();
        public static final PondGenerator SMALL_WATER = PondGenerator.builder(CellType.WATER, 5, 20, 3, 10).build();


        public static final PondGenerator WATER = PondGenerator.builder(CellType.WATER, 50, 200, 30, 50).build();

    }

    public static class TREES {

        public static final TreeGenerator TREE_1 = TreeGenerator.builder().rule("FF+[+F-F-F]-[-F+F+F]").iterations(2)
                .leafSizeFunction((float normDistToCenter, boolean isOuterBranch) -> {
                    if (isOuterBranch) return 3;
                    return 2;
                }).branchThicknessFunction(i -> Math.max(1, 3 - i)).build();

        public static final TreeGenerator TREE_2 = TreeGenerator.builder().rule("FF+[+F-F-F]-[--F+F+F+F]").iterations(3).startLength(40f).angleIncrement((float) (Math.PI / 7f))
                .leafSizeFunction((float normDistToCenter, boolean isOuterBranch) -> {
                    if (isOuterBranch) return 5;
                    return 2;
                }).branchThicknessFunction(i -> switch (i) {
                    case 0 -> 7;
                    case 1, 2 -> 2;
                    default -> 1;
                }).build();

        public static final TreeGenerator TREE_3 = TreeGenerator.builder().rule("FF[-FF][FFF][+FF]").iterations(2).startLength(30f)
                .leafSizeFunction((float normDistToCenter, boolean isOuterBranch) -> {
                    if (isOuterBranch) return 3;
                    return 2;
                }).build();

        public static final TreeGenerator TREE_4 = TreeGenerator.builder().rule("F[-F][FF[-F][+F]]").iterations(2).startLength(18f).angleIncrement((float) (Math.PI / 6f))
                .leafSizeFunction((float normDistToCenter, boolean isOuterBranch) -> isOuterBranch ? 2 : 1)
                .branchThicknessFunction(i -> 1).build();

        public static final TreeGenerator TREE_5 = TreeGenerator.builder().rule("F[-FF]F[+FF][F]").iterations(3).startLength(60f)
                .leafSizeFunction((float normDistToCenter, boolean isOuterBranch) -> {
                    if (isOuterBranch) return 4;
                    return 2;
                }).branchThicknessFunction(i -> switch (i) {
                    case 0 -> 5;
                    case 1, 2 -> 2;
                    default -> 1;
                }).build();

        public static final List<StructureGenerator> ALL = Arrays.asList(TREE_1, TREE_2, TREE_3, TREE_4, TREE_5);
    }

    public static class FLOWERS {
        public static class TULIPS {

            private static final String[][] TULIP_PATTERN = new String[][]{
                    {".", ".", "l", "l", ".", ".", "."},
                    {".", "l", "l", "l", "l", ".", "."},
                    {".", "l", "l", "l", "l", ".", "."},
                    {".", ".", "l", "l", ".", ".", "."},
                    {".", ".", ".", "g", ".", ".", "."},
                    {"g", ".", ".", "g", ".", ".", "g"},
                    {"g", "g", ".", "g", "g", "g", "g"},
                    {"g", "g", ".", "g", "g", "g", "."},
                    {"g", "g", "g", "g", "g", "g", "."},
                    {".", "g", "g", "g", "g", ".", "."},
                    {".", ".", "g", "g", ".", ".", "."},
            };

            private static StaticStructureGenerator TULIP_FROM_TYPE(CellType type) {
                return StaticStructureGenerator.builder().fromStringArray(TULIP_PATTERN, 3, 0, new HashMap<>() {{
                            put(".", null);
                            put("g", CellType.FLOWER_PETAL.GREEN);
                            put("l", type);
                        }}
                ).build();
            }

            public static final StaticStructureGenerator RED = TULIP_FROM_TYPE(CellType.FLOWER_PETAL.RED);
            public static final StaticStructureGenerator PURPLE = TULIP_FROM_TYPE(CellType.FLOWER_PETAL.PURPLE);
            public static final StaticStructureGenerator PINK = TULIP_FROM_TYPE(CellType.FLOWER_PETAL.PINK);
            public static final StaticStructureGenerator YELLOW = TULIP_FROM_TYPE(CellType.FLOWER_PETAL.YELLOW);
            public static final StaticStructureGenerator ORANGE = TULIP_FROM_TYPE(CellType.FLOWER_PETAL.ORANGE);
            public static final StaticStructureGenerator WHITE = TULIP_FROM_TYPE(CellType.FLOWER_PETAL.WHITE);

            public static final List<StaticStructureGenerator> ALL = Arrays.asList(RED, PURPLE, PINK, YELLOW, ORANGE, WHITE);
        }

        public static class ROSES {

            private static final String[][] ROSE_PATTERN = new String[][]{
                    {".", ".", ".", "r", "r", "r", "r", ".", ".", "."},
                    {".", ".", "r", "r", "r", "r", "r", "r", ".", "."},
                    {".", ".", "r", "r", "r", "r", "r", "r", ".", "."},
                    {".", ".", "r", "r", "r", "r", "r", ".", ".", "."},
                    {".", "g", "g", "r", "r", "r", "r", ".", ".", "."},
                    {"g", "g", ".", "g", "r", "r", "g", "g", ".", "."},
                    {".", ".", ".", ".", "g", "g", "g", "g", "g", "."},
                    {".", ".", ".", ".", "g", ".", ".", ".", "g", "g"},
                    {".", ".", ".", "g", "g", ".", ".", ".", ".", "."},
                    {".", ".", ".", "g", ".", ".", ".", ".", ".", "."},
                    {".", ".", ".", "g", ".", ".", ".", ".", ".", "."},
                    {".", ".", ".", "g", "g", ".", ".", ".", ".", "."},
                    {".", ".", ".", ".", "g", ".", ".", ".", ".", "."},
                    {".", ".", ".", ".", "g", ".", ".", ".", ".", "."},
            };

            private static StaticStructureGenerator ROSE_FROM_TYPE(CellType type) {
                return StaticStructureGenerator.builder().fromStringArray(ROSE_PATTERN, 4, 0, new HashMap<>() {{
                            put(".", null);
                            put("g", CellType.FLOWER_PETAL.GREEN);
                            put("r", type);
                        }}
                ).build();
            }

            public static final StaticStructureGenerator WHITE = ROSE_FROM_TYPE(CellType.FLOWER_PETAL.WHITE);
            public static final StaticStructureGenerator YELLOW = ROSE_FROM_TYPE(CellType.FLOWER_PETAL.YELLOW);
            public static final StaticStructureGenerator PINK = ROSE_FROM_TYPE(CellType.FLOWER_PETAL.PINK);
            public static final StaticStructureGenerator ORANGE = ROSE_FROM_TYPE(CellType.FLOWER_PETAL.ORANGE);
            public static final StaticStructureGenerator RED = ROSE_FROM_TYPE(CellType.FLOWER_PETAL.RED);
            public static final StaticStructureGenerator LAVENDER = ROSE_FROM_TYPE(CellType.FLOWER_PETAL.LAVENDER);
            public static final StaticStructureGenerator PURPLE = ROSE_FROM_TYPE(CellType.FLOWER_PETAL.PURPLE);

            public static final List<StaticStructureGenerator> ALL = Arrays.asList(WHITE, YELLOW, PINK, ORANGE, RED, LAVENDER, PURPLE);
        }

        public static class BERRY_BUSHES {
            private static final String[][] BERRY_BUSH_PATTERN = new String[][]{
                    {".", "g", ".", ".", ".", ".", ".", ".", ".", ".", ".", ".", ".", "."},
                    {".", ".", "g", ".", ".", ".", ".", "g", ".", ".", ".", ".", ".", "."},
                    {".", ".", "g", "f", "f", ".", "g", "g", ".", ".", ".", ".", ".", "."},
                    {".", "f", "f", "f", "f", ".", "g", ".", ".", "f", "f", ".", ".", "."},
                    {".", "f", "f", "y", "f", "f", ".", "f", "f", "f", "f", ".", ".", "."},
                    {"g", "g", "f", "f", "f", "f", ".", "f", "f", "y", "f", "f", ".", "."},
                    {".", ".", "f", "f", ".", ".", "g", ".", "f", "f", "f", "f", ".", "."},
                    {".", ".", ".", ".", "f", "f", ".", "g", "f", "f", ".", ".", ".", "."},
                    {".", ".", "g", "g", "f", "f", "f", "f", ".", "g", ".", ".", "g", "g"},
                    {".", "g", ".", "f", "f", "y", "f", "f", ".", "g", ".", "g", "g", "."},
                    {".", ".", ".", "f", "f", "f", "f", "g", ".", ".", "g", "g", ".", "."},
                    {".", ".", ".", ".", ".", "f", "f", ".", "g", ".", "g", ".", ".", "."},
                    {".", ".", ".", ".", ".", ".", ".", ".", ".", "g", ".", ".", ".", "."},
                    {".", ".", ".", ".", ".", ".", ".", ".", ".", "g", ".", ".", ".", "."},
            };

            private static StaticStructureGenerator BERRY_BUSH_FROM_TYPE(CellType type1, CellType type2) {
                return StaticStructureGenerator.builder().fromStringArray(BERRY_BUSH_PATTERN, 4, 0, new HashMap<>() {{
                            put(".", null);
                            put("g", CellType.FLOWER_PETAL.GREEN);
                            put("y", type2);
                            put("f", type1);
                        }}
                ).build();
            }

            public static final StaticStructureGenerator RED = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.RED, CellType.FLOWER_PETAL.YELLOW);
            public static final StaticStructureGenerator YELLOW = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.YELLOW, CellType.FLOWER_PETAL.YELLOW);
            public static final StaticStructureGenerator BLUE = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.BLUE, CellType.FLOWER_PETAL.YELLOW);
            public static final StaticStructureGenerator PURPLE = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.PURPLE, CellType.FLOWER_PETAL.YELLOW);

            public static final StaticStructureGenerator RED_GLOW = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.RED, CellType.FLOWER_PETAL.YELLOW_GLOW_RED);
            public static final StaticStructureGenerator YELLOW_GLOW = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.YELLOW, CellType.FLOWER_PETAL.YELLOW_GLOW_YELLOW);
            public static final StaticStructureGenerator BLUE_GLOW = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.BLUE, CellType.FLOWER_PETAL.YELLOW_GLOW_BLUE);
            public static final StaticStructureGenerator PURPLE_GLOW = BERRY_BUSH_FROM_TYPE(CellType.FLOWER_PETAL.PURPLE, CellType.FLOWER_PETAL.YELLOW_GLOW_PURPLE);

            public static final List<StructureGenerator> ALL_NORMAL = Arrays.asList(RED, YELLOW, BLUE, PURPLE);
            public static final List<StructureGenerator> ALL_GLOW = Arrays.asList(RED_GLOW, YELLOW_GLOW, BLUE_GLOW, PURPLE_GLOW);

        }

        public static final StaticStructureGenerator LAVENDER = StaticStructureGenerator.builder().fromStringArray(
                new String[][]{
                        {".", ".", ".", "l", "l", ".", "."},
                        {".", "l", "l", "l", "l", "l", "l"},
                        {".", ".", "l", "l", "l", "l", "."},
                        {".", "l", "l", "l", "l", "l", "."},
                        {".", "l", "l", "l", "l", "l", "."},
                        {".", ".", "l", "l", "l", "l", "."},
                        {".", ".", "l", "l", "l", ".", "."},
                        {"g", ".", ".", "l", ".", ".", "."},
                        {"g", ".", ".", "g", ".", ".", "g"},
                        {"g", "g", ".", "g", ".", "g", "g"},
                        {".", "g", "g", "g", "g", "g", "g"},
                        {".", "g", "g", "g", "g", "g", "."},
                        {".", ".", "g", "g", "g", ".", "."},
                },
                3, 0, new HashMap<>() {{
                    put(".", null);
                    put("g", CellType.FLOWER_PETAL.GREEN);
                    put("l", CellType.FLOWER_PETAL.LAVENDER);
                }}
        ).build();

        public static final StaticStructureGenerator SUNFLOWER = StaticStructureGenerator.builder().fromStringArray(
                new String[][]{
                        {".", ".", "b", "b", ".", "b", "b", ".", "."},
                        {".", "b", "y", "y", "b", "y", "y", "b", "."},
                        {"b", "y", "y", "y", "y", "y", "y", "y", "b"},
                        {"b", "y", "y", "b", "b", "b", "y", "y", "b"},
                        {".", "b", "y", "b", "b", "b", "y", "b", "."},
                        {"b", "y", "y", "b", "b", "b", "y", "y", "b"},
                        {"b", "y", "y", "y", "y", "y", "y", "y", "b"},
                        {".", "b", "y", "y", "b", "y", "y", "b", "."},
                        {".", ".", "b", "b", "g", "b", "b", ".", "."},
                        {".", ".", ".", ".", "g", ".", ".", ".", "."},
                        {".", "g", "g", ".", "g", ".", ".", "g", "."},
                        {"g", ".", "g", "g", "g", ".", "g", "g", "."},
                        {".", ".", ".", "g", "g", "g", "g", ".", "."},
                        {".", ".", ".", ".", "g", "g", ".", ".", "."},
                        {".", ".", ".", ".", "g", ".", ".", ".", "."},
                        {".", ".", ".", ".", "g", ".", ".", ".", "."},
                },
                3, 0, new HashMap<>() {{
                    put(".", null);
                    put("g", CellType.FLOWER_PETAL.GREEN);
                    put("b", CellType.FLOWER_PETAL.BROWN);
                    put("y", CellType.FLOWER_PETAL.YELLOW);
                }}
        ).build();

        public static final List<StructureGenerator> ALL = new ArrayList<>();

        static {
            ALL.addAll(TULIPS.ALL);
            ALL.addAll(ROSES.ALL);
            ALL.addAll(BERRY_BUSHES.ALL_NORMAL);
            ALL.add(LAVENDER);
            ALL.add(SUNFLOWER);

        }
    }

    public abstract Structure generate(World world, int startX, int startY);
}
