package com.basti_bob.sand_wizard.items;

public class ItemStack {

    public static final ItemStack EMPTY_ITEM_STACK = new ItemStack(ItemType.EMPTY, 0);

    private final ItemType itemType;
    private final int maxAmount;
    private int amount;

    public ItemStack(ItemType itemType) {
        this(itemType, 1);
    }

    public ItemStack(ItemType itemType, int amount) {
        this.itemType = itemType;
        this.maxAmount = itemType.getMaxAmount();
        this.amount = amount;
    }


    public ItemType getItemType() {
        return itemType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public void removeAmount(int amount) {
        this.amount -= amount;
    }

    public void setToMaxAmount() {
        this.amount = getMaxAmount();
    }

    public int getMaxAmount() {
        return maxAmount;
    }
}
