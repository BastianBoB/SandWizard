package com.basti_bob.sand_wizard.rendering;

import com.basti_bob.sand_wizard.SandWizard;

public abstract class GuiElement {

    protected final GuiManager guiManager;

    public GuiElement() {
        this.guiManager = SandWizard.guiManager;
    }

    public abstract void render();

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
