package com.basti_bob.sand_wizard.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.range.FloatRange;
import com.basti_bob.sand_wizard.world.World;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebugScreen {

    protected final Player player;
    protected final SpriteBatch spriteBatch;
    protected final BitmapFont font;
    protected final GlyphLayout glyphLayout;
    protected final ShapeRenderer shapeRenderer;
    private final OrthographicCamera hudCamera;
    private final List<DebugRenderItem> debugRenderItems = new ArrayList<>();

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));

    public DebugScreen(Player player) {
        this.player = player;

        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2.0f, hudCamera.viewportHeight / 2.0f, 1.0f);

        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        float verticalSpace = 15;
        float itemX = 50;
        float[] itemY = {hudCamera.viewportHeight - 50};

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "fps: " + Gdx.graphics.getFramesPerSecond()), false));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "memory usage: " + (int) SandWizard.freeMemory + "MB / " + (int) SandWizard.maxMemory + "MB | " + decimal(100 * (SandWizard.freeMemory / SandWizard.maxMemory)) + " %"), false));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "generated chunks: " + p.getWorld().chunkProvider.chunks.size()), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "loaded chunks: " + p.getWorld().loadedChunks), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "active chunks: " + p.getWorld().activeChunks), true));

        itemY[0] -= verticalSpace;

        int maxLength = 500;
        int lineGraphWidth = 300;
        boolean onlyWorldUpdate = false;
        boolean connectedLines = true;
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "world update time: " + decimal(SandWizard.updateTime) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.updateTime));

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "world render time: " + decimal(SandWizard.renderTime) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.renderTime));

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "combined world time: " + decimal(SandWizard.updateTime + SandWizard.renderTime) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.updateTime + SandWizard.renderTime));

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "delta time: " + decimal(SandWizard.deltaTimeMs) + "ms"), onlyWorldUpdate));
        addDebugRenderItem(itemY, 20, getLineGraphRenderer(itemX, itemY, p -> SandWizard.deltaTimeMs));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "player pos: " + decimal(p.nx) + ", " + decimal(p.ny)), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "player chunk pos: " + World.getChunkPos((int) p.nx) + ", " + World.getChunkPos((int) p.ny)), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "player vel: " + decimal(p.xVel) + ", " + decimal(p.yVel)), true));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "surface biome noise: " + decimal(p.getWorld().worldGeneration.getSurfaceBiomeNoise(World.getChunkPos((int) p.nx)))), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "surface biome: " + p.getWorld().worldGeneration.getSurfaceBiomeType(World.getChunkPos((int) p.nx)).name), true));

        itemY[0] -= verticalSpace;

        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "cave biome noise: " + decimal(p.getWorld().worldGeneration.getCaveBiomeNoise(World.getChunkPos((int) p.nx), World.getChunkPos((int) p.ny)))), true));
        addDebugRenderItem(itemY, getTextRenderItem(itemX, itemY, (p -> "cave biome: " + p.getWorld().worldGeneration.getCaveBiomeType(World.getChunkPos((int) p.nx), World.getChunkPos((int) p.ny)).name), true));
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

        glyphLayout.setText(font, text);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

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

    public void render() {
        hudCamera.update();

        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        spriteBatch.setProjectionMatrix(hudCamera.combined);

        for (DebugRenderItem renderItem : debugRenderItems) {
            renderItem.renderUpdate(player);
            renderItem.render();
        }
    }
}
