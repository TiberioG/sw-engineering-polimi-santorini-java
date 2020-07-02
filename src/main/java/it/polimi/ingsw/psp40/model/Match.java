package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.commons.Publisher;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.network.server.VirtualView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the class for the Match
 *
 * @author tiberioG
 */
public class Match extends Publisher<Message> {

    /* Attributes */

    private Location location;
    private int matchID;
    private Island island;
    private List<Player> listPlayers = new ArrayList<>();
    private Player currentPlayer;
    private List<Card> listCardsInGame = new ArrayList<>();
    private VirtualView virtualView;
    private MatchProperties matchProperties;
    private Player winningPlayer;

    /**
     * Constructor
     *
     * @param matchID     ID of the match
     * @param virtualView associated VirtuaLView instance
     */
    public Match(int matchID, VirtualView virtualView) {
        addListener(virtualView);
        this.matchID = matchID;
        this.island = new Island(virtualView);
        this.location = new Location(virtualView);
        this.matchProperties = new MatchProperties();
        this.virtualView = virtualView;
        publish(new Message("ALL", TypeOfMessage.CREATED_MATCH));
    }

    /**
     * Dumb constructor used for testing
     *
     * @param matchID ID of the match
     */
    public Match(int matchID) {
        this.matchID = matchID;
        this.island = new Island();
        this.location = new Location();
        this.matchProperties = new MatchProperties();
    }

    /* Methods */

    /**
     * Getter of Match ID
     *
     * @return the ID of the match
     */
    public int getMatchID() {
        return matchID;
    }

    /**
     * Getter of the list of all {@link Player} of the match
     *
     * @return the list of all {@link Player} of the match
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(listPlayers));
    }

    /**
     * Getter of the {@link Island} of the match
     *
     * @return the {@link Island} of the match
     */
    public Island getIsland() {
        return island;
    }

    /**
     * Getter of the current {@link Player}
     *
     * @return the current {@link Player}
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Getter of all the  {@link Card} in the match
     *
     * @return a list of {@link Card} in the match
     */
    public List<Card> getCards() {
        return listCardsInGame;
    }

    /**
     * Adds a {@link Card} to the match
     *
     * @param card card to be added
     */
    public void addCard(Card card) {
        this.listCardsInGame.add(card);
    }

    /**
     * Getter of {@link Location}
     *
     * @return {@link Location} associated to this match
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Getter of {@link MatchProperties}
     *
     * @return {@link MatchProperties} of the match associated to this match
     */
    public MatchProperties getMatchProperties() {
        return matchProperties;
    }


    /**
     * Method to set the current player of the match, if it exists in  the array of players of the match.
     *
     * @param currentPlayer player object that you want to add as current player
     * @return if the currentPlayer was successfully added, returns the index in the array of players, else returns -1
     */
    public int setCurrentPlayer(Player currentPlayer) {
        int indexOfPlayer = listPlayers.indexOf(currentPlayer);
        if (indexOfPlayer != -1) this.currentPlayer = currentPlayer;
        return indexOfPlayer;
    }

    /**
     * Method to retrieve player from the listPlayer from name
     *
     * @param name player name that you want to retrieve
     * @return the player founded or null if no exists player with the name of the params
     */
    public Player getPlayerByName(String name) {
        return listPlayers.stream().filter(player -> player.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Method to set the current player of the match, if it exists in  the array of players of the match.
     *
     * @param name player name that you want to add as current player
     * @return if the currentPlayer was successfully added, returns the index in the listPlayers, else returns -1
     */
    public int setCurrentPlayer(String name) {
        Player player = getPlayerByName(name);
        if (player == null) {
            return -1;
        } else {
            this.currentPlayer = player;
            return listPlayers.indexOf(currentPlayer);
        }
    }

    /**
     * Method to add add player to the list of player of the Match
     *
     * @param name     name of the player you wanna add to the Match
     * @param birthday birthday of the player you wanna add to the Match
     * @return the reference to the player obj just created
     */
    public Player createPlayer(String name, Date birthday) {
        Player playToAdd = new Player(name, birthday, virtualView);
        this.listPlayers.add(playToAdd);
        publish(new Message("ALL", TypeOfMessage.LIST_PLAYER_UPDATED, getPlayers()));
        return playToAdd;
    }

    /**
     * Method for select the next current player with the order of listPlayers
     *
     * @return the index of the new currentPlayer
     */
    public int selectNextCurrentPlayer() {
        if (listPlayers.size() == 0) return -1;
        int indexOfCurrentPlayer = listPlayers.indexOf(currentPlayer);
        int indexOfNextCurrentPlayer = indexOfCurrentPlayer != listPlayers.size() - 1 ? indexOfCurrentPlayer + 1 : 0;
        currentPlayer = listPlayers.get(indexOfNextCurrentPlayer);
        return indexOfNextCurrentPlayer;
    }

    /**
     * Method for order the list of player with a specified compator
     *
     * @param comparator the comparator used for order the list of player
     * @return the list of player
     */
    public List<Player> buildOrderedList(Comparator<Player> comparator) {
        //example of comparator Comparator<Player> comparator = Comparator.comparing(Player::getBirthday);
        listPlayers = listPlayers.stream().sorted(comparator).collect(Collectors.toList());
        publish(new Message("ALL", TypeOfMessage.LIST_PLAYER_UPDATED, getPlayers()));
        return getPlayers();
    }

    /**
     * Method to translate the index of the currentPlayer to 0 translating all other players as well
     */
    public void rescaleListFromCurrentPlayer() {
        int indexOfCurrentPlayer = listPlayers.indexOf(currentPlayer);
        if (indexOfCurrentPlayer > 0) {
            List<Player> rescaledListOfPlayer = listPlayers.subList(indexOfCurrentPlayer, listPlayers.size());
            rescaledListOfPlayer.addAll(listPlayers.subList(0, indexOfCurrentPlayer));
            listPlayers = rescaledListOfPlayer;
            publish(new Message("ALL", TypeOfMessage.LIST_PLAYER_UPDATED, getPlayers()));
        }
    }

    /**
     * Method to remove a player who have lost from the match
     *
     * @param name player name that you want to remove
     * @return the list of remaining players
     */
    public List<Player> removePlayer(String name) {
        Player player = getPlayerByName(name);
        if (player != null) {
            player.getWorkers().forEach(worker -> location.removeLocation(worker));
            listPlayers.remove(player);
        }

        publish(new Message("ALL", TypeOfMessage.LIST_PLAYER_UPDATED, getPlayers()));
        return getPlayers();
    }


    /**
     * Method to set the winningplayer of the match and for notify the winning player to the virtualView
     *
     * @param name string of the winning player
     */
    public void setWinningPlayer(String name) {
        Player player = getPlayerByName(name);
        if (player != null) {
            this.winningPlayer = player;
            MatchHistory.deleteMatch(this.getMatchID());
            publish(new Message("ALL", TypeOfMessage.WINNING_PLAYER_UPDATED, this.winningPlayer));
        }
    }

    /**
     * Method used to save the match and allow its persistence
     */
    public void saveMatch() {
        MatchHistory.saveMatch(this);
    }
}
