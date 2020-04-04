package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.HashMap;

public class TurnProperties {
    private static int currentTurnId = -1;
    private static HashMap<Worker, Cell> initialPositionMap;
    private static HashMap<Worker, Integer> initialLevels;




    public static void resetAllParameter() {
        currentTurnId = -1;
        initialPositionMap = new HashMap<>();
        initialLevels = new HashMap<>();
    }

    public static HashMap<Worker, Cell> getInitialPositionMap() {
        return initialPositionMap;
    }

    public static HashMap<Worker, Integer> getInitialLevels() {
        return initialLevels;
    }

    public static int getCurrentTurnId() {
        return currentTurnId;
    }
}
