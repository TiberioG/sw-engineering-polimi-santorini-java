package it.polimi.ingsw.psp40.view;


import it.polimi.ingsw.psp40.controller.Phase;
import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.model.Worker;

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
    void displayUserJoined(String playerJoined, Integer remainingPlayer);
    void displayAddedToQueue(List<String> otherPlayer, Integer remainingPlayer);
    void displayStartingMatch();
    void displayDisconnected(String details);
    void displayGenericMessage(String message);
    void displayCardSelection(HashMap<Integer, Card> cards, int numPlayers);
    void displayChoicePersonalCard(List<Card> availableCards) ;

    void displayCardInGame(List<Card> cardInGame);
    void displayForcedCard(Card card);
    void displaySetInitialPosition(List<Player> playerList);
    void displayAskFirstPlayer(List<Player> allPlayers);
    void displayChoiceOfAvailablePhases();
    void displayChoiceOfAvailableCellForMove();
    void displayChoiceSelectionOfWorker();
    void displayChoiceOfAvailableCellForBuild();
    void displayLocationUpdated();
    void displayCellUpdated(Cell cell);
    void displayWinnerMessage();
    void displayLoserMessage(Player winningPlayer);
    void displayLoserPlayer(Player player);
    void displayEndTurn();

    void displayLobbyCreated(String playersWaiting);


}


