package it.polimi.ingsw.view;

import it.polimi.ingsw.commons.Colors;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Small library of utilities for the CLI
 * @author tiberioG
 */
public class Utils {
    private  PrintWriter out ;
    private Scanner in ;

    public Utils(Scanner in, PrintWriter out) {
        this.in  = in;
        this.out = out;
    }

    /**
     * just an utiliyt to et longest string in a matrix of strings
     * @param matrix
     * @return int of chars of longest string
     */
    public static int longestMatrix(String[][] matrix) {
        int maxLength = 0;
        String longestString = "";
        for (String[] strings : matrix) {
            for (int j = 0; j < matrix.length; j++) {
                if (strings[j].length() > maxLength) {
                    maxLength = strings[j].length();
                    longestString = strings[j];
                }

            }
        }
        return longestString.length();
    }


    public static int longestArray(String[] array) {
        int maxLength = 0;
        String longestString = "";
        for (String s : array) {
            if (s.length() > maxLength) {
                maxLength = s.length();
                longestString = s;
            }

        }
        return longestString.length();
    }


    /**
     * utils for choices
     * @param min smaller number readable
     * @param max biggest number readable
     * @return number read
     */
    public int readNumbers(int min, int max){
        int number;
        do {
            out.println("Choose one number:\n");
            while (!in.hasNextInt()) {
                out.println("\nThat's not a number!\n");
                in.next();
            }
            number = in.nextInt();
        } while (number < min || number > max);
        return number;
    }


    public int[] readPosition(int min, int max){
        int[] coord = new int[2];
        do {
            out.println("Insert coordinates in format x,y:");
            while (!in.hasNext("\\d*,\\d*")) {
                out.println("\nThat's not the correct pattern!");
                in.next();
            }
            in.nextLine();
            String correct = in.nextLine();
            out.println(correct);
            String[] ints= correct.split(",");
            coord[0] = Integer.parseInt(ints[0]);
            coord[1] = Integer.parseInt(ints[0]);
        } while (coord[0] < min || coord[0] > max || coord[1] < min || coord[1] > max);

        return coord;
    }



    /**
     * method to print a single column table in ASCII art with numbered items
     * @param title a string containing the title of the table
     * @param inputList an Array of strings to display numbered
     */
    public void singleTable(String title, String[] inputList){
        final  int SPACEADD = 5;
        int height = inputList.length;

        int width = Math.max(Utils.longestArray(inputList), title.length()) + SPACEADD;
        int innerwidth = width - 4;

        String titleString = centerString(width, title);
        //top line
        out.print("╔");
        for (int i = 0; i< (width); i++ ){
            out.print("═");
        }
        out.print("╗\n");

        //title line
        out.print("║" + titleString.replaceAll("\n", " ").toUpperCase() +"║\n");

        //close tile line
        out.print("╠");
        for (int i = 0; i< (width); i++ ){
            out.print("═");
        }
        out.print("╣\n");

        //middle item lines
        for (int i = 0; i < height; i++ ){
            String nonewline = inputList[i].replaceAll("\n", " ");
            String output = String.format(". %-" + innerwidth + "s", nonewline);

            if (nonewline.length() > Colors.reset().length()) { // if length is less than the colorreset length it means cannot be colored for sure
                if (!Colors.reset().equals(nonewline.substring(nonewline.length() - Colors.reset().length()))) { //then I check if it contains a color reset at the end
                    out.print("║ " + i + output + "║\n"); //if not
                } else { // it there is colorreset at the end: FIX needed!
                    out.print("║ " + i + output + "         ║\n");  //sorry for magic numbers of spaces but just works

                }
            }
            else {
                out.print("║ " + i + output + "║\n");
            }
        }

        //closeline
        out.print("╚");
        for (int i = 0; i< (width); i++ ){
            out.print("═");
        }
        out.print("╝\n");

        out.println();

    }


    public void singleTableCool(String title, String[] inputList, int delay) throws InterruptedException {
        final  int SPACEADD = 5;
        int height = inputList.length;

        int width = Math.max(Utils.longestArray(inputList), title.length()) + SPACEADD;
        int innerwidth = width - 4;

        String titleString = centerString(width, title);

        StringBuilder table = new StringBuilder();
        //top line
        table.append("╔");
        for (int i = 0; i< (width); i++ ){
            table.append("═");
        }
        table.append("╗\n");

        //title line
        table.append("║").append(titleString.replaceAll("\n", " ").toUpperCase()).append("║\n");

        //close tile line
        table.append("╠");
        for (int i = 0; i< (width); i++ ){
            table.append("═");
        }
        table.append("╣\n");

        //middle item lines
        for (int i = 0; i < height; i++ ){
            String nonewline = inputList[i].replaceAll("\n", " ");
            String output = String.format(". %-" + innerwidth + "s", nonewline);

            if (nonewline.length() > Colors.reset().length()) { // if length is less than the colorreset length it means cannot be colored for sure
                if (!Colors.reset().equals(nonewline.substring(nonewline.length() - Colors.reset().length()))) { //then I check if it contains a color reset at the end
                    table.append("║ ").append(i).append(output).append("║\n"); //if not
                } else { // it there is colorreset at the end: FIX needed!
                    table.append("║ ").append(i).append(output).append("         ║\n");  //sorry for magic numbers of spaces but just works

                }
            }
            else {
                table.append("║ ").append(i).append(output).append("║\n");
            }
        }

        //closeline
        table.append("╚");
        for (int i = 0; i< (width); i++ ){
            table.append("═");
        }
        table.append("╝\n");

        String[] lines = table.toString().split(System.getProperty("line.separator"));

        for (int i = 0; i<lines.length; i++){
            out.println(lines[i]);
            TimeUnit.MILLISECONDS.sleep(delay);
        }


    }


    /**
     * Static method used to center a string using blanks
     * @param width int number of the width of output string
     * @param s the string to center with spaces
     * @return a string with added spaces with the original string in the middle
     */
    public static String centerString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
}
