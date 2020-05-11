package it.polimi.ingsw.psp40.controller.strategies.strategyLose;

import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

public class LoseOtherCantLevelUp extends DefaultLose{

    /* Constructor(s) */
    public LoseOtherCantLevelUp (Match match) {
        super(match);
    }

    @Override
    public boolean checkLoseForMove(StrategyMove strategyMove) {
        boolean hasLose = super.checkLoseForMove(strategyMove);
        if (hasLose) match.getMatchProperties().setOthersCantLevelUp(false);
        return hasLose;
    }

    @Override
    public boolean checkLoseForBuild(StrategyBuild strategyBuild, Worker worker) {
        boolean hasLose = super.checkLoseForBuild(strategyBuild, worker);
        if (hasLose) match.getMatchProperties().setOthersCantLevelUp(false);
        return hasLose;
    }
}
