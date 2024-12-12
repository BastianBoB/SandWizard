package com.basti_bob.sand_wizard.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.basti_bob.sand_wizard.rendering.GuiElement;
import com.basti_bob.sand_wizard.rendering.Screen;

import java.util.ArrayList;
import java.util.List;

public class InputHandler implements InputProcessor {

    private static InputHandler INSTANCE;

    private final List<InputElement> inputElements = new ArrayList<>();

    public InputHandler() {
    }

    public static InputHandler getInstance() {
        if (INSTANCE == null) INSTANCE = new InputHandler();

        return INSTANCE;
    }

    public void addInputElement(InputElement inputElement) {
        inputElements.add(inputElement);
    }

    @Override
    public boolean keyDown(int keycode) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.keyDown(keycode);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.keyUp(keycode);
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.keyTyped(character);
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.touchDown(screenX, getAdjustedScreenY(screenY), pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.touchUp(screenX, getAdjustedScreenY(screenY), pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.touchCancelled(screenX, getAdjustedScreenY(screenY), pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.touchDragged(screenX, getAdjustedScreenY(screenY), pointer);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.mouseMoved(screenX, getAdjustedScreenY(screenY));
        }

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.scrolled(amountX, amountY);
        }

        return false;
    }

    private int getAdjustedScreenY(int screenY) {
        return Gdx.graphics.getHeight() - screenY;
    }
}
