package com.basti_bob.sand_wizard.items;

public class ItemStack {

    private final ItemType itemType;
    private int amount;

    public static final ItemStack EMPTY_ITEM_STACK = new ItemStack(ItemType.EMPTY, 0);

    public ItemStack(ItemType itemType, int amount) {
        this.itemType = itemType;
        this.amount = amount;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getAmount() {
        return amount;
    }

    public void addAmount(int amountToAdd) {
        amount = Math.min(amount + amountToAdd, getMaxAmount());
    }

    public void removeAmount(int amountToRemove) {
        amount = Math.max(amount - amountToRemove, 0);
    }

    public int getMaxAmount() {
        return itemType.getMaxStackSize();
    }

    public boolean isEmpty() {
        return amount == 0 || itemType == ItemType.EMPTY;
    }

    public ItemStack split(int splitAmount) {
        if (splitAmount > amount) {
            splitAmount = amount;
        }
        amount -= splitAmount;
        return new ItemStack(itemType, splitAmount);
    }

    public ItemStack splitInHalf() {
        return split(getAmount() / 2);
    }

    public void setToMaxAmount() {
        this.amount = itemType.getMaxStackSize();
    }
}
