package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import org.davidmoten.text.utils.WordWrap;

import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class is used to create a frame inside the CLI
 *
 * @author TiberioG
 */
class Frame {
    private PrintWriter out ;
    private Scanner in ;

    private int[] absInit;
    private int[] absEnd;

    private int lastRowRitten = 1;
    private int rowsadded;

    private int rowSpan;
    private int colSpan;


    public Frame(int[] absInit, int[] absEnd, Scanner in, PrintWriter out ){
        this.absInit = absInit;
        this.absEnd = absEnd;
        this.in = in;
        this.out = out;
        rowSpan = absEnd[0] - absInit[0];
        colSpan = absEnd[1] - absInit[1];
        lastRowRitten = absInit[0];
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

    void clearRight(){
        System.out.print(Colors.reset());
        for (int row = absInit[0]; row <= absEnd[0]; row++){
            Terminal.moveAbsoluteCursor(row, absInit[1]);
            System.out.print("\u001b[K");
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


    void printWrapped (String toWrite)  {
        String wrapped = WordWrap.from(toWrite)
                        .maxWidth(colSpan)
                        .wrap();
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        String[] lines = wrapped.split("\\r?\\n"); //split in lines
        System.out.print(wrapped);
        rowsadded = lines.length;
     }

   void append(String toWrite){
       Terminal.moveAbsoluteCursor(absInit[0] + rowsadded, absInit[1]);
       System.out.println(toWrite);
   }

    void border(){

        for (int i = 0; i < rowSpan ; i ++){
            Terminal.moveAbsoluteCursor(absInit[0] + i, absInit[1]); // scendo di una riga ogni volta
            System.out.print("┃");
        }

        Terminal.moveAbsoluteCursor(absInit[0] , absInit[1]);
        for (int j = 0; j < colSpan; j++){
            System.out.print("━");
        }

        for (int i = 1; i < rowSpan  ; i ++){
            Terminal.moveAbsoluteCursor(absInit[0] + i, absEnd[1] );
            System.out.print("┃");
        }

        Terminal.moveAbsoluteCursor(absEnd[0] , absInit[1]);
        for (int j = 0; j < colSpan; j++){
            System.out.print("━");
        }

    }

    void center (String toWrite, int delay){
        Terminal.hideCursor();
        String[] lines = toWrite.split("\\r?\\n");
        for (int i = 0; i<lines.length; i++){
            int len = lines[i].length();
            int diff = (colSpan - len) / 2;
            if (diff < 0){
                diff = 0;
            }
            Terminal.moveAbsoluteCursor(absInit[0]+ i + 1, absInit[1] + diff);
            System.out.print(lines[i]);
            if (delay != 0) Utils.doTimeUnitSleep(delay);
        }
        lastRowRitten = lastRowRitten + lines.length + 1;
        Terminal.showCursor();
    }


    void centerFixed (String toWrite, int len, int delay){
        Terminal.hideCursor();
        int diff = (colSpan - len) / 2;
        if (diff < 0){
            diff = 0;
        }
        String[] lines = toWrite.split("\\r?\\n");
        for (int i = 0; i<lines.length; i++){
            Terminal.moveAbsoluteCursor(absInit[0]+ i + 1, absInit[1] + diff);
            System.out.print(lines[i]);
            if (delay != 0) {
                Utils.doTimeUnitSleep(delay);
            }
        }
        lastRowRitten = lastRowRitten + lines.length + 1;
        Terminal.showCursor();
    }

    void centerCenterFixed (String toWrite, String title, int len, int hei,  int delay){
        Terminal.hideCursor();
        int diffL = (colSpan - len) / 2;
        if (diffL < 0){
            diffL= 0;
        }
        int diffH = (rowSpan - hei) / 2;
        if (diffH < 0){
            diffH = 0;
        }
        int lenTitle = title.length();
        int diff = (colSpan - lenTitle) / 2;
        if (diff < 0){
            diff = 0;
        }
        Terminal.moveAbsoluteCursor(absInit[0] + diffH - 2 , absInit[1] + diff);
        System.out.print(title);
        String[] lines = toWrite.split("\\r?\\n");
        for (int i = 0; i<lines.length; i++){

            Terminal.moveAbsoluteCursor(absInit[0]+ i + diffH, absInit[1] + diffL);
            System.out.print(lines[i]);
            if (delay != 0) {
                Utils.doTimeUnitSleep(delay);
            }
        }
        lastRowRitten = lastRowRitten + lines.length + 1;
        Terminal.showCursor();
    }


    void centerAppend (String toWrite, int delay){
        String[] lines = toWrite.split("\\r?\\n");
        for (int i = 0; i<lines.length; i++){
            int len = lines[i].length();
            int diff = (colSpan - len) / 2;
            if (diff < 0){
                diff = 0;
            }
            Terminal.moveAbsoluteCursor(lastRowRitten+ i + 1, absInit[1] + diff);
            System.out.print(lines[i]);
            if (delay != 0)  Utils.doTimeUnitSleep(delay);
        }
        lastRowRitten = lastRowRitten + lines.length + 1;
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

    public int[] getAbsEnd(){
        return this.absEnd;
    }


    public PrintWriter getOut() {
        return out;
    }

    public Scanner getIn() {
        return in;
    }
}
