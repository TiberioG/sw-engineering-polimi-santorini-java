package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.commons.Colors;

/**
 * This is the class for the Worker
 * @author Vito96
 */
public class Worker {

    private int id;
    private final Colors color;
    transient private final Player owner;
    private String playerName;

    /**
     * constructor
     * @param id
     * @param color
     * @param owner
     */
    public Worker(int id, Colors color, Player owner) {
        this.id = id;
        this.color = color;
        this.owner = owner;
        this.playerName = owner.getName();
    }

    /**
     * getter of id of Worker
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * getter of {@link Colors} of Worker
     * @return
     */
    public Colors getColor() {
        return color;
    }

    /**
     * getter of the {@link Player} who owns this worker
     * @return
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * getter of the name of the player,
     * we use this since Player is transient and otherwise I cannot get the name of player when it' serialized
     * @return the string
     */
    public String getPlayerName(){
        return playerName;
    }

}
