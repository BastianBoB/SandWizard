package com.basti_bob.sand_wizard.debug;

import com.badlogic.gdx.Gdx;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.rendering.GuiElement;
import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.world_generation.biomes.BiomeType;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebugScreen extends GuiElement {

    protected final Player player;
    private final List<DebugRenderItem> debugRenderItems = new ArrayList<>();

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));

    public DebugScreen(Player player) {
        super();

        this.player = player;

        float verticalSpace = 15;
        float itemX = 50;
        float[] itemY = {Gdx.graphics.getHeight() - 50};

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "fps: " + Gdx.graphics.getFramesPerSecond()), false));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "memory usage: " + (int) SandWizard.freeMemory + "MB / " + (int) SandWizard.maxMemory + "MB | " + decimal(100 * (SandWizard.freeMemory / SandWizard.maxMemory)) + " %"), false));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "generated chunks: " + p.getWorld().chunkManager.getChunks().size()), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "loaded chunks: " + p.getWorld().loadedChunks), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "active chunks: " + p.getWorld().activeChunks), true));

        itemY[0] -= verticalSpace;

        boolean onlyWorldUpdate = false;
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "world update time: " + decimal(SandWizard.updateTime) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.updateTime));

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "world render time: " + decimal(SandWizard.renderTime) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.renderTime));

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "combined world time: " + decimal(SandWizard.updateTime + SandWizard.renderTime) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.updateTime + SandWizard.renderTime));

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "delta time: " + decimal(SandWizard.deltaTimeMs) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.deltaTimeMs));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "player pos: " + decimal(p.getPosition().x) + ", " + decimal(p.getPosition().y)), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "player chunk pos: " + World.getChunkPos((int) p.getPosition().x) + ", " + World.getChunkPos((int) p.getPosition().y)), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "player vel: " + decimal(p.getxVel()) + ", " + decimal(p.getyVel())), true));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "surface biome noise: " + decimal(p.getWorld().worldGeneration.getSurfaceBiomeNoise(World.getChunkPos((int) p.getPosition().x)))), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "surface biome: " + BiomeType.REGISTRY.getEntryName(p.getWorld().worldGeneration.getSurfaceBiomeType(World.getChunkPos((int) p.getPosition().x)))), true));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "cave biome noise: " + decimal(p.getWorld().worldGeneration.getCaveBiomeNoise(World.getChunkPos((int) p.getPosition().x), World.getChunkPos((int) p.getPosition().y)))), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "cave biome: " + BiomeType.REGISTRY.getEntryName(p.getWorld().worldGeneration.getCaveBiomeType(World.getChunkPos((int) p.getPosition().x), World.getChunkPos((int) p.getPosition().y)))), true));
    }

    private void addDebugRenderItem(float[] itemY, DebugRenderItem debugRenderItem) {
        addDebugRenderItem(itemY, 10, debugRenderItem);
    }

    private void addDebugRenderItem(float[] itemY, float heightMargin, DebugRenderItem debugRenderItem) {
        debugRenderItems.add(debugRenderItem);
        itemY[0] -= debugRenderItem.h + heightMargin;
    }

    private LineGraphRenderer getLineGraphRenderer(float itemX, float[] itemY, LineGraphRenderer.ValueSupplier valueSupplier) {
        return new LineGraphRenderer(this, itemX, itemY[0], 300, 100, false, valueSupplier, new FloatRange(0, 16), 500, false);
    }

    private TextRenderItem getTextRenderItem(float itemX, float[] itemY, TextRenderItem.TextSupplier textSupplier, boolean worldUpdate) {
        String text = textSupplier.get(player);

        guiManager.getGlyphLayout().setText(guiManager.getFont(), text);
        float textWidth = guiManager.getGlyphLayout().width;
        float textHeight = guiManager.getGlyphLayout().height;

        return new TextRenderItem(this, itemX, itemY[0], textWidth, textHeight, worldUpdate, textSupplier, 1);
    }

    private String decimal(float num) {
        return decimalFormat.format(num);
    }

    public void worldUpdate() {
        for (DebugRenderItem renderItem : debugRenderItems) {
            renderItem.worldUpdate(player);
        }
    }

    @Override
    public void render() {

        for (DebugRenderItem renderItem : debugRenderItems) {
            renderItem.renderUpdate(player);
            renderItem.render();
        }
    }
}
