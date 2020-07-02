package it.polimi.ingsw.psp40.controller;

/**
 * This class represents an object with all strategy names
 *
 * @author Vito96
 */
public class StrategySettings {
    /* Attributes */
    private String strategyMove;
    private String strategyWin;
    private String strategyBuild;
    private String strategyLose;

    /* Methods */

    /**
     * Getter for strategyMove
     *
     * @return strategyMove's name
     */
    public String getStrategyMove() {
        return strategyMove;
    }

    /**
     * Setter for strategyMove
     */
    public void setStrategyMove(String strategyMove) {
        this.strategyMove = strategyMove;
    }


    /**
     * Getter for strategyWin
     *
     * @return strategyWin's name
     */
    public String getStrategyWin() {
        return strategyWin;
    }

    /**
     * Setter for strategyWin
     */
    public void setStrategyWin(String strategyWin) {
        this.strategyWin = strategyWin;
    }

    /**
     * Getter for strategyBuild
     *
     * @return strategyBuild's name
     */
    public String getStrategyBuild() {
        return strategyBuild;
    }


    /**
     * Setter for strategyBuild
     */
    public void setStrategyBuild(String strategyBuild) {
        this.strategyBuild = strategyBuild;
    }


    /**
     * Getter for strategyLose
     *
     * @return strategyLose's name
     */
    public String getStrategyLose() {
        return strategyLose;
    }

    /**
     * Setter for strategyLose
     */
    public void setStrategyLose(String strategyLose) {
        this.strategyLose = strategyLose;
    }
}
