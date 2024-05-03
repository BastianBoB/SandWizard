package com.basti_bob.sand_wizard.world_generation.structures.trees;

import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world_generation.structures.Structure;
import com.basti_bob.sand_wizard.world_generation.structures.StructureGenerator;
import com.basti_bob.sand_wizard.world_generation.util.Region;

import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;


public class TreeGenerator extends StructureGenerator {

    private final CellType branchCellType, leafCellType;
    private final String rule;
    private final String finalLSystem;
    private final int iterations;
    private final float startLength;

    private final float lengthMultiplier;
    private final float angleIncrement;
    private final BranchThicknessFunction branchThicknessFunction;

    private final ShouldAddLeaf shouldAddLeaf;
    private final LeafSizeFunction leafSizeFunction;

    public TreeGenerator(CellType branchCellType, CellType leafCellType, String rule, int iterations, float startLength, float lengthMultiplier, float angleIncrement, BranchThicknessFunction branchThicknessFunction,
                         LeafSizeFunction leafSize, ShouldAddLeaf shouldAddLeaf) {
        this.branchCellType = branchCellType;
        this.leafCellType = leafCellType;
        this.rule = rule;
        this.finalLSystem = LSystem.generateLSystem(rule, iterations);
        this.iterations = iterations;
        this.lengthMultiplier = lengthMultiplier;
        this.startLength = startLength;
        this.angleIncrement = angleIncrement;
        this.branchThicknessFunction = branchThicknessFunction;

        this.leafSizeFunction = leafSize;
        this.shouldAddLeaf = shouldAddLeaf;
    }

    @Override
    public Structure generate(World world, int startX, int startY) {

        Structure.Builder structureBuilder = Structure.builder();

        List<Branch> branches = generateBranches();

        Set<CellPos> allBranchPositions = new HashSet<>();
        Set<CellPos> allLeafPositions = new HashSet<>();

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
            boolean isOuterBranch = isBranchOuterBranch(branch, branches, branchRegion.centerX, branchRegion.centerY, maximumDistanceToCenter);

            int leafSize = leafSizeFunction.getLeafSize(normalizedDist, isOuterBranch);

            if (!shouldAddLeaf.shouldAffLeaf(normalizedDist)) continue;

            for (CellPos leafPosition : generateLeaves(leafX, leafY, leafSize)) {
                boolean overLaps = allBranchPositions.contains(leafPosition);
//
                if (!overLaps)
                    structureBuilder.addCell(leafCellType, leafPosition.x + startX, leafPosition.y + startY);
            }
        }

        for (CellPos point : allBranchPositions) {

            structureBuilder.addCell(branchCellType, point.x + startX, point.y + startY);
        }

