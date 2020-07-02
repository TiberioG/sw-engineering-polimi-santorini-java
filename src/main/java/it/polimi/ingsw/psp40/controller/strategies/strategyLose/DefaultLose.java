package it.polimi.ingsw.psp40.controller.strategies.strategyLose;

import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

/**
 * This Class is used to define the default rules for loosing a match
 *
 * @author Vito96
 */
public class DefaultLose implements StrategyLose {

    /* Attributes */
    protected Match match;

    /**
     * Constructor
     *
     * @param match
     */
    public DefaultLose(Match match) {
        this.match = match;
    }

    /* Methods */

    /**
     * This method returns TRUE if and oly if  all the workers of a player cannot move
     *
     * @param strategyMove it's the strategy linked to the card of the player
     * @return TRUE if and only if all the workers of a player cannot move according to his card
     */
    @Override
    public boolean checkLoseForMove(StrategyMove strategyMove) {
        return match.getCurrentPlayer().getWorkers().stream().filter(worker -> strategyMove.getAvailableCells(worker).size() > 0).count() == 0;
    }

    /**
     * This method returns TRUE if a worker cannot build any component according to his strategy
     *
     * @param strategyBuild the strategy of the player
     * @param worker        the worker of the player to check
     * @return TRUE if and oly if the worker cannot build any component according to his strategy
     */
    @Override
    public boolean checkLoseForBuild(StrategyBuild strategyBuild, Worker worker) {
        return strategyBuild.getBuildableCells(worker).size() == 0;
    }
}
