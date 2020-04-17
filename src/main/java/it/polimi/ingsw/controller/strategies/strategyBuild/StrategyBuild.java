package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.SantoriniException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is the Interface used to make the workers build
 */
public interface StrategyBuild {
    void build(Component CompToBuild, Cell WhereToBuild, Worker worker) throws SantoriniException;
    List<Cell> getBuildableCells(Worker worker);
}
