package it.polimi.ingsw.controller;

import it.polimi.ingsw.commons.Component;
import it.polimi.ingsw.commons.Publisher;
import it.polimi.ingsw.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.controller.strategies.strategyWin.StrategyWin;
import it.polimi.ingsw.exceptions.SantoriniException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.server.VirtualView;

import java.lang.reflect.Constructor;
import java.util.List;


/**
 * This class manage all operation of a turn of the current player of the match
 * @author Vito96
 */
public class TurnManager {

    /* Attributes */

    private Match match;
    private Player currentPlayer;
    private VirtualView virtualView;
    private CardManager cardManager;
    private Phase currentPhase;
    private Worker selectedWorker;

    //various strategy
    private StrategyMove strategyMove;
    private StrategyWin strategyWin;
    private StrategyBuild strategyBuild;

    /* Constructor(s) */

    /**
     * Constructor: init turnManager's instance
     * @param match the match of the turn
     */
    public TurnManager(Match match, VirtualView virtualView) {
        this(match);
        this.virtualView = virtualView;
    }

    /**
     * Constructor for test
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
        Card card = this.currentPlayer.getCurrentCard();

        //use reflection
        try {
            //make this for all strategies
            strategyMove = (StrategyMove) createStrategyWithReflection("it.polimi.ingsw.controller.strategies.strategyMove." + card.getStrategySettings().getStrategyMove(), new Class[]{Match.class}, new Object[]{match});
            strategyWin = (StrategyWin) createStrategyWithReflection("it.polimi.ingsw.controller.strategies.strategyWin." + card.getStrategySettings().getStrategyWin(), new Class[]{Match.class}, new Object[]{match});
            strategyBuild = (StrategyBuild) createStrategyWithReflection("it.polimi.ingsw.controller.strategies.strategyBuild." + card.getStrategySettings().getStrategyBuild(), new Class[]{Match.class}, new Object[]{match});
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
            TurnProperties.getInitialLevels().put(worker, this.match.getLocation().getLocation(worker).getTower().getTopComponent().getComponentCode());
        });
        this.currentPhase = currentPlayer.getCurrentCard().getInitialPhase();

    };

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private List<Cell> getAvailableCellForMove() {
        Phase nextPhase = checkForPermittedPhase("move");
        List<Cell> availableCell = null;
        if (nextPhase != null) {
            availableCell = this.strategyMove.getAvailableCells(selectedWorker);
        }
        return availableCell;
    }


    public void move(Worker worker, Cell cell) throws SantoriniException {
        Phase nextPhase = checkForPermittedPhase("move");
        if (nextPhase != null) {
            this.strategyMove.move(worker, cell);
            currentPhase = nextPhase;
        }
    };

    public List<Cell> getAvailableCellForBuild() {
        Phase nextPhase = checkForPermittedPhase("build");
        List<Cell> availableCell = null;
        if (nextPhase != null) {
            availableCell = this.strategyBuild.getBuildableCells(selectedWorker);
        }
        return availableCell;
    }

    public void build(Component component, Cell cell) throws SantoriniException {
        Phase nextPhase = checkForPermittedPhase("build");
        if (nextPhase != null) {
            this.strategyBuild.build(component, cell, selectedWorker);
            currentPhase = nextPhase;
        }
    }

    public boolean checkWin() {
        return this.strategyWin.checkWin();
    }

    private Phase checkForPermittedPhase(String type) {
        Phase nextPhase = null;
        for(Phase phase : currentPhase.getNextPhases()) {
            if (phase.equals(type)) {
                nextPhase = phase;
            }
        }
        return nextPhase;
    }

    public List<Phase> getNextPhases() {
        return this.currentPhase.getNextPhases();
    }

    public void selectWorker(Worker worker) {
        if (currentPhase.getType().equals("selectWorker")) this.selectedWorker = worker;
    }

}
