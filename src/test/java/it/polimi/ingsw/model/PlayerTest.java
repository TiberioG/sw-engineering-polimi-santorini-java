package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
    private Player player;
    private String nameOfThePlayer = "NAME";
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Date birthdayOfThePlayer;

    @Before
    public void setUp() throws ParseException {
        birthdayOfThePlayer = dateFormat.parse("25/12/2010");
        this.player = new Player(nameOfThePlayer, birthdayOfThePlayer);
    }

    @Test
    public void getName() {
        Assert.assertEquals(player.getName(), nameOfThePlayer);
    }

    @Test
    public void getBirthday() {
        Assert.assertEquals(player.getBirthday(), birthdayOfThePlayer);
    }

    @Test
    public void checkCreationAndGettingOfTheWorkers() {
        List<Worker> localWorkers = new ArrayList();
        localWorkers.add(player.addWorker(Colors.BLUE));
        localWorkers.add(player.addWorker(Colors.BLUE));
        List<Worker> listOfWorkersOfPlayer = player.getWorkers();
        Assert.assertTrue(listOfWorkersOfPlayer.containsAll(localWorkers) && localWorkers.size() == listOfWorkersOfPlayer.size());
    }
}
