package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.commons.Colors;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Small library of utilities for the CLI, old version
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
                out.println("That's not a number!\n");
                in.next();
            }
            number = in.nextInt();
        } while (number < min || number > max);
        return number;
    }



    public  Date readDate(String kind){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date  date = new Date();
        Date today = new Date();

        try {
            Date oldest = dateFormat.parse("01/01/1900");
        } catch (ParseException e) {
            //it's impossible to trow excep here ehe
        }

        if (kind == null){
            kind = "date";
        }

        out.println("Insert " + kind + " in format dd/MM/yyyy");
        while (true) {
            try {
                date = dateFormat.parse(in.nextLine());
                break;
            } catch (ParseException e) {
                out.println("Wrong format of date");
            }
        }
        //todo comparare data
        return date;
    }



    public List<Integer> readNotSameNumbers(int min, int max, int howmany){
        List<Integer> numbers = new ArrayList<Integer>();

        while (numbers.size() < howmany ) {
            int curinput = readNumbers(min, max);
            if(!numbers.contains(curinput)) {
                numbers.add(curinput);
            }
        }
        return numbers;
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


    public void printMap(String[][] stringIsland ){
        String lineSplit = "";
        StringJoiner splitJoiner = new StringJoiner("┼", "|", "|");
        for (int i= 0; i <5 ; i++) {
            splitJoiner.add(String.format("%14s", "").replace(" ", "-"));
        }
        lineSplit = splitJoiner.toString();
        for (String[] row : stringIsland) {
            StringJoiner sj = new StringJoiner(" | ", "| ", " |");
            for (String col : row) {
                sj.add(col);
            }
            System.out.println(lineSplit);
            System.out.println(sj.toString());
        }
        System.out.println(lineSplit);

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


    public static void maketitle() throws InterruptedException {
        final int DELAY = 2000;
        Terminal.clearAll();
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println(
                        "                                                oooo     oooo ooooooooooo ooooo         oooooooo8   ooooooo  oooo     oooo ooooooooooo               \n" +
                        "                                                 88   88  88   888    88   888        o888     88 o888   888o 8888o   888   888    88                \n" +
                        "                                                  88 888 88    888ooo8     888        888         888     888 88 888o8 88   888ooo8                  \n" +
                        "                                                   888 888     888    oo   888      o 888o     oo 888o   o888 88  888  88   888    oo                \n" +
                        "                                                    8   8     o888ooo8888 o888ooooo88  888oooo88    88ooo88  o88o  8  o88o o888ooo8888"              );
        TimeUnit.MILLISECONDS.sleep(DELAY);
        Terminal.clearAll();
        System.out.println(
                        "                                                                                ooooooooooo   ooooooo                                                   \n" +
                        "                                                                                88  888  88 o888   888o                                                 \n" +
                        "                                                                                    888     888     888                                                 \n" +
                        "                                                                                    888     888o   o888                                                 \n" +
                        "                                                                                   o888o      88ooo88                                                   \n" +
                        "                                                                                                                   ");


        TimeUnit.MILLISECONDS.sleep(DELAY);
        Terminal.clearAll();
        System.out.println(
                        "                                               oooooooo8      o      oooo   oooo ooooooooooo   ooooooo  oooooooooo  ooooo oooo   oooo ooooo           \n" +
                        "                                              888            888      8888o  88  88  888  88 o888   888o 888    888  888   8888o  88   888            \n" +
                        "                                               888oooooo    8  88     88 888o88      888     888     888 888oooo88   888   88 888o88   888            \n" +
                        "                                                      888  8oooo88    88   8888      888     888o   o888 888  88o    888   88   8888   888            \n" +
                        "                                              o88oooo888 o88o  o888o o88o    88     o888o      88ooo88  o888o  88o8 o888o o88o    88  o888o           \n" +
                        "                                                                                                                   \n");

    }






}
