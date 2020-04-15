package it.polimi.ingsw.view.cli;


import it.polimi.ingsw.model.Match;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to change the terminal type in linux, switching from line buffered input to byte
 * using the comand stty https://linux.die.net/man/1/stty
 * Some methods are taken from:
 * https://www.darkcoding.net/software/non-blocking-console-io-is-not-possible/
 */
public class Terminal {

    private static String ttyConfig;
    /**
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void noBuffer() throws IOException, InterruptedException {

        //save current tty configuration
        ttyConfig = stty("-g");

        // set the console to be character-buffered instead of line-buffered
        // min1 with -icanon, set 1 characters minimum for a completed read
        stty("-icanon min 1");

        // disable character echoing
        stty("-echo");

    }

    public static void yesBuffer() throws IOException, InterruptedException {

        //restore previous config
        if (ttyConfig != null) {
            stty(ttyConfig);
            ttyConfig = null;
        }
        //enable again charachter echoing
        stty("echo");

        //enable erase, kill, werase, and rprnt special characters
        stty("icanon min 1");
    }



    public static void resize(int rows, int cols) throws IOException, InterruptedException {
        System.out.print("\u001b[8;50;200t");

    }


    public static void clearAll(){
        System.out.print("\u001b[3J"); //clear
        System.out.print("\u001b[H"); //set cursor at top left
        System.out.print("\u001b[J"); //clear
    }


    public static void saveCursor(){
        System.out.print("\u001b[s");
    }

    public static void restoreCursor(){
        System.out.print("\u001b[u");
    }

    public static void downCursor(int i){
        System.out.print("\u001b[" + i + "B");
    }

    public static void forwCursor(int i){
        System.out.print("\u001b[" + i + "C");
    }

    public static void backwCursor(int i){
        System.out.print("\u001b[" + i + "D");
    }
    public static void upCursor(int i){
        System.out.print("\u001b[" + i + "A");
    }

    public static void moveRelativeCursor(int sugiu, int dxsx){
        if (sugiu < 0 ){
            int abs = Math.abs(sugiu);
            System.out.print("\u001b["+abs+"A"); //up
        }
        else {
            System.out.print("\u001b["+sugiu+"B");
        }
        if (dxsx < 0 ){
            int abs = Math.abs(dxsx);
            System.out.print("\u001b["+abs+"D");
        }
        else {
            System.out.print("\u001b["+dxsx+"C");
        }
    }

    public static void moveAbsoluteCursor(int col, int row){
        System.out.print("\u001b["+row+";"+col+"H");

    }


    /**
     *  Execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(final String args) throws IOException, InterruptedException {

        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] {
                "sh",
                "-c",
                cmd
        });
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(final String[] cmd)  throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        return  new String(bout.toByteArray());
    }


}
