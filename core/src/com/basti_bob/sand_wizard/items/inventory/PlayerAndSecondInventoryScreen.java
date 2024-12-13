package com.basti_bob.sand_wizard.items.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.player.PlayerInventory;
import com.basti_bob.sand_wizard.rendering.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlayerAndSecondInventoryScreen extends Screen {

    private final Inventory inventory;
    private PlayerInventory playerInventory;

    private final List<InventorySlot> allSlots = new ArrayList<>();
    private ItemStack selectedItemStack = ItemStack.EMPTY_ITEM_STACK;
    private InventorySlot selectedSlot = null;
    private boolean isOpen = false;

    public PlayerAndSecondInventoryScreen(@Nullable Inventory inventory) {
        super();
        this.inventory = inventory;

        if (inventory != null)
            this.allSlots.addAll(inventory.getInventorySlots());
    }

    @Override
    public boolean shouldListenToInput() {
        return isOpen;
    }

    public void playerOpenedScreen(Player player) {
        if (playerInventory != null)
            allSlots.removeAll(playerInventory.getInventorySlots());

        this.allSlots.addAll(player.inventory.getInventorySlots());

        this.playerInventory = player.inventory;

        player.openInventoryScreen(this);

        isOpen = true;
    }

    public void playerClosedScreen() {
        isOpen = false;
    }


    @Override
    public void render() {
        playerInventory.render();

        if (inventory != null) inventory.render();
    }

    public boolean canTakeItemOutOfSlot(InventorySlot inventorySlot) {
        return true;
    }

    public boolean canPutItemIntoSlot(InventorySlot inventorySlot) {
        return true;
    }

    public void setStackInSlot(InventorySlot inventorySlot, ItemStack itemStack) {
        inventorySlot.setItemStack(itemStack);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        for (InventorySlot inventorySlot : allSlots) {

            if (!inventorySlot.isMouseOver(screenX, screenY) || !canTakeItemOutOfSlot(inventorySlot)) continue;

            ItemStack stackInSlot = inventorySlot.getItemStack();

            if (isShiftPressed && button == Input.Buttons.LEFT) {
//                    if (!stack.isEmpty()) {
//                        if (itemStorage.receiveItemStack(stack)) {
//                            itemStorage.setItemStack(slotIndex, ItemStack.EMPTY_ITEM_STACK);
//                        }
//                    }
            }

            if (!isShiftPressed && button == Input.Buttons.LEFT) {
                if (selectedItemStack.isEmpty()) {
                    selectedItemStack = stackInSlot;
                    selectedSlot = inventorySlot;
                    setStackInSlot(inventorySlot, ItemStack.EMPTY_ITEM_STACK);
                }
            }

            if (button == Input.Buttons.RIGHT) {
                if (!stackInSlot.isEmpty()) {
                    int splitAmount = stackInSlot.getAmount() / 2;

                    if (selectedItemStack.isEmpty()) {
                        selectedItemStack = stackInSlot.split(splitAmount);
                        selectedSlot = inventorySlot;
                    } else if (selectedItemStack.getItemType() == stackInSlot.getItemType()) {
                        int addAmount = Math.min(splitAmount, selectedItemStack.getMaxAmount() - selectedItemStack.getAmount());
                        selectedItemStack.addAmount(addAmount);
                        stackInSlot.removeAmount(addAmount);
                    }

                    if (stackInSlot.getAmount() == 0) {
                        setStackInSlot(inventorySlot, ItemStack.EMPTY_ITEM_STACK);
                    }
                }
            }

            return true;
        }

        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);

        if (button != Input.Buttons.LEFT || selectedItemStack.isEmpty()) return false;

        boolean droppedAllItems = false;

        for (InventorySlot inventorySlot : allSlots) {
            if (!inventorySlot.isMouseOver(screenX, screenY) || !canPutItemIntoSlot(inventorySlot)) continue;

            if (inventorySlot.isExtractOnly()) break;

            ItemStack stackInSlot = inventorySlot.getItemStack();

            if (stackInSlot.isEmpty()) {
                // Drop in empty slot
                setStackInSlot(inventorySlot, selectedItemStack);
                droppedAllItems = true;
            } else if (stackInSlot.getItemType() == selectedItemStack.getItemType()) {
                // Combine stacks
                if (inventorySlot.tryAddItemStack(selectedItemStack)) {
                    droppedAllItems = true;
                }
            } else {
                //Swap stacks
                ItemStack tempStack = inventorySlot.getItemStack();
                setStackInSlot(inventorySlot, selectedItemStack);
                setStackInSlot(selectedSlot, tempStack);
                droppedAllItems = true;
            }
            break;
        }

        // Return to original slot if not dropped
        if (!droppedAllItems) {
            setStackInSlot(selectedSlot, selectedItemStack);
        }

        selectedItemStack = ItemStack.EMPTY_ITEM_STACK;

        return true;
    }
}
