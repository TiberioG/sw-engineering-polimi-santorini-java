package it.polimi.ingsw.psp40.controller;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.Listener;
import it.polimi.ingsw.psp40.commons.messages.*;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.model.*;
import it.polimi.ingsw.psp40.network.server.VirtualView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class manages the logic of the game
 *
 * @author Vito96
 */
public class Controller implements Listener<Message> {
    private Match match;
    private CardManager cardManager;
    private VirtualView virtualView;
    private TurnManager turnManager;
    private JsonObject oldMatch;

    /**
     * Constructor
     *
     * @param virtualView
     */
    public Controller(VirtualView virtualView) {
        this.virtualView = virtualView;
        cardManager = CardManager.initCardManager();
    }

    /**
     * Method for create a new match with a specified matchId
     *
     * @param matchID the identifier of the new match
     */
    private void createNewMatch(int matchID) {
        virtualView.setMatchID(matchID);
        match = new Match(matchID, this.virtualView);
    }

    /**
     * Method for creating and adding a player to the match
     *
     * @param name     the name of the new player
     * @param birthday the birthday of the new player
     */
    private void addPlayerToMatch(String name, Date birthday) {
        match.createPlayer(name, birthday);
    }


    /**
     * Method for adding the available selected cards to te match
     *
     * @param cardIdList a list of {@link Integer} which rappresent the identifier of the cards
     */
    private void addCardToMatch(List<Integer> cardIdList) {
        cardIdList.forEach(id -> match.addCard(cardManager.getCardById(id)));
    }

    /**
     * Method for associate a card to a specified player
     *
     * @param name   the name of the specified player
     * @param cardId the identifier of the card
     */
    private void addCardToPlayer(String name, int cardId) {
        String nameOfOwnerPlayer = match.getPlayers().stream().filter(player -> player.getCurrentCard() != null && player.getCurrentCard().getId() == cardId).map(Player::getName).findFirst().orElse(null);
        if (nameOfOwnerPlayer == null) {
            match.getPlayerByName(name).setCurrentCard(cardManager.getCardById(cardId));
        }
    }

    /**
     * Method for creating an instance of {@link TurnManager}
     */
    private void initTurnManager() {
        turnManager = new TurnManager(match, virtualView);
    }

