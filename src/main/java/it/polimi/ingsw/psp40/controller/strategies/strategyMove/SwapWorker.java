package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Component;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This strategy allows a {@link Worker}  move into an opponent Worker’s space by forcing their Worker to the space yours just vacated
 *
 * @author sup3rgiu
 */
public class SwapWorker extends DefaultMove {

    /**
     * constructor
     *
     * @param match
     */
    public SwapWorker(Match match) {
        super(match);
    }

    /**
     * @param worker it's the {@link Worker} to move
     * @param cell   it's the new {@link Cell} where is moved
     * @throws ZeroCellsAvailableMoveException
     * @throws WrongCellSelectedMoveException
     * @throws WorkerAlreadyPresentException
     */
    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException {
        List<Cell> availableCells = getAvailableCells(worker);
        if (availableCells.size() == 0) {
            throw new ZeroCellsAvailableMoveException();
        } else if (!availableCells.contains(cell)) {
            throw new WrongCellSelectedMoveException();
        } else {
            if (match.getLocation().getOccupant(cell) != null) // if cell is occupied by an enemy worker
                match.getLocation().swapLocation(match.getLocation().getLocation(worker), cell);
            else
                match.getLocation().setLocation(cell, worker);
        }
    }

    /**
     * Compared to the {@link DefaultMove} this returns also the cell occupied by an enemy worker
     *
     * @param worker it's the {@link Worker} you want to know about
     * @return a list of {@link Cell} where the  {@link Worker} can move
     */
    @Override
    public List<Cell> getAvailableCells(Worker worker) {
        Cell workerCell = match.getLocation().getLocation(worker);
        List<Cell> adjCells = super.getAdjCells(workerCell);
        return adjCells.stream()
                .filter(cell -> cell.getTower().getTopComponent().getComponentCode() <= workerCell.getTower().getTopComponent().getComponentCode() + 1) // remove cells where tower is 2 or more level higher than where the worker is
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> (match.getLocation().getOccupant(cell) == null || match.getLocation().getOccupant(cell).getOwner() != worker.getOwner())) // remove cells where there is a worker owned by the current player
                .collect(Collectors.toList());
    }
}
