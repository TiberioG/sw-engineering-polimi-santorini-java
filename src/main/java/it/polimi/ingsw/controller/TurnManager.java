package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.controller.strategies.strategyWin.StrategyWin;
import it.polimi.ingsw.model.*;

import java.lang.reflect.Constructor;


/**
 * This class manage all operation of a turn of the current player of the match
 * @author Vito96
 */
public class TurnManager {

    /* Attributes */

    private Match match;
    private Player currentPlayer;
    private CardManager cardManager;
    //private Virtual View

    //various strategy
    private StrategyMove strategyMove;
    private StrategyWin strategyWin;
    private StrategyBuild strategyBuild;

    /* Constructor(s) */

    /**
     * Constructor: init turnManager's instance
     * @param match the match of the turn
     */

    public TurnManager(Match match) {
        this.match = match;
        this.currentPlayer = this.match.getCurrentPlayer();
        this.cardManager = CardManager.initCardManager();
        buildStrategies();
        TurnProperties.resetAllParameter();
    }

    /* Methods */

    /**
     * This method create strategy instances using reflection
     * @param className the name of the class of the desired istance
     * @param types the class of the params for the costructor of the desired instance
     * @param parameters the parameters of the params for the constructor of the desired instance
     * @return the desidered istance
     * @throws Exception for errors while creating the instance
     */
    private Object createStrategyWithReflection(String className, Class[] types, Object[] parameters) throws Exception {
        Class clazz = Class.forName(className);
        Constructor constructor =  clazz.getConstructor(types);
        return constructor.newInstance(parameters);
    }

    /**
     * This method create set the strategy of the turn from the card of the current player
     */
    private void buildStrategies() {
        //builder/factory
        int cardId = this.currentPlayer.getCurrentCard();
        Card card = cardManager.getCardById(cardId);

        //use reflection
        try {
            //make this for all strategies
            strategyMove = (StrategyMove) createStrategyWithReflection("it.polimi.ingsw.controller.strategies.strategyMove." + card.getStrategySettings().getStrategyMove(), new Class[]{Match.class}, new Object[]{match});
            strategyWin = (StrategyWin) createStrategyWithReflection("it.polimi.ingsw.controller.strategies.strategyWin." + card.getStrategySettings().getStrategyWin(), new Class[]{Match.class}, new Object[]{match});
            //strategyBuild = (StrategyBuild) createStrategyWithReflection("it.polimi.ingsw.controller.strategies.strategyBuild." + card.getStrategySettings().getStrategyBuild(), new Class[]{Match.class}, new Object[]{match});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method make all operation for init the turn
     */
    public void beginTurn() {
        currentPlayer.getWorkers().forEach(worker -> {
            TurnProperties.getInitialPositionMap().put(worker, this.match.getLocation().getLocation(worker));
        });
    };
}
