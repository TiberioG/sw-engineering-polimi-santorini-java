package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.Message;
import it.polimi.ingsw.commons.Tupla;
import it.polimi.ingsw.commons.TypeOfMessage;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Player;

import java.util.Date;

public class Controller implements Listener<Message> {
    private Match match;
    private CardManager cardManager;

    public Controller() {
        cardManager = CardManager.initCardManager();
    }

    private void createNewMatch() {
        match = new Match(new Date().hashCode());
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
