package it.polimi.ingsw.psp40.controller.strategies.strategyLose;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.controller.Turn;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.controller.strategies.strategyWin.StrategyWin;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

public class DefaultLose implements StrategyLose {

    /* Attributes */

    protected Match match;
    /* Constructor(s) */

    public DefaultLose(Match match) {
        this.match = match;
    }

    /* Methods */

    @Override
    public boolean checkLose(StrategyMove strategyMove) {
        return match.getCurrentPlayer().getWorkers().stream().filter(worker -> strategyMove.getAvailableCells(worker).size() > 0).count() == 0;
    }
}
