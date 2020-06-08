package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Location;

import java.io.IOException;
import java.util.List;


/**
 * This class is used to transform the field of cells and the location form the model to a matrix of
 * "No man is an island"
 * @author TiberioG
 */
public class IslandAdapter {

    private SquareCell[][] matrix ;
    private int len;
    private int hei;
    private Frame frame;

    private final int SPACING_H = 1;
    final int SPACING_V = 1;


    public IslandAdapter(Cell[][] field, Location location, Frame frame){
        this.frame = frame;
        this.matrix = new SquareCell[field.length][field.length];
        this.len = SquareCell.getLen();
        this.hei = SquareCell.getHei();

        for (int row = 0; row < field.length ; row++) {
            for (int col = 0; col < field.length ; col++) {
                matrix[row][col] = new SquareCell(levelify(field[row][col].getTower().getComponents()));
                if (location.getOccupant(row, col) != null) {
                    //case cell is with worker
                    matrix[row][col].setWorker(location.getOccupant(row, col).getColor());
                }
            }
        }
    }


    void print() throws IOException, InterruptedException {
        Terminal.noBuffer();
        Terminal.hideCursor();
        int initRow = frame.getInit()[0];
        int initCol = frame.getInit()[1];

        for (int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].print(initRow + row*(hei + SPACING_H), initCol + col*(len + SPACING_V));
            }
        }
        System.out.print(Colors.reset());
    }

    void debug() {
        int initRow = frame.getInit()[0];
        int initCol = frame.getInit()[1];
        for (int row = 0; row < matrix.length; row ++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[row][col].debug(initRow + row*(hei + SPACING_H), initCol + col*(len + SPACING_V), row, col);
            }
        }
        System.out.print(Colors.reset());
    }

    void setSelected(int r, int c) {
      clearSelected();
      matrix[r][c].setSelected(true);
    }

    void clearSelected() {
        for (SquareCell[] squareCells : matrix) {
            for (int col = 0; col < matrix.length; col++) {
                squareCells[col].setSelected(false);
            }
        }
    }

    void setWorker(int r, int c, Colors color) {
        matrix[r][c].setTempWorker(color);
    }
    void setTempLevel(int r, int c, int templevel) {
        matrix[r][c].setTempLevel(templevel);
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

    private boolean[] levelify(List<Component> components){
        boolean[] levels = new boolean[5];
        for (Component component : components) {
            levels[component.getComponentCode()] = true;
        }
        return levels;
    }


}
