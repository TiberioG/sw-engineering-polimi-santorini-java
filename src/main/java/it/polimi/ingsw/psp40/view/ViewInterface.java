package it.polimi.ingsw.psp40.view;


import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Player;

import java.util.HashMap;
import java.util.List;

/**
 * metto qui tutte le signature dei metodi comuni si aper CLI e GUI
 */
public interface ViewInterface {

    void displaySetup();
    void displaySetupFailure();
    void displayLogin();
    void displayLoginSuccessful();
    void displayLoginFailure(String details);
    void displayUserJoined(String details);
    void displayAddedToQueue(String details);
    void displayStartingMatch();
    void displayDisconnected(String details);
    void displayGenericMessage(String message);
    void displayCardSelection(HashMap<Integer, Card> cards, int numPlayers);
    void displayChoicePersonalCard(List<Card> availableCards) ;

    void displayCardInGame(List<Card> cardInGame);
    void displaySetInitialPosition(List<Player> playerList);
    void displayAskFirstPlayer(List<Player> allPlayers);
    void displayChoiceOfAvailablePhases(List<Phase> phaseList);
    void displayChoiceOfAvailableCellForMove();
    void displayChoiceSelectionOfWorker();
    void displayChoiceOfAvailableCellForBuild();
    void displayMoveWorker();
    void displayBuildBlock();
}


