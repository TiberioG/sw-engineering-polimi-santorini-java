package it.polimi.ingsw.psp40.view;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.view.cli.Utils;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class UtilsTest {
    //generate different lenght strings
   private final String s1  = new String(new  char[1]).replace('\0', 'x');
   private final String s2  = new String(new  char[2]).replace('\0', 'x');
   private final String s3  = new String(new  char[3]).replace('\0', 'x');
   private final String s4  = new String(new  char[4]).replace('\0', 'x');
   private final String s5  = new String(new  char[5]).replace('\0', 'x');
   private final String s6  = new String(new  char[6]).replace('\0', 'x');
   private final String s7  = new String(new  char[7]).replace('\0', 'x');
   private final String s8  = new String(new  char[8]).replace('\0', 'x');
   private final String s9  = new String(new  char[9]).replace('\0', 'x');
   private final String s10 = new String(new char[10]).replace('\0', 'x');
   private final String s11 = new String(new char[11]).replace('\0', 'x');
   private final String s12 = new String(new char[12]).replace('\0', 'x');
   private final String s13 = new String(new char[13]).replace('\0', 'x');
   private final String s14 = new String(new char[14]).replace('\0', 'x');

   private final String swithnewline = "Ciao\n mi \n piace ";
   private final String colored = Colors.RED.getAnsiCode() + s4 +Colors.reset()  ; //ALWAYS RESET THE COLOR!!!

    private static PrintWriter out = new PrintWriter(System.out, true);
    private static Scanner in = new Scanner(System.in);

    private Utils myutils = new Utils(in, out);

    @Test
    public void longestMatrix() {
        String[] line1 = new String[]{s1, s10, s7, s4, s5, s11};
        String[] line2 = new String[]{s8, s14, s12, s1};
        String[] line3 = new String[]{s6, s9, s13};

        String[][] matrix = new String[][]{line1, line2, line3};

        assertEquals(14, Utils.longestMatrix(matrix));

    }

    @Test
    public void longestArray_isReallyLongest() {
        String[] arry = new String[]{s1, s2, s3, s4, s5, s11};

        assertEquals(11, Utils.longestArray(arry));
    }

    @Test
    public void readNumbers() {

    }

    @Test
    public void singleTable_titleIsLongest() throws InterruptedException {
        String[] arry = new String[]{s1, s2, s13, s4, s5, s6};
        myutils.singleTableCool(s14, arry, 100);

    }

    @Test
    public void singleTable_titleIsShortest() throws InterruptedException {
        String[] arry = new String[]{s1, s2, s3, s4, s5, s11};
        myutils.singleTableCool(s2, arry, 100 );

    }

    @Test
    public void singleTable_hasNewLines() throws InterruptedException {
        String[] arry = new String[]{s1, s2, swithnewline, s4, s5, s11};
        myutils.singleTableCool(s2, arry, 100 );
    }

    @Test
    public void singleTable_hasColors() throws InterruptedException {
        String[] arry = new String[]{s1, s2, colored, s4, s5, s11};
        myutils.singleTableCool("options", arry, 100);
    }

    @Test
    public void singleTableCool_hasColors() throws InterruptedException {
        String[] arry = new String[]{s1, s2, colored, s4, s5, s11};
        myutils.singleTableCool("options", arry, 100);

    }

    @Test
    public void singleTable_showColors() throws InterruptedException {
        myutils.singleTableCool("All Colors available", Colors.allNamesColored(), 100);
    }



    @Test
    public void centerString_isReallyCentered() {
        char mychar = 'x';
        char space = ' ';

        String centered = Utils.centerString(11,s1);
        assertEquals(mychar, centered.charAt(5) );
        assertEquals(space, centered.charAt(2));
    }


    @Test
    public void allAnsi(){
        for (int i=1; i<255; i++){
            out.println("\u001B["+i+"m"+ "code:" + i );
        }
    }


    @Test
    public void ansiCursors() throws InterruptedException {
        out.print("ciao");
        TimeUnit.MILLISECONDS.sleep(1200);
        out.println("\u001b[3D");
    }
}
