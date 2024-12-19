package com.basti_bob.sand_wizard.items.inventory;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Inventory {

    public static final Color DEFAULT_SLOT_COLOR = new Color(0.75f, 0.6f, 0.45f, 1f);
    public static final Color DEFAULT_SLOT_BORDER_COLOR = new Color(0.5f, 0.4f, 0.3f, 1f);
    public static final Color DEFAULT_SLOT_HIGH_LIGHT_COLOR = DEFAULT_SLOT_COLOR.cpy().mul(1.3f);
    public static final int DEFAULT_SLOT_SIZE = 40;
    public static final int DEFAULT_SLOT_BORDER_SIZE = 1;

    private final ItemStorage itemStorage;
    private final Color slotColor;
    private final Color slotBorderColor;
    private final Color slotHighLightColor;
    private final int slotSize;
    private final int slotBorderSize;
    private final List<InventorySlot> inventorySlots = new ArrayList<>();

    public Inventory(ItemStorage itemStorage, Color slotColor, Color slotBorderColor, Color slotHighLightColor, int slotSize, int slotBorderSize) {
        super();
        this.itemStorage = itemStorage;
        this.slotColor = slotColor;
        this.slotBorderColor = slotBorderColor;
        this.slotSize = slotSize;
        this.slotBorderSize = slotBorderSize;
        this.slotHighLightColor = slotHighLightColor;
    }

    public void initializeSlots() {
        for (int i = 0; i < getNumSlots(); i++) {
            inventorySlots.add(new InventorySlot(itemStorage, i, getSlotRenderX(i), getSlotRenderY(i), slotSize));
        }
    }

    public Inventory(ItemStorage itemStorage) {
        this(itemStorage, DEFAULT_SLOT_COLOR, DEFAULT_SLOT_BORDER_COLOR, DEFAULT_SLOT_HIGH_LIGHT_COLOR, DEFAULT_SLOT_SIZE, DEFAULT_SLOT_BORDER_SIZE);
    }

    public abstract float getSlotRenderX(int slotIndex);

    public abstract float getSlotRenderY(int slotIndex);

    public int getNumSlots() {
        return itemStorage.getStorageSize();
    }

    public ItemStorage getItemStorage() {
        return itemStorage;
    }

    public List<InventorySlot> getInventorySlots() {
        return inventorySlots;
    }

    public int getSlotSize() {
        return slotSize;
    }

    public Color getSlotBorderColor() {
        return slotBorderColor;
    }

    public Color getSlotColor() {
        return slotColor;
    }

    public Color getSlotHighLightColor() {
        return slotHighLightColor;
    }

    public int getSlotBorderSize() {
        return slotBorderSize;
    }
}
