package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.model.*;

import java.util.Locale;

public interface StrategyBuild {
    void build(Component component, Cell cell, Location location);
}
