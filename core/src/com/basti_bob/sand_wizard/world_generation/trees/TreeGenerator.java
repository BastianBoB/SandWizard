package com.basti_bob.sand_wizard.world_generation.trees;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.util.FloatPredicate;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world_generation.Point;
import com.basti_bob.sand_wizard.world_generation.Region;

import java.awt.geom.Line2D;
import java.util.*;


public class TreeGenerator {

    public static final TreeGenerator TREE_1 = new TreeGeneratorBuilder().rule("FF+[+F-F-F]-[-F+F+F]").iterations(1).leafSizeFunction((float normDistToCenter, boolean isOuterBranch) -> {
        if (isOuterBranch) return 9;
        return 4;
    }).build();

    public static final TreeGenerator TREE_2 = new TreeGeneratorBuilder().rule("FF+[+F-F-F]-[--F+F+F+F]").iterations(2).startLength(25f).build();
    public static final TreeGenerator TREE_3 = new TreeGeneratorBuilder().rule("FF[-FF][FFF][+FF]").startLength(10f).build();
    public static final TreeGenerator TREE_4 = new TreeGeneratorBuilder().rule("FF[-F][FF[-F][+F]]").build();
    public static final TreeGenerator TREE_5 = new TreeGeneratorBuilder().rule("F[-FF]F[+FF][F]").build();

    private final CellType branchCellType, leafCellType;
    private final String lSystem;
    private final int iterations;
    private final float startLength;
    private final float lengthMultiplier;
    private final float angleIncrement;
    private final BranchThicknessFunction branchThicknessFunction;

    private final FloatPredicate shouldAddLeaf;
    private final LeafSizeFunction leafSizeFunction;

    public TreeGenerator(CellType branchCellType, CellType leafCellType, String rule, int iterations, float startLength, float lengthMultiplier, float angleIncrement, BranchThicknessFunction branchThicknessFunction,
                         LeafSizeFunction leafSize, FloatPredicate shouldAddLeaf) {
        this.branchCellType = branchCellType;
        this.leafCellType = leafCellType;
        this.lSystem = LSystem.generateLSystem(rule, iterations);
        this.iterations = iterations;
        this.lengthMultiplier = lengthMultiplier;
        this.startLength = startLength;
        this.angleIncrement = angleIncrement;
        this.branchThicknessFunction = branchThicknessFunction;

        this.leafSizeFunction = leafSize;
        this.shouldAddLeaf = shouldAddLeaf;
    }

    public void placeTree(World world, float posX, float posY) {
        List<Branch> branches = generateBranches(posX, posY);
        List<Cell> cells = new ArrayList<>();

        Set<Point> allBranchPositions = new HashSet<>();
        Set<Point> allLeafPositions = new HashSet<>();

        for (Branch branch : branches) {
            allBranchPositions.addAll(pathBetweenPoints(branch.startX, branch.startY, branch.endX, branch.endY, branchThicknessFunction.getBranchThickness(branch.iteration)));
        }


        Region branchRegion = getRegionsFromPoints(new ArrayList<>(allBranchPositions));
        float maximumDistanceToCenter = branchRegion.getMaximumDistanceToCenter();

        for (Branch branch : branches) {
            if (!branch.hasLeaf) continue;

            float leafX = branch.endX;
            float leafY = branch.endY;

            float normalizedDist = branchRegion.getDistanceToCenter(leafX, leafY) / maximumDistanceToCenter;
            boolean isOuterBranch = isBranchOuterBranch(branch, branches, branchRegion.getCenter(), maximumDistanceToCenter);

            int leafSize = leafSizeFunction.getLeafSize(normalizedDist, isOuterBranch);

            if (!shouldAddLeaf.test(normalizedDist)) continue;

            for (Point leafPosition : generateLeaves(leafX, leafY, leafSize)) {
                boolean overLaps = allBranchPositions.contains(leafPosition);

                if (!overLaps)
                    allLeafPositions.add(leafPosition);
            }
        }

        for (Point point : allBranchPositions) {
            cells.add(branchCellType.createCell(world, point.x, point.y));
        }

        for (Point point : allLeafPositions) {
            cells.add(leafCellType.createCell(world, point.x, point.y));
        }

        for (Cell cell : cells) {
            try {
                world.setCell(cell);
            } catch (Exception e) {

            }
        }
    }

