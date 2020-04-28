package it.polimi.ingsw.psp40.controller.strategies.strategyWin;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.controller.TurnProperties;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

public class DefaultWin implements StrategyWin {

    /* Attributes */

    private Match match;

    /* Constructor(s) */

    public DefaultWin(Match match) {
        this.match = match;
    }

    /* Methods */

    @Override
    public boolean checkWin() {
        boolean hasWin = false;
        for(Worker worker : match.getCurrentPlayer().getWorkers()) {
            if(match.getLocation().getLocation(worker) != TurnProperties.getInitialPositionMap().get(worker)) // check if the worker has moved
                hasWin = match.getLocation().getLocation(worker).getTower().getTopComponent() == Component.THIRD_LEVEL || hasWin;
        }
        return hasWin;
    }
}
