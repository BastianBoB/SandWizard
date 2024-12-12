package com.basti_bob.sand_wizard.debug;

import com.basti_bob.sand_wizard.player.Player;

public abstract class DebugRenderItem {

    protected final DebugScreen debugScreen;
    protected final float x, y, w, h;
    protected final boolean worldUpdate;

    public DebugRenderItem(DebugScreen debugScreen, float x, float y, float w, float h, boolean worldUpdate) {
        this.debugScreen = debugScreen;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.worldUpdate = worldUpdate;
    }

    public void worldUpdate(Player player) {
        if (worldUpdate) update(player);
    }

    public void renderUpdate(Player player) {
        if (!worldUpdate) update(player);
    }

    public abstract void update(Player player);

    public abstract void render();
}
