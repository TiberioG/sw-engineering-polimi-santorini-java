package it.polimi.ingsw.psp40.view.cli;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Class used to manage the appearence of Terminal using mainly ansi codes and stty command
 * @author tiberioG
 */
public class Terminal {
    private static String ttyConfig;

    /**
     * Disable buffer in terminal calling stty
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

    /**
     * enables again buffer if needed, calling again stty
     * @throws IOException
     * @throws InterruptedException
     */
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


    /**
     * Method to resize the terminal
     * To enable these in XTerm, set the following resource to true: allowWindowOps
     * To enable these in iTerm2, deselect the following: Preferences > Profiles > [profile] > Terminal > Disable session-initiated window resizing
     * @param rows
     * @param cols
     */
    public static void resize(int rows, int cols){
        System.out.print("\u001b[8;"+ rows + ";" + cols + "t");

    }

    public static void superClear(){
        System.out.print("\u001b[3J"); //clear
        System.out.print("\u001b[H"); //set cursor at top left
        System.out.print("\u001b[J"); //clear
    }

    /**
     * clear entire screen and delete all lines saved in the scrollback buffer
     */
    public static void clearAll(){
        System.out.print("\u001b[3J");
     }

    /**
     * clear entire screen (and moves cursor to upper left
     */
    public static void clearScreen(){
        System.out.print("\u001b[2J");
    }

    /**
     * clear entire line
     */
    public static void clearLine(){
        System.out.print("\u001b[2K");
    }

    /**
     * Method to enable showing the cursor in Terminal
     */
    public static void showCursor() {
        System.out.print("\u001b[?25h");
    }

    /**
     * Method to disable showing the cursor in Terminal
     */
    public static void hideCursor() {
        System.out.print("\u001b[?25l");
    }

    /**
     * moves cursor relatively to actual position
     * @param sugiu
     * @param dxsx
     */
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

    public static void moveAbsoluteCursor(int row, int col){
        System.out.print("\u001b["+row+";"+col+"H");
    }

    /**
     * This is able to get the coordinates of the current position of cursor
     * I don't delete this cause it took one afternoon to get it working and can be useful in future
     * @return the coordinates of the current position of cursor
     * @throws IOException
     */
    public static int[] getAbsoluteCursor() throws IOException {
        ArrayList<Integer> row = new ArrayList<Integer>();
        ArrayList<Integer> col = new ArrayList<Integer>();

        System.out.print("\u001b[6n"); //Sending this i receive ESC[row;colR
        int next1 = System.in.read();
        int next2 = System.in.read();

        if (next1 == 27 && next2 == 91) { //parsing ESC[
            do {
                row.add(System.in.read());
            } while (row.get(row.size() - 1) != 59); // ;
            do {
                col.add(System.in.read());
            } while (col.get(col.size() - 1) != 82); // R

            row.remove(row.size() - 1);
            col.remove(col.size() - 1);
        }

        int[] out = new int[2];

        //conversion from ASCII -> String -> Int -> DECimal int
        for(int i = col.size() - 1 ; i >= 0; i--){
            int k = 0;
            String cifra = Character.toString(col.get(col.size()- i - 1));
            out[1] = (int) (Integer.parseInt(cifra) * Math.pow(10, i) + out[1]);
        }
        for(int i = row.size() - 1 ; i >=0; i--){
            String cifra = Character.toString(row.get(row.size() - i - 1));
            out[0] = (int) (Integer.parseInt(cifra) * Math.pow(10, i) + out[0]);
        }
        return out;

    }

    /**
     *  Execute the stty command with the specified arguments
     *  against the current active terminal.
     *  author:
     *  https://www.darkcoding.net/software/non-blocking-console-io-is-not-possible/
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
     *  author:
     *  https://www.darkcoding.net/software/non-blocking-console-io-is-not-possible/
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
