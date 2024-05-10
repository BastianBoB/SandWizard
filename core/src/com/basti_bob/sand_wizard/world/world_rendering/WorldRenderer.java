package com.basti_bob.sand_wizard.world.world_rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.BufferUtils;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.ChunkColumnData;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.ChunkLight;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.Light;

import java.nio.FloatBuffer;
import java.util.*;

public class WorldRenderer {

    public static final int GL_FRAMEBUFFER_SRGB = 0x8DB9;

    private final World world;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private final ShapeRenderer shapeRenderer;

    private FloatBuffer worldLightBuffer = BufferUtils.newFloatBuffer(10000);
    private FloatBuffer[] chunkLightBuffers = new FloatBuffer[10000];

    public WorldRenderer(World world, OrthographicCamera camera) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.camera = camera;

        ShaderProgram.pedantic = true;
        Gdx.gl.glEnable(GL32.GL_VERTEX_PROGRAM_POINT_SIZE);
        Gdx.gl.glEnable(GL_FRAMEBUFFER_SRGB);

        shader = generateShader();

        for (int i = 0; i < chunkLightBuffers.length; i++) {
            chunkLightBuffers[i] = BufferUtils.newFloatBuffer(i * Light.NUM_FLOAT_DATA);
        }
    }

    private ShaderProgram generateShader() {
        final String vertexShader = Gdx.files.internal("shaders/vertex_shader.glsl").readString();
        final String fragmentShader = Gdx.files.internal("shaders/fragment_shader.glsl").readString();

        return new ShaderProgram(vertexShader, fragmentShader);
    }

    public void render(Player player) {

        Array2D<Chunk> chunks = player.getRenderingChunks();

        ChunkPos topLeftChunkPos = player.topLeftChunkPos;

        shader.bind();
        shader.setUniformMatrix("u_proj", camera.combined);
        shader.setUniformf("u_pointSize", WorldConstants.CELL_SIZE / camera.zoom * 1.05f);
        shader.setUniformi("u_cellSize", WorldConstants.CELL_SIZE);


        shader.setUniformi("u_dayTimeMinutes", world.updateTimes);
        shader.setUniform2fv("u_cameraPos", new float[]{camera.position.x / WorldConstants.CELL_SIZE, camera.position.y / WorldConstants.CELL_SIZE}, 0, 2);


        int worldLightSSBO = Gdx.gl31.glGenBuffer();
        Gdx.gl31.glBindBuffer(GL31.GL_SHADER_STORAGE_BUFFER, worldLightSSBO);
        Gdx.gl31.glBindBufferBase(GL31.GL_SHADER_STORAGE_BUFFER, 0, worldLightSSBO);
        setWorldLightsBuffer(chunks);

        int chunkLightSSBO = Gdx.gl31.glGenBuffer();
        Gdx.gl31.glBindBuffer(GL31.GL_SHADER_STORAGE_BUFFER, chunkLightSSBO);
        Gdx.gl31.glBindBufferBase(GL31.GL_SHADER_STORAGE_BUFFER, 1, chunkLightSSBO);

        for (int i = 0; i < chunks.rows; i++) {

            ChunkColumnData chunkColumnData = world.chunkProvider.chunkColumns.get(topLeftChunkPos.x + i);
            if (chunkColumnData == null) continue;

            shader.setUniform1fv("terrain_heights", chunkColumnData.terrainHeights, 0, WorldConstants.CHUNK_SIZE);

            for (int j = 0; j < chunks.cols; j++) {
                Chunk chunk = chunks.get(i, j);
                if (chunk == null) continue;

                modifyChunkLightBuffer(chunk, chunkLightSSBO);

                chunk.mesh.render(shader, GL20.GL_POINTS);
            }
        }

        Gdx.gl31.glDeleteBuffer(worldLightSSBO);
        Gdx.gl31.glDeleteBuffer(chunkLightSSBO);

        if (SandWizard.renderChunkBoarder)
            chunkActiveDebugSquares(chunks);
    }

    private void setWorldLightsBuffer(Array2D<Chunk> chunks) {
        int numLights = world.globalLights.size();
        int numLightFloats = numLights * Light.NUM_FLOAT_DATA;

        worldLightBuffer.clear();
        for (Light light : world.globalLights) {
            worldLightBuffer.put(light.getData());
        }

        worldLightBuffer.flip();

        Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, numLightFloats * 4, worldLightBuffer, GL31.GL_DYNAMIC_DRAW);
    }

    private void modifyChunkLightBuffer(Chunk chunk, int chunkLightSSBO) {
        List<ChunkLight> affectedLights = chunk.affectedLights;

        int numAffectedLights = affectedLights.size();
        int numLightFloats = numAffectedLights * Light.NUM_FLOAT_DATA;

        if (numAffectedLights == 0) {
            shader.setUniformi("hasChunkLights", 0);
        } else {
            shader.setUniformi("hasChunkLights", 1);


            FloatBuffer chunkLightBuffer = chunkLightBuffers[numAffectedLights];

            float[] lightsData = new float[numLightFloats];

            chunkLightBuffer.clear();
            for (Light light : affectedLights) {
                if (light == null) continue;

                chunkLightBuffer.put(light.getData());
            }
            chunkLightBuffer.flip();

            shader.setUniform1fv("chunkLightsArray", chunkLightBuffer.array(), 0, numLightFloats);

            //Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, numLightFloats * 4, chunkLightBuffer, GL31.GL_DYNAMIC_DRAW);
        }
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

    private final Color activeColor = new Color(0f, 1f, 0f, 1f);
    private final Color inActiveColor = new Color(1f, 0f, 0f, 1f);
    private final Color unloadedColor = new Color(0f, 0f, 0f, 1f);

    public void renderChunkActiveDebugSquare(Chunk chunk, float chunkRenderX, float chunkRenderY) {

        shapeRenderer.setColor(chunk.isLoaded() ? (chunk.isActive() ? activeColor : inActiveColor) : unloadedColor);

        float rectSize = WorldConstants.CHUNK_SIZE * WorldConstants.CELL_SIZE - 2;
        shapeRenderer.rect(chunkRenderX, chunkRenderY, rectSize, rectSize);
    }
}

