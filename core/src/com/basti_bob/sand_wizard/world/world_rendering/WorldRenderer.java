package com.basti_bob.sand_wizard.world.world_rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.entities.Entity;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.world.world_generation.chunk_data.ChunkColumnData;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import com.basti_bob.sand_wizard.world.chunk.Chunk;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.ChunkLight;
import com.basti_bob.sand_wizard.world.world_rendering.lighting.Light;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class WorldRenderer {

    public static final int GL_FRAMEBUFFER_SRGB = 0x8DB9;

    private static final int WORLD_LIGHTS_BUFFER_BLOCK_INDEX = 0;
    private static final int CHUNK_LIGHTS_BUFFER_BLOCK_INDEX = 1;
    private static final int LIGHTS_INDICES_BUFFER_BLOCK_INDEX = 2;

    private final World world;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private final ShapeRenderer shapeRenderer;

    private FloatBuffer worldLightBuffer = BufferUtils.newFloatBuffer(10000 * Light.NUM_FLOAT_DATA);
    private FloatBuffer chunkLightBuffer = BufferUtils.newFloatBuffer(500000 * Light.NUM_FLOAT_DATA);
    private IntBuffer[] chunkLightIndicesBuffers = new IntBuffer[10000];

    public WorldRenderer(World world, OrthographicCamera camera) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.camera = camera;

        ShaderProgram.pedantic = true;
        Gdx.gl.glEnable(GL32.GL_VERTEX_PROGRAM_POINT_SIZE);

        shader = generateShader();

        for (int i = 0; i < chunkLightIndicesBuffers.length; i++) {
            chunkLightIndicesBuffers[i] = BufferUtils.newIntBuffer(i);
        }
    }

    private ShaderProgram generateShader() {
        final String vertexShader = Gdx.files.internal("shaders/vertex_shader.glsl").readString();
        final String fragmentShader = Gdx.files.internal("shaders/fragment_shader.glsl").readString();

        return new ShaderProgram(vertexShader, fragmentShader);
    }

    public void renderWithoutLights(Player player, Array2D<Chunk> chunks, ChunkPos topLeftChunkPos) {
        for (int i = 0; i < chunks.rows; i++) {

            ChunkColumnData chunkColumnData = world.worldGeneration.chunkColumnDataMap.get(topLeftChunkPos.x + i);
            if (chunkColumnData == null) continue;

            shader.setUniform1fv("terrainHeights", chunkColumnData.terrainHeights, 0, WorldConstants.CHUNK_SIZE);

            for (int j = 0; j < chunks.cols; j++) {
                Chunk chunk = chunks.get(i, j);
                if (chunk == null) continue;

                chunk.mesh.render(shader, GL20.GL_POINTS);
            }
        }
    }

    public void render(Player player) {

        Gdx.gl.glEnable(GL_FRAMEBUFFER_SRGB);

        Array2D<Chunk> chunks = player.getRenderingChunks();
        ChunkPos topLeftChunkPos = player.topLeftChunkPos;

        shader.bind();
        shader.setUniformMatrix("u_proj", camera.combined);
        shader.setUniformf("u_pointSize", WorldConstants.CELL_SIZE / camera.zoom * 1.05f);
        shader.setUniformi("u_cellSize", WorldConstants.CELL_SIZE);

        shader.setUniformi("u_dayTimeMinutes", world.updateTimes);
        shader.setUniform2fv("u_cameraPos", new float[]{camera.position.x / WorldConstants.CELL_SIZE, camera.position.y / WorldConstants.CELL_SIZE}, 0, 2);

        shader.setUniformi("lightingEnabled", SandWizard.lightingEnabled ? 1 : 0);
        float unlitBaseLight = WorldConstants.NOT_VISIBLE_CHUNK_BRIGHTNESS;
        shader.setUniform3fv("u_unlitBaseLight", new float[]{unlitBaseLight, unlitBaseLight, unlitBaseLight}, 0, 3);

        if (SandWizard.lightingEnabled) {
            renderWithLights(player, chunks, topLeftChunkPos);
        } else {
            renderWithoutLights(player, chunks, topLeftChunkPos);
        }

        Gdx.gl.glDisable(GL_FRAMEBUFFER_SRGB);

        for (Entity entity : world.getEntities()) {
            entity.render(camera, shapeRenderer);
        }

        if (SandWizard.renderChunkBoarder)
            chunkActiveDebugSquares(chunks);
    }

    public void renderWithLights(Player player, Array2D<Chunk> chunks, ChunkPos topLeftChunkPos) {

        int worldLightSSBO = Gdx.gl31.glGenBuffer();
        setWorldLightBuffer(chunks, worldLightSSBO);

        int chunkLightSSBO = Gdx.gl31.glGenBuffer();
        setChunksLightBuffer(chunks, chunkLightSSBO);

        int chunkLightIndicesSSBO = Gdx.gl31.glGenBuffer();
        Gdx.gl31.glBindBuffer(GL31.GL_SHADER_STORAGE_BUFFER, chunkLightIndicesSSBO);
        Gdx.gl31.glBindBufferBase(GL31.GL_SHADER_STORAGE_BUFFER, LIGHTS_INDICES_BUFFER_BLOCK_INDEX, chunkLightIndicesSSBO);

        IntBuffer dummyBuffer = BufferUtils.newIntBuffer(1);
        dummyBuffer.put(0);
        dummyBuffer.flip();
        Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, 0, dummyBuffer, GL31.GL_DYNAMIC_DRAW);
        int lastChunkNumLights = 0;
        for (int i = 0; i < chunks.rows; i++) {

            ChunkColumnData chunkColumnData = world.worldGeneration.chunkColumnDataMap.get(topLeftChunkPos.x + i);
            if (chunkColumnData == null) continue;

            shader.setUniform1fv("terrainHeights", chunkColumnData.terrainHeights, 0, WorldConstants.CHUNK_SIZE);

            for (int j = 0; j < chunks.cols; j++) {
                Chunk chunk = chunks.get(i, j);
                if (chunk == null) continue;

                float unlitBaseLight = chunk.isVisibleByPlayer() ? WorldConstants.VISIBLE_CHUNK_BRIGHTNESS : WorldConstants.NOT_VISIBLE_CHUNK_BRIGHTNESS;
                shader.setUniform3fv("u_unlitBaseLight", new float[]{unlitBaseLight, unlitBaseLight, unlitBaseLight}, 0, 3);

                //if(!chunk.isVisibleByPlayer()) continue;

                lastChunkNumLights = setChunkLightIndicesArray(chunk, lastChunkNumLights);

                chunk.mesh.render(shader, GL20.GL_POINTS);
            }
        }

        Gdx.gl31.glDeleteBuffer(worldLightSSBO);
        Gdx.gl31.glDeleteBuffer(chunkLightSSBO);
        Gdx.gl31.glDeleteBuffer(chunkLightIndicesSSBO);
    }

    private void setChunksLightBuffer(Array2D<Chunk> chunks, int ssbo) {
        chunkLightBuffer.clear();
        int lightArrayIndex = 0;

        Set<ChunkLight> alreadyAddedLights = new HashSet<>();

        for (Chunk chunk : chunks.getArray()) {
            if (chunk == null) continue;

            for (ChunkLight light : chunk.affectedLights) {
                if(light == null || !light.isEmittingLight() || alreadyAddedLights.contains(light)) continue;

                alreadyAddedLights.add(light);

                light.shaderArrayIndex = lightArrayIndex;
                chunkLightBuffer.put(light.getData());

                lightArrayIndex++;
            }
        }
        chunkLightBuffer.flip();

        Gdx.gl31.glBindBuffer(GL31.GL_SHADER_STORAGE_BUFFER, ssbo);
        Gdx.gl31.glBindBufferBase(GL31.GL_SHADER_STORAGE_BUFFER, CHUNK_LIGHTS_BUFFER_BLOCK_INDEX, ssbo);
        Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, 0, chunkLightBuffer, GL31.GL_DYNAMIC_DRAW);
    }

    private int setChunkLightIndicesArray(Chunk chunk, int lastChunkNumLights) {
        List<ChunkLight> lights = chunk.affectedLights;
        int numLights = lights.size();

        if (numLights == 0 && lastChunkNumLights == 0) return numLights;

        IntBuffer lightIndicesBuffer = chunkLightIndicesBuffers[numLights];
        lightIndicesBuffer.clear();

        for (int k = 0; k < numLights; k++) {
            ChunkLight light = lights.get(k);
            if (light == null) continue;
            lightIndicesBuffer.put(light.shaderArrayIndex);
        }
        lightIndicesBuffer.flip();

        Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, 0, lightIndicesBuffer, GL31.GL_DYNAMIC_DRAW);


        return numLights;
    }

    private void setWorldLightBuffer(Array2D<Chunk> chunks, int ssbo) {
        worldLightBuffer.clear();
        for (Light light : world.globalLights) {
            if (light.isEmittingLight())
                worldLightBuffer.put(light.getData());
        }
        worldLightBuffer.flip();

        Gdx.gl31.glBindBuffer(GL31.GL_SHADER_STORAGE_BUFFER, ssbo);
        Gdx.gl31.glBindBufferBase(GL31.GL_SHADER_STORAGE_BUFFER, WORLD_LIGHTS_BUFFER_BLOCK_INDEX, ssbo);
        Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, 0, worldLightBuffer, GL31.GL_DYNAMIC_DRAW);
    }

    private void chunkActiveDebugSquares(Array2D<Chunk> chunks) {
        float chunkSize = WorldConstants.CHUNK_SIZE;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int chunkI = 0; chunkI < chunks.rows; chunkI++) {
            for (int chunkJ = 0; chunkJ < chunks.cols; chunkJ++) {

                Chunk chunk = chunks.get(chunkI, chunkJ);
                if (chunk == null) continue;

                float chunkRenderX = (chunk.getPosX() * chunkSize) * WorldConstants.CELL_SIZE;
                float chunkRenderY = (chunk.getPosY() * chunkSize) * WorldConstants.CELL_SIZE;

                renderChunkActiveDebugSquare(chunk, chunkRenderX, chunkRenderY);
            }
        }
        shapeRenderer.end();

    }

    private final Color activeColor = new Color(0f, 1f, 0f, 1f);
    private final Color inActiveColor = new Color(1f, 0f, 0f, 1f);
    private final Color notUpdatingColor = new Color(0f, 0f, 0f, 1f);

    public void renderChunkActiveDebugSquare(Chunk chunk, float chunkRenderX, float chunkRenderY) {

        shapeRenderer.setColor(chunk.isUpdating() ? (chunk.isActive() ? activeColor : inActiveColor) : notUpdatingColor);

        float rectSize = WorldConstants.CHUNK_SIZE * WorldConstants.CELL_SIZE - 1;
        shapeRenderer.rect(chunkRenderX, chunkRenderY, rectSize, rectSize);
    }


}

