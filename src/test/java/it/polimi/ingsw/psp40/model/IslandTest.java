package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class IslandTest {

    private Island island = null;
    private Cell centralCell;
    private Cell borderCell;

    @Before
    public void setUp() {
        this.island = new Island();
        try {
            centralCell = this.island.getCell(2, 2);
            borderCell = this.island.getCell(0,0);
        }
        catch (CellOutOfBoundsException except){
            //no need here to handle except
        }
    }

    @After
    public void tearDown() {
        island = null;
    }

    @Test
    public void getCell_CorrectCoordinates() throws CellOutOfBoundsException {
        int testX = 2;
        int testY = 4;
        assertEquals(island.getField()[testX][testY], island.getCell(testX, testY));
    }

    @Test (expected = CellOutOfBoundsException.class)
    public void getCell_WrongCoordinates_throwsCellOutOfBoundsException() throws CellOutOfBoundsException {
        island.getCell(5, 2); // coordinate x is out of bounds
    }

    @Test
    public void getAdjCell_areReallyAdj()throws CellOutOfBoundsException{
        Cell nw = this.island.getCell(1, 1);
        Cell n  = this.island.getCell(2, 1);
        Cell ne = this.island.getCell(3, 1);
        Cell w  = this.island.getCell(1, 2);
        Cell e  = this.island.getCell(3, 2);
        Cell sw = this.island.getCell(1, 3);
        Cell s  = this.island.getCell(2, 3);
        Cell se = this.island.getCell(3, 3);
        ArrayList<Cell> adjacentCells = new ArrayList<>();
        adjacentCells.add(nw); // 1, 1
        adjacentCells.add(w);  // 1, 2
        adjacentCells.add(sw); // 1, 3
        adjacentCells.add(n);  // 2, 1
        adjacentCells.add(s);  // 2, 3
        adjacentCells.add(ne); // 3, 1
        adjacentCells.add(e);  // 3, 2
        adjacentCells.add(se); // 3, 3
        assertEquals(adjacentCells, this.island.getAdjCells(centralCell));
    }

    @Test
    public void getAdjCell_areReallyAdj_nearUpperBorder()throws CellOutOfBoundsException{
        ArrayList<Cell> adjacentCells = new ArrayList<>();
        adjacentCells.add( this.island.getCell(0, 1) ); // 0, 1
        adjacentCells.add( this.island.getCell(1, 0) );  // 1, 0
        adjacentCells.add( this.island.getCell(1, 1) ); // 1, 1

        assertEquals(adjacentCells, this.island.getAdjCells(borderCell));
    }


}
