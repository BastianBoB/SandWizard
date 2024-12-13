package com.basti_bob.sand_wizard.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.cells.cell_properties.CellColors;
import com.basti_bob.sand_wizard.items.CellItem;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.items.ItemType;
import com.basti_bob.sand_wizard.items.inventory.Inventory;

public class ItemRenderer {

    private final GuiManager guiManager;
    private final int singleCellRenderSize = 15;

    public ItemRenderer() {
        this.guiManager = SandWizard.guiManager;
    }

    public void renderSingleGuiItemWithLabel(ItemStack itemStack, float renderX, float renderY) {
        ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderGuiItem(itemStack.getItemType(), renderX, renderY, 0);
        shapeRenderer.end();

        shapeRenderer.end();

        SpriteBatch batch = guiManager.getSpriteBatch();
        batch.begin();
        renderSlotLabel(itemStack, renderX - Inventory.DEFAULT_SLOT_SIZE/2f, renderY - Inventory.DEFAULT_SLOT_SIZE/2f);
        batch.end();
    }

    public void renderSlotLabel(ItemStack itemStack, float renderX, float renderY) {
        if (itemStack.isEmpty()) return;

        BitmapFont font = guiManager.getFont();
        SpriteBatch batch = guiManager.getSpriteBatch();
        GlyphLayout glyphLayout = guiManager.getGlyphLayout();
        glyphLayout.setText(font, "" + itemStack.getAmount());

        font.setColor(Color.WHITE);
        font.draw(batch, itemStack.getItemType().getDisplayName(), renderX + 3, renderY + Inventory.DEFAULT_SLOT_SIZE - 3);
        font.draw(batch, "" + itemStack.getAmount(), renderX + 3, renderY + 5 + glyphLayout.height);
    }

    public void renderGuiItem(ItemType itemType, float renderX, float renderY, float slotSize) {

        if (itemType instanceof CellItem cellItem) {
            ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();
            CellColors cellColors = cellItem.getCellType().getCellColors();

            float centerX = renderX + slotSize / 2f;
            float centerY = renderY + slotSize / 2f;
            float s = singleCellRenderSize;

            shapeRenderer.setColor(cellColors.getColorWithModIndex(0));
            shapeRenderer.rect(centerX - s, centerY - s, s, s);

            shapeRenderer.setColor(cellColors.getColorWithModIndex(1));
            shapeRenderer.rect(centerX, centerY - s, s, s);

            shapeRenderer.setColor(cellColors.getColorWithModIndex(2));
            shapeRenderer.rect(centerX - s / 2f, centerY, s, s);
        }
    }
}
