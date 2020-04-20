package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;

import java.io.IOException;



/**
 * This class represents a cell in the CLI
 */

public class SquareCell2 {

    private boolean worker;
    private Colors color;
    private int level;
    private final  static int len = 13;
    private final  static int hei = 7;
    private String internal;
    private int startRow;
    private int startCol;

    /**
     *
     * @param worker boolean if the cell contains a worker
     * @param color the {@link Colors} of the worker, if not present must be null
     * @param level the level (int) of the tower at that cell
     */
    public SquareCell2(boolean worker, Colors color, int level){
        this.color = color;
        this.worker = worker;
        this.level = level;

    }


    public void print(int startRow, int startCol) throws IOException, InterruptedException {
        this.startRow = startRow;
        this.startCol = startCol;
        //Terminal.noBuffer();
        if (!worker) {
            if (level == 0) {
                this.noWorkerL0();
            } else if (level == 1) {       //white
                this.noWorkerL0();
                this.noWorkerL1();
            } else if (level == 2) {       //light grey
                this.noWorkerL0();
                this.noWorkerL1();
                this.noWorkerL2();
            } else if (level == 3) {      //dark grey
                this.noWorkerL0();
                this.noWorkerL1();
                this.noWorkerL2();
                this.noWorkerL3();
            } else if (level == 4) {      //blue
                this.noWorkerL0();
                this.noWorkerL1();
                this.noWorkerL2();
                this.noWorkerL3();
                this.noWorkerL4();
            }
        }

        else{
            if (level == 0) {
                this.noWorkerL0();
                this.yesWorker();
            } else if (level == 1) {       //white
                this.noWorkerL0();
                this.noWorkerL1();
                this.yesWorker();
            } else if (level == 2) {       //light grey
                this.noWorkerL0();
                this.noWorkerL1();
                this.noWorkerL2();
                this.yesWorker();
            } else if (level == 3) {      //dark grey
                this.noWorkerL0();
                this.noWorkerL1();
                this.noWorkerL2();
                this.noWorkerL3();
                this.yesWorker();
            } else if (level == 4) {      //blue
                this.noWorkerL0();
                this.noWorkerL1();
                this.noWorkerL2();
                this.noWorkerL3();
                this.noWorkerL4();
                this.yesWorker();
            }

        }

    }


    public void selected(int startRow, int startCol) throws IOException, InterruptedException {
        this.startRow = startRow;
        this.startCol = startCol;
        this.higlited();
    }

    private void noWorkerL0 () throws IOException, InterruptedException {
        for (int i = 0; i < hei ; i ++){
            Terminal.moveAbsoluteCursor(startRow + i, startCol); // scendo di una riga ogni volta
            for (int j = 0; j < len; j++){
                System.out.print("\u001b[48;5;22m" + " "); //verdino
            }
        }
    }


    private void noWorkerL1 () throws IOException, InterruptedException {
        for (int i = 0; i < hei - 2  ; i ++){ // till penultima riga
            Terminal.moveAbsoluteCursor(startRow + i + 1, startCol + 1 ); // scendo di una riga ogni volta con offset 1
            for (int j = 0; j < len - 2 ; j++){
                System.out.print("\u001b[48;5;240m" + " ");
            }
        }
    }

    private void noWorkerL2 () throws IOException, InterruptedException {
        for (int i = 0; i < hei - 4  ; i ++){ // till penultima riga
            Terminal.moveAbsoluteCursor(startRow + i + 2, startCol + 2 ); // scendo di una riga ogni volta  n offset 2
            for (int j = 0; j < len - 4 ; j++){
                System.out.print("\u001b[48;5;244m" + " ");
            }
        }
    }

    private void noWorkerL3 () throws IOException, InterruptedException {
        for (int i = 0; i < hei - 6  ; i ++){ // till penultima riga
            Terminal.moveAbsoluteCursor(startRow + i + 3, startCol + 3 ); // scendo di una riga ogni volta offset 3
            for (int j = 0; j < len - 6 ; j++){
                System.out.print("\u001b[48;5;254m" + " ");
            }
        }
    }

    private void noWorkerL4 () throws IOException, InterruptedException {
        for (int i = 0; i < hei - 6  ; i ++){ // till penultima riga
            Terminal.moveAbsoluteCursor(startRow + i + 3, startCol + 3 ); // scendo di una riga ogni volta offset 3
            for (int j = 0; j < len - 6 ; j++){
                System.out.print("\u001b[48;5;69m" + " ");
            }
        }
    }


    private void yesWorker () throws IOException, InterruptedException {
        for (int i = 0; i < hei - 6  ; i ++){ // till penultima riga
            Terminal.moveAbsoluteCursor(startRow + i + 3, startCol + 3 ); // scendo di una riga ogni volta offset 3
            for (int j = 0; j < len - 6 ; j++){
                if (i == j) {
                    System.out.print(color.getAnsiCode() + "   ▙");
                }
            }
        }
    }

    private void higlited () throws IOException, InterruptedException {
        for (int i = 0; i < hei ; i ++){
            Terminal.moveAbsoluteCursor(startRow + i, startCol); // scendo di una riga ogni volta
            for (int j = 0; j < len; j++){
                System.out.print("\u001b[48;5;202m" + " "); //rossino
            }
        }
    }

    public static int getLen(){
        return len;
    }

    public static int getHei(){
        return hei;
    }
}
