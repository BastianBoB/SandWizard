package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.math.Vector2;

public class WorldConstants {

    public static final Vector2 GRAVITY = new Vector2(0, -0.5f);

    public static final int CHUNK_SIZE = 16;
    public static final int CELL_SIZE = 4;

    public static final int M = 2;

    public static final int PLAYER_CHUNK_RENDER_RADIUS_X = 8 * M;
    public static final int PLAYER_CHUNK_RENDER_RADIUS_Y = 5 * M;

    public static final int PLAYER_CHUNK_LOAD_RADIUS_X = 12 * M;
    public static final int PLAYER_CHUNK_LOAD_RADIUS_Y = 8 * M;

}
