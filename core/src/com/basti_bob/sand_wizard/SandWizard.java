package com.basti_bob.sand_wizard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_rendering.WorldRenderer;

public class SandWizard extends ApplicationAdapter {

    private OrthographicCamera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private Player player;

    public static boolean renderChunkBoarder;

    private float accumulatedTime;
    private final float fixedDeltaTime = 1.0f / 60.0f; // 60 FPS

    public static int updateTimes = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        world = new World();
        worldRenderer = new WorldRenderer(world, camera);
        player = new Player(world, 0, 150);

    }

    @Override
    public void render() {
        updateTimes++;

        System.out.println("fps: " + Gdx.graphics.getFramesPerSecond());

        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulatedTime += deltaTime;

        camera.position.lerp(new Vector3(player.getPosition().scl(WorldConstants.CELL_SIZE), 0), 0.05f);

        float speed = 2f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.xVel = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.xVel = speed;

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.onGround) player.jump();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.zoom *= 0.99;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.zoom *= 1.01;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.zoom = 1;


        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            SandWizard.renderChunkBoarder = !SandWizard.renderChunkBoarder;
        }

        while (accumulatedTime >= fixedDeltaTime) {
            fixedUpdate(fixedDeltaTime);
            accumulatedTime -= fixedDeltaTime;
        }

        renderGame();

    }

    public void fixedUpdate(float deltaTime) {
        camera.update();
        world.update();
        player.update(deltaTime);
    }

    public void renderGame() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        worldRenderer.render(player);
        player.render(camera);
    }




    @Override
    public void dispose() {
    }
}
