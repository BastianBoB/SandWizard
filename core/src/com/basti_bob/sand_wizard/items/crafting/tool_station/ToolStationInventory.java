package com.basti_bob.sand_wizard.items.crafting.tool_station;

import com.badlogic.gdx.Gdx;
import com.basti_bob.sand_wizard.items.inventory.Inventory;
import com.basti_bob.sand_wizard.items.inventory.ItemStorage;
import com.basti_bob.sand_wizard.util.MathUtil;

public class ToolStationInventory extends Inventory {

    private final int numRows = 20;
    private final int numCols = 20;
    private final int centerX = 1980 / 2;
    private final int centerY = (DEFAULT_SLOT_SIZE * 5 + Gdx.graphics.getHeight()) / 2;

    public ToolStationInventory() {
        super(new ItemStorage(1 + 20 * 20));

        this.initializeSlots();
    }

    @Override
    public void initializeSlots() {

        for (int i = 0; i < getNumSlots(); i++) {
            getInventorySlots().add(new ToolStationInventorySlot(getItemStorage(), i, getSlotRenderX(i), getSlotRenderY(i), getSlotSize()));
        }

        this.getInventorySlots().get(0).setExtractOnly(true);
    }

    @Override
    public float getSlotRenderX(int slotIndex) {
        float rowSize = getSlotSize() * numRows;

        if (slotIndex == 0)
            return centerX + rowSize / 2f + getSlotSize();

        return MathUtil.map((slotIndex - 1) % numRows, 0, numRows, centerX - rowSize / 2f, centerX + rowSize / 2f);
    }

    @Override
    public float getSlotRenderY(int slotIndex) {
        float columnSize = getSlotSize() * numCols;

        if (slotIndex == 0)
            return getSlotRenderY(numRows * numCols / 2);

        return MathUtil.map((slotIndex - 1) / numRows, 0, numCols, centerY - columnSize / 2f, centerY + columnSize / 2f);
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }
}
