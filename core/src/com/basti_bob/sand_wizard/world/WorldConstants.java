package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.math.Vector2;

public class WorldConstants {

    //WORLD
    public static final int CHUNK_SIZE = 32;
    public static final int CELL_SIZE = 3;
    public static final long WORLD_GENERATION_SEED = 123L;

    public static final float START_TEMPERATURE = 30f;
    public static final Vector2 GRAVITY = new Vector2(0, -0.2f);

    public static final int NUM_MESH_VERTEX_VALUES = 6;
    public static final int NUM_MESH_VERTICES = CHUNK_SIZE * CHUNK_SIZE * NUM_MESH_VERTEX_VALUES;

    //INVENTORY
    public static final int MAX_ITEM_COUNT = 99;

    //GENERAL
    public static final boolean SAVE_CHUNK_DATA = false;

    //ENTITIES
    public static final boolean PLAYER_FREE_MOVE = true;
    public static final float PLAYER_SPEED = 3f;

    //RENDERING
    public static final boolean RENDER_ENTITY_HITBOX = true;


    //Chunks are loaded in an Ellipse (With no Zoom 23 * 15 chunks are visible)
    public static final class CHUNK_LOADING {
        private static final int BASE_WIDTH = 23;
        private static final int BASE_HEIGHT = 15;

        private static final int RENDER_RADIUS = 0;
        private static final int UPDATE_RADIUS = 3;
        private static final int LOAD_RADIUS = 8;
        private static final int UNLOAD_RADIUS = 20;


        public static final int RENDER_WIDTH = BASE_WIDTH + RENDER_RADIUS * 2;
        public static final int RENDER_HEIGHT = BASE_HEIGHT + RENDER_RADIUS * 2;

        public static final int UPDATE_WIDTH = BASE_WIDTH + UPDATE_RADIUS * 2;
        public static final int UPDATE_HEIGHT = BASE_HEIGHT + UPDATE_RADIUS * 2;

        public static final int LOAD_WIDTH = BASE_WIDTH + LOAD_RADIUS * 2;
        public static final int LOAD_HEIGHT = BASE_HEIGHT + LOAD_RADIUS * 2;

        public static final int UNLOAD_WIDTH = BASE_WIDTH + UNLOAD_RADIUS * 2;
        public static final int UNLOAD_HEIGHT = BASE_HEIGHT + UNLOAD_RADIUS * 2;

        public static final int CHUNK_POOL_SIZE = UNLOAD_WIDTH * UNLOAD_HEIGHT;

    }
}