        return structureBuilder.build();
    }

    public List<CellPos> generateLeaves(float posX, float posY, int leafRadius) {
        List<CellPos> leaves = new ArrayList<>();

        for (int i = -leafRadius; i <= leafRadius; i++) {
            for (int j = -leafRadius; j <= leafRadius; j++) {

                if (i * i + j * j > leafRadius * leafRadius) continue;

                leaves.add(new CellPos((int) (posX + i), (int) (posY + j)));
            }
        }

        return leaves;
    }

    public List<Branch> generateBranches() {

        List<Branch> branches = new ArrayList<>();
        Stack<State> stateStack = new Stack<>();

        float length = (float) (startLength * Math.pow(lengthMultiplier, iterations));

        float x = 0;
        float y = 0;
        float angle = (float) (Math.PI / 2);
        int numBranching = 0;

        for (char c : finalLSystem.toCharArray()) {
            switch (c) {
                case 'F' -> {
                    float newX = (float) (x + Math.cos(angle) * length);
                    float newY = (float) (y + Math.sin(angle) * length);
                    branches.add(new Branch((int) x, (int) y, (int) newX, (int) newY, numBranching));
                    x = newX;
                    y = newY;
                }
                case '+' -> angle += angleIncrement;
                case '-' -> angle -= angleIncrement;
                case '[' -> {
                    stateStack.push(new State(x, y, angle));
                    numBranching++;
                }
                case ']' -> {
                    State state = stateStack.pop();
                    x = state.x;
                    y = state.y;
                    angle = state.angle;
                    numBranching--;
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            branches.get(i).hasLeaf = false;
        }

        return branches;
    }

    public List<CellPos> pathBetweenPoints(float x1, float y1, float x2, float y2, int thickness) {
        List<CellPos> points = new ArrayList<>();

        int xDistance = (int) Math.abs(x2 - x1);
        int yDistance = (int) Math.abs(y2 - y1);

        boolean positiveX = (x2 - x1) > 0;
        boolean positiveY = (y2 - y1) > 0;

        int steps = Math.max(xDistance, yDistance);

        int thickOffMin = -thickness / 2;
        int thickOffMax = (int) Math.ceil(thickness / 2f);

        if (xDistance > yDistance) {
            float slope = Math.abs((y2 - y1) / (x2 - x1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i : -i;
                float y = positiveY ? i * slope : -i * slope;

                for (int k = thickOffMin; k < thickOffMax; k++)
                    points.add(new CellPos((int) (x1 + x), (int) (y1 + y) + k));

            }
        } else {
            float slope = Math.abs((x2 - x1) / (y2 - y1));

            for (int i = 0; i <= steps; i++) {
                float x = positiveX ? i * slope : -i * slope;
                float y = positiveY ? i : -i;

                for (int k = thickOffMin; k < thickOffMax; k++)
                    points.add(new CellPos((int) (x1 + x) + k, (int) (y1 + y)));
            }
        }

        return points;
    }

    private Region getRegionsFromPoints(List<CellPos> points) {
        CellPos first = points.get(0);

        int minX = first.x, minY = first.y, maxX = first.x, maxY = first.y;

        for (int i = 1; i < points.size(); i++) {
            CellPos point = points.get(i);

            int x = point.x;
            int y = point.y;

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        return new Region(minX, minY, maxX, maxY);
    }

    public boolean isBranchOuterBranch(Branch testBranch, List<Branch> branches, int centerX, int centerY, float maximumDistance) {

        Vector2 centerBranchVector = new Vector2(testBranch.endX - centerX, testBranch.endY - centerY);
        float centerBranchDist = centerBranchVector.len();

        Vector2 branchOffsetVector = centerBranchVector.nor();

        float startX = testBranch.endX + branchOffsetVector.x;
        float startY = testBranch.endY + branchOffsetVector.y;

        Vector2 branchEdgeVector = branchOffsetVector.scl(maximumDistance - centerBranchDist);

        Line2D branchEdgeLine = new Line2D.Float(startX, startY, testBranch.endX + branchEdgeVector.x, testBranch.endY + branchEdgeVector.y);

        for (Branch branch : branches) {
            if (branch.equals(testBranch)) continue;

            Line2D branchLine = new Line2D.Float(branch.startX, branch.startY, branch.endX, branch.endY);

            if (branchLine.intersectsLine(branchEdgeLine)) return false;
        }

        return true;
    }


    private static class Branch {
        public final int startX, startY, endX, endY;
        public final int iteration;
        public boolean hasLeaf = true;

        public Branch(int startX, int startY, int endX, int endY, int iteration) {
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

    public static TreeGeneratorBuilder builder() {
        return new TreeGeneratorBuilder();
    }

    public static class TreeGeneratorBuilder {

        private String rule = "FF+[+F-F-F]-[-F+F+F]";
        private CellType branchCellType = CellType.WOOD;
        private CellType leafCellType = CellType.SPRING_LEAF;
        private int iterations = 1;
        private float lengthMultiplier = 0.5f;
        private float startLength = 20f;
        private float angleIncrement = 0.5f;
        private BranchThicknessFunction branchThicknessFunction = i -> 3 - i;

        private LeafSizeFunction leafSizeFunction = (v, b) -> 3;
        private ShouldAddLeaf shouldAddLeave = v -> true;

        private TreeGeneratorBuilder() {
        }

        public TreeGeneratorBuilder rule(String rule) {
            this.rule = rule;
            return this;
        }

        public TreeGeneratorBuilder branchCellType(CellType branchCellType) {
            this.branchCellType = branchCellType;
            return this;
        }

        public TreeGeneratorBuilder leafCellType(CellType leafCellType) {
            this.leafCellType = leafCellType;
            return this;
        }

        public TreeGeneratorBuilder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public TreeGeneratorBuilder startLength(float startLength) {
            this.startLength = startLength;
            return this;
        }

        public TreeGeneratorBuilder lengthMultiplier(float lengthMultiplier) {
            this.lengthMultiplier = lengthMultiplier;
            return this;
        }

        public TreeGeneratorBuilder angleIncrement(float angleIncrement) {
            this.angleIncrement = angleIncrement;
            return this;
        }

        public TreeGeneratorBuilder branchThicknessFunction(BranchThicknessFunction branchThicknessFunction) {
            this.branchThicknessFunction = branchThicknessFunction;
            return this;
        }

        public TreeGeneratorBuilder leafSizeFunction(LeafSizeFunction leafSizeFunction) {
            this.leafSizeFunction = leafSizeFunction;
            return this;
        }

        public TreeGeneratorBuilder shouldAddLeave(ShouldAddLeaf shouldAddLeave) {
            this.shouldAddLeave = shouldAddLeave;
            return this;
        }

        public TreeGenerator build() {
            return new TreeGenerator(branchCellType, leafCellType, rule, iterations, startLength, lengthMultiplier, angleIncrement, branchThicknessFunction, leafSizeFunction, shouldAddLeave);
        }
    }

    public interface LeafSizeFunction {
        int getLeafSize(float normDistToCenter, boolean isOuterBranch);
    }

    public interface BranchThicknessFunction {
        int getBranchThickness(int numBranchingBefore);
    }

    public interface ShouldAddLeaf {

        boolean shouldAffLeaf(float normalizedDist);

    }
}
