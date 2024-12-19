package com.basti_bob.sand_wizard.items.crafting.tool_station;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.items.inventory.Inventory;
import com.basti_bob.sand_wizard.items.inventory.InventorySlot;
import com.basti_bob.sand_wizard.items.inventory.InventoryWithPlayerInventoryScreen;
import com.basti_bob.sand_wizard.rendering.Button;

public class ToolStationScreen extends InventoryWithPlayerInventoryScreen<ToolStationInventory> {

    public ToolStationItemType currentItemType = ToolStationItemType.HANDLE;
    public Color wrongItemTypeSlotColor = new Color(0.3f, 0.3f, 0.3f, 0.5f);

    public Button[] itemTypeButtons = new Button[ToolStationItemType.values().length];
    public Button craftButton;

    public ToolStationScreen(ToolStationInventory inventory) {
        super(inventory);

        float buttonHeight = Inventory.DEFAULT_SLOT_SIZE;
        float buttonWidth = buttonHeight * 2;

        int referenceSlotIndex = inventory.getNumRows() * (inventory.getNumCols() - 1) + 1;
        float buttonX = inventory.getSlotRenderX(referenceSlotIndex) - buttonWidth - 20;
        float buttonY = inventory.getSlotRenderY(referenceSlotIndex);

        for (int i = 0; i < ToolStationItemType.values().length; i++) {
            ToolStationItemType toolStationItemType = ToolStationItemType.values()[i];
            Button button = new Button(buttonX, buttonY, buttonWidth, buttonHeight, toolStationItemType.displayName, () -> currentItemType = toolStationItemType);
            this.addInputElement(button);

            itemTypeButtons[i] = button;
            buttonY -= buttonHeight + 10;
        }

        float craftButtonX = inventory.getSlotRenderX(0) + Inventory.DEFAULT_SLOT_SIZE/2f - buttonWidth/2f;
        float craftButtonY = inventory.getSlotRenderY(0) + 20 + buttonHeight;

        craftButton = new Button(craftButtonX, craftButtonY, buttonWidth, buttonHeight, "Craft", () -> {
            //inventory.craftItem();
        });

        this.addInputElement(craftButton);
    }

    @Override
    public void render() {
        super.render();

        for (Button itemTypeButton : itemTypeButtons) {
            itemTypeButton.render();
        }

        craftButton.render();
    }

    public void renderInventory(Inventory inventory) {
        super.renderInventory(inventory);

        if (inventory instanceof ToolStationInventory toolStationInventory) {
            ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(wrongItemTypeSlotColor);

            for (int i = 0; i < inventory.getNumSlots(); i++) {
                renderWrongItemTypeSlotOverlay(toolStationInventory, i);
            }

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void renderWrongItemTypeSlotOverlay(Inventory inventory, int slotIndex) {
        if (inventory.getInventorySlots().get(slotIndex) instanceof ToolStationInventorySlot toolStationInventorySlot) {
            if (toolStationInventorySlot.getItemStack().isEmpty() || toolStationInventorySlot.getCurrentItemType() == currentItemType)
                return;

            float renderX = inventory.getSlotRenderX(slotIndex);
            float renderY = inventory.getSlotRenderY(slotIndex);

            ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();
            shapeRenderer.rect(renderX, renderY, inventory.getSlotSize(), inventory.getSlotSize());
        }

    }

    @Override
    public void setStackInSlot(InventorySlot inventorySlot, ItemStack itemStack) {

        super.setStackInSlot(inventorySlot, itemStack);

        if (inventorySlot instanceof ToolStationInventorySlot toolStationInventorySlot) {
            toolStationInventorySlot.setCurrentItemType(currentItemType);
        }
    }

    @Override
    public boolean canPutItemIntoSlot(InventorySlot inventorySlot) {
        if (!inventorySlot.getItemStack().isEmpty() && inventorySlot instanceof ToolStationInventorySlot toolStationInventorySlot) {
            return toolStationInventorySlot.getCurrentItemType() == currentItemType;
        }

        return super.canPutItemIntoSlot(inventorySlot);
    }

    @Override
    public boolean canTakeItemOutOfSlot(InventorySlot inventorySlot) {
        if (!inventorySlot.getItemStack().isEmpty() && inventorySlot instanceof ToolStationInventorySlot toolStationInventorySlot) {
            return toolStationInventorySlot.getCurrentItemType() == currentItemType;
        }

        return super.canTakeItemOutOfSlot(inventorySlot);
    }
}
