package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.controller.TurnProperties;
import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

// ARTEMIDE

public class DoubleMove extends DefaultMove {

    /* Constructor(s) */

    public DoubleMove(Match match) {
        super(match);
    }

    /* Methods */

    @Override
    public List<Cell> getAvailableCells(Worker worker) {
        List<Cell> defaultAvailableCells = super.getAvailableCells(worker);
        return defaultAvailableCells.stream()
                .filter(cell -> cell != TurnProperties.getInitialPositionMap().get(worker)) // removes initial position cell
                .collect(Collectors.toList());
    }
}
