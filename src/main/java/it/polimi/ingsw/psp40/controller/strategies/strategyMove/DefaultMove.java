package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultMove implements StrategyMove {

    /* Attributes */

    protected Match match;

    /* Constructor(s) */

    public DefaultMove(Match match) {
        this.match = match;
    }

    /* Methods */

    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException, CellOutOfBoundsException {
        List<Cell> availableCells = getAvailableCells(worker);
        if(availableCells.size() == 0) { throw new ZeroCellsAvailableMoveException(); }
        else if(!availableCells.contains(cell)) { throw new WrongCellSelectedMoveException(); }
        else {
            match.getLocation().setLocation(cell, worker);
        }
    }

    @Override
    public List<Cell> getAvailableCells(Worker worker) {
        Cell workerCell = match.getLocation().getLocation(worker);
        List<Cell> adjCells = this.getAdjCells(workerCell);
        return adjCells.stream()
                .filter(cell -> cell.getTower().getTopComponent().getComponentCode() <= workerCell.getTower().getTopComponent().getComponentCode() + 1) // remove cells where tower is 2 or more level higher than where the worker is
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker
                .collect(Collectors.toList());
    }

    @Override
    public List<Cell> getAdjCells(Cell cell) {
        boolean cantLevelUp = match.getMatchProperties().isOthersCantLevelUp();
        List<Cell> adjCells = match.getIsland().getAdjCells(cell);

        if(cantLevelUp) {
            adjCells = adjCells.stream()
                        .filter(_cell -> _cell.getTower().getTopComponent().getComponentCode() <= cell.getTower().getTopComponent().getComponentCode()) // remove cells where tower is higher than where the worker is
                        .collect(Collectors.toList());
        }

        return adjCells;
    }


}
