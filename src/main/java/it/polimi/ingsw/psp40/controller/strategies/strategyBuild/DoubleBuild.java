package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.controller.TurnProperties;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This Class is used to to define the DEMETRA card
 * This strategy makes possible to build a second time, but not in the same Cell
 * AAATTENTION:: The Build method must be called TWICE during the same turn
 */
public class DoubleBuild extends DefaultBuild {

    public DoubleBuild(Match match) {
        super(match);
    }

    /**
     * This method overrides the one of the superclass {@link DefaultBuild} removing from the list of buildable Cells the cell used to buld by the same worker in the same turn,
     * this is done by using the  {@link TurnProperties} static class where is saved the list of cells built by each worker in every turn
     * @param worker is the {@link Worker} you want to know where it can build
     * @return a list of {@link Cell} near the worker where is possible to build, without the one of previous build
     */
    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        List<Cell> adjCells = match.getIsland().getAdjCells(whereIam);
        return adjCells.stream()
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker
                .filter(cell -> !cell.equals(TurnProperties.getPreviousBuild(worker))) // removes the cell where I built before <------------
                .collect(Collectors.toList());
    }



}
