package com.basti_bob.sand_wizard.items.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.rendering.GuiElement;

import java.util.ArrayList;
import java.util.List;

public abstract class Inventory extends GuiElement {

    public static final Color DEFAULT_SLOT_COLOR = new Color(0.5f, 0.4f, 0.3f, 1f);
    public static final Color DEFAULT_SLOT_BORDER_COLOR = new Color(0.35f, 0.25f, 0.2f, 1f);
    public static final int DEFAULT_SLOT_SIZE = 60;
    public static final int DEFAULT_SLOT_BORDER_SIZE = 2;

    protected final ItemStorage itemStorage;
    protected final Color slotColor;
    protected final Color slotBorderColor;
    protected final int slotSize;
    protected final int slotBorderSize;
    protected final List<InventorySlot> inventorySlots = new ArrayList<>();

    public Inventory(ItemStorage itemStorage, Color slotColor, Color slotBorderColor, int slotSize, int slotBorderSize) {
        super();
        this.itemStorage = itemStorage;
        this.slotColor = slotColor;
        this.slotBorderColor = slotBorderColor;
        this.slotSize = slotSize;
        this.slotBorderSize = slotBorderSize;
    }

    public void initializeSlots() {
        for (int i = 0; i < getNumSlots(); i++) {
            inventorySlots.add(new InventorySlot(itemStorage, i, getSlotRenderX(i), getSlotRenderY(i), slotSize));
        }
    }

    public Inventory(ItemStorage itemStorage) {
        this(itemStorage, DEFAULT_SLOT_COLOR, DEFAULT_SLOT_BORDER_COLOR, DEFAULT_SLOT_SIZE, DEFAULT_SLOT_BORDER_SIZE);
    }

    public abstract float getSlotRenderX(int slotIndex);

    public abstract float getSlotRenderY(int slotIndex);

    public void render() {
        ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < getNumSlots(); i++) {
            renderSlot(i);
        }
        shapeRenderer.end();

        SpriteBatch batch = guiManager.getSpriteBatch();
        batch.begin();
        for (int i = 0; i < getNumSlots(); i++) {
            renderSlotLabel(i);
        }
        batch.end();
    }

    public void renderSlot(int slotIndex) {
        float displayX = getSlotRenderX(slotIndex);
        float displayY = getSlotRenderY(slotIndex);

        ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();

        shapeRenderer.setColor(slotBorderColor);
        shapeRenderer.rect(displayX, displayY, slotSize, slotSize);

        shapeRenderer.setColor(slotColor);
        shapeRenderer.rect(displayX + slotBorderSize, displayY + slotBorderSize, slotSize - slotBorderSize * 2, slotSize - slotBorderSize * 2);
    }

    public void renderSlotLabel(int slotIndex) {
        ItemStack itemStack = itemStorage.getItemStack(slotIndex);

        float displayX = getSlotRenderX(slotIndex);
        float displayY = getSlotRenderY(slotIndex);

        BitmapFont font = guiManager.getFont();
        SpriteBatch batch = guiManager.getSpriteBatch();

        font.setColor(Color.WHITE);
        font.draw(batch, itemStack.getItemType().getDisplayName(), displayX + 5, displayY + slotSize - 5);
        font.draw(batch, "" + itemStack.getAmount(), displayX + 5, displayY + 20);
    }

    public int getNumSlots() {
        return itemStorage.getStorageSize();
    }

    public ItemStorage getItemStorage() {
        return itemStorage;
    }

    public List<InventorySlot> getInventorySlots() {
        return inventorySlots;
    }

    public int getSlotSize() {
        return slotSize;
    }
}
