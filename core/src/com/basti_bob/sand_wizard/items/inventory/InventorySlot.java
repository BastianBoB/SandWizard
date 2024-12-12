package com.basti_bob.sand_wizard.items.inventory;

import com.basti_bob.sand_wizard.items.ItemStack;

public class InventorySlot {

    private final ItemStorage itemStorage;
    private final int slotIndex;
    private final float x, y;
    private final float size;
    private boolean extractOnly;

    public InventorySlot(ItemStorage itemStorage, int slotIndex, float x, float y, float size) {
        this.itemStorage = itemStorage;
        this.slotIndex = slotIndex;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public boolean tryAddItemStack(ItemStack addingStack) {
        ItemStack itemStack = getItemStack();

        if (itemStack.getItemType() != addingStack.getItemType()) return false;

        int amountLeft = itemStack.getMaxAmount() - itemStack.getAmount();

        if (amountLeft == 0) return false;

        if (addingStack.getAmount() < amountLeft) {
            itemStack.addAmount(addingStack.getAmount());

            return true;
        } else {
            itemStack.setToMaxAmount();
            addingStack.removeAmount(amountLeft);

            return false;
        }
    }

    public ItemStack getItemStack() {
        return itemStorage.getItemStack(slotIndex);
    }

    public void setItemStack(ItemStack itemStack) {
        itemStorage.setItemStack(slotIndex, itemStack);
    }

    public boolean isMouseOver(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size;
    }

    public boolean isExtractOnly() {
        return extractOnly;
    }

    public void setExtractOnly(boolean extractOnly) {
        this.extractOnly = extractOnly;
    }
}
