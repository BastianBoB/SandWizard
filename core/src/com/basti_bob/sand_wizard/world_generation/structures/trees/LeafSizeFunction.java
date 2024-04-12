package com.basti_bob.sand_wizard.world_generation.structures.trees;

public interface LeafSizeFunction {
    int getLeafSize(float normDistToCenter, boolean isOuterBranch);
}
