package it.polimi.ingsw.psp40.controller.strategies.strategyWin;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;


/**
 * this class is used for the card PAN
 * You also win if your Worker moves down two or more levels.
 *
 * @author Vito96
 */
public class WinGoingDownTwice implements StrategyWin {
    private Match match;

    /**
     * constructor
     *
     * @param match
     */
    public WinGoingDownTwice(Match match) {
        this.match = match;
    }

    /**
     * Method to check if a worker has moved down twice or more from his previous level
     *
     * @return true if and only if a worker has moved down twice or more from his previous level
     */
    @Override
    public boolean checkWin() {
        for (Worker worker : match.getCurrentPlayer().getWorkers()) {
            if (match.getLocation().getLocation(worker) != match.getMatchProperties().getInitialPositionMap().get(worker)) { // check if the worker has moved
                boolean defaultWin = match.getLocation().getLocation(worker).getTower().getTopComponent() == Component.THIRD_LEVEL;
                boolean wentDownTwiceOrMore = (match.getLocation().getLocation(worker).getTower().getTopComponent().getComponentCode() + 2) <= match.getMatchProperties().getInitialLevels().get(worker);
                return defaultWin || wentDownTwiceOrMore;
            }
        }
        return false;
    }
}
