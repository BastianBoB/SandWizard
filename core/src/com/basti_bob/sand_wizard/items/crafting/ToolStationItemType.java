package com.basti_bob.sand_wizard.items.crafting;

public enum ToolStationItemType {
    HANDLE("Handle"),
    BINDING("Binding"),
    HEAD("Head");

    final String displayName;

    ToolStationItemType(String displayName) {
        this.displayName = displayName;
    }
}
