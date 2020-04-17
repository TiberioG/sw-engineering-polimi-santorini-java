package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;

/**
 * This is the class for the Worker
 * @author Vito96
 */
public class Worker {
    /* Attributes */
    private int id;
    private final Colors color;
    private final Player owner;

    /* Constructor(s) */

    /**
     * Constructor
     */
    public Worker(int id, Colors color, Player owner) {
        this.id = id;
        this.color = color;
        this.owner = owner;
    }

    /* Methods */
    public int getId() {
        return id;
    }
    public Colors getColor() {
        return color;
    }
    public Player getOwner() {
        return owner;
    }

}
