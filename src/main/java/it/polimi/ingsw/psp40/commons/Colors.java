package it.polimi.ingsw.psp40.commons;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public enum Colors {
    WHITE ("\u001B[97m", Color.WHITE),
    BLACK ("\u001B[30m", Color.BLACK),
    BLUE("\u001B[34m", Color.BLUE),
    RED ("\u001B[31m", Color.RED),
    GREEN("\u001B[32m", Color.GREEN),
    YELLOW("\u001B[33m", Color.YELLOW),
    MAGENTA("\u001B[35m", Color.MAGENTA),
    CYAN("\u001B[36m", Color.CYAN),
    GREY("\u001B[37m", Color.GREY);

    private final String ansiCode;
    private final Color color;

    /**
     * Constructor
     * @param ansiCode
     */
    Colors(String ansiCode, Color color) {
        this.ansiCode = ansiCode;
        this.color = color;
    }

    /**
     * Method to get the ansi code of a color
     * @return a String containing the AnsiCode
     */
    public String getAnsiCode() {
        return ansiCode;
    }

    public Color getPaintColor() {
        return color;
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


