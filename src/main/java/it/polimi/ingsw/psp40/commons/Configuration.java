package it.polimi.ingsw.psp40.commons;

public class Configuration {
    public static final String formatDate = "dd/MM/yyyy";
    public static final String minDate = "01/01/1900";
    public static final int serverTimeout = 2000;
    public static final int clientTimeout = 1000;
    public static final String controllerPackage = "it.polimi.ingsw.psp40.controller";
    public static final String strategyMovePackage = controllerPackage + ".strategies.strategyMove";
    public static final String strategyBuildPackage = controllerPackage + ".strategies.strategyBuild";
    public static final String strategyWinPackage = controllerPackage + ".strategies.strategyWin";
    public static final String strategyLosePackage = controllerPackage + ".strategies.strategyLose";
    public static final boolean DEBUG = true;
}
