package com.basti_bob.sand_wizard.items;

import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.registry.RegistryObject;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class ItemType {

    public static final Registry<ItemType> ITEM_TYPES = new Registry<>("item_types");

    public static final ItemType EMPTY = ITEM_TYPES.register("empty", ItemType.builder("Empty").maxAmount(0).build());
    public static final ItemType STONE = ITEM_TYPES.register("stone", ItemType.builder("Stone Block").build());
    public static final ItemType SWORD = ITEM_TYPES.register("sword", ItemType.builder("Sword").maxAmount(1).build());


    private final int maxAmount;
    private final String name;


    public ItemType(Builder builder) {
        this.maxAmount = builder.maxAmount;
        this.name = builder.name;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public String getName() {
        return name;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        public int maxAmount;
        public String name;

        public Builder(String name) {
            this.name = name;
            this.maxAmount = WorldConstants.MAX_ITEM_COUNT;
        }

        public Builder maxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
            return this;
        }

        public ItemType build() {
            return new ItemType(this);
        }
    }
}
