package com.basti_bob.sand_wizard.items.inventory;

import com.basti_bob.sand_wizard.items.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ItemStorage {

    private final List<ItemStack> itemStacks = new ArrayList<>();
    private final int storageSize;

    public ItemStorage(int storageSize) {
        this.storageSize = storageSize;

        for (int i = 0; i < storageSize; i++) {
            itemStacks.add(ItemStack.EMPTY_ITEM_STACK);
        }
    }

    public ItemStack getItemStack(int index) {
        return itemStacks.get(index);
    }

    public boolean receiveItemStack(ItemStack receivingStack) {
        return receiveItemStack(receivingStack, slotIndex -> true);
    }

    public boolean receiveItemStack(ItemStack receivingStack, Predicate<Integer> canPutIntoSlotIndexPredicate) {

        for (int i = 0; i < itemStacks.size(); i++) {
            if (!canPutIntoSlotIndexPredicate.test(i)) continue;

            ItemStack itemStack = itemStacks.get(i);

            if (itemStack.isEmpty()) {
                itemStacks.set(i, receivingStack);
                return true;
            }

            if (tryAddItemStack(itemStack, receivingStack)) return true;
        }

        return false;

    }

    public boolean tryAddItemStack(ItemStack itemStack, ItemStack addingStack) {

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

    public void setItemStack(int slotIndex, ItemStack itemStack) {
        itemStacks.set(slotIndex, itemStack);
    }

    public int getStorageSize() {
        return storageSize;
    }
}
