package it.polimi.ingsw.psp40.controller;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.Configuration;
import it.polimi.ingsw.psp40.commons.PhaseType;
import it.polimi.ingsw.psp40.commons.messages.Message;
import it.polimi.ingsw.psp40.commons.messages.TypeOfMessage;
import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyLose.StrategyLose;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.controller.strategies.strategyWin.StrategyWin;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.network.server.VirtualView;
import it.polimi.ingsw.psp40.model.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;


/**
 * This class manage all operation of a turn of the current player of the match
 * @author Vito96
 */
public class TurnManager {

    /* Attributes */

    private Match match;
    private VirtualView virtualView;

    private HashMap<String, Turn> turnsMap = new HashMap<>();
    private Turn currentTurn;

    /* Constructor(s) */

    /**
     * Constructor to init turnManager's instance
     * @param match the match of the turn
     */
    public TurnManager(Match match, VirtualView virtualView) {
        this.virtualView = virtualView;
        this.match = match;
        createTurns();
        //richiamo la virtual view per notificargli l'inizio del turno con le fasi disponibili
    }

    /**
     * Constructor for test
     */
    public TurnManager(Match match) {
        this.match = match;
        createTurns();
    }

    /* Methods */


    /**
     * This method create and inizialize a turn for every player in the match
     */
    private void createTurns() {
        for(Player player : match.getPlayers()) {
            Turn turn = new Turn(player);
            buildStrategies(turn);
            turnsMap.put(player.getName(), turn);
        }
        currentTurn = turnsMap.get(match.getCurrentPlayer().getName());
        inizializedCurrentTurn();
    }

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
     * @param turn the turn to add strategy to it
     */
    private void buildStrategies(Turn turn) {
        //builder/factory
        Card card = turn.getPlayer().getCurrentCard();
        //use reflection
        try {
            //make this for all strategies
            turn.setStrategyMove((StrategyMove) createStrategyWithReflection(Configuration.strategyMovePackage + "." + card.getStrategySettings().getStrategyMove(), new Class[]{Match.class}, new Object[]{match}));
            turn.setStrategyBuild((StrategyBuild) createStrategyWithReflection(Configuration.strategyBuildPackage + "." + card.getStrategySettings().getStrategyBuild(), new Class[]{Match.class}, new Object[]{match}));
            turn.setStrategyWin((StrategyWin) createStrategyWithReflection(Configuration.strategyWinPackage + "." + card.getStrategySettings().getStrategyWin(), new Class[]{Match.class}, new Object[]{match}));
            turn.setStrategyLose((StrategyLose) createStrategyWithReflection(Configuration.strategyLosePackage + "." + card.getStrategySettings().getStrategyLose(), new Class[]{Match.class}, new Object[]{match}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method call the getAvailableCellForMove method of the strategyMove of the current turn and update the virtualView with a message which contains the available cells
     */
    public void getAvailableCellForMove() {
        List<Cell> availableCell = null;
        if (checkForPermittedPhase(PhaseType.MOVE_WORKER)) {
            availableCell = currentTurn.getAvailableCellForMove();
            updateVirtualView(new Message(getCurrentPlayer().getName(), TypeOfMessage.AVAILABLE_CELL_FOR_MOVE, availableCell));
        }
    }

    /**
     * This method call the move method of the strategyMove of the current turn and and call the updateCurrentPhase method to update the current phase
     * @param cell the cell destination of the selectedWorker
     */
    public void move(Cell cell) throws SantoriniException {
        if (checkForPermittedPhase(PhaseType.MOVE_WORKER)) {
            currentTurn.move(cell);
            updateCurrentPhase(PhaseType.MOVE_WORKER);
        }
    }

    /**
     * This method call the getAvailableCellForBuild method of the strategyMove of the current turn and update the virtualView with a message which contains the available cells
     */
    public void getAvailableCellForBuild() {
        List<Cell> availableCell;
        if (checkForPermittedPhase(PhaseType.BUILD_COMPONENT)) {
            availableCell = currentTurn.getAvailableCellForBuild();

            HashMap<Cell, List<Integer>>  mapOfAvailableCell = new HashMap<>();
            for (Cell cell: availableCell) {
                mapOfAvailableCell.put(cell, currentTurn.getComponentsBuildable(cell));
            }
            updateVirtualView(new Message(getCurrentPlayer().getName(), TypeOfMessage.AVAILABLE_CELL_FOR_BUILD, mapOfAvailableCell));
        }
    }

    /**
     * This method call the build method of the strategyBuild of the current turn and and call the updateCurrentPhase method to update the current phase
     * @param component the component to build on the selected cell
     * @param cell the cell where to build the component
     */
    public void build(Component component, Cell cell) throws SantoriniException {
        if (checkForPermittedPhase(PhaseType.BUILD_COMPONENT)) {
            currentTurn.build(component, cell);
            updateCurrentPhase(PhaseType.BUILD_COMPONENT);
        }
    }

    /**
     * This method check if the list of the next phases contains the phase type in the param
     * @param type the type of phase to check
     * @return a boolean which indicate if the next phases list contains the phase in the param
     */
    private Boolean checkForPermittedPhase(PhaseType type) {
        return currentTurn.getCurrentPhase().getNextPhases().stream().anyMatch(phase -> phase.getType().equals(type));
    }

    /*
    public List<Phase> getNextPhases() {
        return currentTurn.getCurrentPhase().getNextPhases();
    }
    */

    /**
     * This method call the setSelectedWorker method of the current turn and update the virtual view with a message which contains the next phase available
     * @param worker the worker to select
     */
    public void selectWorker(Worker worker) {
        if (currentTurn.getCurrentPhase().getType().equals(PhaseType.SELECT_WORKER)) {
            currentTurn.setSelectedWorker(worker);
            updateVirtualView(new Message(getCurrentPlayer().getName(), TypeOfMessage.NEXT_PHASE_AVAILABLE, currentTurn.getCurrentPhase().getNextPhases()));
        }
    }

    /**
     * This method update the currentPhase with the phase of the same type in the nextPhases list with a check if the player has won or if the turn is over
     * @param type the type of the phase to update
     */
    private void updateCurrentPhase(PhaseType type) {
        currentTurn.updateCurrentPhaseFromType(type);
        match.getMatchProperties().getPerformedPhases().add(type);


        if (currentTurn.getCurrentPhase().getNeedCheckForVictory() && currentTurn.checkWin()) {
            match.setWinningPlayer(getCurrentPlayer().getName());
            return;
        }

        if (currentTurn.getCurrentPhase().getNextPhases() == null) {
            //richiamare la virtual view per notificare la fine del turno
            //seleziono il prossimo turno
            selectNextTurn();
        } else {
            List<Phase> listOfNextPhase = currentTurn.getCurrentPhase().getNextPhases();
            if (listOfNextPhase.size() == 1 && listOfNextPhase.get(0).getType().equals(PhaseType.BUILD_COMPONENT) && currentTurn.checkLoseForBuild()) {
                removePlayerForLost();
            } else updateVirtualView(new Message(getCurrentPlayer().getName(), TypeOfMessage.NEXT_PHASE_AVAILABLE, currentTurn.getCurrentPhase().getNextPhases()));
        }
    }

    /**
     * This method update the current player of the match and consequently makes his turn as the current turn
     */
    private void selectNextTurn() {
        match.selectNextCurrentPlayer();
        currentTurn = turnsMap.get(match.getCurrentPlayer().getName());
        inizializedCurrentTurn();
    }


    /**
     * This method performs all the operations to remove a player who has lost
     */
    private void removePlayerForLost() {
        String nameOfTheLosePlayer = match.getCurrentPlayer().getName();
        match.selectNextCurrentPlayer();
        turnsMap.remove(nameOfTheLosePlayer);
        match.removePlayer(nameOfTheLosePlayer);

        currentTurn = turnsMap.get(match.getCurrentPlayer().getName());
        if (turnsMap.size() == 1) {
            match.setWinningPlayer(currentTurn.getPlayer().getName());
        } else {
            inizializedCurrentTurn();
        }
    }


    /**
     * this method initializes the current round by resetting the match properties related to the rounds and checking a player's loss, also notifies the virtual view of the start of the turn
     */
    private void inizializedCurrentTurn() {
        match.getMatchProperties().resetAllParameter();
        currentTurn.initializeTurn();

        //se non ci sono celle disponibili per muoversi
        if (currentTurn.checkLoseForMove()) {
            removePlayerForLost();
        } else {
            getCurrentPlayer().getWorkers().forEach(worker -> {
                match.getMatchProperties().getInitialPositionMap().put(worker, this.match.getLocation().getLocation(worker));
                match.getMatchProperties().getInitialLevels().put(worker, this.match.getLocation().getLocation(worker).getTower().getTopComponent().getComponentCode());
            });
            updateVirtualView(new Message(currentTurn.getPlayer().getName(), TypeOfMessage.INIT_TURN, currentTurn.getCurrentPhase()));
        }
    }

    /**
     * this method return the player of the current turn
     * @return the player of the current turn
     */
    public Player getCurrentPlayer() {
        return currentTurn.getPlayer();
    }

    /**
     * this method call the displayMessage of the virtualView
     * @param message the message to sent
     */
    private void updateVirtualView(Message message) {
        if (virtualView != null) virtualView.displayMessage(message);
    }

}
