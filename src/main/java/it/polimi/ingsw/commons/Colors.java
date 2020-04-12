package it.polimi.ingsw.commons;

public enum Colors {
    WHITE ("\u001B[97m"),
    BLACK ("\u001B[30m"),
    BLUE("\u001B[34m"),
    RED ("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    GREY("\u001B[37m");

    private final String ansiCode;

    Colors(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    public String getAnsiCode() {
        return ansiCode;
    }

    @Override
    public String toString(){
        return ansiCode;
    }

    public static String reset(){
        return  "\u001B[0m";
    }

}


