package it.polimi.ingsw.view;


import it.polimi.ingsw.commons.messages.CoordinatesMessage;
import it.polimi.ingsw.model.Card;

import java.util.List;

/**
 * metto qui tutte le signature dei metodi comuni si aper CLI e GUI
 */
public interface ViewInterface {

    void displaySetup();
    void displaySetupFailure();
    void displayLogin();
    void displayLoginSuccessful();
    void displayLoginFailure();
    void displayHowManyPlayers();
    void displayUserJoined(String details);
    void displayAddedToQueue(String details);
    void displayStartingMatch();
    void displayDisconnected(String details);
    void displayGenericMessage(String message);
    void displayCardSelection(List<Card> cards, int numPlayers);
    void displaySetInitialPosition(List<CoordinatesMessage> coordinatesMessageList);
}


