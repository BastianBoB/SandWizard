package com.basti_bob.sand_wizard.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.basti_bob.sand_wizard.player.Player;

public class TextRenderItem extends DebugRenderItem {

    private String text;
    private final float scale;
    private final TextSupplier textSupplier;
    private final SpriteBatch spriteBatch;

    public TextRenderItem(DebugScreen debugScreen, float x, float y, float w, float h, boolean worldUpdate, TextSupplier textSupplier, float scale) {
        super(debugScreen, x, y, w, h, worldUpdate);
        this.textSupplier = textSupplier;
        this.scale = scale;
        this.text = textSupplier.get(debugScreen.player);
        this.spriteBatch = debugScreen.getGuiManager().getSpriteBatch();
    }

    @Override
    public void update(Player player) {
        this.text = textSupplier.get(player);
    }

    @Override
    public void render() {
        BitmapFont font = debugScreen.getGuiManager().getFont();
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(scale);
        font.draw(spriteBatch, text, x, y);
        spriteBatch.end();
    }

    public interface TextSupplier {
        String get(Player player);

    }
}
