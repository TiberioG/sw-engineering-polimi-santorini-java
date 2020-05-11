package it.polimi.ingsw.psp40.controller.strategies.strategyLose;


import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Worker;

public interface StrategyLose {
    boolean checkLoseForMove(StrategyMove strategyMove);
    boolean checkLoseForBuild(StrategyBuild strategyBuild, Worker worker);
}
