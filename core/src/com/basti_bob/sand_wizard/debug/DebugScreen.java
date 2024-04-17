package com.basti_bob.sand_wizard.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.world.World;

public class DebugScreen {

    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera hudCamera;

    public DebugScreen() {
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2.0f, hudCamera.viewportHeight / 2.0f, 1.0f);
        spriteBatch = new SpriteBatch();

        font = new BitmapFont();
    }

    public void render(SandWizard sandWizard) {
        World world = sandWizard.world;
        Player player = sandWizard.player;

        hudCamera.update();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 50, hudCamera.viewportHeight - 50);
        font.draw(spriteBatch, "existing chunks: " + world.chunkProvider.chunks.size(), 50, hudCamera.viewportHeight - 80);
        font.draw(spriteBatch, "loaded chunks: " + world.activeChunks, 50, hudCamera.viewportHeight - 110);

        font.draw(spriteBatch, "player pos: " + String.format("%.1f", player.nx) + ", " + String.format("%.1f", player.ny), 50, hudCamera.viewportHeight - 140);
        font.draw(spriteBatch, "player vel: " + String.format("%.1f", player.xVel) + ", " + String.format("%.1f", player.yVel), 50, hudCamera.viewportHeight - 170);

        font.draw(spriteBatch, "memory usage: " + (int) sandWizard.freeMemory + " / " + (int) sandWizard.maxMemory + "MB | " + String.format("%.1f", 100 * (sandWizard.freeMemory / sandWizard.maxMemory)) + " %", 50, hudCamera.viewportHeight - 200);

        font.draw(spriteBatch, "world update time: " + String.format("%.1f", sandWizard.updateTime) + "ms", 50, hudCamera.viewportHeight - 230);
        font.draw(spriteBatch, "world render time: " + String.format("%.1f", sandWizard.renderTime) + "ms", 50, hudCamera.viewportHeight - 260);


        spriteBatch.end();
    }
}
