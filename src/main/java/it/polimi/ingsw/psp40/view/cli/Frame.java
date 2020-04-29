package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import org.davidmoten.text.utils.WordWrap;

import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class is used to create a frame inside the CLI
 */
class Frame {
    private PrintWriter out ;
    private Scanner in ;

    private int[] absInit;
    private int[] absEnd;

    private int rowSpan;
    private int colSpan;


    Frame(int[] absInit, int[] absEnd, Scanner in, PrintWriter out ){
        this.absInit = absInit;
        this.absEnd = absEnd;
        this.in = in;
        this.out = out;
        rowSpan = absEnd[0] - absInit[0];
        colSpan = absEnd[1] - absInit[1];
    }

    void clear(){
        System.out.print(Colors.reset());
        for (int row = absInit[0]; row <= absEnd[0]; row++){
            Terminal.moveAbsoluteCursor(row, absEnd[1]);
            System.out.print("\u001b[1K");
        }
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        in.reset();
    }

    void print(String string){
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        System.out.print(string);
    }

    void start(){
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
    }

    void println(String string){
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        System.out.println(string);
    }


    void printWrapped (String towrite){
        String wrapped =
                WordWrap.from(towrite)
                        .maxWidth(colSpan)
                        .wrap();
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
       System.out.print(wrapped);
    }

    void border(){

    }

    public int getRowSpan(){
        return rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public int[] getInit(){
        return this.absInit;
    }

}
