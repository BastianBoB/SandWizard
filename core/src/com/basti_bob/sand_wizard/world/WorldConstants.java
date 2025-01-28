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

    public static final int PLAYER_CHUNK_RENDER_RADIUS_X = 11;
    public static final int PLAYER_CHUNK_RENDER_RADIUS_Y = 7;

    public static final int PLAYER_CHUNK_UPDATE_RADIUS_X = 15;
    public static final int PLAYER_CHUNK_UPDATE_RADIUS_Y = 11;

    public static final int PLAYER_CHUNK_LOAD_RADIUS_X = 26;
    public static final int PLAYER_CHUNK_LOAD_RADIUS_Y = 22;
    public static final int PLAYER_CHUNK_UNLOAD_RADIUS_X = 41;
    public static final int PLAYER_CHUNK_UNLOAD_RADIUS_Y = 37;

    public static final int CHUNK_POOL_SIZE = (int) (1.2f * (PLAYER_CHUNK_UNLOAD_RADIUS_X * 2 + 1) * (PLAYER_CHUNK_UNLOAD_RADIUS_Y * 2 + 1));
}
