package com.basti_bob.sand_wizard.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.cells.cell_properties.CellColors;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventory {

    private final ItemStorage itemStorage;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final SpriteBatch batch;

    private final int numRows = 10;
    private final int numCols = 4;
    private final int slotSize = 80;
    private final int slotOffset = 20;
    private final int slotBorderSize = 4;


//    private final Color backgroundColor = new Color(1f, 0.5f, 0.25f, 0.25f);
//    private final Color slotColor = new Color(0.25f, 0.125f, 0.0625f, 1f);
//    private final Color slotBorderColor = new Color(0.1f, 0.05f, 0.025f, 1f);

    private final Color backgroundColor =  new Color(0.02f, 0.02f, 0.1f, 0.8f);
    private final Color slotColor = CellColors.c(116, 7, 7);
    private final Color slotBorderColor = CellColors.c(34, 0, 0);


    public PlayerInventory() {
        this.itemStorage = new ItemStorage(numRows * numCols);
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.batch = new SpriteBatch();
    }

    public void render() {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float rowSize = (slotSize + slotOffset) * numRows;
        float columnSize = (slotSize + slotOffset) * numCols;

        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(1980 / 2f - rowSize * 0.7f, 1080 / 2f - columnSize * 0.7f, rowSize * 1.4f, columnSize * 1.4f);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {

                float displayX = MathUtil.map(i, 0, numRows, 1980 / 2f - rowSize / 2, 1980 / 2f + rowSize / 2);
                float displayY = MathUtil.map(j, -1, numCols - 1, 1080 / 2f + columnSize / 2, 1080 / 2f - columnSize / 2);

                shapeRenderer.setColor(slotBorderColor);
                shapeRenderer.rect(displayX, displayY, slotSize, slotSize);

                shapeRenderer.setColor(slotColor);
                shapeRenderer.rect(displayX + slotBorderSize, displayY + slotBorderSize, slotSize - slotBorderSize * 2, slotSize - slotBorderSize * 2);
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        batch.begin();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int index = j * numRows + i;

                ItemStack itemStack = itemStorage.getItemStack(index);

                float displayX = MathUtil.map(i, 0, numRows, 1980 / 2f - rowSize / 2, 1980 / 2f + rowSize / 2);
                float displayY = MathUtil.map(j, -1, numCols - 1, 1080 / 2f + columnSize / 2, 1080 / 2f - columnSize / 2);

                font.setColor(Color.WHITE);
                font.draw(batch, itemStack.getItemType().getName(), displayX + 5, displayY + slotSize - 5);
                font.draw(batch, "" + itemStack.getAmount(), displayX + 5, displayY + 20);

            }
        }

        batch.end();
    }

    public ItemStorage getItemStorage() {
        return itemStorage;
    }
}
