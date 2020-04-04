package it.polimi.ingsw.controller.strategies.strategyMove;

import it.polimi.ingsw.exceptions.SantoriniException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;

public interface StrategyMove {
    void move(Worker worker, Cell cell) throws SantoriniException;
}
