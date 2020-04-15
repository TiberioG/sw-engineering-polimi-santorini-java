package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Location;

import java.io.IOException;


/**
 * This class is used to transform the field of cells and the location form the model to a matrix of {@link SquareCell}
 */
public class IslandAdapter {

    //make singleton??

    private SquareCell[][] matrix ;
    private int len;
    private int wid;

    final int SPACING_H = 1;
    final int SPACING_V = 2;


    /**
     * The constructor
     * @param field from the model, a matrix of {@link Cell}
     * @param location from the model {@link Location}
     * @param len (int) is the length of the rectangles that will appear on the map (in columns)
     * @param wid  (int) is the width of the rectangles that will appear on the map (in rows)
     */
    public IslandAdapter(Cell[][] field, Location  location, int len, int wid){
        this.len = len;
        this.wid = wid;
        matrix = new SquareCell[field.length][field.length];

        for (int i = 0; i < field.length ; i++) {
            for (int j = 0; j < field.length ; j++) {
                if (location.getOccupant(field[i][j]) != null) {
                    //case cell is with worker
                    matrix[i][j] = new SquareCell(true, location.getOccupant(field[i][j]).getColor(), field[i][j].getTower().getTopComponent().getComponentCode(), len, wid);
                } else {
                    //case cell WITHOUT worker
                    matrix[i][j] = new SquareCell(false, null, field[i][j].getTower().getTopComponent().getComponentCode(), len, wid);
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
        for (int i = 0; i < matrix.length; i ++){
            for (int j = 0; j < matrix.length; j++){
                matrix[i][j].print();
                Terminal.downCursor(SPACING_H);
            }
            Terminal.upCursor(matrix.length*(wid + SPACING_H));
            Terminal.forwCursor(len + SPACING_V );
        }

    }

}
