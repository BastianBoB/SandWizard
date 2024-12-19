package com.basti_bob.sand_wizard.items.inventory;

import com.basti_bob.sand_wizard.util.MathUtil;

public class SqaureGridInventory extends Inventory {

    private final int numRows, numCols;
    private final int centerX, centerY;

    public SqaureGridInventory(int numRows, int numCols, int centerX, int centerY) {
        super(new ItemStorage(numRows * numCols));
        this.numRows = numRows;
        this.numCols = numCols;
        this.centerX = centerX;
        this.centerY = centerY;

        this.initializeSlots();
    }

    @Override
    public float getSlotRenderX(int slotIndex) {
        float rowSize = getSlotSize() * numRows;

        return MathUtil.map(slotIndex % numRows, 0, numRows, centerX - rowSize / 2f, centerX + rowSize / 2f);
    }

    @Override
    public float getSlotRenderY(int slotIndex) {
        float columnSize = getSlotSize() * numCols;

        return MathUtil.map(slotIndex / numRows, 0, numCols, centerY - columnSize / 2f, centerY + columnSize / 2f);
    }
}
