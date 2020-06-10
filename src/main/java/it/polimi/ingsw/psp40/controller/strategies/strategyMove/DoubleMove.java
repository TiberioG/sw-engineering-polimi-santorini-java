package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This strategy allows a worker to move one additional time, but not back to its initial space
 * @author sup3rgiu
 */
public class DoubleMove extends DefaultMove {
    /**
     * Constructor
     * @param match
     */
    public DoubleMove(Match match) {
        super(match);
    }

    /**
     * This method allows a {@link Worker} to move again but not in the cell where it was before
     * @param worker it's the {@link Worker} you want to know about
     * @return a list of {@link Cell} where is possible to move
     */
    @Override
    public List<Cell> getAvailableCells(Worker worker) {
        List<Cell> defaultAvailableCells = super.getAvailableCells(worker);
        return defaultAvailableCells.stream()
                .filter(cell -> cell != match.getMatchProperties().getInitialPositionMap().get(worker)) // removes initial position cell
                .collect(Collectors.toList());
    }
}
