package com.basti_bob.sand_wizard.player;

import com.basti_bob.sand_wizard.items.inventory.SqaureGridInventory;

public class PlayerInventory extends SqaureGridInventory {

    private final Player player;

    public PlayerInventory(Player player) {
        super(10, 4, 1980/2, 200);

        this.player = player;
    }
}