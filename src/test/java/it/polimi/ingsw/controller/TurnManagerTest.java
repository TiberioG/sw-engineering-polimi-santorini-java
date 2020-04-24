package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TurnManagerTest {
    private TurnManager turnManager;
    private Match match;

    @Before
    public void setUp() {
        match = new Match(0);
        Player firstPlayer = match.createPlayer("firstPlayer", new Date());
        CardManager cardManager = CardManager.initCardManager();
        firstPlayer.setCurrentCard(cardManager.getCardById(0));
        try {
            match.getLocation().setLocation(match.getIsland().getCell(0,0), firstPlayer.addWorker(Colors.BLUE));
            match.getLocation().setLocation(match.getIsland().getCell(0,1), firstPlayer.addWorker(Colors.BLUE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Player secondPlayer = match.createPlayer("secondPlayer", new Date());
        secondPlayer.setCurrentCard(cardManager.getCardById(1));

        match.setCurrentPlayer(firstPlayer);

        turnManager = new TurnManager(match);
    }

    @Test
    public void hasSamePlayerAsMatch() {
        assertEquals(match.getCurrentPlayer(), turnManager.getCurrentPlayer());
    }
}
