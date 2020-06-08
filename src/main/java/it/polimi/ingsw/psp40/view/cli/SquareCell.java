package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;

/**
 * This class represents a cell in the CLI
 * @author TiberioG
 */

public class SquareCell {
    private final  static int len = 13;
    private final  static int hei = 7;

    private boolean worker;
    private boolean tempWork;

    private Colors color;
    private boolean[] levels;

    private int startRow;
    private int startCol;
    private boolean selected;
    private boolean movable;
    private boolean buildable;


    /**
     * Constructor
     * @param levels an array of booleans that represent if a component exists or not
     */
    public SquareCell(boolean[] levels){
        this.color = null;
        this.worker = false;
        this.levels = levels;
        this.buildable = false;
        this.selected = false;
        this.movable = false;
    }

    /**
     * Sets a worker in this cell
     * @param color of the worker
     */
    public void setWorker(Colors color){
        this.color = color;
        this.worker = true;
    }

    /**
     * Sets temporary color to show the cell is buildable if
     * @param buildable true or false
     */
    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    /**
     * Sets temporary color to show you can move in this cell
     * @param movable true or false
     */
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public void setTempWorker(Colors color){
        this.color = color;
        this.tempWork = true;
    }

    public void setTempLevel(int level){
        this.levels[level] = true;
    }

    public void print(int startRow, int startCol) {
        this.startRow = startRow;
        this.startCol = startCol;

        for (int i = 0; i<= 4; i++){
            if (levels[i]){
                level(i);
            }
        }

        if (worker  || tempWork ) {
            this.yesWorker();
        }

        if(buildable){
            this.special(27);
        }
        if(movable){
            this.special(27);
        }
        if(selected){
            this.special(202);
        }

    }

    public void debug(int startRow, int startCol, int row, int col)  {
        this.startRow = startRow;
        this.startCol = startCol;
       this.coordinates(row, col);
    }

    /**
     * This prints all a level of a cell
     * @param lev int the number of the level
     */
    private void level(int lev){
        int shift = lev;
        if (lev ==  4){
            shift = 3;
        }
        for (int i = 0; i < hei - shift*2  ; i ++){
            Terminal.moveAbsoluteCursor(startRow + i + shift, startCol + shift );
            for (int j = 0; j < len - shift*2 ; j++){
                System.out.print(style(lev, " "));
            }
        }
    }

    /**
     * prints a worker
     */
    private void yesWorker ()  {
        for (int i = 0; i < hei - 6  ; i ++){
            Terminal.moveAbsoluteCursor(startRow + i + 3, startCol + 3 );
            for (int j = 0; j < len - 6 ; j++){
                if (i == j) {
                    System.out.print(color.getAnsiCode() + "   ▙");
                }
            }
        }
    }

    /**
     * shows coordinates on the map
     * @param row
     * @param col
     */
    private void coordinates (int row, int col) {
        System.out.print(Colors.reset());
        Terminal.moveAbsoluteCursor(startRow + hei - 1  , startCol); // scendo di una riga ogni volta
        System.out.print(row + "," + col);

    }

    /**
     * colors a cell
     * @param colorbit ansi code
     */
    private void special (int colorbit)  {
        for (int i = 0; i < hei ; i ++){
            Terminal.moveAbsoluteCursor(startRow + i, startCol); // scendo di una riga ogni volta
              System.out.print("\u001b[48;5;"+ colorbit + "m" + " ");
        }
        Terminal.moveAbsoluteCursor(startRow , startCol); // scendo di una riga ogni volta
        for (int j = 0; j < len; j++){
            System.out.print("\u001b[48;5;"+ colorbit + "m" + " ");
        }
        for (int i = 0; i < hei ; i ++){
            Terminal.moveAbsoluteCursor(startRow + i, startCol + len - 1 ); // scendo di una riga ogni volta
            System.out.print("\u001b[48;5;"+ colorbit + "m" + " ");
        }
        Terminal.moveAbsoluteCursor(startRow + hei -1 , startCol); // scendo di una riga ogni volta
        for (int j = 0; j < len; j++){
            System.out.print("\u001b[48;5;"+ colorbit + "m" + " ");
        }

    }

    /**
     * sets static style for each level
     * @param level
     * @param mono the char used to fill the cell, please use only one
     * @return ansi code
     */
    private String style(int level, String mono){
        switch (level){
            case 0:
                return "\u001b[48;5;22m" + mono;  //verdino
            case 1:
                return "\u001b[48;5;240m" + mono; //grigio1
            case 2:
                return "\u001b[48;5;244m" + mono; //grigio2
            case 3:
                return "\u001b[48;5;254m" + mono; //grigio3
            case 4:
                return "\u001b[48;5;69m" + mono;  //blue
            default:
                return "\u001b[48;5;22m" + "x";
        }
    }

    public static int getLen(){
        return len;
    }

    public static int getHei(){
        return hei;
    }
}
