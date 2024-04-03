package com.basti_bob.sand_wizard.world.world_rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

public class WorldRenderer {

    private final World world;
    private final Camera camera;
    private final ShaderProgram shader;
    private final ShapeRenderer shapeRenderer;

    public WorldRenderer(World world, Camera camera) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.camera = camera;

        ShaderProgram.pedantic = true;
        Gdx.gl.glEnable(GL32.GL_VERTEX_PROGRAM_POINT_SIZE);
        shader = generateShader();
    }

    private ShaderProgram generateShader() {

        final String vertex_shader = Gdx.files.internal("shaders/vertex_shader.glsl").readString();
        final String fragment_shader = Gdx.files.internal("shaders/fragment_shader.glsl").readString();

        return new ShaderProgram(vertex_shader, fragment_shader);
    }

    public void render(Player player) {

        Array2D<Chunk> chunks = player.getRenderingChunks();

        shader.bind();
        shader.setUniformMatrix("u_proj", camera.combined);

        for (Chunk chunk : chunks.getArray()) {
            if (chunk == null) continue;

            chunk.mesh.render(shader, GL20.GL_POINTS);
        }

        if (SandWizard.renderChunkBoarder)
            chunkActiveDebugSquares(chunks);
    }

    private void chunkActiveDebugSquares(Array2D<Chunk> chunks) {
        float chunkSize = WorldConstants.CHUNK_SIZE;


        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int chunkI = 0; chunkI < chunks.rows; chunkI++) {
            for (int chunkJ = 0; chunkJ < chunks.cols; chunkJ++) {

                Chunk chunk = chunks.get(chunkI, chunkJ);
                if (chunk == null) continue;

                float chunkRenderX = (chunk.posX * chunkSize) * WorldConstants.CELL_SIZE;
                float chunkRenderY = (chunk.posY * chunkSize) * WorldConstants.CELL_SIZE;

                renderChunkActiveDebugSquare(chunk, chunkRenderX, chunkRenderY);
            }
        }
        shapeRenderer.end();
    }

    private final Color activeColor = new Color(1f, 1f, 1f, 1);
    private final Color inActiveColor = new Color(0.1f, 0.1f, 0.1f, 1);


    public void renderChunkActiveDebugSquare(Chunk chunk, float chunkRenderX, float chunkRenderY) {

        shapeRenderer.setColor(chunk.isActive() ? activeColor : inActiveColor);

        float rectSize = WorldConstants.CHUNK_SIZE * WorldConstants.CELL_SIZE - 2;
        shapeRenderer.rect(chunkRenderX, chunkRenderY, rectSize, rectSize);
    }
}

