package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.model.Cell;

public interface StrategyBuild {
    void build(Component component, Cell cell);
}
