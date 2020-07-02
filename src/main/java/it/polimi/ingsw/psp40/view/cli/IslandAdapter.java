package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Component;
import it.polimi.ingsw.psp40.model.Location;

import java.io.IOException;
import java.util.List;


/**
 * This class is used to transform the field of cells and the location form the model to a matrix of
 * "No man is an island"
 *
 * @author TiberioG
 */
public class IslandAdapter {

    private SquareCell[][] matrix;
    private int len;
    private int hei;
    private Frame frame;

    private final int SPACING_H = 1;
    final int SPACING_V = 1;


    /**
     * costructor for a new island adapter
     *
     * @param field    a matrix of {@link Cell} coming from model
     * @param location the {@link Location} from model
     * @param frame    the {@link Frame} where to print the island
     */
    public IslandAdapter(Cell[][] field, Location location, Frame frame) {
        this.frame = frame;
        this.matrix = new SquareCell[field.length][field.length];
        this.len = SquareCell.getLen();
        this.hei = SquareCell.getHei();
        int initRow = frame.getInit()[0];
        int initCol = frame.getInit()[1];

        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field.length; col++) {
                matrix[row][col] = new SquareCell(levelify(field[row][col].getTower().getComponents()), initRow + row * (hei + SPACING_H), initCol + col * (len + SPACING_V));
                if (location.getOccupant(row, col) != null) {
                    //case cell is with worker
                    matrix[row][col].setWorker(location.getOccupant(row, col).getColor());
                }
            }
        }
    }

    /**
     * method to print all the island
     *
     * @throws IOException
     * @throws InterruptedException
     */
    void print() throws IOException, InterruptedException {
        Terminal.noBuffer();
        Terminal.hideCursor();

        for (SquareCell[] squareCells : matrix) {
            for (int col = 0; col < matrix.length; col++) {
                squareCells[col].print();
            }
        }
        System.out.print(Colors.reset());
    }

    /**
     * method to add the visualization of the coordinates in the cells of the map
     */
    void debug() {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].coordinates(row, col);
            }
        }
        System.out.print(Colors.reset());
    }

    /**
     * used to set a {@link SquareCell} selected
     *
     * @param r row
     * @param c col
     */
    void setSelected(int r, int c) {
        clearSelected();
        matrix[r][c].setSelected(true);
    }

    /**
     * makes all {@link SquareCell}  NOT selected
     */
    void clearSelected() {
        for (SquareCell[] squareCells : matrix) {
            for (int col = 0; col < matrix.length; col++) {
                squareCells[col].setSelected(false);
            }
        }
    }

    /**
     * adds a worker in a {@link SquareCell}
     *
     * @param r     row
     * @param c     col
     * @param color {@link Colors} of the worker
     */
    void setWorker(int r, int c, Colors color) {
        matrix[r][c].setTempWorker(color);
    }

    /**
     * adds a level in a {@link SquareCell}
     *
     * @param r         row
     * @param c         col
     * @param templevel int level to add
     */
    void setTempLevel(int r, int c, int templevel) {
        matrix[r][c].setTempLevel(templevel);
    }

    /**
     * makes a list of {@link SquareCell} as available
     *
     * @param availableCells a list of {@link Cell}
     */
    public void setMovable(List<Cell> availableCells) {
        for (Cell cell : availableCells) {
            for (int row = 0; row < matrix.length; row++) {
                for (int col = 0; col < matrix.length; col++) {
                    if (row == cell.getCoordX() && col == cell.getCoordY()) {
                        matrix[row][col].setMovable(true);
                    }
                }
            }
        }
    }

    /**
     * makes all the cells NOT movable
     */
    void clearMovable() {
        for (SquareCell[] squareCells : matrix) {
            for (int col = 0; col < matrix.length; col++) {
                squareCells[col].setMovable(false);
            }
        }
    }

    /**
     * getter of the matrix of {@link SquareCell}
     *
     * @return the matrix of {@link SquareCell}
     */
    public SquareCell[][] getMatrix() {
        return this.matrix;
    }

    /**
     * utility to make a list of Components as an array of booleans
     *
     * @param components
     * @return
     */
    private boolean[] levelify(List<Component> components) {
        boolean[] levels = new boolean[5];
        for (Component component : components) {
            levels[component.getComponentCode()] = true;
        }
        return levels;
    }

}
