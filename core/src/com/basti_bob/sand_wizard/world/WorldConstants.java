package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.math.Vector2;

public class WorldConstants {

    public static final Vector2 GRAVITY = new Vector2(0, -0.5f);

    public static final int CHUNK_SIZE = 32;
    public static final int CELL_SIZE = 4;

    public static final float M = 1f;

    public static final int PLAYER_CHUNK_RENDER_RADIUS_X = (int) (8 * M);
    public static final int PLAYER_CHUNK_RENDER_RADIUS_Y = (int) (5 * M);

    public static final int PLAYER_CHUNK_LOAD_RADIUS_X = (int) (8 * M);
    public static final int PLAYER_CHUNK_LOAD_RADIUS_Y = (int) (5 * M);

}
