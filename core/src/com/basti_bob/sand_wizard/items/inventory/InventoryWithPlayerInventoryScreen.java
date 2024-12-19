package com.basti_bob.sand_wizard.items.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.input.InputElement;
import com.basti_bob.sand_wizard.input.InputHandler;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.player.PlayerInventory;
import com.basti_bob.sand_wizard.rendering.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class InventoryWithPlayerInventoryScreen<T extends Inventory> extends Screen {

    private final T inventory;
    private PlayerInventory playerInventory;

    private final List<InventorySlot> allSlots = new ArrayList<>();
    private ItemStack selectedItemStack = ItemStack.EMPTY_ITEM_STACK;
    private boolean isOpen = false;

    private final List<InventorySlot> rightClickedDraggedInventorySlots = new ArrayList<>();

    public InventoryWithPlayerInventoryScreen(@Nullable T inventory) {
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
        renderInventory(playerInventory);

        if (inventory != null) renderInventory(inventory);

        if (!selectedItemStack.isEmpty()) {
            InputHandler inputHandler = SandWizard.inputHandler;
            SandWizard.itemRenderer.renderSingleGuiItemWithLabel(selectedItemStack, inputHandler.getMouseX(), inputHandler.getMouseY());
        }
    }

    public void renderInventory(Inventory inventory) {
        ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < inventory.getNumSlots(); i++) {
            renderSlot(inventory, i);
            renderItem(inventory, i);
        }
        shapeRenderer.end();

        SpriteBatch batch = guiManager.getSpriteBatch();
        batch.begin();
        for (int i = 0; i < inventory.getNumSlots(); i++) {
            renderSlotLabel(inventory, i);
        }
        batch.end();
    }


    public Color getColorForSlot(Inventory inventory, int slotIndex) {
        InputHandler inputHandler = SandWizard.inputHandler;
        return inventory.getInventorySlots().get(slotIndex).isMouseOver(inputHandler.getMouseX(), inputHandler.getMouseY()) ? inventory.getSlotHighLightColor() : inventory.getSlotColor();
    }

    public void renderSlot(Inventory inventory, int slotIndex) {
        float renderX = inventory.getSlotRenderX(slotIndex);
        float renderY = inventory.getSlotRenderY(slotIndex);

        ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();

        shapeRenderer.setColor(inventory.getSlotBorderColor());
        shapeRenderer.rect(renderX, renderY, inventory.getSlotSize(), inventory.getSlotSize());

        shapeRenderer.setColor(getColorForSlot(inventory, slotIndex));
        shapeRenderer.rect(renderX + inventory.getSlotBorderSize(), renderY + inventory.getSlotBorderSize(),
                inventory.getSlotSize() - inventory.getSlotBorderSize() * 2, inventory.getSlotSize() - inventory.getSlotBorderSize() * 2);
    }

    public void renderItem(Inventory inventory, int slotIndex) {
        ItemStack itemStack = inventory.getItemStorage().getItemStack(slotIndex);
        if (itemStack.isEmpty()) return;

        float renderX = inventory.getSlotRenderX(slotIndex);
        float renderY = inventory.getSlotRenderY(slotIndex);

        SandWizard.itemRenderer.renderGuiItem(itemStack.getItemType(), renderX, renderY, inventory.getSlotSize());
    }

    public void renderSlotLabel(Inventory inventory, int slotIndex) {
        ItemStack itemStack = inventory.getItemStorage().getItemStack(slotIndex);
        if (itemStack.isEmpty()) return;

        float renderX = inventory.getSlotRenderX(slotIndex);
        float renderY = inventory.getSlotRenderY(slotIndex);

        SandWizard.itemRenderer.renderSlotLabel(itemStack, renderX, renderY, inventory.getSlotSize());
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
        Inventory targetInventory = playerInventory.getInventorySlots().contains(inventorySlot) ? inventory : playerInventory;

        return targetInventory.getItemStorage().receiveItemStack(itemStack,
                slotIndex -> canPutItemIntoSlot(targetInventory.getInventorySlots().get(slotIndex)));
    }

    public void shiftLeftClickedOnSlot(InventorySlot inventorySlot, ItemStack stackInSlot) {
        if (!canTakeItemOutOfSlot(inventorySlot)) return;

        if (quickMoveItemStack(stackInSlot, inventorySlot)) {
            setStackInSlot(inventorySlot, ItemStack.EMPTY_ITEM_STACK);
        }
    }

    public void leftClickedOnSlot(InventorySlot inventorySlot, ItemStack stackInSlot) {
        if (selectedItemStack.isEmpty()) {
            if (!canTakeItemOutOfSlot(inventorySlot)) return;

            selectedItemStack = stackInSlot;
            setStackInSlot(inventorySlot, ItemStack.EMPTY_ITEM_STACK);
            return;
        }

        if (stackInSlot.isEmpty()) {
            if (!canPutItemIntoSlot(inventorySlot)) return;

            setStackInSlot(inventorySlot, selectedItemStack);
            selectedItemStack = ItemStack.EMPTY_ITEM_STACK;
            return;
        }

        if (selectedItemStack.getItemType() != stackInSlot.getItemType()) {
            if (!canTakeItemOutOfSlot(inventorySlot) || !canPutItemIntoSlot(inventorySlot)) return;

            ItemStack tempStack = inventorySlot.getItemStack();
            setStackInSlot(inventorySlot, selectedItemStack);
            selectedItemStack = tempStack;
            return;
        }

        if (!canPutItemIntoSlot(inventorySlot)) return;

        inventorySlot.tryAddItemStack(selectedItemStack);
    }

    public void rightClickedOnSlot(InventorySlot inventorySlot, ItemStack stackInSlot) {
        if (selectedItemStack.isEmpty()) {
            if (!canTakeItemOutOfSlot(inventorySlot)) return;

            selectedItemStack = stackInSlot.split(stackInSlot.getAmount() / 2);
            return;
        }

        if (stackInSlot.isEmpty()) {
            if (!canPutItemIntoSlot(inventorySlot)) return;

            setStackInSlot(inventorySlot, selectedItemStack.split(1));
            rightClickedDraggedInventorySlots.add(inventorySlot);
            return;
        }

        if (selectedItemStack.getItemType() == stackInSlot.getItemType()) {
            if (!canPutItemIntoSlot(inventorySlot)) return;

            boolean added1ToStack = inventorySlot.tryAddItemStack(selectedItemStack.split(1));
            rightClickedDraggedInventorySlots.add(inventorySlot);

            if (!added1ToStack) {
                selectedItemStack.addAmount(1);
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        for (InventorySlot inventorySlot : allSlots) {

            if (!inventorySlot.isMouseOver(screenX, screenY)) continue;

            ItemStack stackInSlot = inventorySlot.getItemStack();

            if (button == Input.Buttons.LEFT) {
                if (isShiftPressed) {
                    shiftLeftClickedOnSlot(inventorySlot, stackInSlot);
                } else {
                    leftClickedOnSlot(inventorySlot, stackInSlot);
                }
            }

            if (button == Input.Buttons.RIGHT) {
                rightClickedOnSlot(inventorySlot, stackInSlot);
            }

            return true;
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            rightClickedDraggedInventorySlots.clear();
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        for (InventorySlot inventorySlot : allSlots) {

            if (!inventorySlot.isMouseOver(screenX, screenY)) continue;

            ItemStack stackInSlot = inventorySlot.getItemStack();

            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                if (rightClickedDraggedInventorySlots.contains(inventorySlot)) return false;


                rightClickedOnSlot(inventorySlot, stackInSlot);
                rightClickedDraggedInventorySlots.add(inventorySlot);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                shiftLeftClickedOnSlot(inventorySlot, stackInSlot);
            }

            return true;
        }

        return super.touchDragged(screenX, screenY, pointer);
    }
}