    /**
     * Method for retrieve and send the selectable card to a specified player
     *
     * @param nameOfPlayer the name of the specified player
     */
    private void sendSelectableCards(String nameOfPlayer) {
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

    /**
     * Method for check if exist a backuped match with the same players
     *
     * @param playersData a map which contains the player data
     */
    private boolean checkExistanceOfOldMatch(Map<String, String> playersData) {
        List<String> playerNames = new ArrayList<>();
        playersData.forEach((username, date) -> {
            playerNames.add(username);
        });
        oldMatch = MatchHistory.retrieveMatchFromNames(playerNames);
        return oldMatch != null;
    }

    /**
     * Method that allows the creation of a new match
     *
     * @param message message containing player information
     */
    @SuppressWarnings("unused")
    private void startMatch(Message message) {
        Map<String, String> playersData = (Map<String, String>) message.getPayload(Map.class);

        createNewMatch(message.getMatchID());
        playersData.forEach((username, date) -> {
            try {
                addPlayerToMatch(username, new SimpleDateFormat(Configuration.formatDate).parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        match.buildOrderedList(Comparator.comparing(Player::getBirthday).reversed());
        match.setCurrentPlayer(match.getPlayers().get(0));

        if (checkExistanceOfOldMatch(playersData)) {
            virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.PROPOSE_RESTORE_MATCH));
        } else {
            virtualView.displayMessage(new Message("ALL", TypeOfMessage.STARTED_MATCH));
            virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_GAME_CARDS, new ChooseGameCardMessage(cardManager.getCardMap(), match.getPlayers().size())));
        }
    }

    /**
     * Method that allows the recovery of a backuped match or allows the start of a new match
     *
     * @param message message containing the information for creating or retrieving the match
     */
    @SuppressWarnings("unused")
    private void restoreMatch(Message message) {
        boolean restoreMatch = (boolean) message.getPayload(boolean.class);
        if (restoreMatch) {
            match = MatchHistory.restoreMatch(virtualView, oldMatch);
            virtualView.setMatchID(match.getMatchID());

            List<String> usernames = MatchHistory.getPlayersFromBrokenMatch(oldMatch);
            virtualView.restoreMatch(usernames);

            MatchHistory.restorePlayers(match, oldMatch);

            MatchHistory.restoreIsland(match, oldMatch);

            MatchHistory.restoreMatchProperties(match, oldMatch);

            MatchHistory.restoreCurrentPlayer(match, oldMatch);

            MatchHistory.restoreLocation(match, oldMatch);

            virtualView.displayMessage(new Message("ALL", TypeOfMessage.RESTORED_MATCH));
            initTurnManager();
        } else {
            virtualView.displayMessage(new Message("ALL", TypeOfMessage.STARTED_MATCH));
            virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_GAME_CARDS, new ChooseGameCardMessage(cardManager.getCardMap(), match.getPlayers().size())));
        }
    }

    /**
     * Method that allows the recovery of a backuped match or allows the start of a new match
     *
     * @param message message containing the information for creating or retrieving the match
     */
    @SuppressWarnings("unused")
    private void setCardToGame(Message message) {
        List<Integer> listOfIdCard = (List<Integer>) message.getPayload(new TypeToken<List<Integer>>() {
        }.getType());
        addCardToMatch(listOfIdCard);
        sendSelectableCards(match.getPlayers().get(match.selectNextCurrentPlayer()).getName());
    }


    /**
     * Method that allows the sending and choice of available cards and if all players have chosen the card sends the event for the choice of the first player
     *
     * @param message message containing the information of the choicen card
     */
    @SuppressWarnings("unused")
    private void setCardToPlayer(Message message) {
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
    }

    /**
     * Method that allows to set the first player
     *
     * @param message message containing the information of the first player
     */
    @SuppressWarnings("unused")
    private void setFirstPlayer(Message message) {
        String nameOfFirstPlayer = (String) message.getPayload(String.class);
        match.setCurrentPlayer(nameOfFirstPlayer); //mette il primo player selezionato dalla view
        match.rescaleListFromCurrentPlayer();
        virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_POSITION_OF_WORKERS, match.getPlayers())); //getting first player is the fist who position workers
    }

    /**
     * Method that allows you to position the {@link Worker} on the {@link Island}
     *
     * @param message message containing the information of the positioning
     */
    @SuppressWarnings("unused")
    private void setPositionOfWorker(Message message) {
        SelectWorkersMessage selectWorkersMessage = (SelectWorkersMessage) message.getPayload(SelectWorkersMessage.class);
        selectWorkersMessage.getPositionOfWorkers().forEach(position -> {
            Worker worker = match.getCurrentPlayer().addWorker(selectWorkersMessage.getColorOfWorkers());
            try {
                match.getLocation().setLocation(match.getIsland().getCell(position.getX(), position.getY()), worker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (match.selectNextCurrentPlayer() != 0) {
            virtualView.displayMessage(new Message(match.getCurrentPlayer().getName(), TypeOfMessage.CHOOSE_POSITION_OF_WORKERS, match.getPlayers()));
        } else {
            initTurnManager();
        }
    }

    /**
     * Method that allows the selection of the {@link Worker} for the current {@link Turn}
     *
     * @param message message containing the information of the selected {@link Worker}
     */
    @SuppressWarnings("unused")
    private void selectWorker(Message message) {
        turnManager.selectWorker(match.getCurrentPlayer().getWorkers().get((Integer) message.getPayload(Integer.class)));
    }

    /**
     * Method that allows you to retrieve the available cells to move the previously selected {@link Worker}
     *
     * @param message message present for use through reflection
     */
    @SuppressWarnings("unused")
    private void retrieveCellForMove(Message message) {
        turnManager.getAvailableCellForMove();
    }

    /**
     * Method that allows you the {@link Worker} selected on the turn
     *
     * @param message message present for use through reflection
     */
    @SuppressWarnings("unused")
    private void moveWorker(Message message) {
        CoordinatesMessage coordinatesMessage = (CoordinatesMessage) message.getPayload(CoordinatesMessage.class);
        try {
            turnManager.move(match.getIsland().getCell(coordinatesMessage.getX(), coordinatesMessage.getY()));
        } catch (SantoriniException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that allows you to retrieve the available cells to build the previously selected {@link Worker}
     *
     * @param message message present for use through reflection
     */
    @SuppressWarnings("unused")
    private void retrieveCellForBuild(Message message) {
        turnManager.getAvailableCellForBuild();
    }


    /**
     * Method that allows you to build a {@link Component} on a specific {@link Cell} available for construction
     *
     * @param message message that contains {@link Cell} and {@link Component} information for the construction
     */
    @SuppressWarnings("unused")
    private void buildCell(Message message) {
        TuplaGenerics<Component, CoordinatesMessage> tuplaForBuildComponent = (TuplaGenerics<Component, CoordinatesMessage>) message.getPayload(new TypeToken<TuplaGenerics<Component, CoordinatesMessage>>() {
        }.getType());
        try {
            turnManager.build(tuplaForBuildComponent.getFirst(), match.getIsland().getCell(tuplaForBuildComponent.getSecond().getX(), tuplaForBuildComponent.getSecond().getY()));
        } catch (SantoriniException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that allows you to request to finish the turn
     *
     * @param message message present for use through reflection
     */
    @SuppressWarnings("unused")
    private void endTurn(Message message) {
        turnManager.endTurn();
    }

    /**
     * Method that allows to call methods through reflections according to the type of the {@link Message} received
     *
     * @param message message containing the information received
     */
    @Override
    public void update(Message message) {
        TypeOfMessage typeOfMessage = message.getTypeOfMessage();
        try {
            Method methodToInvoke = this.getClass().getDeclaredMethod(typeOfMessage.toString(), Message.class);
            methodToInvoke.invoke(this, message);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
