package com.basti_bob.sand_wizard.cells.other;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;

public class Empty extends Cell {

    private static Empty INSTANCE;

    private Empty(CellType cellType) {
        super(cellType);
    }

    public static Empty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Empty(CellType.EMPTY);
        }

        return INSTANCE;
    }

    @Override
    public int getPosX() {
        try {
            throw new Exception("CANT getPosX OF EMPTY CELL");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPosY() {
        try {
            throw new Exception("CANT getPosY OF EMPTY CELL");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getInChunkX() {
        try {
            throw new Exception("CANT getInChunkX OF EMPTY CELL");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getInChunkY() {
        try {
            throw new Exception("CANT getInChunkY OF EMPTY CELL");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPosition(int posX, int posY) {
        try {
            throw new Exception("CANT SET POSITION OF EMPTY CELL");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
