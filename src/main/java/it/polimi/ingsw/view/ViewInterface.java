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
    void displayLoginSuccessful(String prova);
    void displayLoginFailure();
    void displayCardSelection(List<Card> cards, int numPlayers);
    void displaySetInitialPosition(List<CoordinatesMessage> coordinatesMessageList);
}


