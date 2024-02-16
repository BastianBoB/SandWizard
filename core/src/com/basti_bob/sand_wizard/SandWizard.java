package com.basti_bob.sand_wizard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.WorldRenderer;

public class SandWizard extends ApplicationAdapter {

    private OrthographicCamera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private Player player;

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
        System.out.println("fps: " + Gdx.graphics.getFramesPerSecond());


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        //player.x += 0.9;
        //player.y += 0.9;

        camera.position.lerp(new Vector3(player.getPosition().scl(WorldConstants.CELL_SIZE), 0), 0.5f);

        camera.update();

        player.update();
            world.update();

        worldRenderer.render(player);
    }


    @Override
    public void dispose() {
    }
}
