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
    private Match testMatch;
    private final int testID = 478939;
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
        testMatch = new Match(testID);
        cell1 = new Cell(x1, y1);
        worker1 = new Worker(Colors.BLUE, player);
    }


    @Test
    public void setLocation() throws WorkerAlreadyPresentException {
        Location locationTest = testMatch.getLocation();
        locationTest.setLocation(cell1,worker1);

        assertEquals(cell1, locationTest.getLocation(worker1));
        assertEquals(worker1, locationTest.getOccupant(cell1));

    }

    @Test
    public void getLocation() {
        Location locationTest = testMatch.getLocation();

        try {
            locationTest.setLocation(cell1, worker1);
        }
        catch (WorkerAlreadyPresentException except){
            // no need to manage here
        }

        assertEquals(cell1, locationTest.getLocation(worker1));
        assertNull(locationTest.getLocation(worker2));
    }

    @Test
    public void getOccupant() {
        Location locationInstance = testMatch.getLocation();
        try {
            locationInstance.setLocation(cell1, worker1);
        }
        catch (WorkerAlreadyPresentException except){
            // no need to manage here
        }

        assertNotEquals(worker1, locationInstance.getOccupant(cell2));
        assertEquals(worker1, locationInstance.getOccupant(cell1));
    }
}
