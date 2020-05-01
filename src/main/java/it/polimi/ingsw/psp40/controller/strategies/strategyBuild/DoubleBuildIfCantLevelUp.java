package it.polimi.ingsw.psp40.controller.strategies.strategyBuild;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.PhaseType;
import it.polimi.ingsw.psp40.controller.TurnProperties;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.DefaultMove;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Location;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Class is used to to define the PROMETEO card
 * This strategy makes possible to build even before move if with move the worker can't level up
 */
public class DoubleBuildIfCantLevelUp extends DefaultBuild {

    public DoubleBuildIfCantLevelUp(Match match) {
        super(match);
    }


    /**
     * This method is used to retrieve a list of cells where is possible for a {@link Worker} to build any kind of possible {@link Component}
     * @param worker is the {@link Worker} you want to know where it can build
     * @return a list of {@link Cell} near the worker where is possible to build. If the build is happening before the move, it returns an empty list if the worker, while moving, can level up
     */
    @Override
    public List<Cell> getBuildableCells(Worker worker) {
        boolean alreadyMoved = TurnProperties.getPerformedPhases().stream().anyMatch( phase -> phase.getType() == PhaseType.MOVE_WORKER);

        if(!alreadyMoved) {
            DefaultMove defaultMove = new DefaultMove(match);
            List<Cell> whereCanIMove = defaultMove.getAvailableCells(worker);
            int workerInitialLevel = match.getLocation().getLocation(worker).getTower().getTopComponent().getComponentCode();
            List<Cell> whereCanIMoveGoingUp = whereCanIMove.stream()
                    .filter(cell -> cell.getTower().getTopComponent().getComponentCode() > workerInitialLevel)
                    .collect(Collectors.toList());
            if(whereCanIMoveGoingUp.size() != 0) {
                return new ArrayList<>();
            }
        }

        return super.getBuildableCells(worker);

    }
}
