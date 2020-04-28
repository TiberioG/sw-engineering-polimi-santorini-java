package it.polimi.ingsw.psp40.commons;

public class Configuration {
    public static final String formatDate = "dd/MM/yyyy";
    public static final int serverTimeout = 2000;
    public static final int clientTimeout = 1000;
    private static final String controllerPackage = "it.polimi.ingsw.psp40.controller";
    public static final String strategyMovePackage = controllerPackage + ".strategies.strategyMove";
    public static final String strategyBuildPackage = controllerPackage + ".strategies.strategyBuild";
    public static final String strategyWinPackage = controllerPackage + ".strategies.strategyWin";
}
