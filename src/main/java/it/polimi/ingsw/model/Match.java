package it.polimi.ingsw.model;
import java.util.List;
import java.util.ArrayList;

/**
 * This is the class for the Match
 * @author tiberioG
 */
public class Match {
    /**
     * Constructor
     * @param gameID unique id of the Match
     */
    public Match(int gameID){
        this.gameID = gameID;
        this.island = new Island();
    }

    /* Attributes */

    private int gameID;
    private int numbPlayers; // secondo me ne possiamo fare a meno usando la Arraylist
    private Island island;
    private List<Player> listPlayers = new ArrayList<>();
    private Player currentPlayer ;
    private List<String> listCards = new ArrayList<>(); // qui considero solo le cards in gioco con il loro string ID


    /* Methods */

    public int getGameID() {
        return gameID;
    }
    public int getNumbPlayers() {
        return numbPlayers;
    }
    public List<Player> getPlayers() {
        return listPlayers;
    }
    public Island getBoard() {
        return island;
    }
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    public List<String> getCards() {
        return listCards;
    }

    public void setNumbPlayers(int numbPlayers) {
        this.numbPlayers = numbPlayers;
    }
    public void addCard(String card){
        this.listCards.add(card);
    }

    /**
     * Method to set the current player of the match, if it exists in  the array of players of the match.
     * @param currentPlayer player object that you want to add as current player
     * @return if the currentPlayer was successfully added, returns the index in the array of players, else returns -1
     */
    public int setCurrentPlayer(Player currentPlayer){
        if (listPlayers.contains(currentPlayer)){
            this.currentPlayer = currentPlayer;
            return listPlayers.indexOf(currentPlayer);
        }
        else{return -1;}
    }

    /**
     * Method to add a player to the list of player of the Match
     * @param player select the player object you wanna add to the Match
     * @return returns false if you're adding more palyers than you have selected to have
     */
    public boolean addPlayer(Player player){
        if (this.listPlayers.size() < this.numbPlayers){
            return false;
        }
        else {
            this.listPlayers.add(player);
            return true;
        }
    }
    /**
     * ALTERNATIVE: method to set the list of player passing a list
     * @param listPlayers pass an arraylist of players
     */
    public void setListPlayers(List<Player> listPlayers) {
        this.listPlayers = listPlayers;
    }

}
