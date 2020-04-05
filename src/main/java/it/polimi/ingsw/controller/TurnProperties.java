package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

import java.util.HashMap;

public class TurnProperties {
    private static int currentTurnId = -1;
    private static HashMap<Worker, Cell> initialPositionMap;
    private static HashMap<Worker, Integer> initialLevels;
    private static HashMap<Worker, Cell> builtInThisTurn;




    public static void resetAllParameter() {
        currentTurnId = -1;
        initialPositionMap = new HashMap<>();
        initialLevels = new HashMap<>();
        builtInThisTurn = new HashMap<>();
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

    /**
     * This method is used to update the table of the builds done during a turn
     * @param worker is the {@link Worker} that has built
     * @param cell is the {@link Cell} where the worker has built
     */
    public static void builtNow(Worker worker, Cell cell){
        builtInThisTurn.put(worker, cell);
    }

    /**
     * This method is used to get where a Worker has built during the turn
     * @param worker is the {@link Worker} that has built
     * @return the {@link Cell} where the worker has built
     */
    public static Cell getPreviousBuild(Worker worker){
        return builtInThisTurn.get(worker);
    }

}
