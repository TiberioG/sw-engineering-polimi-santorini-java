package it.polimi.ingsw.controller.strategies.strategyMove;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.model.*;

import java.util.List;
import java.util.stream.Collectors;

// MINOTAURO

public class PushEnemyWorker implements StrategyMove {

    /* Attributes */

    private Match match;

    /* Constructor(s) */

    public PushEnemyWorker(Match match) {
        this.match = match;
    }

    /* Methods */

    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException, CellOutOfBoundsException {
        List<Cell> availableCells = getAvailableCells(worker);
        if(availableCells.size() == 0) { throw new ZeroCellsAvailableMoveException(); }
        else if(!availableCells.contains(cell)) { throw new WrongCellSelectedMoveException(); }
        else {
            if(match.getLocation().getOccupant(cell) != null) { // if the cell is occupied by an enemy worker, shift him
                int xBehind = getCoordinateXBehind(worker, cell);
                int yBehind = getCoordinateYBehind(worker, cell);
                Cell cellBehind = match.getIsland().getCell(xBehind, yBehind); // throws Exception can not happen here because cells are already checked in getAvailableCells()
                match.getLocation().setLocation(cellBehind, match.getLocation().getOccupant(cell)); // moves enemy worker in the cell behind. That cell is free for sure thanks to the previous checks
            }
            match.getLocation().setLocation(cell, worker);
        }
    }

    private List<Cell> getAvailableCells(Worker worker) {
        Cell workerCell = match.getLocation().getLocation(worker);
        List<Cell> adjCells = match.getIsland().getAdjCells(workerCell);
        return adjCells.stream()
                .filter(cell -> !(cell.getTower().getTopComponent().getComponentCode() == workerCell.getTower().getTopComponent().getComponentCode() + 2)) // remove cells where tower is 2 or more level higher than where the worker is
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> (match.getLocation().getOccupant(cell) == null || ((match.getLocation().getOccupant(cell).getOwner() != worker.getOwner()) && checkPositionBehind(worker, cell)) )) // removes cells where there is an allied worker or where there is an enemy worker and the cell behind him is occupied (or doesn't exist)
                .collect(Collectors.toList());
    }

    private boolean checkPositionBehind(Worker worker, Cell cell) {
        // calculates coordinates of the cell behind "cell" (the passed argument) in the movement direction.
        // Ex: Worker is in (x,y)=(2,2), cell is in (x,y)=(2,3), the calculus gives (x,y)=(2,4)
        int xBehind = getCoordinateXBehind(worker, cell);
        int yBehind = getCoordinateYBehind(worker, cell);

        try {
            Cell cellBehind = match.getIsland().getCell(xBehind, yBehind);
            return (cellBehind.getTower().getTopComponent() != Component.DOME) && (match.getLocation().getOccupant(cellBehind) == null);
        } catch (CellOutOfBoundsException e) {
            return false;
        }
    }

    private int getCoordinateXBehind(Worker worker, Cell cell) {
        return cell.getCoordX() + (cell.getCoordX() - match.getLocation().getLocation(worker).getCoordX());
    }

    private int getCoordinateYBehind(Worker worker, Cell cell) {
        return cell.getCoordY() + (cell.getCoordY() - match.getLocation().getLocation(worker).getCoordY());
    }
}