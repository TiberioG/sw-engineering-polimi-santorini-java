package it.polimi.ingsw.psp40.commons;

import java.util.ArrayList;
import java.util.Random;

public enum Colors {
    WHITE ("\u001B[97m"),
    BLACK ("\u001B[30m"),
    BLUE("\u001B[34m"),
    RED ("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    MAGENTA("\u001B[35m"),
    CYAN("\u001B[36m"),
    GREY("\u001B[37m");

    private final String ansiCode;

    /**
     * Constructor
     * @param ansiCode
     */
    Colors(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    /**
     * Method to get the ansi code of a color
     * @return a String containing the AnsiCode
     */
    public String getAnsiCode() {
        return ansiCode;
    }

    public static String[] allNames(){
        String [] list = new String[Colors.values().length];
        for (int i = 0; i < Colors.values().length; i++){
            list[i] = Colors.values()[i].toString();
        }
        return list;
    }

    /**
     * Method used to retireve the a string of all the names of colors, each one colored
     * @return an array of strings constaining the NAME of color plus the ansi code
     */
    public static String[] allNamesColored(){
        String [] list = new String[Colors.values().length];
        for (int i = 0; i < Colors.values().length; i++){
            list[i] = Colors.values()[i].getAnsiCode() + Colors.values()[i].toString() + Colors.reset() ;
        }
        return list;
    }

    /**
     *
     * @return the resetcolor ANSI code
     */
      public static String reset(){
        return  "\u001B[0m";
    }

    public static String randomColor(){
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < Colors.values().length; i++){
            list.add(Colors.values()[i].getAnsiCode());
        }
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));

    }







}


