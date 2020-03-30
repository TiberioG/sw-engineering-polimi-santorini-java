package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.*;

/**
 * This is the class for the Cell
 * @author sup3rgiu
 */

public class Cell {

    /* Attributes */

    private Tower tower;
    private Worker currentWorker;
    private int coordX;
    private int coordY;

    /* Constructor(s) */

    /**
     * Constructor: populates a new cell in the given position
     *
     * @param x coordinate x of the cell
     * @param y coordinate y of the celll
     */
    public Cell(int x, int y) {
        currentWorker = null;
        coordX = x;
        coordY = y;
        tower = new Tower();
    }

    /* Methods */

    /**
     * Returns coordinate X of the cell
     * @return coordinate X
     */
    public int getCoordX() {
        return coordX;
    }

    /**
     * Returns coordinate Y of the cell
     * @return coordinate Y
     */
    public int getCoordY() {
        return coordY;
    }

    /**
     * Returns the tower built on the cell
     *
     * @return tower object as {@link Tower}
     */
    public Tower getTower() {
        return tower;
    }

    /**
     * Returns current {@link Worker} placed on this cell
     *
     * @return worker placed on this cell, null if not present
     */
    public Worker getCurrentWorker() {
        return currentWorker;
    }

    /**
     * Sets a {@link Worker} as worker present on the cell
     *
     * @param worker {@link Worker} to be set as current worker
     * @throws WorkerAlreadyPresentException if a worker is already present on the cell
     */
    public void setCurrentWorker(Worker worker) throws WorkerAlreadyPresentException {
        if(this.currentWorker != null)
            throw new WorkerAlreadyPresentException();
        this.currentWorker = worker;
    }

    /**
     * Removes the current worker. {@link #getCurrentWorker()} will return null
     */
    public void removeCurrentWorker() {
        this.currentWorker = null;
    }

}
