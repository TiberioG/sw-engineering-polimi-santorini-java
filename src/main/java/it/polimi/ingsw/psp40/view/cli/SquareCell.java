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
     * Constructor of a cell
     * @param levels an array of boolean, true if exists the level corresponding to the array index is
     * @param startRow the row position in terminal
     * @param startCol the column position in terminal
     */
    public SquareCell(boolean[] levels, int startRow, int startCol){
        this.color = null;
        this.worker = false;
        this.levels = levels;
        this.buildable = false;
        this.selected = false;
        this.movable = false;
        this.startRow = startRow;
        this.startCol = startCol;
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
     * Sets  color to show the cell is buildable
     * @param buildable true or false
     */
    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    /**
     * Sets  color to show you can move in this cell
     * @param movable true or false
     */
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    /**
     * Sets the cell as selected or not
     * @param selected boolean
     */
    public void setSelected(boolean selected){
        this.selected = selected;
    }

    /**
     * Sets the color of the worker but marks it temporary
     * used when displaying a move before the server sends back the updated location
     * @param color of worker
     */
    public void setTempWorker(Colors color){
        this.color = color;
        this.tempWork = true;
    }

    /**
     * adds a level
     * used when displaying a move before the server sends back the updated location
     * @param level to be added to the array of levels
     */
    public void setTempLevel(int level){
        this.levels[level] = true;
    }

    /**
     * prints the cell in terminal
     */
    public void print() {
        //prints  level if they exists
        for (int i = 0; i<= 4; i++){
            if (levels[i]){ // true means the level is present
                level(i);
            }
        }
        if (worker  || tempWork ) {
            this.yesWorker();    //prints worker
        }
        if(buildable){
            this.special(27); //blue
        }
        if(movable){
            this.special(27); //blue
        }
        if(selected){
            this.special(202); //orange
        }

    }

    /**
     * This prints a level of a cell
     * @param lev int the number of the level
     */
    private void level(int lev){
        int shift = lev;
        if (lev ==  4){ //if level is for, the size is the same as a level 3
            shift = 3;
        }
        // this for gradually builds smaller rectangles depending on the level
        for (int i = 0; i < hei - shift * 2  ; i ++){
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
    public void coordinates (int row, int col) {
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

    /**
     * gets len of cell
     * @return
     */
    public static int getLen(){
        return len;
    }

    /**
     * gets height of cell
     * @return
     */
    public static int getHei(){
        return hei;
    }
}
