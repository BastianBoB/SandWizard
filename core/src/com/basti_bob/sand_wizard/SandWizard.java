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
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.debug.DebugScreen;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.FunctionRunTime;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_rendering.WorldRenderer;
import com.basti_bob.sand_wizard.world_generation.ChunkGenerator;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SandWizard extends ApplicationAdapter {

    private OrthographicCamera camera;

    public World world;
    public WorldRenderer worldRenderer;
    public Player player;

    private DebugScreen debugScreen;

    public static boolean renderChunkBoarder;

    private float accumulatedTime;
    private final float fixedDeltaTime = 1.0f / 60f; // 60 FPS

    public static int updateTimes = 0;


    public float freeMemory, maxMemory, updateTime, renderTime;
    public boolean isUpdating = true;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        world = new World();
        worldRenderer = new WorldRenderer(world, camera);
        player = new Player(world, 0, world.worldGeneration.getTerrainHeight(0));

        world.test();

        this.debugScreen = new DebugScreen();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        Gdx.graphics.getDeltaTime();

        float speed = WorldConstants.PLAYER_SPEED;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.xVel = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.xVel = speed;

        if (WorldConstants.PLAYER_FREE_MOVE) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) player.yVel = speed;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) player.yVel = -speed;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.onGround) player.jump();
        }

        float zoom = 1 * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.zoom *= 1 - zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.zoom *= 1 + zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.zoom = 1;


        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            renderChunkBoarder = !renderChunkBoarder;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            world.setCell(CellType.FIRE, 97, 100);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Y)) {
            fixedUpdate(fixedDeltaTime);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            fixedUpdate(fixedDeltaTime);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            isUpdating = !isUpdating;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            world.setCell(CellType.GLOWBLOCK, (int) player.nx, (int) player.ny);
        }

        accumulatedTime += deltaTime;

        while (accumulatedTime >= fixedDeltaTime) {
            if (isUpdating) {
                fixedUpdate(fixedDeltaTime);
            }
            accumulatedTime -= fixedDeltaTime;
        }
        renderGame();
    }

    public void fixedUpdate(float deltaTime) {
        updateTimes++;

        if(updateTimes % 1800 == 0) {
            world.test();
        }

        updateTime = FunctionRunTime.timeFunction(() -> world.update());

        player.update(deltaTime);


        camera.position.lerp(new Vector3(player.getHeadPosition().scl(WorldConstants.CELL_SIZE), 0), 0.2f);

        if (updateTimes % 5 == 0) {
            Runtime runtime = Runtime.getRuntime();
            freeMemory = runtime.freeMemory() >> 20;
            maxMemory = runtime.maxMemory() >> 20;
        }

        camera.update();
    }

    public void renderGame() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderTime = FunctionRunTime.timeFunction(() -> worldRenderer.render(player));

        player.render(camera);
        debugScreen.render(this);
    }


    @Override
    public void dispose() {
    }
}
