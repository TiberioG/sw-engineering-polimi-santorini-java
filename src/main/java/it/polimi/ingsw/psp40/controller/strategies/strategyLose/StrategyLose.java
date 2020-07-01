package it.polimi.ingsw.psp40.controller.strategies.strategyLose;


import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Worker;

/**
 * This is the interface used to define methods to check if a user has lost
 */
public interface StrategyLose {
    boolean checkLoseForMove(StrategyMove strategyMove);

    boolean checkLoseForBuild(StrategyBuild strategyBuild, Worker worker);
}
