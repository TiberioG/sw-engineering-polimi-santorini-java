package it.polimi.ingsw.view;


import it.polimi.ingsw.commons.messages.CoordinatesMessage;

import java.util.List;

/**
 * metto qui tutte le signature dei metodi comuni si aper CLI e GUI
 */
public interface ViewInterface {

    void displaySetup();
    void displaySetupFailure();
    void displayLogin();
    void displayCardSelection();
    void displaySetInitialPosition(List<CoordinatesMessage> coordinatesMessageList);
}


