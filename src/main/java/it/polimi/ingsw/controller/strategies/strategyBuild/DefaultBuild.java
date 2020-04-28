package it.polimi.ingsw.controller.strategies.strategyBuild;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.controller.TurnManager;
import it.polimi.ingsw.controller.TurnProperties;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the class that implements the strategy used to build new components on the Island of the Match
 * It is the superclass of all the different strategies
 */
public class DefaultBuild implements StrategyBuild {
    /* Attributes */
    protected  Match match;

    /* Constructor(s) */
    public DefaultBuild(Match match) {
        this.match = match;
    }

    /* Methods */

    /**
     * This method is used to build a component on the island of a match
     * @param compToBuild is the type of {@link Component} you want to build
     * @param whereToBuild is the {@link Cell} where you want to add the new component
     * @param worker is the {@link Worker} used to build
     * @throws ZeroCellsAvailableBuildException
     * @throws WrongCellSelectedBuildException
     * @throws ComponentNotAllowed
     * @throws BuildLowerComponentException
     */
    @Override
    public void build(Component compToBuild, Cell whereToBuild, Worker worker) throws ZeroCellsAvailableBuildException, WrongCellSelectedBuildException, ComponentNotAllowed, BuildLowerComponentException {
        List<Cell> buildableCells = getBuildableCells(worker);

        if(buildableCells.size() == 0) //1 check: there are free cells close to the worker where is possible to build?
        {
            throw new ZeroCellsAvailableBuildException();
        }
        else {
            if(!buildableCells.contains(whereToBuild) ) //2 check: where I wanna build is in the list of buildable cells?
            {
                throw new WrongCellSelectedBuildException();
            }
            else {
                if(!getComponentsBuildable(whereToBuild).contains(compToBuild.getComponentCode()) ) //3 check: what I wanna build is allowed there?
                {
                    throw new ComponentNotAllowed();
                }
                match.getIsland().addComponent(compToBuild, whereToBuild); // here I finally add the component in the model
                //whereToBuild.getTower().addComponent(compToBuild);
                TurnProperties.builtNow(worker, whereToBuild); // I also save that this worker has built here in the turnProp.
            }
        }

    }

    /**
     * This method is used to retrieve a list of cells where is possible for a {@link Worker} to build any kind of possible {@link Component}
     * @param worker is the {@link Worker} you want to know where it can build
     * @return a list of {@link Cell} near the worker where is possible to build
     */
    public List<Cell> getBuildableCells(Worker worker) {
        Cell whereIam = match.getLocation().getLocation(worker);
        List<Cell> adjCells = match.getIsland().getAdjCells(whereIam);
        return adjCells.stream()
                .filter(cell -> cell.getTower().getTopComponent() != Component.DOME) // remove cells where the tower is complete
                .filter(cell -> match.getLocation().getOccupant(cell) == null) // removes cells where there is a worker
                .collect(Collectors.toList());
    }

    /**
     * This method is used to know the kind of new component is possible to build on a specific {@link Cell}
     * @param cell is the {@link Cell} where you want to know which is the next buildable component
     * @return a list of integers that represent the {@link Component} code. In this DefaultBuild the list is always made out of only one element
     */
    public List<Integer>getComponentsBuildable(Cell cell){
        List<Integer> comps = new ArrayList<>();
        Component current = cell.getTower().getTopComponent();
        if (current == Component.DOME){ // if tower is already complete
            return comps; // the list of buildable components must be empty
        }
        else {
            comps.add(current.getComponentCode() + 1); // else I add the next component code
            return comps;
        }
    }


}


