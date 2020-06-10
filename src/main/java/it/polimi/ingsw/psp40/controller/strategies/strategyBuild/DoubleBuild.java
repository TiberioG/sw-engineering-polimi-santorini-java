package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This strategy makes possible to build a second time, but not in the same Cell as the previous build action in the same turn
 * Attention: the build method must be called twice during the turn
 * @author TiberioG
 */
public class DoubleBuild extends DefaultBuild {

    /**
     * Constructor
     * @param match
     */
    public DoubleBuild(Match match) {
        super(match);
    }

    /**
     * This method overrides the one of the superclass {@link DefaultBuild} removing from the list of buildable Cells the cell used to buld by the same worker in the same turn,
     * this is done by using the  {@link it.polimi.ingsw.psp40.model.MatchProperties} class where is saved the list of cells built by each worker in every turn
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
                .filter(cell -> !cell.equals(match.getMatchProperties().getPreviousBuild(worker))) // removes the cell where I built before <------------
                .collect(Collectors.toList());
    }



}
