package com.basti_bob.sand_wizard.world.world_rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.BufferUtils;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.lighting.Light;

import java.nio.FloatBuffer;
import java.util.List;

public class WorldRenderer {

    private final World world;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private final ShapeRenderer shapeRenderer;
    private FloatBuffer lightBuffer;

    public WorldRenderer(World world, OrthographicCamera camera) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.camera = camera;

        ShaderProgram.pedantic = true;
        Gdx.gl.glEnable(GL32.GL_VERTEX_PROGRAM_POINT_SIZE);
        shader = generateShader();

    }

    private ShaderProgram generateShader() {

        final String vertexShader = Gdx.files.internal("shaders/vertex_shader.glsl").readString();
        final String fragmentShader = Gdx.files.internal("shaders/fragment_shader.glsl").readString();

        return new ShaderProgram(vertexShader, fragmentShader);
    }

    public void render(Player player) {

        Array2D<Chunk> chunks = player.getRenderingChunks();

        shader.bind();
        shader.setUniformMatrix("u_proj", camera.combined);
        shader.setUniformf("u_pointSize", WorldConstants.CELL_SIZE / camera.zoom * 1.05f);
        shader.setUniformi("u_cellSize", WorldConstants.CELL_SIZE);

        int ssbo = Gdx.gl31.glGenBuffer();
        Gdx.gl31.glBindBuffer(GL31.GL_SHADER_STORAGE_BUFFER, ssbo);

        int lightsIndex = Gdx.gl31.glGetUniformBlockIndex(shader.getHandle(), "lights_data");
        Gdx.gl31.glUniformBlockBinding(shader.getHandle(), lightsIndex, 1);

        int lastNumLights = -1;
        for (Chunk chunk : chunks.getArray()) {
            if (chunk == null) continue;

            List<Light> affectedLights = chunk.affectedLights;
            int numAffectedLights = affectedLights.size();

            if(numAffectedLights != lastNumLights) {
                lightBuffer = BufferUtils.newFloatBuffer(numAffectedLights * Light.NUM_FLOAT_DATA);
            }

            lightBuffer.clear();

            if(lastNumLights != 0 && numAffectedLights != 0) {
                for (int i = 0; i < numAffectedLights; i++) {
                    Light light = (affectedLights.get(i));
                    if (light == null) continue;

                    lightBuffer.put(light.getData());
                }

                lightBuffer.flip();

                Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, numAffectedLights * Light.NUM_FLOAT_DATA * 4, lightBuffer, GL31.GL_DYNAMIC_DRAW);
                Gdx.gl31.glBindBufferBase(GL31.GL_SHADER_STORAGE_BUFFER, 0, ssbo);
            }

            lastNumLights = numAffectedLights;

            chunk.mesh.render(shader, GL20.GL_POINTS);
        }

        Gdx.gl31.glDeleteBuffer(ssbo);

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

