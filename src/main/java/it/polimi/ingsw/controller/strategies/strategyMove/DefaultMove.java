package it.polimi.ingsw.controller.strategies.strategyMove;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultMove implements StrategyMove {

    /* Attributes */

    private Match match;

    /* Constructor(s) */

    public DefaultMove(Match match) {
        this.match = match;
    }

    /* Methods */

    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException {
        List<Cell> availableCells = getAvailableCells(worker);
        if(availableCells.size() == 0) { throw new ZeroCellsAvailableMoveException(); }
        else if(!availableCells.contains(cell)) { throw new WrongCellSelectedMoveException(); }
        else {
            match.getLocation().setLocation(cell, worker);
        }
    }

    private List<Cell> getAvailableCells(Worker worker) {
        Cell workerCell = match.getLocation().getLocation(worker);
        List<Cell> adjCells = match.getIsland().getAdjCells(workerCell);
        return adjCells.stream()
                .filter(cell -> !(cell.getTower().getTopComponent().getComponentCode() == workerCell.getTower().getTopComponent().getComponentCode() + 2)) // remove cells where tower is 2 or more level higher than where the worker is
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker
                .collect(Collectors.toList());
    }
}
