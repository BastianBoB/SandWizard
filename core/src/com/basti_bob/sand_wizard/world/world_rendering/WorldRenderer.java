package com.basti_bob.sand_wizard.world.world_rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.SandWizard;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.Array2D;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldRenderer {

    private final World world;
    private final Camera camera;
    private final Mesh mesh;
    private final ShaderProgram shader;
    private final float[] vertices;
    private final int rows, cols;

    private final ShapeRenderer shapeRenderer;


    public WorldRenderer(World world, Camera camera) {
        this.shapeRenderer = new ShapeRenderer();

        this.world = world;
        this.camera = camera;
        this.rows = WorldConstants.CHUNK_SIZE * (WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_X * 2 + 1);
        this.cols = WorldConstants.CHUNK_SIZE * (WorldConstants.PLAYER_CHUNK_RENDER_RADIUS_Y * 2 + 1);

        int numCells = rows * cols;

        vertices = new float[numCells * 5];

        mesh = new Mesh(true, numCells, 0, new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 3, "a_color"));

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

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        final int chunkSize = WorldConstants.CHUNK_SIZE;

//        final float rM = (float) world.openSimplexNoise.eval(SandWizard.updateTimes * 0.01f, 0, 0) * 0.5f + 0.5f;
//        final float gM = (float) world.openSimplexNoise.eval(0, SandWizard.updateTimes * 0.01f, 0) * 0.5f + 0.5f;
//        final float bM = (float) world.openSimplexNoise.eval(0, 0, SandWizard.updateTimes * 0.01f) * 0.5f + 0.5f;

        for (int chunkI = 0; chunkI < chunks.rows; chunkI++) {
            for (int chunkJ = 0; chunkJ < chunks.cols; chunkJ++) {

                int finalChunkI = chunkI;
                int finalChunkJ = chunkJ;

                Chunk chunk = chunks.get(finalChunkI, finalChunkJ);
                if (chunk == null) continue;

                float chunkRenderX = (chunk.posX * chunkSize) * WorldConstants.CELL_SIZE;
                float chunkRenderY = (chunk.posY * chunkSize) * WorldConstants.CELL_SIZE;

                final Array2D<Cell> chunkGrid = chunk.getGrid();

                executor.submit(() -> {
                    for (int j = 0; j < chunkSize; j++) {

                        for (int i = 0; i < chunkSize; i++) {

                            int cellIndexX = finalChunkI * chunkSize + i;
                            int cellIndexY = finalChunkJ * chunkSize + j;

                            int cellIndex = cellIndexY * rows + cellIndexX;
                            int vertexI = cellIndex * 5;

                            Color color = chunkGrid.get(i, j).getColor();

                            vertices[vertexI] = chunkRenderX + i * WorldConstants.CELL_SIZE;
                            vertices[vertexI + 1] = chunkRenderY + j * WorldConstants.CELL_SIZE;
                            vertices[vertexI + 2] = color.r;
                            vertices[vertexI + 3] = color.g;
                            vertices[vertexI + 4] = color.b;
                        }
                    }
                });
            }
        }

        executor.shutdown();

        mesh.setVertices(vertices);
        shader.bind();
        shader.setUniformMatrix("u_proj", camera.combined);
        mesh.render(shader, GL20.GL_POINTS);


        if (SandWizard.renderChunkBoarder)
            chunkActiveDebugSquares(chunks, chunkSize);
    }

    private void chunkActiveDebugSquares(Array2D<Chunk> chunks, int chunkSize) {

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

    private final Color activeColor = new Color(0.5f, 0.5f, 0.5f, 1);
    private final Color inActiveColor = new Color(0.1f, 0.1f, 0.1f, 1);


    public void renderChunkActiveDebugSquare(Chunk chunk, float chunkRenderX, float chunkRenderY) {

        shapeRenderer.setColor(chunk.isActive() ? activeColor : inActiveColor);

        float rectSize = WorldConstants.CHUNK_SIZE * WorldConstants.CELL_SIZE - 1;
        shapeRenderer.rect(chunkRenderX, chunkRenderY, rectSize, rectSize);
    }
}

