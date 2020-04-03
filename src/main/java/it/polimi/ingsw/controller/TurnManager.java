package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

public class TurnManager {
    private Match match;
    private Player currentPlayer;
    //private HashMap<Worker, Cell> initialCellMap = new HashMap<>();
    //private Virtual View

    //various strategy



    private void buildStrategies() {
        //builder/factory
        //String id = this.currentPlayer.getCurrentCard();
        //StrategyJsonMapper strategyJsonMapper = CardManager.getCardInfo(id)

        //use reflection
        //Class<AvailableCell> class = Class.forName("it.polimi.ingsw.controller.strategies" + strategyJsonMapper.strategyRetrieveCellForMove);
        //Constructor<?> constructor = clazz.getConstructor(String.class, Integer.class);
        //Object instance = constructor.newInstance("stringparam", 42);

        //make this for all strategies
    }

    public TurnManager(Match match) {
        this.match = match;
        this.currentPlayer = this.match.getCurrentPlayer();
        buildStrategies();
        TurnProperties.resetAllParameter();
    }

    public void beginTurn() {
        currentPlayer.getWorkers().forEach(worker -> {
            TurnProperties.getInitialPositionMap().put(worker, worker.getCurrentCell());
        });
    };
}