    private Region getRegionsFromPoints(List<Point> points) {
        Point first = points.get(0);

        int minX = first.x, minY = first.y, maxX = first.x, maxY = first.y;

        for (int i = 1; i < points.size(); i++) {
            Point point = points.get(i);

            int x = point.x;
            int y = point.y;

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        return new Region(minX, minY, maxX, maxY);
    }

    public List<Point> generateLeaves(float posX, float posY, int leafRadius) {
        List<Point> leaves = new ArrayList<>();

        for (int i = -leafRadius; i <= leafRadius; i++) {
            for (int j = -leafRadius; j <= leafRadius; j++) {

                if (i * i + j * j > leafRadius * leafRadius) continue;

                leaves.add(new Point((int) (posX + i), (int) (posY + j)));
            }
        }

        return leaves;
    }

    public List<Branch> generateBranches(float startX, float startY) {
        List<Branch> branches = new ArrayList<>();
        Stack<State> stateStack = new Stack<>();

        float length = (float) (startLength * Math.pow(lengthMultiplier, iterations));
        float x = startX;
        float y = startY;
        float angle = (float) (Math.PI / 2);

        for (char c : lSystem.toCharArray()) {
            switch (c) {
                case 'F' -> {
                    float newX = (float) (x + Math.cos(angle) * length);
                    float newY = (float) (y + Math.sin(angle) * length);

                    //hier weitermachen arschloch (wahrscheinlich jedes mal die Iterationen vom LSystem erstellen, sodass die iterationen mit übernommen werden)
                    branches.add(new Branch(x, y, newX, newY));
                    x = newX;
                    y = newY;
                }
                case '+' -> angle += angleIncrement;
                case '-' -> angle -= angleIncrement;
                case '[' -> stateStack.push(new State(x, y, angle));
                case ']' -> {
                    State state = stateStack.pop();
                    x = state.x;
                    y = state.y;
                    angle = state.angle;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            branches.get(i).hasLeaf = false;
        }

        return branches;
    }

    public List<Point> pathBetweenPoints(float x1, float y1, float x2, float y2, int thickness) {
        List<Point> points = new ArrayList<>();

        int xDistance = (int) Math.abs(x2 - x1);
        int yDistance = (int) Math.abs(y2 - y1);

        boolean positiveX = (x2 - x1) > 0;
        boolean positiveY = (y2 - y1) > 0;

        int steps = Math.max(xDistance, yDistance);

        if (xDistance > yDistance) {
            float slope = Math.abs((y2 - y1) / (x2 - x1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i : -i;
                float y = positiveY ? i * slope : -i * slope;

                for (int k = -thickness; k <= thickness; k++)
                    points.add(new Point((int) (x1 + x), (int) (y1 + y + thickness)));

            }
        } else {
            float slope = Math.abs((x2 - x1) / (y2 - y1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i * slope : -i * slope;
                float y = positiveY ? i : -i;

                for (int k = -thickness; k <= thickness; k++)
                    points.add(new Point((int) (x1 + x + thickness), (int) (y1 + y)));
            }
        }

        return points;
    }

    public boolean isBranchOuterBranch(Branch testBranch, List<Branch> branches, Point center, float maximumDistance) {

        Vector2 centerBranchVector = new Vector2(testBranch.endX - center.x, testBranch.endY - center.y);
        float centerBranchDist = centerBranchVector.len();

        Vector2 branchEdgeVector = centerBranchVector.nor().scl(maximumDistance - centerBranchDist);

        Line2D branchEdgeLine = new Line2D.Float(testBranch.endX, testBranch.endY, testBranch.endX + branchEdgeVector.x, testBranch.endY + branchEdgeVector.y);

        for (Branch branch : branches) {
            if (branch.equals(testBranch)) continue;

            Line2D branchLine = new Line2D.Float(branch.startX, branch.startY, branch.endX, branch.endY);

            if (branchLine.intersectsLine(branchEdgeLine)) return false;
        }

        return true;
    }


    private List<Branch> getOuterBranches(Point center, List<Branch> branches) {
        List<Branch> availableBranches = new ArrayList<>(branches);

        for (int i = 0; i < availableBranches.size(); i++) {
            Branch branch = availableBranches.get(i);

            float cblEndX = branch.endX > 0 ? branch.endX - 1 : branch.endX + 1;
            float cblEndY = branch.endY > 0 ? branch.endY - 1 : branch.endY + 1;

            Line2D centerBranchLine = new Line2D.Float(center.x, center.y, cblEndX, cblEndY);

            for (int k = 0; k < availableBranches.size(); k++) {
                Branch checkBranch = availableBranches.get(k);

                if (checkBranch.equals(branch)) continue;

                Line2D checkBranchLine = new Line2D.Float(checkBranch.startX, checkBranch.startY, checkBranch.endX, checkBranch.endY);

                if (centerBranchLine.intersectsLine(checkBranchLine))
                    availableBranches.remove(k--);

            }
        }

        return availableBranches;
    }

    private static class Branch {
        public final float startX, startY, endX, endY;
        public final int iteration;
        public boolean hasLeaf = true;

        public Branch(float startX, float startY, float endX, float endY, int iteration) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.iteration = iteration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Branch branch = (Branch) o;
            return Float.compare(branch.startX, startX) == 0 && Float.compare(branch.startY, startY) == 0 && Float.compare(branch.endX, endX) == 0 && Float.compare(branch.endY, endY) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(startX, startY, endX, endY);
        }
    }

    private static class State {
        float x, y, angle;

        public State(float x, float y, float angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }

    private static class TreeGeneratorBuilder {

        private String rule = "FF+[+F-F-F]-[-F+F+F]";
        private CellType branchCellType = CellType.WOOD;
        private CellType leafCellType = CellType.SPRING_LEAF;
        private int iterations = 1;
        private float lengthMultiplier = 0.5f;
        private float startLength = 20f;
        private float angleIncrement = 0.5f;
        private BranchThicknessFunction branchThicknessFunction = i -> 5 - i;

        private LeafSizeFunction leafSizeFunction = (v, b) -> 5;
        private FloatPredicate shouldAddLeave = v -> true;

        private TreeGeneratorBuilder() {
        }

        private TreeGeneratorBuilder rule(String rule) {
            this.rule = rule;
            return this;
        }

        private TreeGeneratorBuilder branchCellType(CellType branchCellType) {
            this.branchCellType = branchCellType;
            return this;
        }

        private TreeGeneratorBuilder leafCellType(CellType leafCellType) {
            this.leafCellType = leafCellType;
            return this;
        }

        private TreeGeneratorBuilder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        private TreeGeneratorBuilder startLength(float startLength) {
            this.startLength = startLength;
            return this;
        }

        private TreeGeneratorBuilder lengthMultiplier(float lengthMultiplier) {
            this.lengthMultiplier = lengthMultiplier;
            return this;
        }

        private TreeGeneratorBuilder angleIncrement(float angleIncrement) {
            this.angleIncrement = angleIncrement;
            return this;
        }

        private TreeGeneratorBuilder branchThicknessFunction(float branchThicknessFunction) {
            this.branchThicknessFunction = branchThicknessFunction;
            return this;
        }

        private TreeGeneratorBuilder leafSizeFunction(LeafSizeFunction leafSizeFunction) {
            this.leafSizeFunction = leafSizeFunction;
            return this;
        }

        private TreeGeneratorBuilder shouldAddLeave(FloatPredicate shouldAddLeave) {
            this.shouldAddLeave = shouldAddLeave;
            return this;
        }

        private TreeGenerator build() {
            return new TreeGenerator(branchCellType, leafCellType, rule, iterations, startLength, lengthMultiplier, angleIncrement, branchThicknessFunction, leafSizeFunction, shouldAddLeave);
        }
    }

}
