package it.polimi.ingsw.psp40.controller.strategies.strategyWin;

import it.polimi.ingsw.psp40.model.Component;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

/**
 * This class describes the default rules for winning a match: if a worker reaches the third level
 *
 * @author Vito96
 */
public class DefaultWin implements StrategyWin {

    private Match match;

    /**
     * Constructor
     *
     * @param match
     */
    public DefaultWin(Match match) {
        this.match = match;
    }

    /**
     * This returns true if a worker has moved till the third level
     * check if the worker comes from the third level and check if the worker has moved
     *
     * @return true if and only if a worker has moved till the third level
     */
    @Override
    public boolean checkWin() {
        boolean hasWin = false;
        for (Worker worker : match.getCurrentPlayer().getWorkers()) {
            boolean workerComeFromThirdLevel = match.getMatchProperties().getInitialPositionMap().get(worker).getTower().getTopComponent() == Component.THIRD_LEVEL,  // check if the worker comes from the third level
                    workerHasMoved = match.getLocation().getLocation(worker) != match.getMatchProperties().getInitialPositionMap().get(worker);  // check if the worker has moved
            if (workerHasMoved && !workerComeFromThirdLevel)
                hasWin = match.getLocation().getLocation(worker).getTower().getTopComponent() == Component.THIRD_LEVEL || hasWin;
        }
        return hasWin;
    }
}
