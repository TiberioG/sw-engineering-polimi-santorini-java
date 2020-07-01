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
    private PrintWriter out;
    private Scanner in;

    private int[] absInit;
    private int[] absEnd;

    private int lastRowRitten = 1;
    private int rowsadded;

    private int rowSpan;
    private int colSpan;


    /**
     * constructor of a new frame
     *
     * @param absInit the cursor position where the frame will start
     * @param absEnd  the cursor position where the frame will end
     * @param in      scanner
     * @param out     printwriter
     */
    public Frame(int[] absInit, int[] absEnd, Scanner in, PrintWriter out) {
        this.absInit = absInit;
        this.absEnd = absEnd;
        this.in = in;
        this.out = out;
        rowSpan = absEnd[0] - absInit[0];
        colSpan = absEnd[1] - absInit[1];
        lastRowRitten = absInit[0];
    }

    /**
     * Clears everything inside the frame
     */
    void clear() {
        System.out.print(Colors.reset());
        for (int row = absInit[0]; row <= absEnd[0]; row++) {
            Terminal.moveAbsoluteCursor(row, absEnd[1]);
            System.out.print("\u001b[1K");
        }
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        in.reset();
    }

    /**
     * clears everything in frame if it's a frame that's on the right of another one
     */
    void clearRight() {
        System.out.print(Colors.reset());
        for (int row = absInit[0]; row <= absEnd[0]; row++) {
            Terminal.moveAbsoluteCursor(row, absInit[1]);
            System.out.print("\u001b[K");
        }
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        in.reset();
    }

    /**
     * prints a string in the frame
     *
     * @param string
     */
    void print(String string) {
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
        System.out.print(string);
    }

    /**
     * sets the cursor at the beginning of the frame
     */
    void start() {
        Terminal.moveAbsoluteCursor(absInit[0], absInit[1]);
    }

    /**
     * prints as in println in the frame
     *
     * @param string
     */
    void println(String string) {
        start();
        System.out.println(string);
    }


    /**
     * prints strings automatically adding new lines if the length of the input excedes the width of the frame
     *
     * @param toWrite string
     */
    void printWrapped(String toWrite) {
        String wrapped = WordWrap.from(toWrite)
                .maxWidth(colSpan)
                .wrap();
        start();
        String[] lines = wrapped.split("\\r?\\n"); //split in lines
        System.out.print(wrapped);
        rowsadded = lines.length;
    }

    /**
     * appends string in the frame
     *
     * @param toWrite
     */
    void append(String toWrite) {
        Terminal.moveAbsoluteCursor(absInit[0] + rowsadded, absInit[1]);
        System.out.println(toWrite);
    }

    /**
     * prints a border around the frame
     */
    void border() {
        for (int i = 0; i < rowSpan; i++) {
            Terminal.moveAbsoluteCursor(absInit[0] + i, absInit[1]); // scendo di una riga ogni volta
            System.out.print("┃");
        }

        start();
        for (int j = 0; j < colSpan; j++) {
            System.out.print("━");
        }

        for (int i = 1; i < rowSpan; i++) {
            Terminal.moveAbsoluteCursor(absInit[0] + i, absEnd[1]);
            System.out.print("┃");
        }

        start();
        for (int j = 0; j < colSpan; j++) {
            System.out.print("━");
        }

    }

    /**
     * prints a string centered
     *
     * @param toWrite
     * @param delay   effect in milliseconds
     */
    void center(String toWrite, int delay) {
        Terminal.hideCursor();
        String[] lines = toWrite.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            int len = lines[i].length();
            int diff = (colSpan - len) / 2;
            if (diff < 0) {
                diff = 0;
            }
            Terminal.moveAbsoluteCursor(absInit[0] + i + 1, absInit[1] + diff);
            System.out.print(lines[i]);
            if (delay != 0) Utils.doTimeUnitSleep(delay);
        }
        lastRowRitten = lastRowRitten + lines.length + 1;
        Terminal.showCursor();
    }

    /**
     * forces to write in center specifying the lenght of the input
     * used when some ansi codes change the actual length of the input
     *
     * @param toWrite input string
     * @param len     of the center part
     * @param delay   milliseconds effect
     */
    void centerFixed(String toWrite, int len, int delay) {
        Terminal.hideCursor();
        int diff = (colSpan - len) / 2;
        if (diff < 0) {
            diff = 0;
        }
        String[] lines = toWrite.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            Terminal.moveAbsoluteCursor(absInit[0] + i + 1, absInit[1] + diff);
            System.out.print(lines[i]);
            if (delay != 0) {
                Utils.doTimeUnitSleep(delay);
            }
        }
        lastRowRitten = lastRowRitten + lines.length + 1;
        Terminal.showCursor();
    }

    /**
     * prints in the center of the frame both vertically and horizontally
     * used for hourglass
     *
     * @param toWrite input string
     * @param title   title
     * @param len
     * @param hei
     * @param delay
     */
    void centerCenterFixed(String toWrite, String title, int len, int hei, int delay) {
        Terminal.hideCursor();
        int diffL = (colSpan - len) / 2;
        if (diffL < 0) {
            diffL = 0;
        }
        int diffH = (rowSpan - hei) / 2;
        if (diffH < 0) {
            diffH = 0;
        }
        int lenTitle = title.length();
        int diff = (colSpan - lenTitle) / 2;
        if (diff < 0) {
            diff = 0;
        }
        Terminal.moveAbsoluteCursor(absInit[0] + diffH - 2, absInit[1] + diff);
        System.out.print(title);
        String[] lines = toWrite.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {

            Terminal.moveAbsoluteCursor(absInit[0] + i + diffH, absInit[1] + diffL);
            System.out.print(lines[i]);
            if (delay != 0) {
                Utils.doTimeUnitSleep(delay);
            }
        }
        lastRowRitten = lastRowRitten + lines.length + 1;
        Terminal.showCursor();
    }

    /**
     * getter of how many rows has the frame
     *
     * @return many rows has the frame
     */
    public int getRowSpan() {
        return rowSpan;
    }

    /**
     * getter of how many columns has the frame
     *
     * @return many columns has the frame
     */
    public int getColSpan() {
        return colSpan;
    }

    /**
     * getter for the coordinates where the frame starts
     *
     * @return the coordinates where the frame starts
     */
    public int[] getInit() {
        return this.absInit;
    }

    /**
     * getter for the coordinates where the frame ends
     *
     * @return the coordinates where the frame ends
     */
    public int[] getAbsEnd() {
        return this.absEnd;
    }

    /**
     * getter for the PrintWriter
     *
     * @return the printwriter of this frame
     */
    public PrintWriter getOut() {
        return out;
    }

    /**
     * getter for the Scanner
     *
     * @return the Scanner of this frame
     */
    public Scanner getIn() {
        return in;
    }
}
