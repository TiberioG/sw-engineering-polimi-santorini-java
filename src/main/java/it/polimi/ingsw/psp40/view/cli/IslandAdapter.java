package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Location;

import java.io.IOException;
import java.util.List;


/**
 * This class is used to transform the field of cells and the location form the model to a matrix of
 */
public class IslandAdapter {

    //make singleton??

    /**
     *
     */
    private SquareCell[][] matrix ;
    private int len;
    private int hei;
    private int[] cursor = {7, 60};

    private final int SPACING_H = 1;
    final int SPACING_V = 1;


    /**
     * The constructor
     * @param field from the model, a matrix of {@link Cell}
     * @param location from the model {@link Location}
     */
    IslandAdapter(Cell[][] field, Location location){
        matrix = new SquareCell[field.length][field.length];
        len = SquareCell.getLen();
        hei = SquareCell.getHei();

        for (int row = 0; row < field.length ; row++) {
            for (int col = 0; col < field.length ; col++) {
                if (location.getOccupant(field[row][col]) != null) {
                    //case cell is with worker
                    matrix[row][col] = new SquareCell(true, location.getOccupant(field[row][col]).getColor(), field[row][col].getTower().getTopComponent().getComponentCode());
                } else {
                    //case cell WITHOUT worker
                    matrix[row][col] = new SquareCell(false, null, field[row][col].getTower().getTopComponent().getComponentCode());
                }
            }
        }
    }

    /**
     * This method is used to print the island as an
     * @throws IOException
     * @throws InterruptedException
     */
    void print() throws IOException, InterruptedException {
        //Terminal.noBuffer();
        int initRow = cursor[0];
        int initCol = cursor[1];

        for (int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].print(initRow + row*(hei + SPACING_H), initCol + col*(len + SPACING_V));
            }
        }
        System.out.print(Colors.reset());
    }

    void debug() throws IOException, InterruptedException {
        //Terminal.noBuffer();
        int initRow = cursor[0];
        int initCol = cursor[1];

        for (int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].debug(initRow + row*(hei + SPACING_H), initCol + col*(len + SPACING_V), row, col);
            }
        }
        System.out.print(Colors.reset());
    }


    void setSelected(int r, int c) {
        for (SquareCell[] squareCells : matrix) {
            for (int col = 0; col < matrix.length; col++) {
                squareCells[col].setSelected(false);
            }
        }
        matrix[r][c].setSelected(true);
    }


    public void setMovable (List<Cell> availableCells ) {
        for (Cell cell : availableCells) {
            for (int row = 0; row < matrix.length; row ++) {
                for (int col = 0; col < matrix.length; col++) {
                    if (row == cell.getCoordX() && col == cell.getCoordY()){
                        matrix[row][col].setMovable(true);
                    }
                }
            }
        }
    }

    void clearMovable() {
        for (SquareCell[] squareCells : matrix) {
            for (int col = 0; col < matrix.length; col++) {
                squareCells[col].setMovable(false);
            }
        }
    }

    public SquareCell[][] getMatrix(){
        return this.matrix;
    }

}
