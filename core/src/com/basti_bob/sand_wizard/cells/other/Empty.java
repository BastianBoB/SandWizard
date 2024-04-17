package com.basti_bob.sand_wizard.cells.other;

import com.basti_bob.sand_wizard.cells.Cell;
import com.basti_bob.sand_wizard.cells.CellType;
import com.basti_bob.sand_wizard.world.World;

public class Empty extends Cell {

    //public static Empty INSTANCE;

    public Empty(CellType cellType) {
        super(cellType);
    }


    //    public Empty getInstance(){
//        if(INSTANCE == null) {
//            INSTANCE = new Empty(CellType.EMPTY, null, po)
//        }
//
//        return INSTANCE;
//    }

//    @Override
//    public int getX() {
//        try {
//            throw new Exception("CANT GET POSITION OF EMPTY CELL");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public int getY() {
//        try {
//            throw new Exception("CANT GET POSITION OF EMPTY CELL");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void setPosition(int posX, int posY) {
//        try {
//            throw new Exception("CANT SET POSITION OF EMPTY CELL");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
