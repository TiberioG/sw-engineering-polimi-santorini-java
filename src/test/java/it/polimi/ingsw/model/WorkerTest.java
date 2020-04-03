package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WorkerTest {
    private Worker worker;
    private Colors colorBlue = Colors.BLUE;

    @Before
    public void setUp() {
        worker = new Worker(colorBlue);
    }

    @Test
    public void getColor() {
        Assert.assertSame(worker.getColor(), colorBlue);
    }

}
