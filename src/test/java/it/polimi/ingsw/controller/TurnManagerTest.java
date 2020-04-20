package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import it.polimi.ingsw.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TurnManagerTest {
    private TurnManager turnManager;
    private Match match;

    @Before
    public void setUp() throws PlayerNotPresentException {
        match = new Match(0);
        Player currentPlayer = match.createPlayer("firstPlayer", new Date());
        CardManager cardManager = CardManager.initCardManager();
        currentPlayer.setCurrentCard(cardManager.getCardById(0));
        match.setCurrentPlayer(currentPlayer);

        turnManager = new TurnManager(match);
    }

    @Test
    public void hasSamePlayerAsMatch() {
        assertEquals(match.getCurrentPlayer(), turnManager.getCurrentTurn().getPlayer());
    }
}
