package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.SantoriniException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Locale;


public interface StrategyBuild {
    void build(Component CompToBuild, Cell WhereToBuild, Worker worker) throws SantoriniException;
}
