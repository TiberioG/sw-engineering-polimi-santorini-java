package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Location;

import java.io.IOException;



/**
 * This class is used to transform the field of cells and the location form the model to a matrix of {@link SquareCell}
 */
public class IslandAdapter2 {

    //make singleton??

    private SquareCell2[][] matrix ;
    private int len;
    private int hei;

    final int SPACING_H = 1;
    final int SPACING_V = 2;


    /**
     * The constructor
     * @param field from the model, a matrix of {@link Cell}
     * @param location from the model {@link Location}
     * @param len (int) is the length of the rectangles that will appear on the map (in columns)
     * @param wid  (int) is the width of the rectangles that will appear on the map (in rows)
     */
    public IslandAdapter2(Cell[][] field, Location  location){
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
    public void print () throws IOException, InterruptedException {
        Terminal.noBuffer();
        int initRow = Terminal.getAbsoluteCursor()[0];
        int initCol = Terminal.getAbsoluteCursor()[1];

        for (int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].print(initRow + row*(hei + SPACING_H), initCol + col*(len + SPACING_H));
            }
        }

    }



}
