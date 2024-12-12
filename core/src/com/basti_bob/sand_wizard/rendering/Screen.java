package com.basti_bob.sand_wizard.rendering;

import com.badlogic.gdx.Input;
import com.basti_bob.sand_wizard.input.InputElement;
import com.basti_bob.sand_wizard.input.InputHandler;

public abstract class Screen extends GuiElement implements InputElement {

    public Screen() {
        super();
        InputHandler.getInstance().addInputElement(this);
    }
}
