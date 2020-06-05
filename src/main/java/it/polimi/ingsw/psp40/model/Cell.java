package it.polimi.ingsw.psp40.model;

/**
 * This is the class for the Cell
 * @author sup3rgiu
 */

public class Cell {
    private Tower tower;
    private int coordX;
    private int coordY;

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
     * Returns an array of both coordinates
     * @return array of integer [coordX, coordY]
     */
    public int[] getCoordXY(){
        return new int[]{coordX, coordY};
    }

    /**
     * Returns the tower built on the cell
     * @return tower object as {@link Tower}
     */
    public Tower getTower() {
        return tower;
    }

}
