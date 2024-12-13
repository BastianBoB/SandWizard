package com.basti_bob.sand_wizard.items;

import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class ItemProperties {

    public static final ItemProperties EMPTY = ItemProperties.builder("Empty").build();

    public static final ItemProperties STONE = ItemProperties.builder("Stone").build();
    public static final ItemProperties WOOD = ItemProperties.builder("Wood").build();
    public static final ItemProperties MARBLE = ItemProperties.builder("Marble").build();
    public static final ItemProperties ICE = ItemProperties.builder("Ice").build();


    public static final ItemProperties SWORD = ItemProperties.builder("Sword").maxStackSize(1).build();

    public final String displayName;
    public final int maxStackSize;

    public ItemProperties(Builder builder) {
        this.displayName = builder.displayName;
        this.maxStackSize = builder.maxStackSize;
    }

    public static Builder builder(String displayName) {
        return new Builder(displayName);
    }

    public static class Builder {

        public String displayName;
        public int maxStackSize = WorldConstants.MAX_ITEM_COUNT;

        public Builder(String displayName) {
            this.displayName = displayName;
        }

        public Builder maxStackSize(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public ItemProperties build() {
            return new ItemProperties(this);
        }
    }
}
