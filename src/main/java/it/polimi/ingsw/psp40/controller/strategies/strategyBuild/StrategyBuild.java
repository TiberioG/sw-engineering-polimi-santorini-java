package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;

/**
 * This is the Interface used to make the workers build, we define a method to actually build components and two methods to get what is allowed to build
 * according to the different strategies
 */
public interface StrategyBuild {
    void build(Component CompToBuild, Cell WhereToBuild, Worker worker) throws SantoriniException;
    List<Cell> getBuildableCells(Worker worker);
    List<Integer> getComponentsBuildable(Cell cell);
}
