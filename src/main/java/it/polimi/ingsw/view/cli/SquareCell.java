package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;

import java.io.IOException;

/**
 * This class represents a cell in the CLI
 */

public class SquareCell {

    private boolean worker;
    private Colors color;
    private int level;
    private int len;
    private int hei;
    private String internal;

    /**
     *
     * @param worker boolean if the cell contains a worker
     * @param color the {@link Colors} of the worker, if not present must be null
     * @param level the level (int) of the tower at that cell
     * @param len (int) is the length of the rectangles that will appear on the map (in columns)
     * @param hei  wid  (int) is the width of the rectangles that will appear on the map (in rows)
     */
    public SquareCell(boolean worker, Colors color, int level, int len, int hei){
        this.color = color;
        this.worker = worker;
        this.level = level;
        this.len = len;
        this.hei = hei;


        //todo mettere colori di background
        if (level == 0){
            internal = "\u001b[48;5;34m" + " "; //green for ground floor
        }
        else if (level == 1){       //white
            internal = "\u001b[48;5;254m" + " ";
        }
        else if (level == 2){       //light grey
            internal ="\u001b[48;5;244m"  + " " ;
        }
        else if (level == 3){      //dark grey
            internal ="\u001b[48;5;240m" +  " " ;
        }
        else if (level == 4){      //blue
            internal="\u001b[48;5;69m" + "X";
        }

    }


    public void print() throws IOException, InterruptedException {

        if (worker){
            this.yesWorker();
        }
        else {
            this.noWorker();
        }


    }

    private void noWorker () throws IOException, InterruptedException {
        Terminal.noBuffer();
        for (int i = 0; i < hei ; i ++){ // till penultima riga
            for (int j = 0; j < len; j++){
                System.out.print(internal);
            }
            Terminal.backwCursor(len);
            Terminal.downCursor(1);
        }
    }


    private void yesWorker() throws IOException, InterruptedException {
        Terminal.noBuffer();
        for (int i = 0; i < hei - 1 ; i ++){ // till penultima riga
            for (int j = 0; j < len; j++){
                System.out.print(internal);
            }
            Terminal.backwCursor(len);
            Terminal.downCursor(1);
        }

        //last row put worker in corner down right

        for (int j = 0; j < len- 2; j++){
            System.out.print(internal);
        }
        System.out.print(color.getAnsiCode() + "WW");
        Terminal.backwCursor(len);
        Terminal.downCursor(1);

    }
}
