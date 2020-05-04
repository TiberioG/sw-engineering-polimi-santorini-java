package it.polimi.ingsw.psp40.view.cli;

import it.polimi.ingsw.psp40.commons.Colors;
import it.polimi.ingsw.psp40.commons.Configuration;

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
    private PrintWriter out ;
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
        in.nextLine();
        return number;
    }

    /**
     * Manages the insertion of an integer on command line input,
     * asking it again until it not a valid value.
     *
     * @param minValue       is the minimum acceptable value of the input
     * @param maxValue       is the maximum acceptable value of the input
     * @return the value of the input
     */

    public int validateIntInput( int minValue, int maxValue) {
        int output;
        try {
            output = in.nextInt();
        } catch (InputMismatchException e) {
            output = minValue - 1;
            in.nextLine();
        }
        while (output > maxValue || output < minValue) {
            System.out.println("Value must be between " + minValue + " and " + maxValue + ". Please, try again:");
            try {
                output = in.nextInt();
            } catch (InputMismatchException e) {
                output = minValue - 1;
                in.nextLine();
            }
        }
        in.nextLine(); // handle nextInt()
        return output;
    }


    public Date readDate(String kind){
        DateFormat dateFormat = new SimpleDateFormat(Configuration.formatDate);
        Date date = new Date();
        Date today = new Date();
        Date oldest = new Date();
        try {
            oldest = dateFormat.parse("01/01/1900");
        } catch (ParseException e) {
            //it's impossible to trow excep here ehe
        }

        if (kind == null){
            kind = "date";
        }

        out.println("Insert " + kind + " in format " + Configuration.formatDate);
        while (true) {
            try {
                date = dateFormat.parse(in.nextLine());
                if (date.before(today) && date.after(oldest)){
                    break;
                }else{
                    if (date.after(today)){
                        out.println("you're too young to play this game");
                    }
                    if (date.before(oldest)) {
                        out.println("you're too old to play this game");
                    }

                }
            } catch (ParseException e) {
                out.println("Wrong format of date");
            }
        }



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


    public int[] readPosition_old(int min, int max){
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


    public int[] readPosition(int min, int max){
        int[] coord = new int[2];
        String input;
        do {
            input = in.nextLine(); //todo non capisco perchè la prima volta mi leggel la linea vuota, mi aiutate pliz?
            while (!input.matches("([" + min + "-" + max + "]),([" + min + "-" + max + "])")) {
                out.println("This is not an allowed coordinate");
                input = in.nextLine();
            }
            String[] ints = input.split(",");
            coord[0] = Integer.parseInt(ints[0]);
            coord[1] = Integer.parseInt(ints[0]);
        }while (coord[0] < min || coord[0] > max || coord[1] < min || coord[1] > max);
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


    public String  tableString(String title, String[] inputList)  {
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

       return  table.toString();

    }




    public String form(String title, int width){
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

        //formarea
           int centerwidth = width - 2;
            String empty = String.format("%0" + centerwidth + "d", 0).replace('0', ' ');
            table.append("║ ").append(empty).append(" ║\n");



        //closeline
        table.append("╚");
        for (int i = 0; i< (width ); i++ ){
            table.append("═");
        }
        table.append("╝\n");

        return  table.toString();
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


    String readnames() {
        String name = in.nextLine();

        while (name.equals("All") || name.isEmpty() || name.matches("^\\s*$")){
            out.println("\nThis username is not allowed, sorry! :(\n\n" +
                    "INSERT ANOTHER ONE:\n");
            name = in.nextLine();
        }
        return name;
    }

    String readIp() {
        String ip;
        ip = in.nextLine();

        while (!isValidIp(ip)) {
            System.out.println("This is not a valid IPv4 address. Please, try again:");
            ip = in.nextLine();
        }
        return ip;
    }


    String readIpNew() {
        String ip;
        ip = in.nextLine();

        while (!isValidIp(ip)) {
            System.out.println("This is not a valid IPv4 address. Please, try again:");
            ip = in.nextLine();
        }
        return ip;
    }

    public static boolean isValidIp(String input) {
        return input.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$") || input.equals("localhost");
    }




}
