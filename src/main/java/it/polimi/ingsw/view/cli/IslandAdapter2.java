package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Location;

import java.io.IOException;
import java.util.List;


/**
 * This class is used to transform the field of cells and the location form the model to a matrix of
 */
public class IslandAdapter2 {

    //make singleton??

    private SquareCell2[][] matrix ;
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
    IslandAdapter2(Cell[][] field, Location location){
        matrix = new SquareCell2[field.length][field.length];
        len = SquareCell2.getLen();
        hei = SquareCell2.getHei();

        for (int row = 0; row < field.length ; row++) {
            for (int col = 0; col < field.length ; col++) {
                if (location.getOccupant(field[row][col]) != null) {
                    //case cell is with worker
                    matrix[row][col] = new SquareCell2(true, location.getOccupant(field[row][col]).getColor(), field[row][col].getTower().getTopComponent().getComponentCode());
                } else {
                    //case cell WITHOUT worker
                    matrix[row][col] = new SquareCell2(false, null, field[row][col].getTower().getTopComponent().getComponentCode());
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


    void setSelected(int r, int c) {
        for (int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].setSelected(false);
            }
        }
        matrix[r][c].setSelected(true);
    }



    public void setMovable (List<Cell> availableCells ) {
        for (Cell cell : availableCells) {
            for (int row = 0; row < matrix.length; row ++) {
                for (int col = 0; col < matrix.length; col++) {
                    if (row == cell.getCoordY() && col == cell.getCoordX()){
                        matrix[row][col].setMovable(true);
                    }
                }
            }
        }
    }

    void clearMovable() {
        for (int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].setMovable(false);
            }
        }
    }

}
