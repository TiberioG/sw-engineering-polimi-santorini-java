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
 * This class is used to define the default rules to move a {@link Worker}
 * @author sup3rgiu
 */
public class DefaultMove implements StrategyMove {
    protected Match match;

    /**
     * Constructor
     * @param match
     */
    public DefaultMove(Match match) {
        this.match = match;
    }

    /**
     * this method is used to change the model, changing the location {@link Cell} of a {@link Worker}
     * @param worker it's the {@link Worker} to move
     * @param cell it's the new {@link Cell} where is moved
     * @throws ZeroCellsAvailableMoveException
     * @throws WrongCellSelectedMoveException
     * @throws WorkerAlreadyPresentException
     * @throws CellOutOfBoundsException
     */
    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException, CellOutOfBoundsException {
        List<Cell> availableCells = getAvailableCells(worker);
        if(availableCells.size() == 0) { throw new ZeroCellsAvailableMoveException(); }
        else if(!availableCells.contains(cell)) { throw new WrongCellSelectedMoveException(); }
        else {
            match.getLocation().setLocation(cell, worker);
        }
    }

    /**
     * This method is used to get from the Model a list of {@link Cell} where a worker can move according to the default rules
     * @param worker it's the {@link Worker} you want to know about
     * @return a list of {@link Cell} where the  {@link Worker} can move according to the default rules
     */
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

    /**
     * This method is used to get the adjacent cells of a specified cell, removing the ones where there is a slope > +1 block
     * This is used if the strategy {@link OthersCantLevelUp} is enabled
     * @param cell where you want to check the movable cells, excluding going up a level
     * @return a list of {@link Cell} which are adjacent to the input parameter, excluding the possibility to go up a level
     */
    @Override
    public List<Cell> getAdjCells(Cell cell) {
        boolean cantLevelUp = match.getMatchProperties().getOthersCantLevelUp();
        List<Cell> adjCells = match.getIsland().getAdjCells(cell);

        if(cantLevelUp) {
            adjCells = adjCells.stream()
                        .filter(_cell -> _cell.getTower().getTopComponent().getComponentCode() <= cell.getTower().getTopComponent().getComponentCode()) // remove cells where tower is higher than where the worker is
                        .collect(Collectors.toList());
        }

        return adjCells;
    }

}
