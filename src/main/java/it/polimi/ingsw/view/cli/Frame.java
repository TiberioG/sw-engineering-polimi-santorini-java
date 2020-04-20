package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;
import org.davidmoten.text.utils.WordWrap;

public class Frame {

    private int[] absInit;
    private int[] absEnd;

    private int rowSpan;
    private int colSpan;


    Frame(int[] absInit, int[] absEnd){
        this.absInit = absInit;
        this.absEnd = absEnd;
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
    }

    void print(){
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);

    }

    public int[] getInit(){
        return this.absInit;
    }


    void writeln (String towrite){
        String wrapped =
                WordWrap.from(towrite)
                        .maxWidth(rowSpan)
                        .insertHyphens(true) // true is the default
                        .wrap();
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        System.out.print(wrapped);
    }

}
