package it.polimi.ingsw.psp40.controller.strategies.strategyLose;

import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Match;

public class LoseOtherCantLevelUp extends DefaultLose{

    /* Constructor(s) */
    public LoseOtherCantLevelUp (Match match) {
        super(match);
    }

    public boolean checkLose(StrategyMove strategyMove) {
        boolean hasLose = super.checkLose(strategyMove);
        if (!hasLose) match.getMatchProperties().setOthersCantLevelUp(false);
        return hasLose;
    }
}
