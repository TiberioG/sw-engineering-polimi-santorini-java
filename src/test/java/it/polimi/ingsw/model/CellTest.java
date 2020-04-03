package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {

    private Cell cell;
    private Worker worker;
    private int x = 2;
    private int y = 3;

    @Before
    public void setUp() {
        cell = new Cell(x, y);
        worker = new Worker(Colors.BLUE);
    }

    @After
    public void tearDown() {
        cell = null;
        worker = null;
    }

    @Test
    public void getCoordX() {
        assertEquals(x, cell.getCoordX());
    }

    @Test
    public void getCoordY() {
        assertEquals(y, cell.getCoordY());
    }
}
