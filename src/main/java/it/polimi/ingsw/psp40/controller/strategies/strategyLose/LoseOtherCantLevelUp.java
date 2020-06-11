package it.polimi.ingsw.psp40.controller.strategies.strategyLose;

import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.OthersCantLevelUp;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

/**
 * This Class is used when a player can lose because the strategy {@link OthersCantLevelUp}
 * is applied which means in this turn the workers cannot level up
 * @author Vito96
 */
public class LoseOtherCantLevelUp extends DefaultLose{

    /**
     * Constructor
     * @param match
     */
    public LoseOtherCantLevelUp (Match match) {
        super(match);
    }


    /**
     * This method returns TRUE and resets the flag  if and only if all the workers of a player cannot move
     * @param strategyMove it's the strategy linked to the card of the player
     * @return TRUE if and only if all the workers of a player cannot move according to his card
     */
    @Override
    public boolean checkLoseForMove(StrategyMove strategyMove) {
        boolean hasLose = super.checkLoseForMove(strategyMove);
        if (hasLose) match.getMatchProperties().setOthersCantLevelUp(false);
        return hasLose;
    }

    /**
     * This method returns TRUE and resets the flag if a worker cannot build any component according to his strategy
     * @param strategyBuild the strategy of the player
     * @param worker the worker of the player to check
     * @return TRUE if and only if the worker cannot build any component according to his strategy
     */
    @Override
    public boolean checkLoseForBuild(StrategyBuild strategyBuild, Worker worker) {
        boolean hasLose = super.checkLoseForBuild(strategyBuild, worker);
        if (hasLose) match.getMatchProperties().setOthersCantLevelUp(false);
        return hasLose;
    }
}
