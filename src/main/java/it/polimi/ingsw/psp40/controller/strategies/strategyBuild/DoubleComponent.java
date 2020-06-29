package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This strategy makes possible to build two Components at the same time, but the second cannot be a {@link Component#DOME}
 *
 * @author TiberioG
 */
public class DoubleComponent extends DefaultBuild {

    /**
     * Constructor
     *
     * @param match
     */
    public DoubleComponent(Match match) {
        super(match);
    }


    /**
     * This method overrides the default one returning the standard cells if it's used for the first time in a turn
     * If it's used for a second time it returns only the cell where the worker built before
     *
     * @param worker is the {@link Worker} you want to know where it can build
     * @return a list of {@link Cell}
     */
    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        List<Cell> adjCells = match.getIsland().getAdjCells(whereIam);
        if (match.getMatchProperties().getPreviousBuild(worker) == null) {// if i haven't aready built anything
            return adjCells.stream()
                    .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                    .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker
                    .collect(Collectors.toList());
        } else return adjCells.stream()
                .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker, this is overkill
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> cell.equals(match.getMatchProperties().getPreviousBuild(worker))) // adds only the cell where I built before <------------
                .filter(cell -> match.getMatchProperties().getPreviousBuild(worker).getTower().getTopComponent() != Component.THIRD_LEVEL) // remove cells where there is a third level cause DOME is not allowed
                .collect(Collectors.toList());
    }


}



