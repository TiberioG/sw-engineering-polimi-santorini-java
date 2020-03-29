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
     * Method to add a player object  to the list of players of the Match
     * @param player player to be added to the list
     */
    public void addPlayer(Player player) {
        this.listPlayers.add(player);
    }

    /**
     * Method to add and add player to the list of player of the Match
     * @param name name of the player you wanna add to the Match
     * @param birthday birthday of the player you wanna add to the Match
     */
    public void createPlayer(String name, Date birthday) {
        this.listPlayers.add(new Player(name,birthday));
    }

    /**
     * OBJECT version of Method to set the current player of the match, only if it exists in the array of players of the match.
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

    /**
     * NAME version of Method to set the current player of the match, only if it exists in the array of players of the match.
     * @param nameCurrent player string name that you want to add as current player
     * @return returns the index in the array of players of the current player just added, else throws an exception
     */

    public int setCurrentPlayerByName(String nameCurrent) throws PlayerNotPresentException {
         //check if player name exists in list
        int exists = 0;
        for (Player listPlayer : listPlayers) {
            if (listPlayer.getName().equals(nameCurrent)) {
                exists++;
            }
        }
        if (exists == 1){
            for (Player listPlayer : listPlayers) {
                if (listPlayer.getName().equals(nameCurrent)) {
                    currentPlayer = listPlayer;
                }
            }
                return listPlayers.indexOf(currentPlayer);
        } else {
            throw new PlayerNotPresentException();
        }
    }

    /**
     * NAME version of Method to get the player given the name
     * @param namePlayer string containing name of player you want to get
     * @return returns the player object
     */
    Player getPlayerByName(String namePlayer) throws PlayerNotPresentException {
        //check if player name exists in list
        int exists = 0;
        Player playerFound = null;

        for (Player listPlayer : listPlayers) {
            if (listPlayer.getName().equals(namePlayer)) {
                exists++;
            }
        }
        if (exists == 1){
            for (Player listPlayer : listPlayers) {
                if (listPlayer.getName().equals(namePlayer)) {
                    playerFound = listPlayer;
                }
            }
            return playerFound;
        } else {
            throw new PlayerNotPresentException();
        }
    }

}
