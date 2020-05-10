package it.polimi.ingsw.psp40.controller;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.Listener;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.model.*;
import it.polimi.ingsw.psp40.network.server.VirtualView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        cardIdList.forEach(id -> match.addCard(cardManager.getCardById(id)));
    }

    private void addCardToPlayer(String name, int cardId) {
        String nameOfOwnerPlayer = match.getPlayers().stream().filter(player -> player.getCurrentCard() != null && player.getCurrentCard().getId() == cardId).map(Player::getName).findFirst().orElse(null);
        if (nameOfOwnerPlayer == null) {
            match.getPlayerByName(name).setCurrentCard(cardManager.getCardById(cardId));
        }
    }

    private void initTurnManager() {
        turnManager = new TurnManager(match, virtualView);
    }

    private void sendSelectableCards (String nameOfPlayer) {
        List<TuplaGenerics<Card, String>> listOfSelectableCards = new ArrayList<>();

        match.getCards().forEach(card -> {
            String nameOfOwnerPlayer = match.getPlayers().stream().filter(player -> {
                Card cardOfPlayer = player.getCurrentCard();
                if (cardOfPlayer != null && cardOfPlayer.getId() == card.getId()) {
                    return true;
                } else return false;
            }).map(Player::getName).findFirst().orElse(null);
            listOfSelectableCards.add(new TuplaGenerics<>(card, nameOfOwnerPlayer));
        });

        virtualView.displayMessage(new Message(nameOfPlayer, TypeOfMessage.CHOOSE_PERSONAL_CARD, listOfSelectableCards));
    }


    @Override
    public void update(Message message) {
        TypeOfMessage type = message.getTypeOfMessage();
        switch (type) {
            case START_MATCH: //if I receive this i'm ready to create a new match
                createNewMatch();
                ((Map<String, String>)message.getPayload(Map.class)).forEach((username, date) -> {
                    try {
                        addPlayerToMatch(username, new SimpleDateFormat(Configuration.formatDate).parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });
                match.setCurrentPlayer(match.getPlayers().get(0));
                virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_GAME_CARDS, new ChooseGameCardMessage(cardManager.getCardMap(), match.getPlayers().size())));
                break;
            case SET_CARDS_TO_GAME: //if i receive this, the card for the game have been chosen, now I have to associate them to players
                List<Integer> listOfIdCard = (List<Integer>) message.getPayload(new TypeToken<List<Integer>>() {}.getType());
                addCardToMatch(listOfIdCard);
                sendSelectableCards(match.getPlayers().get(match.selectNextCurrentPlayer()).getName());
                break;
            case SET_CARD_TO_PLAYER:
                addCardToPlayer(message.getUsername(), (Integer) message.getPayload(Integer.class));

                if (match.selectNextCurrentPlayer() != 0) {
                    sendSelectableCards(match.getCurrentPlayer().getName());
                } else {
                    List<Integer> listOfIdCardsAlreadyAssigned = match.getPlayers().stream().filter(player -> player.getCurrentCard() != null).map(player -> player.getCurrentCard().getId()).collect(Collectors.toList());
                    Card cardAvailable = match.getCards().stream().filter(card -> !listOfIdCardsAlreadyAssigned.contains(card.getId())).findFirst().orElse(null);

                    match.getCurrentPlayer().setCurrentCard(cardAvailable);
                    virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.FORCED_CARD, cardAvailable));

                    virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_FIRST_PLAYER, match.getPlayers()));
                }
                break;
            case SET_FIRST_PLAYER:
                String nameOfFirstPlayer = (String)message.getPayload(String.class);
                match.buildOrderedList(Comparator.comparing(Player::getBirthday)); //fa lista ordinata prima
                match.setCurrentPlayer(nameOfFirstPlayer); //mette il primo player selezionato dalla view
                match.rescaleListFromCurrentPlayer();
                virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_POSITION_OF_WORKERS, match.getPlayers())); //getting first player is the fist who position workers
                break;
            case SET_POSITION_OF_WORKER:
                SelectWorkersMessage selectWorkersMessage = (SelectWorkersMessage) message.getPayload(SelectWorkersMessage.class);
                selectWorkersMessage.getPositionOfWorkers().forEach(position -> {
                    Worker worker = match.getCurrentPlayer().addWorker(selectWorkersMessage.getColorOfWorkers());
                    try {
                        match.getLocation().setLocation(match.getIsland().getCell(position.getX(),position.getY()), worker);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                if (match.selectNextCurrentPlayer() != 0) {
                    virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_POSITION_OF_WORKERS, match.getPlayers()));
                } else {
                    initTurnManager();
                }
                break;

            case SELECT_WORKER:
                turnManager.selectWorker(match.getCurrentPlayer().getWorkers().get((Integer) message.getPayload(Integer.class)));
                break;
            case RETRIEVE_CELL_FOR_MOVE:
                turnManager.getAvailableCellForMove();
                break;
            case MOVE_WORKER:
                CoordinatesMessage coordinatesMessage = (CoordinatesMessage) message.getPayload(CoordinatesMessage.class);
                try {
                    turnManager.move(match.getIsland().getCell(coordinatesMessage.getX(), coordinatesMessage.getY()));
                } catch (SantoriniException e) {
                    e.printStackTrace();
                }
                break;
            case RETRIEVE_CELL_FOR_BUILD:
                turnManager.getAvailableCellForBuild();
                break;
            case BUILD_CELL:
                TuplaGenerics<Component, CoordinatesMessage> tuplaForBuildComponent = (TuplaGenerics<Component, CoordinatesMessage>) message.getPayload(new TypeToken<TuplaGenerics<Component,CoordinatesMessage>>() {}.getType());
                try {
                    turnManager.build(tuplaForBuildComponent.getFirst(), match.getIsland().getCell(tuplaForBuildComponent.getSecond().getX(), tuplaForBuildComponent.getSecond().getY()));
                } catch (SantoriniException e) {
                    e.printStackTrace();
                }
                break;
            /*case INIT_NEW_MATCH:
                if (match.isMatchedEnded() == true) {
                    String nameOfWinningPlayer = match.getCurrentPlayer().getName();
                    List<Player> playerList = match.getPlayers();
                    createNewMatch();

                    playerList.forEach(player -> {
                        addPlayerToMatch(player.getName(), player.getBirthday());
                    });

                    match.setCurrentPlayer(match.getPlayerByName(nameOfWinningPlayer));
                    match.rescaleListFromCurrentPlayer();
                    virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_GAME_CARDS, new ChooseGameCardMessage(cardManager.getCardMap(), match.getPlayers().size())));
                };
                break;
            */
        }
    }
}
