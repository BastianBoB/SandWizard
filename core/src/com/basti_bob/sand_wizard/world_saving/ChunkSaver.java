package com.basti_bob.sand_wizard.world_saving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.Chunk;
import com.basti_bob.sand_wizard.world.World;
import com.basti_bob.sand_wizard.world.WorldConstants;
import org.apache.commons.lang3.StringUtils;

public class ChunkSaver {

    public static final String CHUNK_SAVING_LOCATION = "world/chunks/";

    private static String getChunkFileLocation(int chunkX, int chunkY) {
        return CHUNK_SAVING_LOCATION + chunkX + "," + chunkY;
    }

    public static void writeChunk(Chunk chunk) {

        FileHandle file = Gdx.files.local(getChunkFileLocation(chunk.posX, chunk.posY));

        int sameCellCount = 0;
        String lastCellName = null;

        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
            for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {
                CellType cellType = chunk.getCellFromInChunkPos(i, j).getCellType();

                String idName = cellType.idName;

                if (lastCellName == null || lastCellName.equals(idName)) {
                    sameCellCount++;
                } else {
                    stringBuilder.append(lastCellName).append(",").append(sameCellCount).append(",");
                    sameCellCount = 1;
                }

                lastCellName = idName;
            }
        }

        stringBuilder.append(lastCellName).append(",").append(sameCellCount).append(",");

        file.writeString(stringBuilder.toString(), false);
    }


    public static Chunk readChunk(World world, int chunkX, int chunkY) {
        try {

            FileHandle file = Gdx.files.local(getChunkFileLocation(chunkX, chunkY));
            if (!file.exists()) return null;

            Chunk chunk = new Chunk(world, chunkX, chunkY);

            String[] cellsAndNumbers = file.readString().split(",");

            int arrayIndex = 0;
            int repeatedCellIteration = 0;

            CellType currentCellType = CellType.fromName(cellsAndNumbers[arrayIndex]);
            int numRepeatingCells = Integer.parseInt(cellsAndNumbers[arrayIndex + 1]);

            for (int j = 0; j < WorldConstants.CHUNK_SIZE; j++) {
                for (int i = 0; i < WorldConstants.CHUNK_SIZE; i++) {

                    if (repeatedCellIteration >= numRepeatingCells) {
                        repeatedCellIteration = 0;
                        arrayIndex += 2;
                        currentCellType = CellType.fromName(cellsAndNumbers[arrayIndex]);
                        numRepeatingCells = Integer.parseInt(cellsAndNumbers[arrayIndex + 1]);
                    }

                    chunk.setCellWithInChunkPos(currentCellType, i, j);

                    repeatedCellIteration++;
                }
            }

            return chunk;
        } catch (Exception e) {
            System.out.println("FAILED READING CHUNK at: " + chunkX + ", " + chunkY);
            return null;
        }
    }


}
