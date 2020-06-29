package it.polimi.ingsw.psp40.model;

import it.polimi.ingsw.psp40.commons.PhaseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to set temporary flags and save some state of a turn
 *
 * @author Vito96, TiberioG, sup3rgiu
 */
public class MatchProperties {
    private HashMap<Worker, Cell> initialPositionMap = new HashMap<>();
    private HashMap<Worker, Integer> initialLevels = new HashMap<>();
    private HashMap<Worker, Cell> builtInThisTurn = new HashMap<>();
    private List<PhaseType> performedPhases = new ArrayList<>();
    private boolean othersCantLevelUp = false;


    /**
     * this resets all the parameters
     */
    public void resetParameterForTurn() {
        initialPositionMap = new HashMap<>();
        initialLevels = new HashMap<>();
        builtInThisTurn = new HashMap<>();
        performedPhases = new ArrayList<>();
    }

    /**
     * getter of a HashMap of initial {@link Cell} position of workers at beginning of turn
     *
     * @return
     */
    public HashMap<Worker, Cell> getInitialPositionMap() {
        return initialPositionMap;
    }

    /**
     * getter of a HashMap of initial level of workers at beginning of turn
     *
     * @return
     */
    public HashMap<Worker, Integer> getInitialLevels() {
        return initialLevels;
    }

    /**
     * Getter of {@link PhaseType} happened in the turn
     *
     * @return
     */
    public List<PhaseType> getPerformedPhases() {
        return performedPhases;
    }

    /**
     * This method is used to update the table of the builds done during a turn
     *
     * @param worker is the {@link Worker} that has built
     * @param cell   is the {@link Cell} where the worker has built
     */
    public void builtNow(Worker worker, Cell cell) {
        builtInThisTurn.put(worker, cell);
    }

    /**
     * This method is used to get where a Worker has built during the turn
     *
     * @param worker is the {@link Worker} that has built
     * @return the {@link Cell} where the worker has built
     */
    public Cell getPreviousBuild(Worker worker) {
        return builtInThisTurn.get(worker);
    }

    /**
     * getter of flag for {@link it.polimi.ingsw.psp40.controller.strategies.strategyMove.OthersCantLevelUp}
     *
     * @return the saved othersCantLevelUp
     */
    public boolean getOthersCantLevelUp() {
        return othersCantLevelUp;
    }

    /**
     * Setter of flag for {@link it.polimi.ingsw.psp40.controller.strategies.strategyMove.OthersCantLevelUp}
     */
    public void setOthersCantLevelUp(boolean othersCantLevelUp) {
        this.othersCantLevelUp = othersCantLevelUp;
    }
}
