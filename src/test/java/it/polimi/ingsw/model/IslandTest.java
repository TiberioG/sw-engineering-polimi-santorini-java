package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CellOutOfBoundsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IslandTest {

    private Island island = null;


    @Before
    public void setUp() {
        this.island = new Island();
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


}