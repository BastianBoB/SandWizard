package com.basti_bob.sand_wizard.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.basti_bob.sand_wizard.Player;
import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.util.Array2D;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        final String vertex_shader = "attribute vec2 a_position;\n" +
                "attribute vec3 a_color;\n" +
                "varying vec3 v_color;\n" +
                "uniform mat4 u_proj;\n" +
                "void main() {\n" +
                "   v_color = a_color;\n" +
                "   gl_Position = u_proj * vec4(a_position, 0.0, 1.0);\n" +
                "   gl_PointSize = " + WorldConstants.CELL_SIZE + ";\n" +
                "}\n";

        final String fragment_shader = "varying vec3 v_color;" +
                "void main() {\n" +
                "   gl_FragColor = vec4(v_color, 1.0);\n" +
                "}";

        ShaderProgram.pedantic = true;
        shader = new ShaderProgram(vertex_shader, fragment_shader);
        Gdx.gl.glEnable(GL32.GL_VERTEX_PROGRAM_POINT_SIZE);
    }

    public void render(Player player) {
        long start = System.nanoTime();

        Array2D<Chunk> chunks = player.getRenderingChunks();

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        final int chunkSize = WorldConstants.CHUNK_SIZE;

        for (int chunkI = 0; chunkI < chunks.rows; chunkI++) {
            for (int chunkJ = 0; chunkJ < chunks.cols; chunkJ++) {

                int finalChunkI = chunkI;
                int finalChunkJ = chunkJ;

                Chunk chunk = chunks.get(finalChunkI, finalChunkJ);
                if (chunk == null) continue;

                float chunkRenderX = (chunk.posX * chunkSize - chunkSize / 2f) * WorldConstants.CELL_SIZE;
                float chunkRenderY = (chunk.posY * chunkSize - chunkSize / 2f) * WorldConstants.CELL_SIZE;

                final Array2D<Cell> chunkGrid = chunk.getGrid();

                executor.submit(() -> {
                    for (int i = 0; i < chunkSize; i++) {
                        for (int j = 0; j < chunkSize; j++) {

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
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Executor interrupted");
        }

        //System.out.println("setting Colors took: " + (System.nanoTime() - start) / 1e6 + " ms");

        start = System.nanoTime();
        mesh.setVertices(vertices);
        shader.bind();
        shader.setUniformMatrix("u_proj", camera.combined);
        mesh.render(shader, GL20.GL_POINTS);


        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int chunkI = 0; chunkI < chunks.rows; chunkI++) {
            for (int chunkJ = 0; chunkJ < chunks.cols; chunkJ++) {

                Chunk chunk = chunks.get(chunkI, chunkJ);
                if (chunk == null) continue;

                float chunkRenderX = (chunk.posX * chunkSize - chunkSize / 2f) * WorldConstants.CELL_SIZE;
                float chunkRenderY = (chunk.posY * chunkSize - chunkSize / 2f) * WorldConstants.CELL_SIZE;

                renderChunkActiveDebugSquare(chunk, chunkRenderX, chunkRenderY);
            }
        }
        shapeRenderer.end();


        //System.out.println("transfer and render took: " + (System.nanoTime() - start) / 1e6 + " ms");
    }

    private void renderChunkActiveDebugSquare(Chunk chunk, float chunkRenderX, float chunkRenderY) {
        if (chunk.isActive()) {
            shapeRenderer.setColor(Color.GREEN);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        float rectSize = WorldConstants.CHUNK_SIZE * WorldConstants.CELL_SIZE - 2;
        shapeRenderer.rect(chunkRenderX, chunkRenderY, rectSize, rectSize);
    }
}
