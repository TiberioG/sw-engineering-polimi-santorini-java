package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.commons.Colors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class WorkerTest {
    private Worker worker;
    private Colors colorBlue = Colors.BLUE;

    @Before
    public void setUp() {
        worker = new Worker(0,colorBlue, new Player("domiziana", new Date(9999999)));
    }

    @Test
    public void getColor() {
        Assert.assertSame(worker.getColor(), colorBlue);
    }
}
