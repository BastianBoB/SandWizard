package com.basti_bob.sand_wizard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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


    long totalUpdateTime = 0;
    int updateCount = 0;

    @Override
    public void render() {
        updateCount++;
       System.out.println("fps: " + Gdx.graphics.getFramesPerSecond());

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.position.lerp(new Vector3(player.getPosition().scl(WorldConstants.CELL_SIZE), 0), 0.5f);
        camera.update();

        player.update();

        long start = System.nanoTime();

        world.update();

        long time = (System.nanoTime() - start);
        totalUpdateTime += time;

        //System.out.println("updating " +  world.chunks.size() + " chunks took:" + time / 1e6 + " ms" + "    avg: " + totalUpdateTime / updateCount / 1e6 + " ms");


        start = System.nanoTime();



        worldRenderer.render(player);

        //System.out.println("rendering world took: " + (System.nanoTime() - start) / 1e6 + " ms");

        if(Gdx.input.isKeyPressed(Input.Keys.A)) player.moveBy(-2, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.D)) player.moveBy(2, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.W)) player.moveBy(0, 2);
        if(Gdx.input.isKeyPressed(Input.Keys.S)) player.moveBy(0, -2);


//        int worldSandCount = 0;
//
//        for(Chunk chunk : world.chunks) {
//
//            for(int i = 0; i <32; i++){
//                for(int j = 0; j < 32; j++) {
//                    if(chunk.getGrid().get(i, j) instanceof Sand) worldSandCount++;
//                }
//            }
//        }
//
//        System.out.println(Sand.instanceCounter + ", " + worldSandCount);
    }


    @Override
    public void dispose() {
    }
}
