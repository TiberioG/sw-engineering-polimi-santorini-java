package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.controller.strategies.strategyWin.StrategyWin;
import it.polimi.ingsw.model.*;

import java.lang.reflect.Constructor;

public class TurnManager {
    private Match match;
    private Player currentPlayer;
    private CardManager cardManager;
    //private HashMap<Worker, Cell> initialCellMap = new HashMap<>();
    //private Virtual View

    //various strategy
    private StrategyMove strategyMove;
    private StrategyWin strategyWin;
    private StrategyBuild strategyBuild;

    private Object createStrategyWithReflection(String className, Class[] types, Object[] parameters) throws Exception {
            //Class.forName("it.polimi.ingsw.controller.strategies.strategyMove." + card.getStrategySettings().getStrategyMove());
            Class clazz = Class.forName(className);
            Constructor constructor =  clazz.getConstructor(types);
            return constructor.newInstance(parameters);
    }

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
            System.out.println(e.getMessage());
        }
    }

    public TurnManager(Match match) {
        this.match = match;
        this.currentPlayer = this.match.getCurrentPlayer();
        this.cardManager = CardManager.initCardManager();
        buildStrategies();
        TurnProperties.resetAllParameter();
    }

    public void beginTurn() {
        currentPlayer.getWorkers().forEach(worker -> {
            TurnProperties.getInitialPositionMap().put(worker, this.match.getLocation().getLocation(worker));
            TurnProperties.getInitialLevels().put(worker, this.match.getLocation().getLocation(worker).getTower().getTopComponent().getComponentCode());
        });
    };
}
