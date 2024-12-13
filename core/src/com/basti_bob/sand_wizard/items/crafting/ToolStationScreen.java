package com.basti_bob.sand_wizard.items.crafting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.items.inventory.Inventory;
import com.basti_bob.sand_wizard.items.inventory.InventorySlot;
import com.basti_bob.sand_wizard.items.inventory.InventoryWithPlayerInventoryScreen;
import com.basti_bob.sand_wizard.rendering.Button;

import java.util.function.Predicate;

public class ToolStationScreen extends InventoryWithPlayerInventoryScreen {

    public ToolStationItemType currentItemType = ToolStationItemType.HANDLE;

    public Button[] itemTypeButtons = new Button[ToolStationItemType.values().length];

    public ToolStationScreen(ToolStationInventory inventory) {
        super(inventory);

        float buttonWidth = 200;
        float buttonHeight = Inventory.DEFAULT_SLOT_SIZE;

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
    }

    @Override
    public void render() {
        super.render();

        for (Button itemTypeButton : itemTypeButtons) {
            itemTypeButton.render();
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
