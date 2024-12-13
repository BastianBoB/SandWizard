package com.basti_bob.sand_wizard.items.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.input.InputHandler;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.player.PlayerInventory;
import com.basti_bob.sand_wizard.rendering.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class InventoryWithPlayerInventoryScreen extends Screen {

    private final Inventory inventory;
    private PlayerInventory playerInventory;

    private final List<InventorySlot> allSlots = new ArrayList<>();
    private ItemStack selectedItemStack = ItemStack.EMPTY_ITEM_STACK;
    private InventorySlot selectedSlot = null;
    private boolean isOpen = false;

    public InventoryWithPlayerInventoryScreen(@Nullable Inventory inventory) {
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

        if (!selectedItemStack.isEmpty()) {
            InputHandler inputHandler = SandWizard.inputHandler;
            SandWizard.itemRenderer.renderSingleGuiItemWithLabel(selectedItemStack, inputHandler.getMouseX(), inputHandler.getMouseY());
        }
    }

    public boolean canTakeItemOutOfSlot(InventorySlot inventorySlot) {
        return true;
    }

    public boolean canPutItemIntoSlot(InventorySlot inventorySlot) {
        return !inventorySlot.isExtractOnly();
    }

    public void setStackInSlot(InventorySlot inventorySlot, ItemStack itemStack) {
        inventorySlot.setItemStack(itemStack);
    }

    public boolean quickMoveItemStack(ItemStack itemStack, InventorySlot inventorySlot) {
        Inventory targetInventory = playerInventory.inventorySlots.contains(inventorySlot) ? inventory : playerInventory;

        return targetInventory.getItemStorage().receiveItemStack(itemStack,
                slotIndex -> canPutItemIntoSlot(targetInventory.getInventorySlots().get(slotIndex)));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        for (InventorySlot inventorySlot : allSlots) {

            if (!inventorySlot.isMouseOver(screenX, screenY)) continue;

            if(!canTakeItemOutOfSlot(inventorySlot)) break;

            ItemStack stackInSlot = inventorySlot.getItemStack();

            if (isShiftPressed && button == Input.Buttons.LEFT) {
                if(quickMoveItemStack(stackInSlot, inventorySlot)) {
                    setStackInSlot(inventorySlot, ItemStack.EMPTY_ITEM_STACK);
                }
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

        return super.touchDown(screenX, screenY, pointer, button);
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (button != Input.Buttons.LEFT || selectedItemStack.isEmpty()) return  super.touchUp(screenX, screenY, pointer, button);;

        boolean droppedAllItems = false;

        for (InventorySlot inventorySlot : allSlots) {
            if (!inventorySlot.isMouseOver(screenX, screenY)) continue;

            if (!canPutItemIntoSlot(inventorySlot)) break;

            ItemStack stackInSlot = inventorySlot.getItemStack();

            if (stackInSlot.isEmpty()) {
                setStackInSlot(inventorySlot, selectedItemStack);
                droppedAllItems = true;
            } else if (stackInSlot.getItemType() == selectedItemStack.getItemType()) {
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
