package com.basti_bob.sand_wizard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.FunctionRunTime;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_rendering.WorldRenderer;

import java.util.ArrayList;
import java.util.List;

public class SandWizard extends ApplicationAdapter {

    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;

    private World world;
    private WorldRenderer worldRenderer;
    private Player player;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public static boolean renderChunkBoarder;

    private float accumulatedTime;
    private final float fixedDeltaTime = 1.0f / 60f; // 60 FPS
    private final int maxUpdatesPerFrame = 3;

    public static int updateTimes = 0;


    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        world = new World();
        worldRenderer = new WorldRenderer(world, camera);
        player = new Player(world, -200, 150);

        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2.0f, hudCamera.viewportHeight / 2.0f, 1.0f);
        spriteBatch = new SpriteBatch();

        font = new BitmapFont();

    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulatedTime += deltaTime;

        camera.position.lerp(new Vector3(player.getPosition().scl(WorldConstants.CELL_SIZE), 0), 0.05f);

        float speed = 2f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.xVel = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.xVel = speed;

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.onGround) player.jump();

        float zoom = 1 * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.zoom *= 1 - zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.zoom *= 1 + zoom;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.zoom = 1;


        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            SandWizard.renderChunkBoarder = !SandWizard.renderChunkBoarder;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (accumulatedTime >= fixedDeltaTime) {
            FunctionRunTime.timeFunction("Fixed Update", () -> fixedUpdate(fixedDeltaTime));
            accumulatedTime -= fixedDeltaTime;
            //renderGame();
        }

        renderGame();
    }

    public void fixedUpdate(float deltaTime) {
        updateTimes++;

        world.update();
        player.update(deltaTime);

        if(updateTimes % 20 == 0) {
            Runtime runtime = Runtime.getRuntime();
            freeMemory = runtime.freeMemory() >> 20;
            maxMemory = runtime.maxMemory() >> 20;
        }
    }

    float freeMemory, maxMemory;

    public void renderGame() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        worldRenderer.render(player);
        player.render(camera);

        hudCamera.update();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 50, hudCamera.viewportHeight - 50);
        font.draw(spriteBatch, "existing chunks: " + world.chunks.size(), 50, hudCamera.viewportHeight - 80);
        font.draw(spriteBatch, "loaded chunks: " + world.activeChunks, 50, hudCamera.viewportHeight - 110);

        font.draw(spriteBatch, "player pos: " + String.format("%.1f", player.nx) + ", " + String.format("%.1f", player.ny), 50, hudCamera.viewportHeight - 140);
        font.draw(spriteBatch, "player vel: " + String.format("%.1f", player.xVel) + ", " + String.format("%.1f", player.yVel), 50, hudCamera.viewportHeight - 170);

        font.draw(spriteBatch, "memory usage: " + (int)freeMemory + " / " + (int) maxMemory + "MB | " + String.format("%.1f", 100 * (freeMemory/maxMemory)) + " %", 50, hudCamera.viewportHeight - 200);
        spriteBatch.end();
    }




    @Override
    public void dispose() {
    }
}
