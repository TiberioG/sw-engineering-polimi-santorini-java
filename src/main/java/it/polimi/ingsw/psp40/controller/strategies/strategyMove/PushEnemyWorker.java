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


/**
 * This strategy check if your Worker may move into an opponent Worker’s space, their Worker can be
 * forced one space straight backwards to an unoccupied space at any level.
 *
 * @author sup3rgiu
 */

public class PushEnemyWorker extends DefaultMove {

    /**
     * constructor
     *
     * @param match
     */
    public PushEnemyWorker(Match match) {
        super(match);
    }


    /**
     * This move allows moving also in the space of an enemy worker and forces him one space straight backwards to an unoccupied space at any level
     *
     * @param worker it's the {@link Worker} to move
     * @param cell   it's the new {@link Cell} where is moved
     * @throws ZeroCellsAvailableMoveException
     * @throws WrongCellSelectedMoveException
     * @throws WorkerAlreadyPresentException
     * @throws CellOutOfBoundsException
     */
    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException, CellOutOfBoundsException {
        List<Cell> availableCells = getAvailableCells(worker);
        if (availableCells.size() == 0) {
            throw new ZeroCellsAvailableMoveException();
        } else if (!availableCells.contains(cell)) {
            throw new WrongCellSelectedMoveException();
        } else {
            if (match.getLocation().getOccupant(cell) != null) { // if the cell is occupied by an enemy worker, shift him
                int xBehind = getCoordinateXBehind(worker, cell);
                int yBehind = getCoordinateYBehind(worker, cell);
                Cell cellBehind = match.getIsland().getCell(xBehind, yBehind); // throws Exception can not happen here because cells are already checked in getAvailableCells()
                match.getLocation().setLocation(cellBehind, match.getLocation().getOccupant(cell)); // moves enemy worker in the cell behind. That cell is free for sure thanks to the previous checks
            }
            match.getLocation().setLocation(cell, worker);
        }
    }

    /**
     * This gets the available cells
     * remove cells where tower is 2 or more level higher than where the worker is
     * removes cells where there is an allied worker or where there is an enemy worker and the cell behind him is occupied (or doesn't exist)
     * remove cells where the tower is complete
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
                .filter(cell -> (match.getLocation().getOccupant(cell) == null || ((match.getLocation().getOccupant(cell).getOwner() != worker.getOwner()) && checkPositionBehind(worker, cell)))) // removes cells where there is an allied worker or where there is an enemy worker and the cell behind him is occupied (or doesn't exist)
                .collect(Collectors.toList());
    }

    /**
     * calculates coordinates of the cell behind "cell" (the passed argument) in the movement direction.
     * Ex: Worker is in (x,y)=(2,2), cell is in (x,y)=(2,3), the calculus gives (x,y)=(2,4)
     *
     * @param worker
     * @param cell
     * @return
     */
    private boolean checkPositionBehind(Worker worker, Cell cell) {
        int xBehind = getCoordinateXBehind(worker, cell);
        int yBehind = getCoordinateYBehind(worker, cell);

        try {
            Cell cellBehind = match.getIsland().getCell(xBehind, yBehind);
            return (cellBehind.getTower().getTopComponent() != Component.DOME) && (match.getLocation().getOccupant(cellBehind) == null);
        } catch (CellOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * simple getter of coord x behind a worker
     *
     * @param worker
     * @param cell
     * @return int X
     */
    private int getCoordinateXBehind(Worker worker, Cell cell) {
        return cell.getCoordX() + (cell.getCoordX() - match.getLocation().getLocation(worker).getCoordX());
    }

    /**
     * simple getter of coord y behind a worker
     *
     * @param worker
     * @param cell
     * @return int y
     */
    private int getCoordinateYBehind(Worker worker, Cell cell) {
        return cell.getCoordY() + (cell.getCoordY() - match.getLocation().getLocation(worker).getCoordY());
    }
}
