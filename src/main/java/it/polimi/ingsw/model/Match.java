package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.PlayerNotPresentException;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is the class for the Match
 * @author tiberioG
 */
public class Match {
    /* Attributes */
    private int matchID;
    private Island island;
    private List<Player> listPlayers = new ArrayList<>();
    private Player currentPlayer ;
    private List<String> listCards = new ArrayList<>(); // qui considero solo le cards in gioco con il loro string ID

    /**
     * Constructor
     */
    public Match(int matchID){
        this.matchID = matchID;
        this.island = new Island();
    }

    /* Methods */

    public int getMatchID() {
        return matchID;
    }
    public List<Player> getPlayers() {
        return listPlayers;
    }
    public Island getIsland() {
        return island;
    }
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    public List<String> getCards() {
        return listCards;
    }

    public void addCard(String card){
        this.listCards.add(card);
    }


    /**
     * Method to add a player to the list of players of the Match
     * @param Player player to be added to the list
     */
    public void addPlayer(Player player) {
        this.listPlayers.add(player);
    }

    /**
     * Method to set the current player of the match, only if it exists in the array of players of the match.
     * @param currentPlayer player object that you want to add as current player
     * @return returns the index in the array of players of the current player just added, else throws an exception
     */
    public int setCurrentPlayer(Player currentPlayer) throws PlayerNotPresentException {
        if (listPlayers.contains(currentPlayer)){
            this.currentPlayer = currentPlayer;
            return listPlayers.indexOf(currentPlayer);
        } else {
            throw new PlayerNotPresentException();
        }
    }


}
