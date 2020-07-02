package it.polimi.ingsw.psp40.view;


import it.polimi.ingsw.psp40.model.Card;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Player;

import java.util.HashMap;
import java.util.List;

public interface ViewInterface {

    /**
     * Method that allows the display of the form for entering server data
     */
    void displaySetup();

    /**
     * Method that allows the display the failure of the setup with server
     */
    void displaySetupFailure();

    /**
     * Method that allows the display of the form for entering user data
     */
    void displayLogin();

    /**
     * Method that allows the display of successfully login of the user
     */
    void displayLoginSuccessful();

    /**
     * Method that allows the display of user login failure
     *
     * @param details the details of the failure
     */
    void displayLoginFailure(String details);

    /**
     * Method that allows you to view the arrival of a new player in the match
     *
     * @param playerJoined    the name of the new player
     * @param remainingPlayer the players required remaining to start the match
     */
    void displayUserJoined(String playerJoined, Integer remainingPlayer);

    /**
     * Method that allows you to view the arrival of a new player in the match in a existing lobby
     *
     * @param otherPlayer     the name of the new others player
     * @param remainingPlayer the players required remaining to start the match
     */
    void displayAddedToQueue(List<String> otherPlayer, Integer remainingPlayer);

    /**
     * Method that allows you to propose to the user the recovery of an old game
     */
    void displayProposeRestoreMatch();

    /**
     * Method that allows you to see that the game is starting
     */
    void displayStartingMatch();

    /**
     * Method that allows you to view the disconnection of a player
     *
     * @param username the name of disconnected player
     */
    void displayDisconnectedUser(String username);

    /**
     * Method that notifies disconnection from the server
     */
    void displayDisconnected();

    /**
     * Method that allows the display of a generic message
     */
    void displayGenericMessage(String message);

    /**
     * Method that allows the display of the form for the choice of cards to use in the match
     *
     * @param cards      a map that contains the information of the available cards
     * @param numPlayers the number of players in the match that allows you to know the number of cards to select
     */
    void displayCardSelection(HashMap<Integer, Card> cards, int numPlayers);

    /**
     * Method that allows the display of the form for the choice of the personal card
     *
     * @param availableCards a list with the available cards
     */
    void displayChoicePersonalCard(List<Card> availableCards);

    /**
     * Method that notifies the automatic assignment of the personal card
     *
     * @param card the assigned card
     */
    void displayForcedCard(Card card);

    /**
     * Method that allows you to view the map for choosing the initial positions of the workers
     *
     * @param playerList a list of the player that contains the already selected color of the worker
     */
    void displaySetInitialPosition(List<Player> playerList);

    /**
     * Method that allows the display of the form for choosing the first player of the match
     *
     * @param allPlayers list of players to choose from
     */
    void displayAskFirstPlayer(List<Player> allPlayers);

    /**
     * Method that allows you to choose an available phase of your turn
     *
     * @param allPlayers list of players to choose from
     */
    void displayChoiceOfAvailablePhases();

    /**
     * Method that allows you to choose an available cell for move phase
     */
    void displayChoiceOfAvailableCellForMove();

    /**
     * Method that allows you to choose a worker for the current turn
     */
    void displayChoiceSelectionOfWorker();

    /**
     * Method that allows you to choose an available cell for build phase
     */
    void displayChoiceOfAvailableCellForBuild();

    /**
     * Method that allows you to notify the location update for the map update
     */
    void displayLocationUpdated();

    /**
     * Method that allows you to notify the update of a cell
     */
    void displayCellUpdated(Cell cell);

    /**
     * Method that allows you to view players' updates within the match
     */
    void displayPlayersUpdated();

    /**
     * Method of displaying the victory message
     */
    void displayWinnerMessage();

    /**
     * Method of displaying the losing message
     *
     * @param winningPlayer the player who has won the match
     */
    void displayLoserMessage(Player winningPlayer);

    /**
     * Method of displaying the losing of other player
     *
     * @param losingPlayer the player who has lose
     */
    void displayLoserPlayer(Player losingPlayer);

    /**
     * Method that notifies the end of current turn
     */
    void displayEndTurn();

    /**
     * Method that allows you to display the lobby
     */
    void displayLobbyCreated(String playersWaiting);

    /**
     * Method that allows the display of a recovered match
     */
    void displayRestoredMatch();


}


