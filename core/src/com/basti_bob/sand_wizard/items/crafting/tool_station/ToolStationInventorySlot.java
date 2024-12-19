package com.basti_bob.sand_wizard.items.crafting.tool_station;

import com.basti_bob.sand_wizard.items.inventory.InventorySlot;
import com.basti_bob.sand_wizard.items.inventory.ItemStorage;

public class ToolStationInventorySlot extends InventorySlot {

    private ToolStationItemType currentItemType;

    public ToolStationInventorySlot(ItemStorage itemStorage, int slotIndex, float x, float y, float size) {
        super(itemStorage, slotIndex, x, y, size);
        currentItemType = ToolStationItemType.HANDLE;
    }

    public ToolStationItemType getCurrentItemType() {
        return currentItemType;
    }

    public void setCurrentItemType(ToolStationItemType currentItemType) {
        this.currentItemType = currentItemType;
    }
}
