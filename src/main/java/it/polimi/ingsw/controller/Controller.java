package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.Tupla;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.model.CardManager;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.Date;
import java.util.List;

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
            case START_MATCH: //if I receive this i'm ready to create a new match
                createNewMatch();
                ((List<String>)message.getPayload(List.class)).forEach( username -> addPlayerToMatch(username, null)); // todo modificare tutto mettendo che ricevo anche la data di nascita
                virtualView.displayMessage(new Message(match.getPlayers().get(0).getName(), TypeOfMessage.CHOOSE_GAME_CARDS, cardManager.getCardMap())); // todo: scegliere il primo giocatore correttamente e decidere che payload mandare nel messaggio
                break;
            case CARDS_SET_GAME: //if i receive this
                List<Integer> listOfIdCard = (List) message.getPayload(List.class);
                addCardToMatch(listOfIdCard);
                break;
            case CARD_SET_PLAYER:
                Tupla tuplaSetPlayer = (Tupla) message.getPayload(Tupla.class);
                addCardToPlayer((String) tuplaSetPlayer.getFirst(), (Integer) tuplaSetPlayer.getSecond());
                break;
            case SELECT_WORKER:
                Tupla tuplaSelectWorker = (Tupla) message.getPayload(Tupla.class);
                turnManager.selectWorker(match.getCurrentPlayer().getWorkers().get((int) tuplaSelectWorker.getSecond()));
                break;

        }
    }
}
