package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.messages.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.server.VirtualView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Listener<Message> {
    private Match match;
    private CardManager cardManager;
    private VirtualView virtualView;
    private TurnManager turnManager;

    /**
     *
     * @param virtualView npn
     */
    public Controller(VirtualView virtualView) {
        this.virtualView = virtualView;
        cardManager = CardManager.initCardManager();
    }

    private void createNewMatch() {
        match = new Match(new Date().hashCode(), this.virtualView);
        virtualView.setMatch(match);
    }

    private void addPlayerToMatch(String name, Date birthday) {
        match.createPlayer(name, birthday);
    }

    private void addCardToMatch(List<Integer> cardIdList) {
        cardIdList.stream().forEach(id -> match.addCard(cardManager.getCardById(id)));
    }

    private void addCardToPlayer(String name, int cardId) {
        //todo gestire controllo che la carta non sia associata ad altri player
        for (Player player : match.getPlayers()) {
            if (player.getName().equals(name)) player.setCurrentCard(cardManager.getCardById(cardId));
        }
    }

    private void initTurnManager() {
        turnManager = new TurnManager(match, virtualView);
    }


    @Override
    public void update(Message message) {
        TypeOfMessage type = message.getTypeOfMessage();
        switch (type) {
            case START_MATCH: //if I receive this i'm ready to create a new match
                createNewMatch();
                ((Map<String, String>)message.getPayload(Map.class)).forEach((username, date) -> {
                    try {
                        addPlayerToMatch(username, new SimpleDateFormat("MMMM d, yyyy").parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });
                virtualView.displayMessage(new Message(match.getPlayers().get(0).getName(), TypeOfMessage.CHOOSE_GAME_CARDS, new ChooseGameCardMessage(cardManager.getCardMap(), match.getPlayers().size())));
                break;
            case SETTED_CARDS_TO_GAME: //if i receive this, the card for the game have been chosen, now I have to associate them to players
                List<Integer> listOfIdCard = (List) message.getPayload(List.class);
                addCardToMatch(listOfIdCard);
                virtualView.displayMessage(new Message(match.getPlayers().get(0).getName(), TypeOfMessage.CHOOSE_PLAYERS_CARD, match.getCards()));
                break;
            case SET_CARD_TO_PLAYER:
                Tupla tuplaSetPlayer = (Tupla) message.getPayload(Tupla.class);
                addCardToPlayer((String) tuplaSetPlayer.getFirst(), (Integer) tuplaSetPlayer.getSecond());

                if (match.selectNextCurrentPlayer() != 0) {
                    virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_PLAYERS_CARD, match.getCards()));
                } else {
                    virtualView.displayMessage(new Message(match.getPlayers().get(0).getName(), TypeOfMessage.CHOOSE_FIRST_PLAYER, match.getCards()));
                }
                break;
            case SET_FIRST_PLAYER:
                String nameOfFirstPlayer = (String)message.getPayload(String.class);
                match.buildOrderedList(Comparator.comparing(Player::getBirthday));
                match.setCurrentPlayer(nameOfFirstPlayer);
                virtualView.displayMessage(new Message(match.getPlayers().get(0).getName(), TypeOfMessage.CHOOSE_POSITION_OF_WORKERS, match.getCards()));
                break;
            case SET_POSITION_OF_WORKER:
                SelectWorkersMessage selectWorkersMessage = (SelectWorkersMessage) message.getPayload(SelectWorkersMessage.class);
                selectWorkersMessage.getPositionOfWorkers().stream().forEach(position -> {
                    Worker worker = match.getCurrentPlayer().addWorker(selectWorkersMessage.getColorOfWorkers());
                    try {
                        match.getLocation().setLocation(match.getIsland().getCell(position.getX(),position.getY()), worker);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


                if (match.selectNextCurrentPlayer() != 0) {
                    virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_POSITION_OF_WORKERS, match.getCards()));
                } else {
                    initTurnManager();
                }
                break;

            case SELECT_WORKER:
                Tupla tuplaSelectWorker = (Tupla) message.getPayload(Tupla.class);
                turnManager.selectWorker(match.getCurrentPlayer().getWorkers().get((int) tuplaSelectWorker.getSecond()));
                break;
            case RETRIEVE_CELL_FOR_MOVE:
                turnManager.getAvailableCellForMove();
                break;
            case MOVE_WORKER:
                //turnManager.move();
                break;
            case RETRIEVE_CELL_FOR_BUILD:
                turnManager.getAvailableCellForBuild();
                break;
            case BUILD_CELL:
                //turnManager.build();
                break;

        }
    }
}
