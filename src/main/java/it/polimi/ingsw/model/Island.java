package it.polimi.ingsw.model;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.commons.messages.Message;
import it.polimi.ingsw.commons.messages.TypeOfMessage;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.exceptions.RemoveGroundLevelException;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the class for the Island (billboard)
 * @author sup3rgiu & modified by tiberioG
 */
public class Island extends Publisher<Message> {

    /* Attributes */

    private Cell[][] field;
    private final static int numRow = 5;
    private final static int numCol = 5;

    private final static int maxX = numCol - 1;
    private final static int maxY = numRow - 1;

    /* Constructor(s) */

    /**
     * Constructor
     */
    public Island() {
        this.field = new Cell[numRow][numCol];
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
    } //todo fare una copia qui

    /**
     * Returns max coordinate X of the billboard
     * @return max coordinate X
     */
    public static int getMaxX() {
        return maxX;
    }

    /**
     * Returns max coordinate Y of the billboard
     * @return max coordinate Y
     */
    public static int getMaxY() {
        return maxY;
    }

    /**
     * Returns the cell with the given coordinates
     * @param x coordinate X
     * @param y coordinate Y
     * @return cell at the given coordinate
     * @throws CellOutOfBoundsException if coordinate X or coordinate Y are out of island bounds
     */
    public Cell getCell(int x, int y) throws CellOutOfBoundsException {
        if(x < 0 || x > maxX || y < 0 || y > maxY)
            throw new CellOutOfBoundsException();
        return this.field[x][y];
    }

    /**
     * Return the adjacent cells given an instance of cell
     * @param cell initial cell
     * @return an {@link ArrayList} of {@link Cell}
     */
    public ArrayList<Cell> getAdjCells (Cell cell) {
        int x = cell.getCoordX();
        int y = cell.getCoordY();
        ArrayList<Cell> adjacentCells = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i != 0) || ( j!= 0)) {
                    if (x+i >= 0 && y+j >= 0 && x+i<=maxX && y+j<=maxY) { //check boundaries
                        adjacentCells.add(field[x+i][y+j]);
                    }
                }
            }
        }
        return adjacentCells;

    }


    /**
     * the only public method to build things, this one calls the protected one in Tower.
     * Then sends a message to the view
     * @param component
     * @param cell
     * @throws BuildLowerComponentException
     */
    public  void addComponent(Component component, Cell cell) throws BuildLowerComponentException {
       cell.getTower().addComponent(component);
       this.update();
       this.updateSpecific(cell);
    }

    /**
     * the only public method to remove things, this one calls the protected one in Tower.
     * Then sends a message to the view
     * @param cell
     */

    public  void removeComponent( Cell cell) throws  RemoveGroundLevelException { //todo per me è inutile
        cell.getTower().removeComponent();
        this.update();
        this.updateSpecific(cell);
    }
    


    /**
     * sends to the view a copy of all the field
     */
    private void update (){
        publish(new Message("ALL", TypeOfMessage.ISLAND_UPDATED, field));
    }

    /**
     * sends the cell taht has just been modified with a new component
     * @param cell
     */
    private void updateSpecific(Cell cell){
        publish(new Message("ALL", TypeOfMessage.TOWER_UPDATED, cell));
    }


}
