package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

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
                .filter(cell -> cell != match.getMatchProperties().getInitialPositionMap().get(worker)) // removes initial position cell
                .collect(Collectors.toList());
    }
}
