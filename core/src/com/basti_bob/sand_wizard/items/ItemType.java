package com.basti_bob.sand_wizard.items;

import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.registry.Registry;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class ItemType {

    public static final Registry<ItemType> ITEM_TYPES = new Registry<>("item_types");

    public static final ItemType EMPTY = ITEM_TYPES.register("empty", new ItemType(ItemProperties.EMPTY));
    public static final ItemType STONE = ITEM_TYPES.register("stone", new CellItem(ItemProperties.STONE, CellType.SOLID.STONE));
    public static final ItemType WOOD = ITEM_TYPES.register("wood", new CellItem(ItemProperties.WOOD, CellType.SOLID.WOOD));
    public static final ItemType MARBLE = ITEM_TYPES.register("marble", new CellItem(ItemProperties.MARBLE, CellType.SOLID.MARBLE));
    public static final ItemType ICE = ITEM_TYPES.register("ice", new CellItem(ItemProperties.ICE, CellType.SOLID.ICE));



    public static final ItemType SWORD = ITEM_TYPES.register("sword", new ItemType(ItemProperties.SWORD));

    private final String displayName;
    private final int maxStackSize;

    public ItemType(ItemProperties properties) {
        this.displayName = properties.displayName;
        this.maxStackSize = properties.maxStackSize;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public String getDisplayName() {
        return displayName;
    }
}
