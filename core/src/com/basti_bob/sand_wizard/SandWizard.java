package com.basti_bob.sand_wizard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.debug.DebugScreen;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.registry.RegistryLoader;
import com.basti_bob.sand_wizard.registry.RegistryTreePrint;
import com.basti_bob.sand_wizard.util.FunctionRunTime;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.explosions.Explosion;
import com.basti_bob.sand_wizard.world.world_rendering.WorldRenderer;

public class SandWizard extends ApplicationAdapter {

    private OrthographicCamera camera;

    public World world;
    public WorldRenderer worldRenderer;
    public static Player player;

    private DebugScreen debugScreen;

    public static boolean renderChunkBoarder, lightingEnabled;
    private float accumulatedTime;
    private final float fixedDeltaTime = 1.0f / 60f; // 60 FPS

    public static int updateTimes = 0;

    public static float freeMemory, maxMemory;
    public static float deltaTimeMs, updateTime, renderTime;
    public static boolean renderDebugScreen = true;
    public static boolean isUpdating = true;

    public static long previousTime = -1;

    @Override
    public void create() {
        RegistryLoader.loadRegistries();
        RegistryTreePrint.printRegistryTree();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        world = new World();
        worldRenderer = new WorldRenderer(world, camera);
        player = new Player(world, 0, -1000);

        //world.test();

        this.debugScreen = new DebugScreen(player);
    }


    @Override
    public void render() {
        if (previousTime == -1) previousTime = System.nanoTime();

        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - previousTime) / 1e9f;
        previousTime = currentTime;

        deltaTimeMs = deltaTime * 1000f;

        if (deltaTimeMs > 13) {
            System.out.println(deltaTimeMs + ", " + (updateTime + renderTime));
        }

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


        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            renderChunkBoarder = !renderChunkBoarder;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            world.setCell(CellType.GAS.FIRE, 97, 100);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            world.setCell(CellType.SOLID.GLOWBLOCK.createCell(), (int) player.nx, (int) player.ny);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            world.addExplosion(new Explosion(world, (int) player.nx, (int) player.ny, 32, 2000));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            renderDebugScreen = !renderDebugScreen;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            world.test();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            lightingEnabled = !lightingEnabled;
        }


        accumulatedTime += deltaTime; //Gdx.graphics.getDeltaTime();

        if (accumulatedTime >= fixedDeltaTime) {
            fixedUpdate(fixedDeltaTime);

            accumulatedTime -= fixedDeltaTime;
        }
        renderGame();
    }

    public void fixedUpdate(float deltaTime) {
        //world.setCell(CellType.FIRE, (int) player.nx, (int) player.ny);

        updateTimes++;

        if (updateTimes % 150 == 0) {
           // world.test();
        }

        //world.addExplosion(new Explosion(world, (int) player.nx, (int) player.ny, 32, 2000));

        updateTime = FunctionRunTime.timeFunction(() -> world.update());

        player.update(deltaTime);


        camera.position.lerp(new Vector3(player.getHeadPosition().scl(WorldConstants.CELL_SIZE), 0), 0.2f);

        if (updateTimes % 5 == 0) {
            Runtime runtime = Runtime.getRuntime();
            freeMemory = runtime.freeMemory() >> 20;
            maxMemory = runtime.maxMemory() >> 20;
        }

        camera.update();

        debugScreen.worldUpdate();
    }

    public void renderGame() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderTime = FunctionRunTime.timeFunction(() -> worldRenderer.render(player));

        player.render(camera);

        if (renderDebugScreen)
            debugScreen.render();
    }


    @Override
    public void dispose() {
    }
}
