package com.basti_bob.sand_wizard.rendering;

import com.badlogic.gdx.Input;
import com.basti_bob.sand_wizard.input.InputElement;
import com.basti_bob.sand_wizard.input.InputHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class Screen extends GuiElement implements InputElement {

    public List<InputElement> inputElements = new ArrayList<>();

    public Screen() {
        super();
        InputHandler.getInstance().addInputElement(this);
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
                inputElement.touchDown(screenX, screenY, pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.touchUp(screenX, screenY, pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.touchCancelled(screenX, screenY, pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.touchDragged(screenX, screenY, pointer);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        for (InputElement inputElement : inputElements) {
            if (inputElement.shouldListenToInput())
                inputElement.mouseMoved(screenX, screenY);
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

    @Override
    public boolean shouldListenToInput() {
        return true;
    }
}
