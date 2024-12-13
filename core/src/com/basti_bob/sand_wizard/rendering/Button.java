package com.basti_bob.sand_wizard.rendering;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.basti_bob.sand_wizard.input.InputElement;
import com.basti_bob.sand_wizard.util.MathUtil;

public class Button extends GuiElement implements InputElement {

    private Color color = Color.WHITE;
    private Color borderColor = Color.GRAY;
    private Color textColor = Color.BLACK;
    private final int borderSize = 4;

    private final float x, y, width, height;
    private final String text;
    private final Runnable clickAction;

    private boolean clickedDown = false;
    private final float clickShrinkFactor = 0.05f;

    public Button(float x, float y, float width, float height, String text, Runnable clickAction) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.clickAction = clickAction;
    }

    @Override
    public void render() {
        ShapeRenderer shapeRenderer = guiManager.getShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float xOff = clickedDown ? width * clickShrinkFactor : 0;
        float yOff = clickedDown ? height * clickShrinkFactor : 0;

        float renderWidth = width - xOff * 2;
        float renderHeight = height - yOff * 2;

        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x + xOff, y + yOff, renderWidth, renderHeight);

        shapeRenderer.setColor(color);
        shapeRenderer.rect(x + xOff + borderSize, y + yOff + borderSize, renderWidth - borderSize * 2, renderHeight - borderSize * 2);

        shapeRenderer.end();

        SpriteBatch batch = guiManager.getSpriteBatch();
        BitmapFont font = guiManager.getFont();
        GlyphLayout glyphLayout = guiManager.getGlyphLayout();
        glyphLayout.setText(font, text);
        float textHeight = glyphLayout.height;

        batch.begin();
        font.setColor(textColor);
        font.draw(batch, text, x + width / 2f, y + height / 2f + textHeight / 2f, 0, Align.center, false);
        batch.end();

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && MathUtil.isPointInRect(screenX, screenY, x, y, width, height)) {
            clickedDown = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && clickedDown) {
            clickAction.run();

            clickedDown = false;
            return true;
        }

        return false;
    }


    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
