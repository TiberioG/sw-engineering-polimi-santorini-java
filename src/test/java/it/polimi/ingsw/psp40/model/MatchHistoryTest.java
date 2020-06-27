package it.polimi.ingsw.psp40.model;

import com.google.gson.JsonObject;
import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.network.server.Server;
import it.polimi.ingsw.psp40.network.server.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;


public class MatchHistoryTest {
    private Match match;
    private JsonObject jsonMatch;
    private List<String> oldNames = new ArrayList<>();

    @Before
    public void setUp() {
        match = new Match(0);
        CardManager cardManager = CardManager.initCardManager();
        match.createPlayer("player1", new Date());
        match.getPlayerByName("player1").setCurrentCard(cardManager.getCardById(0));
        match.createPlayer("player2", new Date());
        match.getPlayerByName("player2").setCurrentCard(cardManager.getCardById(1));
        match.setCurrentPlayer("player1");

        Player player1 = match.getPlayerByName("player1");
        player1.setCurrentCard(cardManager.getCardById(0));
        player1.addWorker(Colors.BLUE);
        player1.addWorker(Colors.BLUE);

        Player player2 = match.getPlayerByName("player2");
        player2.setCurrentCard(cardManager.getCardById(1));
        player2.addWorker(Colors.RED);
        player2.addWorker(Colors.RED);

        match.setCurrentPlayer("player1");

        try {
            match.getLocation().setLocation(match.getIsland().getCell(0,0), player1.getWorkers().get(0));
            match.getLocation().setLocation(match.getIsland().getCell(1,0), player1.getWorkers().get(1));
            match.getLocation().setLocation(match.getIsland().getCell(1,1), player2.getWorkers().get(0));
            match.getLocation().setLocation(match.getIsland().getCell(3,1), player2.getWorkers().get(1));
            match.getIsland().getCell(0,2).getTower().getComponents().add(Component.FIRST_LEVEL);
            match.getIsland().getCell(2,2).getTower().getComponents().add(Component.FIRST_LEVEL);
            match.getIsland().getCell(0,1).getTower().getComponents().add(Component.FIRST_LEVEL);
            match.getIsland().getCell(0,1).getTower().getComponents().add(Component.SECOND_LEVEL);
            match.getIsland().getCell(0,1).getTower().getComponents().add(Component.THIRD_LEVEL);

        } catch (WorkerAlreadyPresentException e) {
            e.printStackTrace();
        } catch (CellOutOfBoundsException e) {
            e.printStackTrace();
        }

        match.getMatchProperties().setOthersCantLevelUp(true);
        match.getCurrentPlayer().getWorkers().forEach(worker -> {
            match.getMatchProperties().getInitialPositionMap().put(worker, this.match.getLocation().getLocation(worker));
            match.getMatchProperties().getInitialLevels().put(worker, this.match.getLocation().getLocation(worker).getTower().getTopComponent().getComponentCode());
        });

        match.saveMatch();

        oldNames = new ArrayList<>();
        oldNames.add("player1");
        oldNames.add("player2");

        jsonMatch = MatchHistory.retrieveMatchFromNames(oldNames);
    }

    @After
    public void tearDown() {
        MatchHistory.deleteMatch(0);
    }

    @Test
    public void deleteMatchTest() {
        Match newMatch = MatchHistory.restoreMatch(new VirtualView(new Server()), jsonMatch);
        assertTrue(newMatch != null);
    }

    @Test
    public void getNamesOfBrokenMatch_samePlayer() {
        List<String> oldPlayerList = MatchHistory.getPlayersFromBrokenMatch(jsonMatch);
        assertTrue(oldNames.containsAll(oldPlayerList) && oldNames.size() == oldPlayerList.size());
    }

    @Test
    public void restorePlayers_samePlayers() {
        Match newMatch = MatchHistory.restoreMatch(new VirtualView(new Server()), jsonMatch);
        MatchHistory.restorePlayers(newMatch, jsonMatch);
        assertTrue(newMatch.getPlayers().size() == oldNames.size());
        assertTrue(newMatch.getPlayers().stream().map(player -> player.getName()).collect(Collectors.toList()).containsAll(oldNames));
        assertTrue(match.getPlayerByName("player1").getCurrentCard().getId() == 0 && match.getPlayerByName("player2").getCurrentCard().getId() == 1);
    }

    @Test
    public void restoreCurrentPlayer_sameCurrentPlayer() {
        Match newMatch = MatchHistory.restoreMatch(new VirtualView(new Server()), jsonMatch);
        MatchHistory.restorePlayers(newMatch, jsonMatch);
        MatchHistory.restoreCurrentPlayer(newMatch, jsonMatch);
        assertTrue(newMatch.getCurrentPlayer().getName().equals("player1"));

    }

    @Test
    public void restoreIsland_SameIsland() {
        Match newMatch = MatchHistory.restoreMatch(new VirtualView(new Server()), jsonMatch);
        MatchHistory.restoreIsland(newMatch, jsonMatch);
        boolean sameBlock = true;
        try {
            sameBlock = sameBlock && newMatch.getIsland().getCell(0,2).getTower().getTopComponent().equals(Component.FIRST_LEVEL);
            sameBlock = sameBlock && newMatch.getIsland().getCell(2,2).getTower().getTopComponent().equals(Component.FIRST_LEVEL);
            sameBlock = sameBlock && newMatch.getIsland().getCell(0,1).getTower().getComponents().get(1).equals(Component.FIRST_LEVEL);
            sameBlock = sameBlock && newMatch.getIsland().getCell(0,1).getTower().getComponents().get(2).equals(Component.SECOND_LEVEL);
            sameBlock = sameBlock && newMatch.getIsland().getCell(0,1).getTower().getComponents().get(3).equals(Component.THIRD_LEVEL);
        } catch (CellOutOfBoundsException e) {
            e.printStackTrace();
        }

        assertTrue(sameBlock);
    }

    @Test
    public void restoreLocation_SameLocation() {
        Match newMatch = MatchHistory.restoreMatch(new VirtualView(new Server()), jsonMatch);
        MatchHistory.restorePlayers(newMatch, jsonMatch);
        MatchHistory.restoreLocation(newMatch, jsonMatch);
        Worker worker11 = match.getLocation().getOccupant(0,0);
        Worker worker21 = match.getLocation().getOccupant(1,0);
        Worker worker12 = match.getLocation().getOccupant(1,1);
        Worker worker22 = match.getLocation().getOccupant(3,1);
        assertTrue(worker11.getPlayerName().equals(match.getPlayers().get(0).getName()) && worker11.getId() == 0);
        assertTrue(worker21.getPlayerName().equals(match.getPlayers().get(0).getName()) && worker21.getId() == 1);
        assertTrue(worker12.getPlayerName().equals(match.getPlayers().get(1).getName()) && worker12.getId() == 0);
        assertTrue(worker22.getPlayerName().equals(match.getPlayers().get(1).getName()) && worker22.getId() == 1);
    }

    @Test
    public void restoreMatchProperties_SameProperties(){
        Match newMatch = MatchHistory.restoreMatch(new VirtualView(new Server()), jsonMatch);
        MatchHistory.restoreMatchProperties(newMatch, jsonMatch);

        assertTrue(newMatch.getMatchProperties().getOthersCantLevelUp());
    }

}
