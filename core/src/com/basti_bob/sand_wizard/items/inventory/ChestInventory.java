package com.basti_bob.sand_wizard.items.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class ChestInventory extends SqaureGridInventory {

    public ChestInventory() {
        super(10, 4, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() /2 + 300);
    }
}
