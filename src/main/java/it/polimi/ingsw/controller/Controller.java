package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.Tupla;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.Date;

public class Controller implements Listener<Message> {
    private Match match;
    private CardManager cardManager;
    private VirtualView virtualView;

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


    @Override
    public void update(Message message) {
        TypeOfMessage type = message.getTypeOfMessage();

        switch (type) {
            case CREATED_PLAYER:
                Player player = (Player) message.getObjectFromJson(Player.class);
                System.out.println(player);
                break;
            case CREATE_MATCH: //if I receive this i'm ready to create a new match
                createNewMatch();
                break;
            case ADD_PLAYER: //if i receive this
                Tupla tupla = (Tupla) message.getObjectFromJson(Tupla.class);
                addPlayerToMatch((String) tupla.getFirst(), (Date) tupla.getSecond());
        }
    }
}
