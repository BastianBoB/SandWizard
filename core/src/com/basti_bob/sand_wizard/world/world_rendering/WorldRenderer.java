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
import com.basti_bob.sand_wizard.world.coordinates.CellPos;
import com.basti_bob.sand_wizard.world.coordinates.ChunkPos;
import com.basti_bob.sand_wizard.world.lighting.Light;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WorldRenderer {

    public static final int GL_FRAMEBUFFER_SRGB = 0x8DB9;


    private final World world;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private final ShapeRenderer shapeRenderer;

    private final HashMap<Integer, FloatBuffer> lightBuffers = new HashMap<>();

    public WorldRenderer(World world, OrthographicCamera camera) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.camera = camera;

        ShaderProgram.pedantic = true;
        Gdx.gl.glEnable(GL32.GL_VERTEX_PROGRAM_POINT_SIZE);
        Gdx.gl.glEnable(GL_FRAMEBUFFER_SRGB);

        shader = generateShader();

    }

    private ShaderProgram generateShader() {

        final String vertexShader = Gdx.files.internal("shaders/vertex_shader.glsl").readString();
        final String fragmentShader = Gdx.files.internal("shaders/fragment_shader.glsl").readString();

        return new ShaderProgram(vertexShader, fragmentShader);
    }

    public void render(Player player) {

        Array2D<Chunk> chunks = player.getRenderingChunks();

        ChunkPos topLeftChunkPos = player.topLeftChunkPos;
        ChunkPos bottomRightChunkPos = player.bottomRightChunkPos;


        shader.bind();
        shader.setUniformMatrix("u_proj", camera.combined);
        shader.setUniformf("u_pointSize", WorldConstants.CELL_SIZE / camera.zoom * 1.05f);
        shader.setUniformi("u_cellSize", WorldConstants.CELL_SIZE);

        //shader.setUniform2fv("u_playerPos", new float[]{player.nx, player.ny}, 0, 2);
        shader.setUniform2fv("u_cameraPos", new float[]{camera.position.x / WorldConstants.CELL_SIZE, camera.position.y / WorldConstants.CELL_SIZE}, 0, 2);

//        shader.setUniform2fv("u_topLeftChunkPos", new float[]{topLeftChunkPos.x, topLeftChunkPos.y}, 0, 2);
//        shader.setUniform2fv("u_bottomRightChunkPos", new float[]{bottomRightChunkPos.x, bottomRightChunkPos.y}, 0, 2);
        System.out.println(bottomRightChunkPos.y + " , " + topLeftChunkPos.y);


        int ssbo = Gdx.gl31.glGenBuffer();
        Gdx.gl31.glBindBuffer(GL31.GL_SHADER_STORAGE_BUFFER, ssbo);

        int lightsIndex = Gdx.gl31.glGetUniformBlockIndex(shader.getHandle(), "lights_data");
        Gdx.gl31.glUniformBlockBinding(shader.getHandle(), lightsIndex, 1);

        int lastNumLights = -1;

        for (int i = 0; i < chunks.rows; i++) {

            ChunkColumnData chunkColumnData = world.chunkProvider.chunkColumns.get(topLeftChunkPos.x + i);

            if (chunkColumnData == null) continue;

            shader.setUniform1fv("terrain_heights", chunkColumnData.terrainHeights, 0, WorldConstants.CHUNK_SIZE);

            for (int j = 0; j < chunks.cols; j++) {
                Chunk chunk = chunks.get(i, j);

                if (chunk == null) continue;

                List<Light> affectedLights = chunk.affectedLights;
                int numAffectedLights = affectedLights.size();

                if (!(lastNumLights == 0 && numAffectedLights == 0)) { //dont have to modify the buffer with 2 empty light chunks
                    FloatBuffer buffer = lightBuffers.get(numAffectedLights);

                    if (buffer == null) {
                        buffer = BufferUtils.newFloatBuffer(numAffectedLights * Light.NUM_FLOAT_DATA);
                        lightBuffers.put(numAffectedLights, buffer);
                    }

                    buffer.clear();

                    for (int k = 0; k < numAffectedLights; k++) {
                        if (k == affectedLights.size()) break;

                        Light light = (affectedLights.get(k));
                        if (light == null) continue;

                        buffer.put(light.getData());
                    }

                    buffer.flip();

                    Gdx.gl31.glBufferData(GL31.GL_SHADER_STORAGE_BUFFER, numAffectedLights * Light.NUM_FLOAT_DATA * 4, buffer, GL31.GL_DYNAMIC_DRAW);
                    Gdx.gl31.glBindBufferBase(GL31.GL_SHADER_STORAGE_BUFFER, 0, ssbo);
                }

                lastNumLights = numAffectedLights;

                chunk.mesh.render(shader, GL20.GL_POINTS);
            }

            Gdx.gl31.glDeleteBuffer(ssbo);

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

