package com.basti_bob.sand_wizard.items.crafting;

import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.items.inventory.InventorySlot;
import com.basti_bob.sand_wizard.items.inventory.ItemStorage;

public class ToolStationInventorySlot extends InventorySlot {

    private ToolStationItemType currentItemType;

    public ToolStationInventorySlot(ItemStorage itemStorage, int slotIndex, float x, float y, float size) {
        super(itemStorage, slotIndex, x, y, size);
        currentItemType = ToolStationItemType.HANDLE;
    }

    public void setItemStack(ItemStack itemStack, ToolStationItemType toolStationItemType) {
        super.setItemStack(itemStack);

        this.currentItemType = toolStationItemType;
    }


    public ToolStationItemType getCurrentItemType() {
        return currentItemType;
    }

    public void setCurrentItemType(ToolStationItemType currentItemType) {
        this.currentItemType = currentItemType;
    }
}
