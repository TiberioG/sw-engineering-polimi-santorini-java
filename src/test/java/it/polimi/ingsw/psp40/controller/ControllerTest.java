package it.polimi.ingsw.psp40.controller;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.SelectWorkersMessage;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.model.*;
import it.polimi.ingsw.psp40.network.server.Server;
import it.polimi.ingsw.psp40.network.server.VirtualView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
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
    private String nameOfFirstPlayer = "one";
    private String nameOfSecondPlayer = "one";


    public class VirtualViewForTest extends VirtualView {
        public VirtualViewForTest(Server server) {
            super(server);
        }

        @Override
        public void displayMessage(Message message) {
            hashMapOfDisplayMessage.put(message.getTypeOfMessage(), message);
        }

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

    private boolean verifyDisplayMessageCall(TypeOfMessage typeOfMessage) {
        return hashMapOfDisplayMessage.get(typeOfMessage) != null;
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
        match.createPlayer("player1", new Date());
        match.createPlayer("player2", new Date());
        match.setCurrentPlayer("player1");
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
    public void setPositionOfWorker_testInitTurn() {
        Match match = Mockito.spy(new Match(0));
        match.createPlayer("player1", new Date());
        match.getPlayerByName("player1").setCurrentCard(cardManager.getCardById(0));
        match.createPlayer("player2", new Date());
        match.getPlayerByName("player2").setCurrentCard(cardManager.getCardById(1));
        match.setCurrentPlayer("player1");
        try {
            FieldSetter.setField(controller, controller.getClass().getDeclaredField("match"), match);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        List<CoordinatesMessage> coordinatesMessageList = new ArrayList<>();
        coordinatesMessageList.add(new CoordinatesMessage(1,1));
        coordinatesMessageList.add(new CoordinatesMessage(2,1));
        controller.update(new Message("ALL", TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(Colors.BLUE,  coordinatesMessageList)));
        coordinatesMessageList = new ArrayList<>();
        coordinatesMessageList.add(new CoordinatesMessage(3,1));
        coordinatesMessageList.add(new CoordinatesMessage(2,2));
        controller.update(new Message("ALL", TypeOfMessage.SET_POSITION_OF_WORKER, new SelectWorkersMessage(Colors.RED,  coordinatesMessageList)));
        assertTrue(verifyDisplayMessageCall(TypeOfMessage.INIT_TURN));
    }}
