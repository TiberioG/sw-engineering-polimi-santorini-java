package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;

/**
 * This is the Interface used to make the workers move
 */
public interface StrategyMove {
    void move(Worker worker, Cell cell) throws SantoriniException;
    List<Cell> getAvailableCells(Worker worker);
    List<Cell> getAdjCells (Cell cell);
}
