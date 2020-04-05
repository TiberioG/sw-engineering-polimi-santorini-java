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

/**
 * This Class is used to to define the ATLANTE card
 * This strategy makes possible to build a {@link Component#DOME} in every level of the Island
 */
public class DomeAnywhere extends DefaultBuild {

    public DomeAnywhere(Match match) {
        super(match);
    }

    /**
     * This method overrides the one of the superclass {@link DefaultBuild} adding the possibility to build a {@link Component#DOME} in addition to the normal level buildable wih the default rules
     * @param cell is the {@link Cell} where you want to know which are the components buildable
     * @return a list of integers that represent the {@link Component} code
     */
    @Override
    public List<Integer> getComponentsBuildable(Cell cell) {
        List<Integer> comps = new ArrayList<>();
        Component current = cell.getTower().getTopComponent();
        if (current == Component.DOME) { // if tower is already complete
            return comps; // the list of buildable components must be empty
        } else {
            comps.add(current.getComponentCode() + 1);
            comps.add(Component.DOME.getComponentCode()); // a dome is allowed everywhere (if not already present) !!!! <----------
            return comps;
        }
    }
}
