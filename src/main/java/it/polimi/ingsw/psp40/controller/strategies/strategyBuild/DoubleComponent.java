package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class is used to to define the EFESTO card
 * This strategy makes possible to build two Components at the same time, but the second cannot be a {@link Component#DOME}
 */
public class DoubleComponent extends DefaultBuild {

    public DoubleComponent(Match match) {
        super(match);
    }

    /**
     * This method overrides the one of superclass {@link DefaultBuild} adding the possibility to build two components at the same time.
     * @param cell is the {@link Cell} where you want to know which are the  buildable components
     * @return a list of integers that represent the {@link Component} code of the buildable components
     */
    @Override
    public List<Integer> getComponentsBuildable(Cell cell) {
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



