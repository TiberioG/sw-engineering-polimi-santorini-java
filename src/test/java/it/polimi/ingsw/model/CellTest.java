package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class CellTest {

    private Cell cell;
    private Worker worker;
    private int x = 2;
    private int y = 3;

    @Before
    public void setUp() {
        cell = new Cell(x, y);
        worker = new Worker(0, Colors.BLUE, new Player("Vespasiano", new Date(6666666)));
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
