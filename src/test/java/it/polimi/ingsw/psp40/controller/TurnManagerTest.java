package it.polimi.ingsw.psp40.controller;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.network.server.Server;
import it.polimi.ingsw.psp40.network.server.VirtualView;
import it.polimi.ingsw.psp40.model.CardManager;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TurnManagerTest {
    private TurnManager turnManager;
    private Match match;
    private Player firstPlayer;
    private Player secondPlayer;

    @Before
    public void setUp() {
        match = new Match(0);
        firstPlayer = match.createPlayer("firstPlayer", new Date());
        secondPlayer = match.createPlayer("secondPlayer", new Date());
        CardManager cardManager = CardManager.initCardManager();
        firstPlayer.setCurrentCard(cardManager.getCardById(0));
        secondPlayer.setCurrentCard(cardManager.getCardById(1));
        try {
            match.getLocation().setLocation(match.getIsland().getCell(0,0), firstPlayer.addWorker(Colors.BLUE));
            match.getLocation().setLocation(match.getIsland().getCell(0,1), firstPlayer.addWorker(Colors.BLUE));
            match.getLocation().setLocation(match.getIsland().getCell(3,0), secondPlayer.addWorker(Colors.RED));
            match.getLocation().setLocation(match.getIsland().getCell(3,1), secondPlayer.addWorker(Colors.RED));
        } catch (Exception e) {
            e.printStackTrace();
        }

        match.setCurrentPlayer(firstPlayer);

        turnManager = new TurnManager(match, new VirtualView(new Server()));
    }


    @Test
    public void testFirstTurnOfMatch_currentPlayerEqualSecondPlayer() {
        turnManager.selectWorker(turnManager.getCurrentPlayer().getWorkers().get(0));
        turnManager.getAvailableCellForMove();
        try {
            turnManager.move(match.getIsland().getCell(1,0));
            turnManager.getAvailableCellForBuild();
            turnManager.build(Component.FIRST_LEVEL, match.getIsland().getCell(0,0) );
        } catch (SantoriniException e) {
            e.printStackTrace();
        }

        assertTrue(turnManager.getCurrentPlayer().equals(secondPlayer));


    }

    @Test
    public void hasSamePlayerAsMatch() {
        Assert.assertEquals(match.getCurrentPlayer(), turnManager.getCurrentPlayer());
    }
}
