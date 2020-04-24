package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.exceptions.PlayerNotPresentException;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * This is the class for the Match
 * @author tiberioG
 */
public class Match extends Publisher<Message> {
    /* Attributes */
    private Location location;
    private int matchID;
    private Island island;
    private List<Player> listPlayers = new ArrayList<>();
    private Player currentPlayer ;
    private List<Card> listCardsInGame = new ArrayList<>(); // todo: caricare qui le carte nel gioco
    private VirtualView virtualView;

    /**
     * Constructor
     */
    public Match(int matchID, VirtualView virtualView) {
        addListener(virtualView);
        this.matchID = matchID ;
        this.island = new Island();
        this.location = new Location(virtualView);
        this.virtualView = virtualView;
        publish(new Message("ALL", TypeOfMessage.CREATED_MATCH, this));
    }

//todo remove after tests
    /**
     * dumb constructior
     * @param matchID
     */
    public Match(int matchID) {
        this.matchID = matchID ;
        this.island = new Island();
        this.location = new Location();
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
    public List<Card> getCards() {
        return listCardsInGame;
    }

    public void addCard(Card card){
        this.listCardsInGame.add(card);
    }


    public Location getLocation() {
        return location;
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
     * Method to set the current player of the match, if it exists in  the array of players of the match.
     * @param name player name that you want to add as current player
     * @return if the currentPlayer was successfully added, returns the index in the array of players, else returns -1
     */
    public int setCurrentPlayer(String name) {
        Player currentPlayer = listPlayers.stream().filter(player -> player.getName().equals(name)).findFirst().orElse(null);
        if (currentPlayer == null){
            return -1;
        } else {
            this.currentPlayer = currentPlayer;
            return listPlayers.indexOf(currentPlayer);
        }
    }


    /**
     * Method to add add player to the list of player of the Match
     * @param name name of the player you wanna add to the Match
     * @param birthday birthday of the player you wanna add to the Match
     * @return the reference to the player obj just created
     */
    public Player createPlayer(String name, Date birthday) {
        Player playToAdd = new Player(name,birthday, virtualView);
        this.listPlayers.add(playToAdd);
        //TODO togliere commento che sminckia la view, mi printa il json se lo lascio
        //publish(new Message("ALL", TypeOfMessage.CREATED_PLAYER, playToAdd));
        return playToAdd;
    }


    public int selectNextCurrentPlayer() {
        if (listPlayers.size() == 0) return -1;
        int indexOfCurrentPlayer = listPlayers.indexOf(currentPlayer);
        int indexOfNextCurrentPlayer = indexOfCurrentPlayer == listPlayers.size() - 1 ? indexOfCurrentPlayer + 1 : 0;
        currentPlayer = listPlayers.get(indexOfCurrentPlayer);
        return indexOfCurrentPlayer;
    }

    public List<Player> buildOrderedList(Comparator<Player> comparator) {
        //example of comparator Comparator<Player> comparator = Comparator.comparing(Player::getBirthday);
        return listPlayers = listPlayers.stream().sorted(comparator.reversed()).collect(Collectors.toList());
    }
}
