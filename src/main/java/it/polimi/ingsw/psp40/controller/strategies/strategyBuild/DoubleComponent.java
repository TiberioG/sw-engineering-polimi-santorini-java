package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.psp40.exceptions.ComponentNotAllowed;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedBuildException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableBuildException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Class is used to to define the EFESTO card
 * This strategy makes possible to build two Components at the same time, but the second cannot be a {@link Component#DOME}
 */
public class DoubleComponent extends DefaultBuild {

    public DoubleComponent(Match match) {
        super(match);
    }


    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        List<Cell> adjCells = match.getIsland().getAdjCells(whereIam);
        if(match.getMatchProperties().getPreviousBuild(worker) == null) {// if i haven't aready built anything
            return adjCells.stream()
                    .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                    .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker
                    .collect(Collectors.toList());
        }
        else return adjCells.stream()
                .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker, this is overkill
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> cell.equals(match.getMatchProperties().getPreviousBuild(worker))) // adds only the cell where I built before <------------
                .filter(cell -> match.getMatchProperties().getPreviousBuild(worker).getTower().getTopComponent() != Component.THIRD_LEVEL) // remove cells where there is a third level cause DOME is not allowed
                .collect(Collectors.toList());
    }


}



