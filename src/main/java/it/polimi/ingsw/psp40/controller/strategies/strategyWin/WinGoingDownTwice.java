package it.polimi.ingsw.psp40.controller.strategies.strategyWin;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.controller.TurnProperties;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

// PAN

public class WinGoingDownTwice implements StrategyWin {

    /* Attributes */

    private Match match;

    /* Constructor(s) */

    public WinGoingDownTwice(Match match) {
        this.match = match;
    }

    /* Methods */

    @Override
    public boolean checkWin() {
        for(Worker worker : match.getCurrentPlayer().getWorkers()) {
            if(match.getLocation().getLocation(worker) != TurnProperties.getInitialPositionMap().get(worker)) { // check if the worker has moved
                boolean defaultWin = match.getLocation().getLocation(worker).getTower().getTopComponent() == Component.THIRD_LEVEL;
                boolean wentDownTwiceOrMore = (match.getLocation().getLocation(worker).getTower().getTopComponent().getComponentCode() + 2) <= TurnProperties.getInitialLevels().get(worker);
                return defaultWin || wentDownTwiceOrMore;
            }
        }
        return false;
    }
}
