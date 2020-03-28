package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.*;

/**
 * This is the class for the Cell
 * @author sup3rgiu
 */

public class Cell implements Constructor{

    /* Attributes */

    private List<Component> tower = new ArrayList<>();
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
        tower.add(Component.GROUND);
    }

    /* Methods */

    /**
     * Returns the tower built on the cell
     *
     * @return tower object as {@link List<Component>}
     */
    public List<Component> getTower() {
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

    /**
     * Adds the given component at the top of the tower
     *
     * @param component {@link Component} to be added
     */
    @Override
    public void addLevel(Component component) {
        tower.add(component);
    }

    /**
     * Removes the last {@link Component} of the tower.
     *
     * @return {@link Component} removed. Null if tower has only the {@link Component#GROUND} component
     */
    @Override
    public Component removeLevel() {
        if(tower.size() > 1) { // greater than 1 because first level can not be removed (GROUND)
            return tower.remove(tower.size() - 1);
        }
        return null;
    }
}
