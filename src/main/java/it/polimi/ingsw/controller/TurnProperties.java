package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.HashMap;

public class TurnProperties {
    private static int currentTurnId = -1;
    private static HashMap<Worker, Cell> initialPositionMap;




    public static void resetAllParameter() {
        currentTurnId = -1;
        initialPositionMap = new HashMap<>();
    }

    public static HashMap<Worker, Cell> getInitialPositionMap() {
        return initialPositionMap;
    }

    public static int getCurrentTurnId() {
        return currentTurnId;
    }
}
