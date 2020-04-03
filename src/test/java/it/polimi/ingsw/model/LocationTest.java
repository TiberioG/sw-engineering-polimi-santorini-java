package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class LocationTest {
    private Player player = new Player("Agrippa", new Date(2393939));
    private Island island = new Island();
    private Cell cell1;
    private Cell cell2;
    private Worker worker1;
    private Worker worker2;
    private int x1 = 2;
    private int y1 = 3;
    private int x2 = 4;
    private int y2 = 1;

    @Before
    public void setUp() {
        cell1 = new Cell(x1, y1);
        worker1 = new Worker(Colors.BLUE);
    }


    @Test
    public void myLocation() {
    }

    @Test
    public void setLocation() throws WorkerAlreadyPresentException {
        Location.myLocation().setLocation(cell1,worker1);

        assertEquals(cell1, Location.myLocation().getLocation(worker1));
        assertEquals(worker1, Location.myLocation().getOccupant(cell1));

    }

    @Test
    public void getLocation() {
        try {
            Location.myLocation().setLocation(cell1, worker1);
        }
        catch (WorkerAlreadyPresentException except){
            // no need to manage here
        }

        assertEquals(cell1, Location.myLocation().getLocation(worker1));
        assertNull(Location.myLocation().getLocation(worker2));
    }

    @Test
    public void getOccupant() {
        try {
            Location.myLocation().setLocation(cell1, worker1);
        }
        catch (WorkerAlreadyPresentException except){
            // no need to manage here
        }

        assertNotEquals(worker1, Location.myLocation().getOccupant(cell2));
        assertEquals(worker1, Location.myLocation().getOccupant(cell1));
    }
}
