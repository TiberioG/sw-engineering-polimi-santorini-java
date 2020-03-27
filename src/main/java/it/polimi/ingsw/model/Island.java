package it.polimi.ingsw.model;

/**
 * This is the class for the Island (billboard)
 * @author sup3rgiu
 */
public class Island {

    /* Attributes */

    private Cell[][] field;

    /* Constructor(s) */

    /**
     * Constructor
     */
    public Island() {
        this.field = new Cell[5][5];
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
}
