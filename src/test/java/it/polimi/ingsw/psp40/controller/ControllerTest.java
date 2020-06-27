package it.polimi.ingsw.psp40.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.model.*;
import it.polimi.ingsw.psp40.network.server.Server;
import it.polimi.ingsw.psp40.network.server.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ControllerTest {
    private CardManager cardManager;
    private Controller controller;
    private VirtualView virtualView;
    private HashMap<String, Date> hashMapOfNewPlayers;
    private HashMap<TypeOfMessage, Message> hashMapOfDisplayMessage = new HashMap<>();
    private String nameOfFirstPlayer = "player1";
    private String nameOfSecondPlayer = "player2";


    public class VirtualViewForTest extends VirtualView {
        public VirtualViewForTest(Server server) {
            super(server);
        }

        @Override
        public void displayMessage(Message message) {
            hashMapOfDisplayMessage.put(message.getTypeOfMessage(), message);
        }
    }

    private Match createFakeMatch() {
        Match match = Mockito.spy(new Match(0));
        match.createPlayer(nameOfFirstPlayer, new Date());
        match.getPlayerByName(nameOfFirstPlayer).setCurrentCard(cardManager.getCardById(0));
        match.createPlayer(nameOfSecondPlayer, new Date());
        match.getPlayerByName(nameOfSecondPlayer).setCurrentCard(cardManager.getCardById(1));
        match.setCurrentPlayer(nameOfFirstPlayer);

        Player player1 = match.getPlayerByName(nameOfFirstPlayer);
        player1.setCurrentCard(cardManager.getCardById(0));
        player1.addWorker(Colors.BLUE);
        player1.addWorker(Colors.BLUE);

        Player player2 = match.getPlayerByName(nameOfSecondPlayer);
        player2.setCurrentCard(cardManager.getCardById(1));
        player2.addWorker(Colors.RED);
        player2.addWorker(Colors.RED);

        match.setCurrentPlayer(nameOfFirstPlayer);

        try {
            match.getLocation().setLocation(match.getIsland().getCell(0, 0), player1.getWorkers().get(0));
            match.getLocation().setLocation(match.getIsland().getCell(1, 0), player1.getWorkers().get(1));
            match.getLocation().setLocation(match.getIsland().getCell(1, 1), player2.getWorkers().get(0));
            match.getLocation().setLocation(match.getIsland().getCell(3, 1), player2.getWorkers().get(1));
        } catch (WorkerAlreadyPresentException e) {
            e.printStackTrace();
        } catch (CellOutOfBoundsException e) {
            e.printStackTrace();
        }
        return match;
    }

    @Before
    public void setUp() {
        virtualView = new VirtualViewForTest(new Server());
        controller = new Controller(virtualView);
        cardManager = CardManager.initCardManager();
        hashMapOfNewPlayers = new HashMap<>();
        hashMapOfDisplayMessage = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat(Configuration.formatDate);
        try {
            hashMapOfNewPlayers.put(nameOfFirstPlayer, dateFormat.parse("01/01/1999"));
            hashMapOfNewPlayers.put(nameOfSecondPlayer, dateFormat.parse("01/01/1996"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        MatchHistory.deleteMatch(0);
    }

    private boolean verifyDisplayMessageCall(TypeOfMessage typeOfMessage) {
        return hashMapOfDisplayMessage.get(typeOfMessage) != null;
    }

    private void cleanDisplayMessageCall() {
        hashMapOfNewPlayers = new HashMap<>();
    }

    @Test
    public void startNewMatchTest() {
        controller.update(new Message("ALL", TypeOfMessage.START_MATCH, hashMapOfNewPlayers));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.CHOOSE_GAME_CARDS));
    }

    @Test
    public void setCardToGameTest() {
        controller.update(new Message("ALL", TypeOfMessage.START_MATCH, hashMapOfNewPlayers));
        List<Integer> listOfSelectedCard = new ArrayList<>();
        listOfSelectedCard.add(0);
        listOfSelectedCard.add(1);
        controller.update(new Message("ALL", TypeOfMessage.SET_CARDS_TO_GAME, listOfSelectedCard));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.CHOOSE_PERSONAL_CARD));
    }

    @Test
    public void setCardToPlayerTest_firstPlayer() {
        controller.update(new Message("ALL", TypeOfMessage.START_MATCH, hashMapOfNewPlayers));
        List<Integer> listOfSelectedCard = new ArrayList<>();
        listOfSelectedCard.add(0);
        listOfSelectedCard.add(1);
        controller.update(new Message("ALL", TypeOfMessage.SET_CARDS_TO_GAME, listOfSelectedCard));
        controller.update(new Message(nameOfFirstPlayer, TypeOfMessage.SET_CARD_TO_PLAYER, 0));
        controller.update(new Message(nameOfSecondPlayer, TypeOfMessage.SET_CARD_TO_PLAYER, 1));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.FORCED_CARD));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.CHOOSE_FIRST_PLAYER));
    }

    @Test
    public void setFirstPlayer_firstPlayer() {
        controller.update(new Message("ALL", TypeOfMessage.START_MATCH, hashMapOfNewPlayers));
        controller.update(new Message(nameOfSecondPlayer, TypeOfMessage.SET_FIRST_PLAYER, nameOfFirstPlayer));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.CHOOSE_POSITION_OF_WORKERS));

    }


    @Test
    public void setPositionOfWorker_testChoosePositionOfWorkersMessage() {
        Match match = Mockito.spy(new Match(0));
        match.createPlayer(nameOfFirstPlayer, new Date());
        match.createPlayer(nameOfSecondPlayer, new Date());
        match.setCurrentPlayer(nameOfFirstPlayer);
        try {
            FieldSetter.setField(controller, controller.getClass().getDeclaredField("match"), match);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        List<CoordinatesMessage> coordinatesMessageList = new ArrayList<>();
        coordinatesMessageList.add(new CoordinatesMessage(0,1));
        coordinatesMessageList.add(new CoordinatesMessage(1,1));
        controller.update(new Message("ALL", TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(Colors.BLUE,  coordinatesMessageList)));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.CHOOSE_POSITION_OF_WORKERS));
    }

    @Test
    public void setPositionOfWorker_testInitializedMatch() {
        Match match = Mockito.spy(new Match(0));
        match.createPlayer(nameOfFirstPlayer, new Date());
        match.getPlayerByName(nameOfFirstPlayer).setCurrentCard(cardManager.getCardById(0));
        match.createPlayer(nameOfSecondPlayer, new Date());
        match.getPlayerByName(nameOfSecondPlayer).setCurrentCard(cardManager.getCardById(1));
        match.setCurrentPlayer(nameOfFirstPlayer);
        try {
            FieldSetter.setField(controller, controller.getClass().getDeclaredField("match"), match);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        List<CoordinatesMessage> coordinatesMessageList = new ArrayList<>();
        coordinatesMessageList.add(new CoordinatesMessage(1,1));
        coordinatesMessageList.add(new CoordinatesMessage(2,1));
        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(Colors.BLUE,  coordinatesMessageList)));
        coordinatesMessageList = new ArrayList<>();
        coordinatesMessageList.add(new CoordinatesMessage(3,1));
        coordinatesMessageList.add(new CoordinatesMessage(2,2));
        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(Colors.RED,  coordinatesMessageList)));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.INIT_TURN));
    }

    @Test
    public void simulateTurn_testTurn() {
        Match match = createFakeMatch();

        try {
            FieldSetter.setField(controller, controller.getClass().getDeclaredField("match"), match);
            FieldSetter.setField(controller, controller.getClass().getDeclaredField("turnManager"), new TurnManager(match, virtualView));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.SELECT_WORKER, 0));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.NEXT_PHASE_AVAILABLE));

        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.RETRIEVE_CELL_FOR_MOVE));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.AVAILABLE_CELL_FOR_MOVE));

        cleanDisplayMessageCall();
        CoordinatesMessage moveCoord = new CoordinatesMessage(0, 1);
        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.MOVE_WORKER, moveCoord));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.NEXT_PHASE_AVAILABLE));

        cleanDisplayMessageCall();
        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.RETRIEVE_CELL_FOR_BUILD));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.AVAILABLE_CELL_FOR_BUILD));

        CoordinatesMessage buildCoord = new CoordinatesMessage(0,0);
        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.BUILD_CELL, new TuplaGenerics<>(Component.FIRST_LEVEL, buildCoord)));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.END_TURN));

    }

    @Test
    public void restoreMatch_restoreOldMatch() {
        Match match = createFakeMatch();

        MatchHistory.saveMatch(match);

        List<String> namesOfPlayers = new ArrayList<>();
        namesOfPlayers.add(nameOfFirstPlayer);
        namesOfPlayers.add(nameOfSecondPlayer);
        JsonObject jsonMatch = MatchHistory.retrieveMatchFromNames(namesOfPlayers);

        try {
            FieldSetter.setField(controller, controller.getClass().getDeclaredField("oldMatch"), jsonMatch);
        } catch (NoSuchFieldException e) {}

        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.RESTORE_MATCH, true));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.RESTORED_MATCH));
        MatchHistory.deleteMatch(0);
    }

    @Test
    public void restoreMatch_createNewMatch() {
        Match match = createFakeMatch();
        try {
            FieldSetter.setField(controller, controller.getClass().getDeclaredField("match"), match);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        controller.update(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.RESTORE_MATCH, false));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.STARTED_MATCH));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.CHOOSE_GAME_CARDS));
    }
}
