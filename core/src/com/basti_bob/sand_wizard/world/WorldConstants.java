package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.math.Vector2;

public class WorldConstants {

    public static final int CHUNK_SIZE = 32;
    public static final int CELL_SIZE = 3;

    public static final int NUM_MESH_VERTEX_VALUES = 6;
    public static final int NUM_MESH_VERTICES = CHUNK_SIZE * CHUNK_SIZE * NUM_MESH_VERTEX_VALUES;

    public static final boolean SAVE_CHUNK_DATA = false;
    public static final boolean PLAYER_FREE_MOVE = true;
    public static final float PLAYER_SPEED = 2.5f;

    public static final float START_TEMPERATURE = 30f;

    public static final Vector2 GRAVITY = new Vector2(0, -0.3f);

    public static final float M = 1f;

    public static final int PLAYER_CHUNK_RENDER_RADIUS_X = 11;
    public static final int PLAYER_CHUNK_RENDER_RADIUS_Y = 7;
//
//    public static final int PLAYER_CHUNK_RENDER_RADIUS_X = (int) (17 * M);
//    public static final int PLAYER_CHUNK_RENDER_RADIUS_Y = (int) (13 * M);

//    public static final int PLAYER_CHUNK_UPDATE_RADIUS_X = 16;
//    public static final int PLAYER_CHUNK_UPDATE_RADIUS_Y = 9;

    public static final int PLAYER_CHUNK_LOAD_RADIUS_X = (int) (14 * M);
    public static final int PLAYER_CHUNK_LOAD_RADIUS_Y = (int) (10 * M);

    public static final int PLAYER_CHUNK_UNLOAD_RADIUS_X = (int) (17 * M);
    public static final int PLAYER_CHUNK_UNLOAD_RADIUS_Y = (int) (13 * M);
}
