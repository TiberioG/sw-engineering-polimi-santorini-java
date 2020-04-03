package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Colors;

/**
 * This is the class for the Worker
 * @author Vito96
 */
public class Worker {
    /* Attributes */

    private final Colors color;
    //private Cell currentCell; no more need with location

    /* Constructor(s) */

    /**
     * Constructor
     */
    public Worker(Colors color) {
        this.color = color;
    }

    /* Methods */

    public Colors getColor() {
        return color;
    }

}
