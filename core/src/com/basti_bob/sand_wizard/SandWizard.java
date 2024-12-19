package com.basti_bob.sand_wizard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.debug.DebugScreen;
import com.basti_bob.sand_wizard.entities.fireworks.BasicRisingLightFireworkEntity;
import com.basti_bob.sand_wizard.input.InputHandler;
import com.basti_bob.sand_wizard.items.ItemStack;
import com.basti_bob.sand_wizard.items.ItemType;
import com.basti_bob.sand_wizard.items.crafting.tool_station.ToolStationInventory;
import com.basti_bob.sand_wizard.items.crafting.tool_station.ToolStationScreen;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.registry.RegistryLoader;
import com.basti_bob.sand_wizard.rendering.GuiManager;
import com.basti_bob.sand_wizard.rendering.ItemRenderer;
import com.basti_bob.sand_wizard.util.FunctionRunTime;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.world_rendering.WorldRenderer;

public class SandWizard extends ApplicationAdapter {

    private OrthographicCamera worldCamera;
    public static GuiManager guiManager;
    public static ItemRenderer itemRenderer;
    public static InputHandler inputHandler;

    public World world;
    public WorldRenderer worldRenderer;
    public static Player player;

    private DebugScreen debugScreen;

    public static boolean renderChunkBoarder, lightingEnabled;
    private static float accumulatedTime;
    private static final float FIXED_DELTA_TIME = 1.0f / 60f; // 60 FPS

    public static int updateTimes = 0;

    public static float freeMemory, maxMemory;
    public static float deltaTimeMs, updateTime, renderTime;
    public static boolean renderDebugScreen = true;
    public static boolean isUpdating = true;

    public static long previousTime = -1;

    public static ToolStationInventory toolStationInventory;
    public static ToolStationScreen toolStationScreen;

    @Override
    public void create() {

        RegistryLoader.loadRegistries();

        //RegistryTreePrint.printRegistryTree();

        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldCamera.update();

        guiManager = new GuiManager();
        guiManager.update();

        itemRenderer = new ItemRenderer();
        inputHandler = new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        world = new World();
        worldRenderer = new WorldRenderer(world, worldCamera);
        player = new Player(world, 0, 800);
        world.addEntity(player);

        //world.entities.add(new Spider(world, 0, 800));

        this.debugScreen = new DebugScreen(player);

        toolStationInventory = new ToolStationInventory();
        toolStationScreen = new ToolStationScreen(toolStationInventory);
    }


    @Override
    public void render() {
        if (previousTime == -1) previousTime = System.nanoTime();

        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - previousTime) / 1e9f;
        previousTime = currentTime;

        deltaTimeMs = deltaTime * 1000f;

        inputHandler.update();

        float speed = WorldConstants.PLAYER_SPEED;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.setxVel(-speed);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.setxVel(speed);

        if (WorldConstants.PLAYER_FREE_MOVE) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) player.setyVel(speed);
            if (Gdx.input.isKeyPressed(Input.Keys.S)) player.setyVel(-speed);
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.onGround) player.jump();
        }

        float zoom = 1 * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) worldCamera.zoom *= 1 - zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) worldCamera.zoom *= 1 + zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) worldCamera.zoom = 1;


        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            renderChunkBoarder = !renderChunkBoarder;
            //world.entities.add(new Spider(world, player.getPosition().x, player.getPosition().y));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (player.openedInventoryScreen)
                player.closeInventoryScreen();
            else
                player.onlyPlayerInventoryScreen.playerOpenedScreen(player);

        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            if (player.openedInventoryScreen)
                player.closeInventoryScreen();
            else
                toolStationScreen.playerOpenedScreen(player);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            player.inventory.getItemStorage().receiveItemStack(new ItemStack(ItemType.SWORD, 1));
            player.inventory.getItemStorage().receiveItemStack(new ItemStack(ItemType.MARBLE, 99));
            player.inventory.getItemStorage().receiveItemStack(new ItemStack(ItemType.WOOD, 99));
            player.inventory.getItemStorage().receiveItemStack(new ItemStack(ItemType.STONE, 99));
            player.inventory.getItemStorage().receiveItemStack(new ItemStack(ItemType.ICE, 99));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            world.setCell(CellType.GAS.FIRE, 97, 100);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Y)) {
            fixedUpdate(FIXED_DELTA_TIME);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            fixedUpdate(FIXED_DELTA_TIME);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            isUpdating = !isUpdating;
        }

//        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
//            world.addExplosion(new Explosion(world, (int) player.getPosition().x, (int) player.getPosition().y, 32, 2000));
//        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            renderDebugScreen = !renderDebugScreen;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            //world.test()

            world.addEntity(new BasicRisingLightFireworkEntity(world, player.getPosition().x, player.getPosition().y, Color.RED));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            lightingEnabled = !lightingEnabled;
        }

        accumulatedTime += deltaTime; //Gdx.graphics.getDeltaTime();

        if (accumulatedTime >= FIXED_DELTA_TIME) {
            fixedUpdate(FIXED_DELTA_TIME);

            accumulatedTime -= FIXED_DELTA_TIME;
        }

        worldCamera.update();
        guiManager.update();

        renderGame();
    }

    public void fixedUpdate(float deltaTime) {

        updateTimes++;
        updateTime = FunctionRunTime.timeFunction(() -> world.update());

        if (updateTimes % 5 == 0) {
            Runtime runtime = Runtime.getRuntime();
            freeMemory = runtime.freeMemory() >> 20;
            maxMemory = runtime.maxMemory() >> 20;
        }

        worldCamera.position.lerp(new Vector3(player.getHeadPosition().scl(WorldConstants.CELL_SIZE), 0), 0.2f);


        debugScreen.worldUpdate();
    }

    public void renderGame() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderTime = FunctionRunTime.timeFunction(() -> worldRenderer.render(player));

        if (renderDebugScreen)
            debugScreen.render();
    }


    @Override
    public void dispose() {
    }
}
