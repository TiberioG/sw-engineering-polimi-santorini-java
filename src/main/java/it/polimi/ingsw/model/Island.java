package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CellOutOfBoundsException;

/**
 * This is the class for the Island (billboard)
 * @author sup3rgiu
 */
public class Island {

    /* Attributes */

    private Cell[][] field;
    private final static int maxX = 5;
    private final static int maxY = 5;

    /* Constructor(s) */

    /**
     * Constructor
     */
    public Island() {
        this.field = new Cell[maxX][maxY];
        for (int i = 0; i < this.field.length; i++) {
            for (int j = 0; j < this.field.length; j++) {
                field[i][j] = new Cell(i, j);
            }
        }
    }

    /* Methods */

    /**
     * Get game field
     * @return game field object
     */
    public Cell[][] getField() {
        return this.field;
    }

    /**
     * Returns the cell with the given coordinates
     * @param x coordinate X
     * @param y coordinate Y
     * @return cell at the given coordinate
     * @throws CellOutOfBoundsException if coordinate X or coordinate Y are out of island bounds
     */
    public Cell getCell(int x, int y) throws CellOutOfBoundsException {
        if(x < 0 || x > maxX-1 || y < 0 || y > maxY-1)
            throw new CellOutOfBoundsException();
        return this.field[x][y];
    }
}
