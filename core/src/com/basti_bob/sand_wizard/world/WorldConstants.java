package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.math.Vector2;

public class WorldConstants {

    public static final Vector2 GRAVITY = new Vector2(0, -0.3f);
    public static final float AIR_FRICTION = 0.7f;

    public static final boolean SAVE_CHUNK_DATA = true;

    public static final int CHUNK_SIZE = 64;
    public static final int CELL_SIZE = 3;

    public static final float M = 0.5f;

    public static final int PLAYER_CHUNK_RENDER_RADIUS_X = (int) (14 * M);
    public static final int PLAYER_CHUNK_RENDER_RADIUS_Y = (int) (8 * M);

    public static final int PLAYER_CHUNK_LOAD_RADIUS_X = (int) (14 * M);
    public static final int PLAYER_CHUNK_LOAD_RADIUS_Y = (int) (8 * M);


}
