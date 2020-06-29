package it.polimi.ingsw.psp40.controller;

import it.polimi.ingsw.psp40.commons.Component;
import it.polimi.ingsw.psp40.commons.PhaseType;
import it.polimi.ingsw.psp40.controller.strategies.strategyBuild.StrategyBuild;
import it.polimi.ingsw.psp40.controller.strategies.strategyLose.StrategyLose;
import it.polimi.ingsw.psp40.controller.strategies.strategyMove.StrategyMove;
import it.polimi.ingsw.psp40.controller.strategies.strategyWin.StrategyWin;
import it.polimi.ingsw.psp40.exceptions.SantoriniException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Player;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;

/**
 * This class is used to define a turn
 *
 * @author Vito96
 */
public class Turn {
    private Player player;
    private Phase currentPhase;
    private Worker selectedWorker;

    private StrategyMove strategyMove;
    private StrategyWin strategyWin;
    private StrategyBuild strategyBuild;
    private StrategyLose strategyLose;

    /**
     * Constructor to init turn instance
     *
     * @param player the player of the turn
     */
    public Turn(Player player) {
        this.player = player;
        initializeTurn();
    }

    /**
     * Method to initialize the turn
     */
    public void initializeTurn() {
        currentPhase = player.getCurrentCard().getInitialPhase();
        selectedWorker = null;
    }

    /**
     * Method to set the strategyBuild in the turn
     *
     * @param strategyBuild the strategyBuild to set
     */
    public void setStrategyBuild(StrategyBuild strategyBuild) {
        this.strategyBuild = strategyBuild;
    }

    /**
     * Method to set the strategyMove in the turn
     *
     * @param strategyMove the strategyMove to set
     */
    public void setStrategyMove(StrategyMove strategyMove) {
        this.strategyMove = strategyMove;
    }

    /**
     * Method to set the strategyWin in the turn
     *
     * @param strategyWin the strategyWin to set
     */
    public void setStrategyWin(StrategyWin strategyWin) {
        this.strategyWin = strategyWin;
    }

    /**
     * Method to set the strategyLose in the turn
     *
     * @param strategyLose the strategyLose to set
     */
    public void setStrategyLose(StrategyLose strategyLose) {
        this.strategyLose = strategyLose;
    }


    /**
     * Method to retrieve the player of the turn
     *
     * @return the {@link Player} of the turn
     */
    public Player getPlayer() {
        return player;
    }


    /**
     * This method call the getAvailableCells of the strategyMove for retrieve the available cell for move
     *
     * @return an {@link List<Cell>} of available cells
     */
    public List<Cell> getAvailableCellForMove() {
        return this.strategyMove.getAvailableCells(selectedWorker);
    }


    /**
     * This method call the move of the strategyMove for move the selected {@link Worker}
     */
    public void move(Cell cell) throws SantoriniException {
        this.strategyMove.move(selectedWorker, cell);
    }

    /**
     * This method call the getBuildableCells of the strategyMove for retrieve the available cell for build
     *
     * @return an {@link List<Cell>} of available cells
     */
    public List<Cell> getAvailableCellForBuild() {
        return this.strategyBuild.getBuildableCells(selectedWorker);
    }

    /**
     * This method call the getComponentsBuildable of the strategyMove for retrieve the components available for specified cell
     *
     * @return an {@link List<Integer>} which rappresent the specified {@link Component}
     */
    public List<Integer> getComponentsBuildable(Cell cell) {
        return this.strategyBuild.getComponentsBuildable(cell);
    }

    /**
     * This method call the build of the strategyBuild
     *
     * @param component the component to build
     * @param cell      the cell to build on
     */
    public void build(Component component, Cell cell) throws SantoriniException {
        this.strategyBuild.build(component, cell, selectedWorker);
    }

    /**
     * This method call the checkWin of the strategyWin
     *
     * @return a {@link boolean} which indicate if the player has win or not
     */
    public boolean checkWin() {
        return this.strategyWin.checkWin();
    }

    /**
     * This method set the {@link Worker} in the turn
     *
     * @return a {@link boolean} which indicate if the player has win or not
     */
    public void setSelectedWorker(Worker selectedWorker) {
        this.selectedWorker = selectedWorker;
    }

    /**
     * This method call the checkLoseForMove of the strategyLose
     *
     * @return a {@link boolean} which indicate if the player has lost
     */
    public boolean checkLoseForMove() {
        return strategyLose.checkLoseForMove(strategyMove);
    }

    /**
     * This method call the checkLoseForBuild of the strategyLose
     *
     * @return a {@link boolean} which indicate if the player has lost
     */
    public boolean checkLoseForBuild() {
        return strategyLose.checkLoseForBuild(strategyBuild, selectedWorker);
    }

    /**
     * This method updates the currente phase from the parameter type, otherwise it throws an run time exception
     */
    public void updateCurrentPhaseFromType(PhaseType type) {
        Phase tempCurrentPhase = currentPhase.getNextPhases().stream().filter(phase -> phase.getType().equals(type)).findFirst().orElse(null);
        if (tempCurrentPhase == null) throw new RuntimeException("Cannot update CurrentPhase with a null phase");
        else currentPhase = tempCurrentPhase;
    }

    /**
     * This method return the current phase of the turn
     *
     * @return the current phase of the turn
     */
    public Phase getCurrentPhase() {
        return currentPhase;
    }
}
