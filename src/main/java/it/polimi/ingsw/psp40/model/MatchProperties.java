package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.commons.PhaseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatchProperties {
    private int currentTurnId = -1;
    private HashMap<Worker, Cell> initialPositionMap = new HashMap<>();
    private HashMap<Worker, Integer> initialLevels = new HashMap<>();
    private HashMap<Worker, Cell> builtInThisTurn = new HashMap<>();
    private List<PhaseType> performedPhases = new ArrayList<>();
    private boolean othersCantLevelUp = false;


    public void resetParameterForTurn() {
        currentTurnId = -1;
        initialPositionMap = new HashMap<>();
        initialLevels = new HashMap<>();
        builtInThisTurn = new HashMap<>();
        performedPhases = new ArrayList<>();
    }

    public HashMap<Worker, Cell> getInitialPositionMap() {
        return initialPositionMap;
    }

    public HashMap<Worker, Integer> getInitialLevels() {
        return initialLevels;
    }

    public List<PhaseType> getPerformedPhases() {
        return performedPhases;
    }

    public int getCurrentTurnId() {
        return currentTurnId;
    }

    /**
     * This method is used to update the table of the builds done during a turn
     * @param worker is the {@link Worker} that has built
     * @param cell is the {@link Cell} where the worker has built
     */
    public void builtNow(Worker worker, Cell cell){
        builtInThisTurn.put(worker, cell);
    }

    /**
     * This method is used to get where a Worker has built during the turn
     * @param worker is the {@link Worker} that has built
     * @return the {@link Cell} where the worker has built
     */
    public Cell getPreviousBuild(Worker worker){
        return builtInThisTurn.get(worker);
    }

    public boolean isOthersCantLevelUp() {
        return othersCantLevelUp;
    }

    public void setOthersCantLevelUp(boolean othersCantLevelUp) {
        this.othersCantLevelUp = othersCantLevelUp;
    }
}
