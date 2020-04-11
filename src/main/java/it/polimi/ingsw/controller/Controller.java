package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Listener;
import it.polimi.ingsw.commons.VirtualViewEventName;
import it.polimi.ingsw.model.Match;

import java.util.Date;

public class Controller implements Listener<VirtualViewEventName> {
    private Match match;

    public Controller() {

    }
    
    public void createNewMatch() {
        match = new Match(new Date().hashCode());
    }

    @Override
    public void update(VirtualViewEventName eventName, Object... objects) {
        switch (eventName) {
            case PLAYER_INFORMATION_ADDED:
                match.createPlayer((String)objects[0], (Date)objects[1]);
            break;
            case CARDS_IN_GAME_SELECTED:
                match.addCard((String)objects[0]);
                match.addCard((String)objects[1]);
                match.addCard((String)objects[2]);
                break;
        }
    }
}
