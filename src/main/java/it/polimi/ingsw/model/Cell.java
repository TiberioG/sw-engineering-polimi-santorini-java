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
    private int coordX;
    private int coordY;
    //private Worker currentWorker; no more need with Location

    /* Constructor(s) */

    /**
     * Constructor: populates a new cell in the given position
     * @param x coordinate x of the cell
     * @param y coordinate y of the celll
     */
    public Cell(int x, int y) {
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
     * @return tower object as {@link Tower}
     */
    public Tower getTower() {
        return tower;
    }



}
