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
    }


    public void print() throws IOException, InterruptedException {

        String internal = Colors.BLUE.getAnsiCode();
        String last;
        if (level == 0){
            internal = "\u001b[32m" + "█"; //green for grounf
        }
        else if (level == 1){       //white
            internal = "\u001b[97m" + "█";
        }
        else if (level == 2){       //light grey
            internal ="\u001b[37m"  + "█" ;
        }
        else if (level == 3){      //dark grey
            internal ="\u001b[31m" +  "█" ;
        }
        else if (level == 4){      //blue
            internal="\u001b[94m" + "▓";
        }


        Terminal.noBuffer();
        for (int i = 0; i < hei; i ++){
            for (int j = 0; j < len; j++){
                System.out.print(internal);
            }
            Terminal.backwCursor(len);
            Terminal.downCursor(1);
        }

    }
}
