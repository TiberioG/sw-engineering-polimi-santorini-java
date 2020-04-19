package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.Tupla;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Controller implements Listener<Message> {
    private Match match;
    private CardManager cardManager;
    private VirtualView virtualView;
    private TurnManager turnManager;

    /**
     *
     * @param virtualView
     */
    public Controller(VirtualView virtualView) {
        this.virtualView = virtualView;
        cardManager = CardManager.initCardManager();
    }

    private void createNewMatch() {
        match = new Match(new Date().hashCode(), this.virtualView);
    }

    private void addPlayerToMatch(String name, Date birthday) {
        match.createPlayer(name, birthday);
    }

    private void addCardToMatch(List<Integer> cardIdList) {
        cardIdList.stream().forEach(id -> match.addCard(cardManager.getCardById(id)));
    }

    private void addCardToPlayer(String name, int cardId) {
        for (Player player : match.getPlayers()) {
            if (player.getName().equals(name)) player.setCurrentCard(cardManager.getCardById(cardId));
        }
    }

    private void initTurManager() {
        turnManager = new TurnManager(match, virtualView);
    }


    @Override
    public void update(Message message) {
        TypeOfMessage type = message.getTypeOfMessage();

        switch (type) {
            case CREATE_MATCH: //if I receive this i'm ready to create a new match
                createNewMatch();
                break;
            case ADD_PLAYER: //if i receive this
                Tupla tuplaPlayerData = (Tupla) message.getObjectFromJson(Tupla.class);
                addPlayerToMatch((String) tuplaPlayerData.getFirst(), (Date) tuplaPlayerData.getSecond());
                break;
            case CARDS_SET_GAME: //if i receive this
                List<Integer> listOfIdCard = (List) message.getObjectFromJson(List.class);
                addCardToMatch(listOfIdCard);
                break;
            case CARD_SET_PLAYER:
                Tupla tuplaSetPlayer = (Tupla) message.getObjectFromJson(Tupla.class);
                addCardToPlayer((String) tuplaSetPlayer.getFirst(), (Integer) tuplaSetPlayer.getSecond());
                break;
            case SELECT_WORKER:
                Tupla tuplaSelectWorker = (Tupla) message.getObjectFromJson(Tupla.class);
                turnManager.selectWorker(match.getCurrentPlayer().getWorkers().get((int) tuplaSelectWorker.getSecond()));
                break;
        }
    }
}
