package it.polimi.ingsw.view;

import java.io.PrintWriter;
import java.util.Scanner;

public class Utils {
    private  PrintWriter out ;
    private Scanner in ;

    public Utils(Scanner in, PrintWriter out) {
        this.in =in;
        this.out=out;
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

    public void listPrinter(String title, String[] inputList){
        int height = inputList.length;

        int width = Math.max(Utils.longestArray(inputList), title.length());

        for (int i = 0; i< (width+4); i++ ){
            out.print("═");
        }



        out.println("\n\n╔═══════════════════════╗\n" +
                "║        OPTIONS        ║\n" +
                "╠═══════════════════════╣\n" +
                "║ 1. WHITE              ║\n" +
                "║ 2. BLACK              ║\n" +
                "║ 3. BLUE               ║\n" +
                "║ 4. RED                ║\n" +
                "║ 5. GREEN              ║\n" +
                "║ 6. YELLOW             ║\n" +
                "╚═══════════════════════╝");

        out.println();




    }
}
