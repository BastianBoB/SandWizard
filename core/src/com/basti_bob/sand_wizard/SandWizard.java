package com.basti_bob.sand_wizard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.FunctionRunTime;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.WorldRenderer;
import com.basti_bob.sand_wizard.world_saving.ChunkSaver;

public class SandWizard extends ApplicationAdapter {

    private OrthographicCamera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private Player player;

    public static boolean renderChunkBoarder;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        world = new World();
        worldRenderer = new WorldRenderer(world, camera);
        player = new Player(world, 0, 0);

    }

    @Override
    public void render() {
        //System.out.println("fps: " + Gdx.graphics.getFramesPerSecond());

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.position.lerp(new Vector3(player.getPosition().scl(WorldConstants.CELL_SIZE), 0), 0.5f);

        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.moveBy(-2, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.moveBy(2, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.moveBy(0, 2);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.moveBy(0, -2);

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.zoom *= 0.99;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.zoom *= 1.01;


        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            SandWizard.renderChunkBoarder = !SandWizard.renderChunkBoarder;
        }

        camera.update();
        player.update();

        world.update();
        worldRenderer.render(player);

//        FunctionRunTime.timeFunction("updating " + world.numActiveChunks + "/" + world.chunks.size() + " chunks", () -> world.update());
//        FunctionRunTime.timeFunction("rendering world", () -> worldRenderer.render(player));
    }


    @Override
    public void dispose() {
    }
}
