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
    private Location location;
    private int matchID;
    private Island island;
    private List<Player> listPlayers = new ArrayList<>();
    private Player currentPlayer ;
    private List<String> listCards = new ArrayList<>(); // qui considero solo le cards in gioco con il loro string ID

    /**
     * Constructor
     */
    public Match(int matchID){
        this.matchID = matchID ;
        this.island = new Island();
        this.location = Location.myLocation();
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
     * Method to set the current player of the match, if it exists in  the array of players of the match.
     * @param currentPlayer player object that you want to add as current player
     * @return if the currentPlayer was successfully added, returns the index in the array of players, else returns -1
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
     * Method to add add player to the list of player of the Match
     * @param name name of the player you wanna add to the Match
     * @param birthday birthday of the player you wanna add to the Match
     * @return the reference to the player obj just created
     */
    public Player createPlayer(String name, Date birthday) {
        Player playToAdd = new Player(name,birthday);
        this.listPlayers.add(playToAdd);
        return playToAdd;
    }

    public Location getLocation() {
        return location;
    }
}
