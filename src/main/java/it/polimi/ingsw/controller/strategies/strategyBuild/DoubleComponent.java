package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.controller.TurnProperties;
import it.polimi.ingsw.exceptions.BuildLowerComponentException;
import it.polimi.ingsw.exceptions.ComponentNotAllowed;
import it.polimi.ingsw.exceptions.WrongCellSelectedBuildException;
import it.polimi.ingsw.exceptions.ZeroCellsAvailableBuildException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// EFESTO

public class DoubleComponent implements StrategyBuild {
    /* Attributes */
    private Match match;


    /* Constructor(s) */
    public DoubleComponent(Match match) {
        this.match = match;
    }

    /* Methods */

    @Override
    public void build(Component compToBuild, Cell whereToBuild, Worker worker) throws ZeroCellsAvailableBuildException, WrongCellSelectedBuildException, ComponentNotAllowed, BuildLowerComponentException {
        List<Cell> buildableCells = getBuildableCells(worker);

        if(buildableCells.size() == 0) //1 check: threre are free cells close to the worker where is possible to build
        {
            throw new ZeroCellsAvailableBuildException();
        }
        else {
            if(!buildableCells.contains(whereToBuild) ) //2 check: where I wanna build is in the list of buildable cells
            {
                throw new WrongCellSelectedBuildException();
            }
            else {
                if(!getComponentsBuildable(whereToBuild).contains(compToBuild.getComponentCode()) ) //3 check: what I wanna build is allowed there
                {
                    throw new ComponentNotAllowed();
                }
                whereToBuild.getTower().addComponent(compToBuild);
                TurnProperties.builtNow(worker, whereToBuild);
            }
        }

    }

    private List<Cell> getBuildableCells(Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        List<Cell> adjCells = match.getIsland().getAdjCells(whereIam);
        return adjCells.stream()
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker
                .collect(Collectors.toList());
    }

    private List<Integer>getComponentsBuildable(Cell cell){
        List<Integer> comps = new ArrayList<>();
        Component current = cell.getTower().getTopComponent();
        if (current == Component.DOME){ // if tower is already complete
            return comps; // the list of buildable components must be empty
        }
        if (current == Component.THIRD_LEVEL) {
            comps.add(current.getComponentCode() + 1); // add a dome as normal
        }
        if (current == Component.SECOND_LEVEL){
            comps.add(current.getComponentCode() + 1); // add third
        }
        if (current == Component.FIRST_LEVEL){
            comps.add(current.getComponentCode() + 1); // add third
            comps.add(current.getComponentCode() + 2); // and fourth since it's doublebuild allowed
        }
        if (current == Component.GROUND){
            comps.add(current.getComponentCode() + 1); // add first
            comps.add(current.getComponentCode() + 2); // and second since it's doublebuild allowed
        }
        return comps;
    }

}



