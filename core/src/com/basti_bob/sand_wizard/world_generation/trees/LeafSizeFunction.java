package com.basti_bob.sand_wizard.world_generation.trees;

public interface LeafSizeFunction {

    int getLeafSize(float normDistToCenter, boolean isOuterBranch);
}
